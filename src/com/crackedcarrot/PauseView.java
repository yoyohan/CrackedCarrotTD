package com.crackedcarrot;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.crackedcarrot.menu.R;

/**
 * Class that functions as the pause view. It creates the
 * dialog consisting of the level instructions.
 */
public class PauseView extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	/** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	setContentView(R.layout.pause);
    	
    	Button resume = (Button) findViewById(R.id.resume);
    	resume.setOnClickListener(
    			new View.OnClickListener() {
    				public void onClick(View v) {
    					GameInit.pause = false;
    					GameInit.pauseSemaphore.release();
    					finish();
    				}
    			});	
    	}
}