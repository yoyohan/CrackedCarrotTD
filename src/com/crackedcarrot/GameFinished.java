package com.crackedcarrot;

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
import android.widget.ImageView;
import android.widget.TextView;

public class GameFinished extends Activity {
	
	public ScoreNinjaAdapter scoreNinjaAdapter;
	
	private int score;
	private int mapChoice;
	private boolean multiplayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamefinished);

        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
        Typeface typefaceSniglet = Typeface.createFromAsset(getAssets(), "fonts/Sniglet.ttf");
        
        Bundle extras  = getIntent().getExtras();
        score          = extras.getInt("score");
        mapChoice  = extras.getInt("map");
        boolean win    = extras.getBoolean("win");
        multiplayer = extras.getBoolean("multiplayer", false);

        
		// Handle scoreninja-thingie.
    	ScoreNinjaAdapter scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzeroone", "E70411F009D4EDFBAD53DB7BE528BFE2");
		SharedPreferences settings = getSharedPreferences("Options", 0);
	    if (settings.getBoolean("optionsHighscore", false) && ScoreNinjaAdapter.isInstalled(this) == false) {
	    		// If ScoreNinja is enabled but not installed we try to install it:
	    	scoreNinjaAdapter.show();
	    }
	    
	    ImageView imageTitle = (ImageView) findViewById(R.id.GameFinishedImageViewTitle);
	    if (win)
	    	imageTitle.setImageResource(R.drawable.victory);
	    else
	    	imageTitle.setImageResource(R.drawable.defeat);

	    
	    ImageView image = (ImageView) findViewById(R.id.GameFinishedImageViewImage);
	    if (win)
	    	image.setImageResource(R.drawable.win);
	    else
	    	image.setImageResource(R.drawable.loose);

	    
        TextView tvTitle = (TextView) findViewById(R.id.GameFinishedTextViewTitle);
        tvTitle.setTypeface(typefaceSniglet);
        if (win)
        	tvTitle.setText("Congratulations!");
        else
        	tvTitle.setText("You lost...");
        
        TextView tvText = (TextView) findViewById(R.id.GameFinishedTextViewText);
        tvText.setTag(typefaceSniglet);
        if (win)
        	tvText.setText("You've slain all the vile rabbits and their evil companions and saved your precious carrots!");
        else
        	tvText.setText("The vile rabbits have conquered your pitiful backgarden, and worse, the world!");
        
        TextView tvScore = (TextView) findViewById(R.id.GameFinishedTextViewScore);
        tvScore.setTypeface(typefaceSniglet);
        tvScore.setText("Final score: " + extras.getInt("score"));
        
    	// Save everything and return to mainmenu.
        Button buttonBack = (Button) findViewById(R.id.GameFinished_Button_Ok);
        buttonBack.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		backButton();
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

           // Load/prepare Scoreninja if it's active and installed.
       SharedPreferences settings = getSharedPreferences("Options", 0);
       if (settings.getBoolean("optionsHighscore", false) && ScoreNinjaAdapter.isInstalled(this) && this.multiplayer == false) {
    	   
           if (mapChoice == 1) {
           	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzeroone", "E70411F009D4EDFBAD53DB7BE528BFE2");
           } else if (mapChoice == 2) {
           	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerotwo", "26CCAFB5B609DEB078F18D52778FA70B");
           } else if (mapChoice == 3) {
           	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerothree", "41F4C7AEF5A4DEF7BDC050AEB3EA37FC");
           } else if (mapChoice == 4) {
           	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerofour", "EF3428A86CD2387E603C7CE41B9AAD34");
           } else if (mapChoice == 5) {
           	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerofive", "FDF504FBDF1BF8E53968ED55CA591213");
           } else if (mapChoice == 6) {
           	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerosix", "28E2D9AB8D002455400C1D93B09D9A64");
           }
    	   
    	   scoreNinjaAdapter.show(score);
       }
    }

    
    private void backButton() {
    	finish();
    }
	
}
