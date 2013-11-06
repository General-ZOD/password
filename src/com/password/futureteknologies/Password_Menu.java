package com.password.futureteknologies;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Password_Menu extends Activity{  
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.log_out: 
                                        SharedPreferences pref = getSharedPreferences("Consolidator", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor current_editor = pref.edit();
                                        current_editor.clear().commit();
                                        Toast.makeText(Password_Menu.this, "You have been logged out", Toast.LENGTH_SHORT).show();                          
                                        Intent intent = new Intent(Password_Menu.this, Login.class);
                                        startActivity(intent);                
                                     break;
            case R.id.update_settings:
                                     Intent update = new Intent(Password_Menu.this, UpdateSettings.class);
                                     startActivity(update);
                                     break;
            case R.id.help_link:
                                     Intent help_link = new Intent(Password_Menu.this, Information.class);
                                     help_link.putExtra("content", "guide");
                                     startActivity(help_link);
                                     break;                 
            case R.id.home_link:
                                     Intent home_link = new Intent(Password_Menu.this, Home.class);
                                     startActivity(home_link);
                                     break;                
        }
        return true;
    }
}
