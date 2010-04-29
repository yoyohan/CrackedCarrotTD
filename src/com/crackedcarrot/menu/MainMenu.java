package com.crackedcarrot.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.crackedcarrot.GameInit;
import com.crackedcarrot.multiplayer.*;

public class MainMenu extends Activity {
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		Log.d("MAINMENU", "onKeyDown KEYCODE_BACK");
    		Log.d("MAINMENU", "Calling System.exit(0)");
    		System.exit(0);
    		return true;
       	}
    	return super.onKeyDown(keyCode, event);
    } 


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
        Button StartGameButton = (Button)findViewById(R.id.StartGame);
        StartGameButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		//Intent StartGame = new Intent(MainMenu.this,StartGame.class);
        		//startActivity(StartGame);
        		Intent StartGame = new Intent(v.getContext(),MapOp.class);
        		startActivity(StartGame);
        	}
        });
        
        Button ResumeButton = (Button)findViewById(R.id.Resume);
    	View ResumeWrap = (View) findViewById(R.id.ResumeWrap);

        	// See if there's any old game saved that can be resumed.
        	SharedPreferences resume = getSharedPreferences("resume", 0);
        	int resumes = resume.getInt("resumes", 0);

       	if (resumes > -1 && resumes < 3) {
	        ResumeButton.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		//Send the level variable to the game loop and start it
	        		Intent StartGame = new Intent(v.getContext(),GameInit.class);
	        		StartGame.putExtra("com.crackedcarrot.menu.map", 0);
	        		StartGame.putExtra("com.crackedcarrot.menu.difficulty", 0);
	        		startActivity(StartGame);
	        	}
	        });
        	ResumeButton.setEnabled(true);
        } else {
        	ResumeButton.setEnabled(false);
        	ResumeButton.setVisibility(View.GONE);
        	ResumeWrap.setVisibility(View.GONE);
        }

        Button OptionsButton = (Button)findViewById(R.id.Options);
        OptionsButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		Intent Options = new Intent(MainMenu.this,Options.class);
        		startActivity(Options);
        	}
        });
        
        Button HelpButton = (Button)findViewById(R.id.Help);
        HelpButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		Intent Help = new Intent(MainMenu.this,InstructionWebView.class);
        		startActivity(Help);
        	}
        });
        
        Button MultiPlayerButton = (Button)findViewById(R.id.Multiplayer);
        MultiPlayerButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		Intent Multiplayer = new Intent(MainMenu.this,MultiplayerOp.class);
        		startActivity(Multiplayer);
        		finish();
        	}
        });
        
    }
    
	// Called when we get focus again (after a game has ended).
    @Override
    public void onRestart() {
        super.onRestart();

    	// See if there's any old game saved that can be resumed.
    	SharedPreferences settings = getSharedPreferences("Resume", 0);
    	int resume = settings.getInt("Resume", 0);

        Button ResumeButton = (Button)findViewById(R.id.Resume);
    	View   ResumeWrap = (View) findViewById(R.id.ResumeWrap);
    	
       	if (resume > -1 && resume < 4) {
	        ResumeButton.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		//Send the level variable to the game loop and start it
	        		Intent StartGame = new Intent(v.getContext(),GameInit.class);
	        		StartGame.putExtra("com.crackedcarrot.menu.map", 0);
	        		StartGame.putExtra("com.crackedcarrot.menu.difficulty", 0);
	        		startActivity(StartGame);
	        	}
	        });
        	ResumeButton.setEnabled(true);
        	ResumeButton.setVisibility(View.VISIBLE);
        	ResumeWrap.setVisibility(View.VISIBLE);
        } else {
        	ResumeButton.setEnabled(false);
        	ResumeButton.setVisibility(View.GONE);
        	ResumeWrap.setVisibility(View.GONE);
        }
    	
    }
    
}