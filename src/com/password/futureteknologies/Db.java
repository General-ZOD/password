package com.password.futureteknologies;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;
import android.content.Context;

public class Db extends SQLiteOpenHelper{
    public static String DB = "aggregator";
    public static final int VERSION = 1;
    
    //tables
    public static String USER_TB = "users";
    public static final String USER_ID = "_id";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String NICKNAME ="nick_name";
    public static final String PICTURE="picture";
    public static final String DATE = "date_created";
    
        
    public static String PASS_TB="passwords";
    public static String TEMP_TB="temp_passwords";
    public static final String PASS_ID = "_id";
    public static final String APP = "application";
    public static final String APP_URL ="url";
    public static final String APP_USERNAME ="username";
    public static final String APP_PASSWORD = "password";
    public static final String APP_DATE = "date_created";   
    
    public Db(Context context){
        super(context, DB, null, VERSION);
    }
    
    public void close(SQLiteDatabase db){
        db.close();
    }  
    
    @Override
    public void onCreate(SQLiteDatabase db){
        //String 
        db.execSQL(this.User_Table_Creation());
        db.execSQL(this.Password_Table_Creation());
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version){
        //save the data in the table before removing it, and put it back once done
        try{
            db.execSQL(this.Temp_Password_Table_Creation());
            db.execSQL(String.format("insert into %s (select %s, %s, %s, %s, %s, %s from %s);", 
                                                                        TEMP_TB, PASS_ID, APP, APP_URL, APP_USERNAME, PASSWORD, APP_DATE, PASS_TB)); 
            db.execSQL("drop table if exists " + PASS_TB);
            db.execSQL(this.Password_Table_Creation());  
            db.execSQL(String.format("insert into %s (select %s, %s, %s, %s, %s, %s from %s);", 
                                                                        PASS_TB, PASS_ID, APP, APP_URL, APP_USERNAME, PASSWORD, APP_DATE, TEMP_TB)); 
             db.execSQL("drop table if exists " + TEMP_TB);           
        }
        catch(Exception e){
            //
        }
    }
    
    public SQLiteDatabase open() throws SQLException{
        return this.getWritableDatabase();
    }   
   
    private String Password_Table_Creation(){
        String sql = String.format("create table %s (%s integer primary key autoincrement, %s text not null, %s text null, %s text not null,"
                + " %s text not null, %s integer not null);", 
                                                                   PASS_TB, PASS_ID, APP, APP_URL, APP_USERNAME, PASSWORD, APP_DATE);
        return sql;
    }  
    
    private String Temp_Password_Table_Creation(){
        String sql = String.format("create table %s (%s integer primary key autoincrement, %s text not null, %s text null, %s text not null,"
                + " %s text not null, %s integer not null);", 
                                                                   TEMP_TB, PASS_ID, APP, APP_URL, APP_USERNAME, PASSWORD, APP_DATE);
        return sql;
    }     
    
    private String User_Table_Creation(){
        String sql = String.format("create table %s (%s integer primary key autoincrement, %s text not null, %s text not null,"
                + " %s text null, %s text not null, %s integer not null);", 
                                                                   USER_TB, USER_ID, EMAIL, PASSWORD, NICKNAME, PICTURE, DATE);
        return sql;
    }
}
