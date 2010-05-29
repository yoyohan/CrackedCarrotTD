package com.crackedcarrot.menu;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.crackedcarrot.GameInit;
import com.crackedcarrot.GameLoop;
import com.crackedcarrot.multiplayer.*;
import com.scoreninja.adapter.ScoreNinjaAdapter;

public class MainMenu extends Activity {
	
	
	Dialog dialog;
	private int resumes;

	
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
    
    
    	// Shows the "New Game or Resume old game?"-dialog.
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
		    case 1:
		    	dialog = new Dialog(this,R.style.NextlevelTheme);
		        dialog.setContentView(R.layout.levelresume);
		    	dialog.setCancelable(true);

		    	TextView textView = (TextView) dialog.findViewById(R.id.LevelResume_Text);
		    	textView.setText("Resume last game? You have " + (3 - resumes) + " resume(s) left.");

		    	Button buttonStartGame = (Button) dialog.findViewById(R.id.Resume_StartGame);
		        buttonStartGame.setOnClickListener(new OnClickListener() {
		        	public void onClick(View v) {
		        		dialog.dismiss();
		        		Intent StartGame = new Intent(v.getContext(),MapOp.class);
		        		startActivity(StartGame);
		        	}
		        });

		    	Button buttonResume = (Button) dialog.findViewById(R.id.Resume_Resume);
		        buttonResume.setOnClickListener(new OnClickListener() {
		        	public void onClick(View v) {
		        		//Send the level variable to the game loop and start it
		        		dialog.dismiss();
		        		Intent StartGame = new Intent(v.getContext(),GameInit.class);
		        		StartGame.putExtra("com.crackedcarrot.menu.map", 0);
		        		StartGame.putExtra("com.crackedcarrot.menu.difficulty", 0);
		        		startActivity(StartGame);
		        	}
		        });
		        
		        dialog.setOnDismissListener(
		    		new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							// nothing. dialog is already closed.
						}
		    		});
		        
		    	break;
		    	
		    default:
		    	Log.d("MAINMENU", "onCreateDialog got unknown dialog id: " + id);
		        dialog = null;
    	}
    	return dialog;
    }
    
	protected void onPrepareDialog(int id, Dialog dialog) {
	    switch(id) {
	    case 1:
	    	TextView textView = (TextView) dialog.findViewById(R.id.LevelResume_Text);
	    	textView.setText("Resume last game? You have " + (3 - resumes) + " resume(s) left.");
	    	
	    default:
	    	Log.d("MAINMENU", "onPrepareDialog got unknown dialog id: " + id);
	        dialog = null;
	    }
	}



	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        
        /** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    		// Handle scoreninja-thingie before starting game.
    	final ScoreNinjaAdapter scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzeroone", "E70411F009D4EDFBAD53DB7BE528BFE2");
    	final boolean           scoreNinjaIsInstalled = ScoreNinjaAdapter.isInstalled(this);
    	
        Button StartGameButton = (Button)findViewById(R.id.StartGame);
        StartGameButton.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
            	// See if there's any old game saved that can be resumed.
            	SharedPreferences resume = getSharedPreferences("resume", 0);
            	resumes = resume.getInt("resumes", -1);
            	Log.d("MAINMENU", "resumes: " + resumes);
            	
            	if (resumes > -1 && resumes < 3) {
            		showDialog(1);
            	} else {
            			
	        		SharedPreferences settings = getSharedPreferences("Options", 0);
	        	    if (settings.getBoolean("optionsHighscore", false) && scoreNinjaIsInstalled == false) {
	        	    		// If ScoreNinja is enabled but not installed we try to install it:
	        	    	scoreNinjaAdapter.show(0);
	        	    } else {
	        	    	Intent StartGame = new Intent(v.getContext(),MapOp.class);
	        	    	startActivity(StartGame);
	        	    }
            	}
        	}
        });
        
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
        	}
        });
        
    }

	// Called when we get focus again (after a game has ended).
    @Override
    public void onResume() {
    	super.onResume();
    	
    		// Update the resumes variable in case it's changed.
        // If you touch this please tell fredrik about it.
    	SharedPreferences resume = getSharedPreferences("resume", 0);
    	resumes = resume.getInt("resumes", -1);
    }
    
	// Called when we get focus again (after a game has ended).
    @Override
    public void onRestart() {
        super.onRestart();
        
        	// Update the resumes variable in case it's changed.
        // If you touch this please tell fredrik about it.
    	SharedPreferences resume = getSharedPreferences("resume", 0);
    	resumes = resume.getInt("resumes", -1);
    }
    
}