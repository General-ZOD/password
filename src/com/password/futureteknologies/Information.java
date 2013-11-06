package com.password.futureteknologies;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.Window;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;

public class Information extends Activity{
    String user, picture;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.information);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_window);
        Dialog progressbar = ProgressDialog.show(this, "", "Loading.....");
        TextView title = (TextView) findViewById(R.id.window_title);
         title.setText( "App Guide");        
               
        WebView layout = (WebView) findViewById(R.id.info_layout);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        layout.startAnimation(anim);
        layout.getSettings().setJavaScriptEnabled(true);
      
        String link = getContent();
        layout.loadUrl(link);
        progressbar.dismiss();
    }
    
    private String getContent(){
        String content = getIntent().getStringExtra("content");
        
        if (content.equalsIgnoreCase("tos")){
            return "http://futureteknologies.com/custom/the_vault/the_vault.html";
        }
        else{
            return "http://futureteknologies.com";
        }
    }
}
