package com.crackedcarrot.menu;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.crackedcarrot.menu.R;

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
    					finish();
    				}
    			});
      
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setBackgroundColor(0);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
        mWebView.loadUrl("file:///android_asset/instructions.html");
	}	
}