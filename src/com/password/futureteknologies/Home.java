package com.password.futureteknologies;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

public class Home extends Password_Menu{
    private  ImageView user_image;
    private ImageView add_record, view_record, help_link, update_record, logout;
    String user, picture;
    
    private void login(){
        SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
        user = pref.getString("user", ""); 
        picture = pref.getString("picture", "");
        
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
        setContentView(R.layout.home);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_window);
        
        TextView title = (TextView) findViewById(R.id.window_title);
         title.setText(user + "'s Home Page ");        
               
        LinearLayout layout = (LinearLayout) findViewById(R.id.home_layout);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        layout.startAnimation(anim);
        
        user_image = (ImageView) findViewById(R.id.user_image);
       add_record = (ImageView) findViewById(R.id.add_record_link);
        view_record = (ImageView) findViewById(R.id.view_record_link);
        help_link = (ImageView) findViewById(R.id.help_link);
        update_record = (ImageView) findViewById(R.id.update_settings_link);
        logout = (ImageView) findViewById(R.id.logout_link);
        
        if (picture.length() > 0){
            user_image.setImageURI(null);
            Uri picture_uri = Uri.parse(picture);
            user_image.setImageURI(picture_uri);
        }
        
        ViewClick view_listener = new ViewClick();
       add_record.setOnClickListener(view_listener);
        view_record.setOnClickListener(view_listener);
       update_record.setOnClickListener(view_listener);
       help_link.setOnClickListener(view_listener);
       logout.setOnClickListener(view_listener);
    }
    
    @Override
    public void onResume(){
        super.onResume();
        this.login();     
    }
    
   private class ViewClick implements OnClickListener{
        
        public void onClick(View view){
            switch(view.getId()){
                case R.id.add_record_link: 
                    Intent add_record = new Intent(Home.this, Record.class);
                    add_record.putExtra("tab", 0);
                    startActivity(add_record);
                    break;
                case R.id.view_record_link: 
                    Intent view_record = new Intent(Home.this, Record.class);
                    view_record.putExtra("tab", 1);
                    startActivity(view_record);                    
                    break;
                case R.id.update_settings_link: 
                    Intent update_settings = new Intent(Home.this, UpdateSettings.class);
                    startActivity(update_settings);  
                    break;
                case R.id.help_link:
                    Intent information  = new Intent(Home.this, Information.class);
                    information.putExtra("content", "guide");
                    startActivity(information);  
                    break;
                case R.id.logout_link: 
                    SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
                    SharedPreferences.Editor current_editor = pref.edit();
                    current_editor.remove("user").commit();
                    Toast.makeText(Home.this, "You have been logged out", Toast.LENGTH_SHORT).show();                          
                    Intent logout = new Intent(Home.this, Login.class);
                    startActivity(logout);                      
                    break;
            }
        }
   } 
}
