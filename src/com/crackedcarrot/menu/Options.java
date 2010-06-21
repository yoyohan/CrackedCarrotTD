package com.crackedcarrot.menu;

import com.crackedcarrot.menu.R;
import com.scoreninja.adapter.ScoreNinjaAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Options extends Activity {

	private boolean optionsHighscore;
	private boolean optionsNextLevel;
	private boolean optionsSound;
	
	private Button button1;
	private Button button2;
	private Button button3;

    private ScoreNinjaAdapter scoreNinjaAdapter;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_options);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    	// Register on ScoreNinja.
        scoreNinjaAdapter = new ScoreNinjaAdapter(this, "crackedcarrotd", "25912218B4FA767CCBE9F34735C93589");
    	
        // Restore preferences
        SharedPreferences settings = getSharedPreferences("Options", 0);
        optionsHighscore = settings.getBoolean("optionsHighscore", false);
        optionsNextLevel = settings.getBoolean("optionsNextLevel", true);
        optionsSound     = settings.getBoolean("optionsSound", true);
        
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Sniglet.ttf");
        
        button1 = (Button) findViewById(R.id.MainMenuOptionsButton1);
        button1.setTypeface(face);
        button1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
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

        
        button2 = (Button) findViewById(R.id.MainMenuOptionsButton2);
        button2.setTypeface(face);
        button2.setOnClickListener(new OnClickListener() {
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

        
        	// Enable/disable NextLevel-dialog.
        button3 = (Button) findViewById(R.id.MainMenuOptionsButton3);
        button3.setTypeface(face);
        button3.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        			// Toggles highscore on or off
        		if (optionsNextLevel) {
        			setNextLevel(false);
        		} else {
        			setNextLevel(true);
        		}
        	}
        });
        	// Update highscore-image
        setNextLevel(optionsNextLevel);
        
        
        Button buttonSave = (Button) findViewById(R.id.MainMenuOptionsButtonOk);
        buttonSave.setTypeface(face);
        buttonSave.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		setSave();
        	}
        });
        
        Button HighscoreButton = (Button) findViewById(R.id.MainMenuOptionsButton4);
        HighscoreButton.setTypeface(face);
        HighscoreButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent Highscore = new Intent(Options.this,Highscore.class);
        		startActivity(Highscore);
        	}
        });
    }

    // Unfortunate API, but you must notify ScoreNinja onActivityResult.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      scoreNinjaAdapter.onActivityResult(
          requestCode, resultCode, data);
    }

    
    @Override
    protected void onStop() {
       super.onStop();

      SharedPreferences settings = getSharedPreferences("Options", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean("optionsHighscore", optionsHighscore);
      editor.putBoolean("optionsNextLevel", optionsNextLevel);
      editor.putBoolean("optionsSound", optionsSound);

      editor.commit();
    }

    
    public void setHighscore(boolean b) {
    	this.optionsHighscore = b;
    	
    	if (b) {
			button2.setText("ScoreNinja: On");
    	} else {
			button2.setText("ScoreNinja: Off");
    	}
    }
    
    public void setNextLevel(boolean b) {
    	this.optionsNextLevel = b;
    	
    	if (b) {
			button3.setText("NextLevel: On");
    	} else {
			button3.setText("NextLevel: Off");
    	}
    }
    
    public void setSave() {
    	this.finish();
    }
    
    public void setSound(boolean b) {
    	this.optionsSound = b;
    	
    	if (b) {
			button1.setText("Sounds: On");
    	} else {
			button1.setText("Sounds: Off");
    	}
    }

}
