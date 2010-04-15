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

	private boolean optionsHighscore;
	private boolean optionsSound;
	private ImageButton imageButton1;
	private ImageButton imageButton2;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_options);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	
        // Restore preferences
        SharedPreferences settings = getSharedPreferences("Options", 0);
        optionsHighscore = settings.getBoolean("optionsHighscore", false);
        optionsSound     = settings.getBoolean("optionsSound", false);
        
        
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

        
        imageButton2 = (ImageButton) findViewById(R.id.MainMenuOptionsImageButton2);
        imageButton2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        			// Toggles highscore on or off
        		if (optionsHighscore) {
        			setHighscore(false);
        		} else {
        			setHighscore(true);
        		}
        	}
        });
        	// Update highscore-image
        setHighscore(optionsHighscore);
        
        
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

      SharedPreferences settings = getSharedPreferences("Options", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean("optionsHighscore", optionsHighscore);
      editor.putBoolean("optionsSound", optionsSound);

      editor.commit();
    }

    
    public void setHighscore(boolean b) {
    	this.optionsHighscore = b;
    	
    	if (b) {
			imageButton2.setImageResource(R.drawable.button_highscore_on);
    	} else {
			imageButton2.setImageResource(R.drawable.button_highscore_off);
    	}
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
