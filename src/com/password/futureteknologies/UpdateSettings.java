package com.password.futureteknologies;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.database.Cursor;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.Window;

public class UpdateSettings extends Password_Menu{
    static final int USER_IMAGE = 1;
    private Button btn;
    private DBAccess db_access;
    private Cursor cursor;
    private long position;
    private  EditText email_box, pass_box, confirm_pass_box, nickname_box;
    private  String email, pass, confirm_pass, nick;
    private String user, picture_string;
    private ImageView picture;
    private Bitmap avatar;
    private boolean image_is_updated = false;
    private TextView title;
    
    public void DisplayList(){
        cursor = db_access.selectSettings();
        if (cursor == null){
            Toast.makeText(this, "Application Error", Toast.LENGTH_SHORT).show();
        }
        else{
            cursor.moveToFirst();
            position = cursor.getLong(0);
            email  = cursor.getString(1);
            nick = cursor.getString(2);
            pass = cursor.getString(3);
            
            email_box.setText(email);
            nickname_box.setText(nick);
            pass_box.setText(pass);
            confirm_pass_box.setText(pass);
        }          
    }    
    
    private void login(){
        SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
        user = pref.getString("user", ""); 
        picture_string = pref.getString("picture", "");
        
        if (user.length() == 0){
            Intent intent = new Intent(this, Login.class);
            this.startActivity(intent);
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.login();        
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.update_settings);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_window);
        title = (TextView) findViewById(R.id.window_title);
         title.setText("Update Settings for " + user);
        
        picture = (ImageView) findViewById(R.id.update_user_image);
        email_box = (EditText) findViewById(R.id.update_email);
        pass_box = (EditText) findViewById(R.id.update_pass);
        confirm_pass_box = (EditText) findViewById(R.id.update_confirm_pass);
        nickname_box = (EditText) findViewById(R.id.update_nick); 
        
        ViewClick image_change = new ViewClick();   
        picture.setOnClickListener(image_change);        
        
        if (picture_string.length() > 0){
            Uri picture_uri = Uri.parse(picture_string);
            picture.setImageBitmap(null);
            picture.setImageURI(picture_uri);
        }
        
        this.db_access = new DBAccess(this);
        this.DisplayList();
      
        btn = (Button) findViewById(R.id.update_btn);
        btn.setOnClickListener (new OnClickListener(){
            
            public void onClick(View view){
                Dialog progressbar = ProgressDialog.show(UpdateSettings.this, "", "Updating.....");
                
                email = email_box.getText().toString();
                pass =  pass_box.getText().toString();
                confirm_pass = confirm_pass_box.getText().toString();
                nick = nickname_box.getText().toString();
                
                if (email.length() == 0){
                    Toast.makeText(UpdateSettings.this, "Email is Required", Toast.LENGTH_LONG).show();
                }
                else if (!email.matches("^[-a-zA-Z0-9_\\.]+@[-a-z0-9_]+\\.[-a-z0-9_]+([-\\.a-z])*$")){
                    Toast.makeText(UpdateSettings.this, "Email address format is invalid", Toast.LENGTH_LONG).show();
                }
                else if(nick.length() == 0){
                    Toast.makeText(UpdateSettings.this, "Nickname  is Required", Toast.LENGTH_LONG).show();
                }                
                else if (pass.length() == 0){
                    Toast.makeText(UpdateSettings.this, "Password is Required", Toast.LENGTH_LONG).show();
                }
                else if (confirm_pass.length() == 0){
                    Toast.makeText(UpdateSettings.this, "Confirm Password is Required", Toast.LENGTH_LONG).show();
                }
                else if (!pass.equals(confirm_pass)){
                    Toast.makeText(UpdateSettings.this, "Password and Confirm Password must match", Toast.LENGTH_LONG).show();
                }
                else{
                    try{
                            //save the new image
                        if (image_is_updated){
                            picture_string = "avatar.png";
                            avatar.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput(picture_string, MODE_PRIVATE));                   
                            Uri image_uri = Uri.fromFile(new File(getFilesDir(), picture_string));
                            picture_string = image_uri.toString();                              
                        }

                            int result = db_access.updateSettings(email, nick, pass, picture_string,  position);
                            if (result == 1){
                                Toast.makeText(UpdateSettings.this, "Information has been updated", Toast.LENGTH_SHORT).show();
                                SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
                                SharedPreferences.Editor current_editor = pref.edit();
                                current_editor.putString("user", nick);
                                current_editor.putString("picture", picture_string);
                                current_editor.commit();
                                
                                         title.setText("Update Settings for " + nick);
                                DisplayList();
                            }
                            else{
                                Toast.makeText(UpdateSettings.this, "Error inserting data into database", Toast.LENGTH_LONG).show();
                            }
                    }
                    catch(IOException e){
                        Toast.makeText(UpdateSettings.this, "Error saving the avatar", Toast.LENGTH_LONG).show();
                    }
                }
                
                progressbar.dismiss();
            }
        });
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.update_layout);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        layout.startAnimation(anim);  
    }
    
    @Override
    public void onResume(){
        super.onResume();
        this.login();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent_data){
        if (requestCode == USER_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                Uri image_uri = intent_data.getData();
                try{
                      picture_string ="avatar.jpg";

                      //Only resize the source file
                      BitmapFactory.Options option = new BitmapFactory.Options();
                      option.inSampleSize = 2;
                      option.inJustDecodeBounds = true;
                      InputStream stream = getContentResolver().openInputStream(image_uri);
                      BitmapFactory.decodeStream(stream, null, option);
                      stream.close();
                      
                      //retrieve and use the source file
                      option = new BitmapFactory.Options();
                      option.inSampleSize = 5;
                      stream = getContentResolver().openInputStream(image_uri);
                      avatar = BitmapFactory.decodeStream(stream, null, option);
                      stream.close();                                       
 
                      picture.setImageBitmap(avatar);
                      image_is_updated = true;
                }
                catch(IOException e){
                    Toast.makeText(UpdateSettings.this, "Error saving the avatar", Toast.LENGTH_LONG).show();
                }                
            }
        }
    }      
    
   private class ViewClick implements View.OnClickListener{
        
        public void onClick(View view){
            switch(view.getId()){
                case R.id.update_user_image: 
                    Intent user_image = new Intent(Intent.ACTION_PICK);
                    user_image.setType("image/*");
                    startActivityForResult(user_image, USER_IMAGE);
                    break;
            }
        }
   }     
}
