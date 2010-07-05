package com.crackedcarrot;

import com.crackedcarrot.menu.R;
import com.scoreninja.adapter.ScoreNinjaAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class GameFinished extends Activity {
	
	public ScoreNinjaAdapter scoreNinjaAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamefinished);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
        Typeface typefaceSniglet = Typeface.createFromAsset(getAssets(), "fonts/Sniglet.ttf");
        
        Bundle extras  = getIntent().getExtras();
        int mapChoice = extras.getInt("map");

        
        if (mapChoice == 1) {
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzeroone", "E70411F009D4EDFBAD53DB7BE528BFE2");
        } else if (mapChoice == 2) {
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerotwo", "26CCAFB5B609DEB078F18D52778FA70B");
        } else if (mapChoice == 3) {
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerothree", "41F4C7AEF5A4DEF7BDC050AEB3EA37FC");
        }
        
        
        TextView tvScore = (TextView) findViewById(R.id.GameFinishedTextViewScore);
        tvScore.setTypeface(typefaceSniglet);
        tvScore.setText("Final score: " + extras.getInt("score"));
    }
    

    // Unfortunate API, but you must notify ScoreNinja onActivityResult.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      scoreNinjaAdapter.onActivityResult(
          requestCode, resultCode, data);
    }
    
    /*
     * 	        	 case DIALOG_HIGHSCORE_ID:
	        		 SharedPreferences settings2 = gameInit.getSharedPreferences("Options", 0);
	        	     if (settings2.getBoolean("optionsHighscore", false) && ScoreNinjaAdapter.isInstalled(gameInit)) {
	        	    	 	// If ScoreNinja is enabled and installed we show it to the player: 
	        	    	 gameInit.scoreNinjaAdapter.show(msg.arg1);
	        	     }
	        		 break;
     */
	
}
