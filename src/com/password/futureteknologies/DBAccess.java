package com.password.futureteknologies;

import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DBAccess {
    private Db db_object;
    private SQLiteDatabase db;
    
    public DBAccess(Context context){
        this.db_object = new Db(context);
        this.db = this.db_object.open();
    }
    
    public int checkUserExistence(){
        Cursor cursor = null;
        try{
               cursor = this.db.query(Db.USER_TB, new String[]{Db.USER_ID}, null, null, null, null, null);
               if (cursor.getCount() > 0){
                   return 1;
               }
               else{
                   return 0;
               }
        }
        catch(Exception e){
            return -1;
        }        
    }
    
    public int deleteData(long id){
        try{
            int rows_affected = this.db.delete(Db.PASS_TB, Db.PASS_ID + "=?", new String[] {String.valueOf(id)});
            if (rows_affected > 0){
                return 1;
            }
            else{
                return 0;
            }
        }
        catch(Exception e){
            return 0;
        }
    }
    
    public int insertRecord(String app_name, String url, String username, String password){
        try{
               String current_date = String.valueOf(System.currentTimeMillis());
                ContentValues values = new ContentValues();
                values.put(Db.APP, app_name);
                values.put(Db.APP_URL, url);
                values.put(Db.APP_USERNAME, username);
                values.put(Db.APP_PASSWORD, password);
                values.put(Db.APP_DATE, current_date);
                this.db.insert(Db.PASS_TB, null, values);
                return 1;
        }
        catch(Exception e){
              return 0;
        }         
    }
    
    public int insertUserSettings(String email, String nickname, String password, String picture){
        try{
              String current_date = String.valueOf(System.currentTimeMillis());
                           
                ContentValues values = new ContentValues();
                values.put(Db.EMAIL, email);
                values.put(Db.PASSWORD, password);
                values.put(Db.NICKNAME, nickname);
                values.put(Db.PICTURE, picture);
                values.put(Db.DATE, current_date);
                this.db.insert(Db.USER_TB, null, values);
                return 1;
        }
        catch(Exception e){
              return 0;
        }      
    }    
    
    public Cursor login(String username, String password){
        Cursor cursor = null;
        try{
               cursor = this.db.query(Db.USER_TB, new String[]{Db.USER_ID, Db.NICKNAME, Db.PICTURE}, Db.EMAIL  +"=? and " + Db.PASSWORD + "=?",
                                                                  new String[]{username, password}, null, null, null);
               return cursor;
        }
        catch(Exception e){
            return cursor;
        }         
    }
    
    public Cursor selectData(){
        Cursor cursor = null;
        try{
               cursor = this.db.query(Db.PASS_TB, new String[]{Db.PASS_ID, Db.APP, Db.APP_USERNAME}, null, null, null, null, Db.APP + " asc");
               return cursor;
        }
        catch(Exception e){
            return cursor;
        }         
    } 
    
    public Cursor selectDataById(long id){
        Cursor cursor = null;
        try{
               cursor = this.db.query(Db.PASS_TB, new String[]{Db.PASS_ID, Db.APP, Db.APP_URL, Db.APP_USERNAME, 
                                                                   Db.APP_PASSWORD, Db.APP_DATE}, Db.PASS_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
               return cursor;
        }
        catch(Exception e){
            return cursor;
        }         
    }
    
    
    public Cursor selectDataBySearch(String search){
        Cursor cursor = null;
        try{
             String clause = " like '%' || ? || '%' "; 
               cursor = this.db.query(Db.PASS_TB, new String[]{Db.PASS_ID, Db.APP, Db.APP_URL, Db.APP_USERNAME, 
                                                                   Db.APP_PASSWORD, Db.APP_DATE}, Db.APP + clause +  "  or " + Db.APP_URL + clause + "  or  " +
                                                                   Db.APP_USERNAME + clause + " or " +  Db.APP_PASSWORD + clause,
                                                                   new String[]{search, search, search, search}, null, null, Db.APP + " asc");
               return cursor;
        }
        catch(Exception e){
            return cursor;
        }         
    }    
    
    public Cursor selectSettings(){
        Cursor cursor = null;
        try{
               cursor = this.db.query(Db.USER_TB, new String[]{Db.USER_ID, Db.EMAIL, Db.NICKNAME, Db.PASSWORD, Db.PICTURE}, 
                                                                   null, null, null, null, null);
               return cursor;
        }
        catch(Exception e){
            return cursor;
        }         
    }  
    
    public Cursor sortData(String search, String sort_direction){
        Cursor cursor = null;
       
        if (search.equalsIgnoreCase("Date")){
            search = Db.APP_DATE;
        }      
        else if (search.equalsIgnoreCase("Password")){
            search = Db.APP_PASSWORD;
        }
        else if (search.equalsIgnoreCase("Url")){
            search = Db.APP_URL;
        }          
        else if (search.equalsIgnoreCase("Username")){
            search = Db.APP_USERNAME;
        }
        else{ // the date is the default
            search = Db.APP;
        }
        
        try{
               cursor = this.db.query(Db.PASS_TB, new String[]{Db.PASS_ID, Db.APP, Db.APP_URL, Db.APP_USERNAME, 
                                                                   Db.APP_PASSWORD, Db.APP_DATE}, null, null, null, null, search + "  " + sort_direction);
               return cursor;
        }
        catch(Exception e){
            return cursor;
        }         
    }     
    
    public int updateRecord(String app_name, String url, String username, String password, long id){
        try{
            String string_id = String.valueOf(id);
                           
                ContentValues values = new ContentValues();
                values.put(Db.APP, app_name);
                values.put(Db.APP_URL, url);
                values.put(Db.APP_USERNAME, username);
                values.put(Db.APP_PASSWORD, password);
                this.db.update(Db.PASS_TB, values, Db.PASS_ID + "=?", new String[]{string_id});
                return 1;
        }
        catch(Exception e){
              return 0;
        }         
    }
    
    public int updateSettings(String email, String nickname, String password, String picture, long id){
        try{
            String string_id = String.valueOf(id);
                           
                ContentValues values = new ContentValues();
                values.put(Db.EMAIL, email);
                values.put(Db.NICKNAME, nickname);
                values.put(Db.PASSWORD, password);
                values.put(Db.PICTURE, picture);
                this.db.update(Db.USER_TB, values, Db.USER_ID + "=?", new String[]{string_id});
                return 1;
        }
        catch(Exception e){
              return 0;
        }         
    }     
}
