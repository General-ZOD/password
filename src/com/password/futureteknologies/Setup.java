package com.password.futureteknologies;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Setup extends Activity{
    static final int USER_IMAGE = 1;
    private Button btn;
    private DBAccess db_access; 
    private String picture_string="";
    private ImageView picture;
    private Bitmap avatar;
    private boolean image_is_updated = false;   
    private TextView tos_link;
    private  EditText email_box, pass_box, confirm_pass_box, nickname_box;
    
    private void checkUserExistence(){
      //check if user information has been inserted into the system; if not, redirect to the setup page
        this.db_access = new DBAccess(this);
        int is_user_setup  = this.db_access.checkUserExistence(); // 1: yes, 0: no,  -1: error
        
        if (is_user_setup == 1 ){
            Intent intent = new Intent(this, Login.class);
            this.startActivity(intent);
        }
        else if (is_user_setup == -1){
            Toast.makeText(this, "Error accessing database", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.checkUserExistence();
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.setup);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_window);
        
        TextView title = (TextView) findViewById(R.id.window_title);
        title.setText("Application Setup");
        this.db_access = new DBAccess(this);
        
        tos_link = (TextView) findViewById(R.id.tos_link);
        
        picture = (ImageView) findViewById(R.id.setup_user_image);
        email_box = (EditText) findViewById(R.id.setup_email);
        pass_box = (EditText) findViewById(R.id.setup_pass);
        confirm_pass_box = (EditText) findViewById(R.id.setup_confirm_pass);
        nickname_box = (EditText) findViewById(R.id.setup_nick); 
        btn = (Button) findViewById(R.id.setup_btn);        
        
        ViewClick click_action = new ViewClick();   
        picture.setOnClickListener(click_action);
        btn.setOnClickListener (click_action);
        tos_link.setOnClickListener(click_action);
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.setup_layout);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        layout.startAnimation(anim);  
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
                    Toast.makeText(Setup.this, "Error saving the avatar", Toast.LENGTH_LONG).show();
                }  
            }
        }
    }    
    
    @Override
    public void onResume(){
        super.onResume();
        this.checkUserExistence();
    }
    
   private class ViewClick implements View.OnClickListener{
        
        public void onClick(View view){
            switch(view.getId()){
                case R.id.setup_user_image: 
                    Intent user_image = new Intent(Intent.ACTION_PICK);
                    user_image.setType("image/*");
                    startActivityForResult(user_image, USER_IMAGE);
                    break;
                case R.id.setup_btn: setup(); break;
                case R.id.tos_link: getInformation(); break;    
            }
        }
        
        private void getInformation(){
            Intent info = new Intent(Setup.this, Information.class);
            info.putExtra("content", "tos");
            startActivity(info);
        }
        
        private void setup(){
                String email, pass, confirm_pass, nick;
                
                email = email_box.getText().toString().trim();
                pass =  pass_box.getText().toString().trim();
                confirm_pass = confirm_pass_box.getText().toString().trim();
                nick = nickname_box.getText().toString().trim();
                
                if (email.length() == 0){
                    Toast.makeText(Setup.this, "Email is Required", Toast.LENGTH_LONG).show();
                }
                else if (!email.matches("^[-a-zA-Z0-9_\\.]+@[-a-z0-9_]+\\.[-a-z0-9_]+([-\\.a-z])*$")){
                    Toast.makeText(Setup.this, "Email address format is invalid", Toast.LENGTH_LONG).show();
                }
                else if(nick.length() == 0){
                    Toast.makeText(Setup.this, "Nickname  is Required", Toast.LENGTH_LONG).show();
                }
                else if (pass.length() == 0){
                    Toast.makeText(Setup.this, "Password is Required", Toast.LENGTH_LONG).show();
                }
                else if (confirm_pass.length() == 0){
                    Toast.makeText(Setup.this, "Confirm Password is Required", Toast.LENGTH_LONG).show();
                }
                else if (!pass.equals(confirm_pass)){
                    Toast.makeText(Setup.this, "Password and Confirm Password must match", Toast.LENGTH_LONG).show();
                }
                else if (picture_string.length() == 0){
                    Toast.makeText(Setup.this, "Please, upload an image by clicking on the image", Toast.LENGTH_LONG).show();
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
                            
                        int result = db_access.insertUserSettings(email, nick, pass, picture_string);
                        if (result == 1){
                            Toast.makeText(Setup.this, "Setup has been completed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Setup.this, Login.class);
                            Setup.this.startActivity(intent);
                        }
                        else{
                            Toast.makeText(Setup.this, "Error inserting data into database", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch(IOException e){
                        Toast.makeText(Setup.this, "Error saving the avatar", Toast.LENGTH_LONG).show();
                    }                      
                }            
        }
   }      
}
