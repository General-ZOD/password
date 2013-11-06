package com.password.futureteknologies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

public class Login extends Activity {
    private DBAccess db_access;
    
    private void checkUserExistence(){
      //check if user information has been inserted into the system; if not, redirect to the setup page
        this.db_access = new DBAccess(this);
        int is_user_setup  = this.db_access.checkUserExistence(); // 1: yes, 0: no,  -1: error
        
        if (is_user_setup == 0 ){
            Intent intent = new Intent(this, Setup.class);
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
        setContentView(R.layout.login);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_window);
        
         TextView title = (TextView) findViewById(R.id.window_title);
         title.setText("Please, log in");
        
        Button btn = (Button) findViewById(R.id.login_btn);
        btn.setOnClickListener (new View.OnClickListener(){
            
            public void onClick(View view){
                EditText email_box, pass_box;
                String email, pass;
                email_box = (EditText) findViewById(R.id.login_email);
                pass_box = (EditText) findViewById(R.id.login_pass);
                
                email = email_box.getText().toString();
                pass =  pass_box.getText().toString();
                
                if (email.length() == 0){
                    Toast.makeText(Login.this, "Email is Required", Toast.LENGTH_LONG).show();
                }
                else if (!email.matches("^[-a-zA-Z0-9_\\.]+@[-a-z0-9_]+\\.[-a-z0-9_]+([-\\.a-z])*$")){
                    Toast.makeText(Login.this, "Email address format is invalid", Toast.LENGTH_LONG).show();
                    email_box.setText("");
                }
                else if (pass.length() == 0){
                    Toast.makeText(Login.this, "Password is Required", Toast.LENGTH_LONG).show();
                }
                else{
                    Cursor result = db_access.login(email, pass);
                    int no_of_rows = result.getCount();
                    if (result == null){
                        Toast.makeText(Login.this, "Error retrieving data from the app", Toast.LENGTH_LONG).show();
                    }
                    else if (no_of_rows == 0){
                        Toast.makeText(Login.this, "Wrong Email/Password", Toast.LENGTH_LONG).show();
                    }
                    else if (no_of_rows > 0){
                        result.moveToFirst();
                        String nickname = result.getString(1);
                        String picture_uri = result.getString(2);
                        SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
                        SharedPreferences.Editor current_editor = pref.edit();
                        current_editor.putString("user", nickname);
                        current_editor.putString("picture", picture_uri);
                        current_editor.commit();                          
                       
                        Intent intent = new Intent(Login.this, Home.class);
                        Login.this.startActivity(intent);                        
                    }
                }
            }
        });  
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.login_layout);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        layout.startAnimation(anim);
    }
    
    @Override
    public void onResume(){
        super.onResume();
        this.checkUserExistence();
        
        SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
        String user = pref.getString("user", ""); 
        
        if (user.length() > 0){
            Intent intent = new Intent(this, Record.class);
            this.startActivity(intent);     
        }
    }
}
