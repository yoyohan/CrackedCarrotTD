package com.crackedcarrot.menu;

import com.crackedcarrot.GameInit;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

/**
 * Class that functions as the instruction view. It creates the
 * dialog consisting of the level instructions.
 */
public class InstructionWebView extends Activity {
    
	private WebView mWebView;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	/** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	setContentView(R.layout.webinstruction);
    	
    	Button close = (Button) findViewById(R.id.closewebdialog);
    	close.setOnClickListener(
    			new View.OnClickListener() {
    				public void onClick(View v) {
    					GameInit.pause = false;
    					GameInit.pauseSemaphore.release();
    					finish();
    				}
    			});

    	final Button back = (Button) findViewById(R.id.backwebdialog);
    	back.setOnClickListener(
    			new View.OnClickListener() {
    				public void onClick(View v) {
    					mWebView.goBack();
    				}
    			});
    	
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setBackgroundColor(0);

        
        mWebView.setWebViewClient(new WebViewClient() {
        	   public void  onPageFinished(WebView  view, String  url) {
        		   if (mWebView.canGoBack()) {
        			  back.setVisibility(View.VISIBLE); 
        		   } else {
         			  back.setVisibility(View.INVISIBLE); 
        		   }
        	   }
        	 });

        //mWebView.canGoBack();
        
        
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
        
        mWebView.loadUrl("file:///android_asset/instructions.html");
	}	
}