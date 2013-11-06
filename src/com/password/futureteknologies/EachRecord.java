package com.password.futureteknologies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.net.Uri;
import android.view.Window;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EachRecord extends Password_Menu{
    private DBAccess db_object;
    private Cursor cursor;
    private long position;
    private EditText app_name, app_url, app_user,app_password;
    private TextView txt_app_name, txt_app_url, txt_app_user, txt_app_password, txt_app_date;
    private Button edit_btn, update_btn, delete_btn, cancel_btn;
    private boolean is_in_edit_mode = false;
    
    private void login(){
        SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
        String user = pref.getString("user", ""); 
        
        if (user.length() == 0){
            Intent intent = new Intent(this, Login.class);
            this.startActivity(intent);
        }        
    }
    
    private void checkEditMode(){
        if (this.is_in_edit_mode){ //User is in edit mode            
            //invisible items
            txt_app_name.setVisibility(View.GONE);
            txt_app_url.setVisibility(View.GONE);
            txt_app_user.setVisibility(View.GONE);
            txt_app_password.setVisibility(View.GONE);        
            edit_btn.setVisibility(View.GONE);
            delete_btn.setVisibility(View.GONE);     
            
            //Visible items
            app_name.setVisibility(View.VISIBLE);
            app_url.setVisibility(View.VISIBLE);
            app_user.setVisibility(View.VISIBLE);
            app_password.setVisibility(View.VISIBLE); 
            update_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);             
        }
        else{
            //Invisible items
            app_name.setVisibility(View.GONE);
            app_url.setVisibility(View.GONE);
            app_user.setVisibility(View.GONE);
            app_password.setVisibility(View.GONE); 
            update_btn.setVisibility(View.GONE);
            cancel_btn.setVisibility(View.GONE); 
            
            //Visible items
            txt_app_name.setVisibility(View.VISIBLE);
            txt_app_url.setVisibility(View.VISIBLE);
            txt_app_user.setVisibility(View.VISIBLE);
            txt_app_password.setVisibility(View.VISIBLE);        
            edit_btn.setVisibility(View.VISIBLE);
            delete_btn.setVisibility(View.VISIBLE);            
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        this.login();
        
        Intent each_record = this.getIntent();
        position = each_record.getLongExtra("selected_id", -1);
        
        if (position == -1){
            // send user back to the record page
            Intent go_back = new Intent(this, Record.class);
            this.startActivity(go_back);
        }
         requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.each_record);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_window);
   
        LinearLayout layout = (LinearLayout) findViewById(R.id.each_record_layout);          
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        layout.startAnimation(anim);
        
        app_name = (EditText) findViewById(R.id.each_app);
        app_url = (EditText) findViewById(R.id.each_url);
        app_user = (EditText) findViewById(R.id.each_email);
        app_password = (EditText) findViewById(R.id.each_password);
        txt_app_name = (TextView) findViewById(R.id.text_each_app);
        txt_app_url = (TextView) findViewById(R.id.text_each_url);
       txt_app_user = (TextView) findViewById(R.id.text_each_email);
        txt_app_password = (TextView) findViewById(R.id.text_each_password);        
        txt_app_date = (TextView) findViewById(R.id.each_date);
        
        txt_app_url.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        TextView url_view = (TextView) view;
                        String view_text = url_view.getText().toString().trim();
                        Uri webpage;
                        if (view_text.startsWith("http://")){
                            webpage = Uri.parse(view_text);
                        }
                        else if (view_text.startsWith("https://")){
                            webpage = Uri.parse(view_text);
                        }
                        else{
                            webpage = Uri.parse("http://" + view_text);
                        }
                        //Need to redirect to my site with explanation if  the link is invalid
                        try{
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                            startActivity(webIntent);
                        }
                        catch(Exception e){
                            Toast.makeText(EachRecord.this, "The URL is invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
        });
        
        this.db_object = new DBAccess(this);
        
        edit_btn = (Button) findViewById(R.id.each_record_edit_btn);
        update_btn = (Button) findViewById(R.id.each_record_update_btn);
        delete_btn = (Button) findViewById(R.id.each_record_delete_btn);
        cancel_btn = (Button) findViewById(R.id.each_record_cancel_btn);
        
        edit_btn.setOnClickListener(new EditRecord());
        update_btn.setOnClickListener(new EditRecord());
        delete_btn.setOnClickListener(new EditRecord());
        cancel_btn.setOnClickListener(new EditRecord());
        
        this.DisplayList();
    }
    
    @Override
    public  void onResume(){
        super.onResume();
        this.login();
        if (position == -1){
            // send user back to the record page
            Intent go_back = new Intent(this, Record.class);
            this.startActivity(go_back);
        }
        else{
            this.DisplayList();
        }
    }
    
    private String returnDateFormat(long date_to_use){
        String date="";
        if (date_to_use == 0){
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            date = format.format(new Date());
        }
        else{
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            date = format.format(new Date(date_to_use));            
        }
        return date;
    }  
    
    public void DisplayList(){
        cursor = db_object.selectDataById(position);
        if (cursor == null){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        else{
            cursor.moveToFirst();
            position  = cursor.getLong(0); 
            String name, url, user, password, date;
            name = cursor.getString(1);
            url = cursor.getString(2);
            user = cursor.getString(3);
            password = cursor.getString(4);
            date = this.returnDateFormat(Long.parseLong(cursor.getString(5)));
      
            txt_app_name.setText(name);
            txt_app_url.setText(url);
            txt_app_user.setText(user);
            txt_app_password.setText(password);
            txt_app_date.setText(date);   
            
            checkEditMode();
            
           TextView title = (TextView) findViewById(R.id.window_title);
            title.setText(name);
        }
      
    }
    
    //this is for adding a record into the system
    private class EditRecord implements View.OnClickListener{
        
        public void onClick(View view){
                switch(view.getId()){
                    case R.id.each_record_edit_btn: this.showEdit(); break;
                    case R.id.each_record_update_btn: this.updateRecord(); break;
                    case R.id.each_record_delete_btn:
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(EachRecord.this);
                                                    alert.setTitle("Deletion Confirmation");
                                                    alert.setMessage("Delete this data?");
                                                    alert.setCancelable(false);
                                                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface di, int i) {
                                                            deleteRecord();
                                                        }
                                                    });
                                                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                                                            public void onClick(DialogInterface di, int i) {
                                                                di.cancel();
                                                                Toast.makeText(EachRecord.this, "Deletion cancelled", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    AlertDialog dialog = alert.create();
                                                    dialog.show();
                                                   break;
                    case R.id.each_record_cancel_btn: this.cancel(); break;
                }
            }
        
        private void cancel(){
             is_in_edit_mode = false;
             checkEditMode();
            
            Toast.makeText(EachRecord.this, "All updates have been cancelled", Toast.LENGTH_SHORT).show();
        }
        
        protected void deleteRecord(){

            int result = db_object.deleteData(position); //1:success, 0:error
            if (result == 1){
                Toast.makeText(EachRecord.this, "Data has been deleted", Toast.LENGTH_SHORT).show();
                position = -1;
                Intent go_to_records = new Intent(EachRecord.this, Record.class);
                startActivity(go_to_records);
            }
        }        
        
        private void showEdit(){
             is_in_edit_mode = true;
            app_name.setText( txt_app_name.getText());
            app_url.setText(txt_app_url.getText());
            app_user.setText(txt_app_user.getText());
            app_password.setText(txt_app_password.getText());
            
            checkEditMode();
        }
        
        private void updateRecord(){
            //update the record
            Boolean error = false;
            String error_message = "";
            String name =   app_name.getText().toString().trim();
            String url = app_url.getText().toString().trim();
            String username = app_user.getText().toString().trim();
            String password = app_password.getText().toString().trim();
            
            if (name.trim().length() == 0){
                error = true;
                error_message = "Application Name is required";
                app_name.setText("");
            }
            if (username.trim().length() == 0){
                error = true;
                error_message =  "User name or Email is required";
                app_user.setText("");
            }
            if (password.trim().length() == 0){
                error = true;
                error_message = "Password is required";
                app_password.setText("");
            }
            
            if (error){
                Toast.makeText(EachRecord.this, error_message, Toast.LENGTH_SHORT).show();
            }
            else{
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                int result = db_object.updateRecord(name, url, username, password, position);
                if (result == 1){
                    Toast.makeText(EachRecord.this, "Record has been updated", Toast.LENGTH_SHORT).show();
                     is_in_edit_mode = false;
                    DisplayList();
                }
                else{
                    Toast.makeText(EachRecord.this, "Error updating the record", Toast.LENGTH_SHORT).show();
                }
            }            
        }
    }
}
