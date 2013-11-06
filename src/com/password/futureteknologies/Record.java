package com.password.futureteknologies;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import java.util.List;
import java.util.ArrayList;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemSelectedListener;

public class Record extends Activity{
    private DBAccess db_object;
    private Cursor cursor;
    private TabHost layout;
    private LinearLayout search_layout, sort_layout, header_layout;
    private Button add_btn, done_btn, search_btn, refresh_btn;
    private ListView list;
    private TextView no_data, add_data, view_data;
    private Spinner sort_by;
    private String sort_direction = "asc";
    private String sort_column= "";
    private int currentTab = 1;
    private CheckBox chk_sort_direction;
    private String user;  //this is specifically for the currently logged in user
    private EditText search_view, app_name, app_url, app_username, app_password;
    
    private void login(){
        SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
        user = pref.getString("user", ""); 
        
        if (user.length() == 0){
            Intent intent = new Intent(this, Login.class);
            this.startActivity(intent);
        }
    }
    
    private void changeHeader(){
        if (currentTab == 1){ //view record
            add_data.setTypeface(Typeface.DEFAULT);
            add_data.setGravity(Gravity.CENTER);
            add_data.setTextColor(Color.rgb(207, 245, 247));
            add_data.setBackgroundResource(R.drawable.left_header);

            view_data.setTypeface(Typeface.DEFAULT_BOLD);
            view_data.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            view_data.setTextColor(Color.rgb(207, 245, 247));        
            view_data.setBackgroundColor(Color.rgb(119, 27, 27));
            search_view.setText("");
            search_layout.setVisibility(View.GONE);
            sort_layout.setVisibility(View.GONE);
            
            displayList();
        }
        else{ //add record
            add_data.setTypeface(Typeface.DEFAULT_BOLD);
            add_data.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            add_data.setTextColor(Color.rgb(207, 245, 247));
            add_data.setBackgroundColor(Color.rgb(119, 27, 27));

            view_data.setTypeface(Typeface.DEFAULT);
            view_data.setGravity(Gravity.CENTER);
            view_data.setTextColor(Color.rgb(207, 245, 247));        
            view_data.setBackgroundResource(R.drawable.right_header);       
            
            app_name.setText("");
            app_url.setText("");
            app_username.setText("");
            app_password.setText("");            
        }
       
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.record_menu, menu);
        return true;
    }   
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
         //Toast.makeText(Record.this, String.format("%d", item.getItemId()), Toast.LENGTH_SHORT).show();
        switch(item.getItemId()){
            case R.id.log_out: 
                                        SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor current_editor = pref.edit();
                                        current_editor.clear().commit();
                                        Toast.makeText(Record.this, "You have been logged out", Toast.LENGTH_SHORT).show();                          
                                        Intent intent = new Intent(Record.this, Login.class);
                                        startActivity(intent);                
                                     break;
            case R.id.sort_records:
                                    layout.setCurrentTab(1);
                                    search_layout.setVisibility(View.GONE);
                                    List<String> options = new ArrayList<String>();
                                    options.add("Sort by");
                                    options.add("Date");
                                    options.add( "Password");
                                    options.add("Url");
                                    options.add( "Username");
                                    options.add("Website");
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    Spinner spinner = (Spinner) findViewById(R.id.spinner_record);
                                    spinner.setAdapter(adapter);
                                    sort_layout.setVisibility(View.VISIBLE);
                                     break;
            case R.id.search_records: 
                                     layout.setCurrentTab(1);
                                    sort_layout.setVisibility(View.GONE);
                                    search_layout.setVisibility(View.VISIBLE);
                                     break;                 
            case R.id.home_link:
                                     Intent home_link = new Intent(Record.this, Home.class);
                                     startActivity(home_link);
                                     break;                
        }
        return true;
    }    
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        this.login();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.record);
        
        currentTab = getIntent().getIntExtra("tab", 1);
        
        db_object = new DBAccess(this);
   
        //find all needed views
        layout = (TabHost) findViewById(R.id.record_layout);   
        search_layout = (LinearLayout) findViewById(R.id.search_layout);
        sort_layout = (LinearLayout) findViewById(R.id.sort_layout); 
        add_btn = (Button) findViewById(R.id.btn_add_record);
        done_btn = (Button) findViewById(R.id.btn_add_done);
        search_btn = (Button) findViewById(R.id.btn_search_record);  
        refresh_btn = (Button) findViewById(R.id.btn_refresh);
        list = (ListView) findViewById(R.id.name_list);
        no_data = (TextView) findViewById(R.id.txt_empty);
         sort_by = (Spinner) findViewById(R.id.spinner_record);
        chk_sort_direction = (CheckBox) findViewById(R.id.sort_direction);
        header_layout = (LinearLayout) findViewById(R.id.record_header_layout);
        search_view = (EditText) findViewById(R.id.txt_search_record);
        
        app_name = (EditText) findViewById(R.id.app_name);
        app_url = (EditText) findViewById(R.id.app_url);
        app_username = (EditText) findViewById(R.id.app_username);
        app_password = (EditText) findViewById(R.id.app_password);        
        
        layout.setup();
        
        add_data = new TextView(this);
        add_data.setText("Add Data");
        add_data.setTextSize(30);
        add_data.setHeight(50);
        
        view_data = new TextView(this);
        view_data.setText("View Data");
        view_data.setTextSize(30);
        view_data.setHeight(50);
        
        layout.addTab(layout.newTabSpec("add_data").setIndicator(add_data).setContent(R.id.add_data_layout));
        layout.addTab(layout.newTabSpec("view_data").setIndicator(view_data).setContent(R.id.view_data_layout));
        layout.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

                                public void onTabChanged(String string) {
                                    if (string.equals("add_data")){
                                        currentTab = 0;
                                    }
                                    else{
                                        currentTab = 1;
                                        displayList();                                     
                                    }
                                    changeHeader();
                                }
                            });
        changeHeader();
        layout.setCurrentTab(currentTab);        
        
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        layout.startAnimation(anim);
        
        
        BtnResponse btn_obj = new BtnResponse();
        add_btn.setOnClickListener(btn_obj);
        done_btn.setOnClickListener(btn_obj);
        search_btn.setOnClickListener(btn_obj);
        refresh_btn.setOnClickListener(btn_obj);
        
        SortData sort_obj = new SortData();
        sort_by.setOnItemSelectedListener(sort_obj);
        
        chk_sort_direction.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    Dialog progressbar = ProgressDialog.show(Record.this, "", "Sorting.....");
                                    CheckBox check = (CheckBox) view;
                                    if (check.isChecked()){
                                        sort_direction = "desc";
                                    }
                                    else{
                                        sort_direction = "asc";                                       
                                    }
                                   Cursor cursor = db_object.sortData(sort_column, sort_direction);
                                   displayData(cursor);
                                    progressbar.dismiss();                                    
                                }
                            });
    }
    
    @Override
    public  void onResume(){
        super.onResume();
        this.login();
        this.displayList();
    }
    
    private void displayList(){
        cursor = db_object.selectData();
        displayData(cursor);
    }
    
    private void displayData(Cursor cursor){
        if (cursor == null){
            no_data.setText("No data saved yet");
            no_data.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
            header_layout.setVisibility(View.GONE);
        }
        else if (cursor.getCount() == 0){
            no_data.setText("No data saved yet");
            no_data.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
            header_layout.setVisibility(View.GONE);
        }
        else{
           
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_rows, cursor, new String[]{Db.APP, Db.APP_USERNAME},
                                                                                                        new int[]{R.id.list_first, R.id.list_last});
            list.setAdapter(adapter); 
            list.setOnItemClickListener(new ViewRecord());
            list.setVisibility(View.VISIBLE);
            header_layout.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.GONE);
        }        
    }
    
    //this is for viewing each record
    private class ViewRecord implements AdapterView.OnItemClickListener{
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            
            Intent each_record = new Intent(Record.this, EachRecord.class);
            each_record.putExtra("selected_id", id);
            startActivity(each_record);
        }
    }
    
    //this is for adding a record into the system
    private class BtnResponse implements View.OnClickListener{
        
        public void onClick(View view){
            switch(view.getId()){
                case  R.id.btn_add_record: this.addData(); break;
                case  R.id.btn_add_done: layout.setCurrentTab(1); break;
                case R.id.btn_search_record: this.searchData(); break;
                case R.id.btn_refresh: changeHeader(); break;    
            }
        }        
        
        private void addData(){
            Boolean error = false;
            String error_message = "";
            
            String name, url, username, password;
            name = app_name.getText().toString().trim();
            url = app_url.getText().toString().trim();
            username = app_username.getText().toString().trim();
            password = app_password.getText().toString().trim();
            
            if (name.length() == 0){
                error = true;
                error_message = "Application Name is required";
                app_name.setText("");
            }
            if (username.length() == 0){
                error = true;
                error_message =  "User name or Email is required";
                app_username.setText("");
            }
            if (password.length() == 0){
                error = true;
                error_message = "Password is required";
                app_password.setText("");
            }
            
            if (error){
                Toast.makeText(Record.this, error_message, Toast.LENGTH_LONG).show();
            }
            else{
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                int result = db_object.insertRecord(name, url, username, password);
                if (result == 1){
                    Toast.makeText(Record.this, "Record has been added", Toast.LENGTH_LONG).show();
                    app_name.setText("");
                    app_url.setText("");
                    app_username.setText("");
                    app_password.setText("");
                    displayList();
                }
                else{
                    Toast.makeText(Record.this, "Error inserting record", Toast.LENGTH_LONG).show();
                }
            }            
        }
        
        private void searchData(){
            Dialog progressbar = ProgressDialog.show(Record.this, "", "Searching.....");
            String search_txt = search_view.getText().toString();
            if (search_txt.trim().length() > 0){
                InputMethodManager hide_key = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                hide_key.hideSoftInputFromWindow(search_view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);                
                Cursor cursor = db_object.selectDataBySearch(search_txt.trim());
                displayData(cursor);
            }
            progressbar.dismiss();
       }     
    }
    
    private class SortData implements OnItemSelectedListener{
    
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                Dialog progressbar = ProgressDialog.show(Record.this, "", "Sorting.....");
                sort_column = parent.getItemAtPosition(pos).toString();
                Cursor cursor = db_object.sortData(sort_column, sort_direction);
                displayData(cursor);
                progressbar.dismiss();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Not needed
            }    
    }
}
