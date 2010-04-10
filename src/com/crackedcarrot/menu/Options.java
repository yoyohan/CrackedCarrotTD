package com.crackedcarrot.menu;

import com.crackedcarrot.menu.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class Options extends Activity {

	private boolean optionsSound;
	private ImageButton imageButton1;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_options);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	
        // Restore preferences
        SharedPreferences settings = getSharedPreferences("Options", 0);
        optionsSound = settings.getBoolean("optionsSound", false);
        
        
        imageButton1 = (ImageButton) findViewById(R.id.MainMenuOptionsImageButton1);
        imageButton1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Log.d("OPTIONS", "Clicked button 1");
        		
        			// Toggles sound on or off
        		if (optionsSound) {
        			setSound(false);
        		} else {
        			setSound(true);
        		}
        	}
        });
        	// Update the Sound on/off image.
        setSound(optionsSound);

        
        final ImageButton imageButton2 = (ImageButton) findViewById(R.id.MainMenuOptionsImageButton2);
        imageButton2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Log.d("OPTIONS", "Clicked button 2");
        	}
        });
        
        
        ImageButton imageButtonSave = (ImageButton) findViewById(R.id.MainMenuOptionsImageButtonSave);
        imageButtonSave.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Log.d("OPTIONS", "Clicked button Save");
        	}
        });
        
    }
    
    @Override
    protected void onStop() {
       super.onStop();
    
      // Save user preferences. We need an Editor object to
      // make changes. All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences("Options", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean("optionsSound", optionsSound);

      // Don't forget to commit your edits!!!
      editor.commit();
    }
    
    
    public void setSound(boolean b) {
    	this.optionsSound = b;
    	
    	if (b) {
			imageButton1.setImageResource(R.drawable.button_sound_on);
    	} else {
			imageButton1.setImageResource(R.drawable.button_sound_off);
    	}
    }

}