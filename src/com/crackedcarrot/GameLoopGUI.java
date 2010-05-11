package com.crackedcarrot;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crackedcarrot.UI.UIHandler;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.menu.InstructionWebView;
import com.crackedcarrot.menu.R;

	/*
	 * 
	 * This class builds on the GameInit-class with the purpose of containing all the
	 * required GUI elements for the GameLoop-class, for cleanliness/readability.
	 * 
	 */

public class GameLoopGUI {
	
	private GameInit gameInit;

	private Dialog dialog = null;
	
	private Dialog dialogNextLevel = null;
	private Dialog dialogPause = null;
	private Dialog dialogQuit = null;
	private ProgressDialog dialogWait = null;
	private Dialog dialogScore = null;
	private Dialog dialogMpWon = null;
	private Dialog dialogMpLost = null;
	private Dialog dialogCompare = null;
	
    private int          healthBarState = 3;
    private int          healthProgress = 100;
    private int          resume;
    private int			 playerScore;
    private int			 opponentScore;

    private Drawable     healthBarDrawable;
    private ImageView    enImView;
	private LinearLayout statusBar;
    private ProgressBar  healthProgressBar;
    private TextView     currencyView;
    private TextView     nrCreText;
    private TextView     playerHealthView;
    private UIHandler	 hud;

    // Used when we ask for the instruction view
    private int 		currentSelectedTower;
	
    
    	// For readability-reasons.
    public final int DIALOG_NEXTLEVEL_ID = 1;
    public final int DIALOG_WON_ID       = 2;
    public final int DIALOG_LOST_ID      = 3;
    public final int DIALOG_HIGHSCORE_ID = 4;
    final int DIALOG_QUIT_ID	= 5;
    final int DIALOG_RESUMESLEFT_ID = 6;
    final int DIALOG_PAUSE_ID       = 7;
    public final int WAIT_OPPONENT_ID = 8;
    public final int CLOSE_WAIT_OPPONENT = 9;
    public final int LEVEL_SCORE = 10;
    public final int MULTIPLAYER_WON = 11;
    public final int MULTIPLAYER_LOST = 12;
    public final int COMPARE_PLAYERS = 13;
    
    public final int GUI_PLAYERMONEY_ID     = 20;
    public final int GUI_PLAYERHEALTH_ID    = 21;
    public final int GUI_CREATUREVIEW_ID    = 22;
    final int GUI_CREATURELEFT_ID    = 23;
    public final int GUI_PROGRESSBAR_ID     = 24;
    public final int GUI_NEXTLEVELINTEXT_ID = 25;
    public final int GUI_SHOWSTATUSBAR_ID   = 26;
    public final int GUI_SHOWHEALTHBAR_ID   = 27;
    public final int GUI_HIDEHEALTHBAR_ID   = 28;

    
    final Button towerbutton1;
    final Button towerbutton2;
    final Button towerbutton3;
    final Button towerbutton4;
    final LinearLayout towertext;
    
    final LinearLayout towerUpgrade;
    final Button upgradeA;
    final Button upgradeB;
    final Button sellTower;
    final Button closeUpgrade;
    
    final Button tower2Information;

    
   	// Constructor. A good place to initiate all our different GUI-components.
    public GameLoopGUI(GameInit gi, final UIHandler hud) {
    	gameInit = gi;
    	this.hud = hud;
    	
    	towerUpgrade = (LinearLayout) gameInit.findViewById(R.id.upgrade_layout);
    	upgradeA = (Button) gameInit.findViewById(R.id.upgrade_a);
    	upgradeB = (Button) gameInit.findViewById(R.id.upgrade_b);
    	sellTower = (Button) gameInit.findViewById(R.id.sell);
    	closeUpgrade = (Button) gameInit.findViewById(R.id.close_upgrade);
    	
        towertext = (LinearLayout) gameInit.findViewById(R.id.ttext);
        towerbutton1 = (Button) gameInit.findViewById(R.id.t1);
        towerbutton2 = (Button) gameInit.findViewById(R.id.t2);
        towerbutton3 = (Button) gameInit.findViewById(R.id.t3);
        towerbutton4 = (Button) gameInit.findViewById(R.id.t4);
        
        // Tower information. Clicking this will open information about this tower
        tower2Information = (Button) gameInit.findViewById(R.id.t2info);
        tower2Information.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
   	    		gameInit.gameLoop.pause();
        		Intent ShowInstr = new Intent(v.getContext(),InstructionWebView.class);
        		ShowInstr.putExtra("com.crackedcarrot.menu.tower", currentSelectedTower);
        		gameInit.startActivity(ShowInstr);
        	}
        });
        
    	
        // Create an pointer to the statusbar
        statusBar = (LinearLayout) gameInit.findViewById(R.id.status_menu);
        
		// Create the TextView showing number of enemies left
        nrCreText = (TextView) gameInit.findViewById(R.id.nrEnemyLeft);
        
        // Create the progress bar, showing the enemies total health
        healthProgressBar = (ProgressBar) gameInit.findViewById(R.id.health_progress);
        healthProgressBar.setMax(healthProgress);
        healthProgressBar.setProgress(healthProgress);
        healthBarDrawable = healthProgressBar.getProgressDrawable();
		healthBarDrawable.setColorFilter(Color.parseColor("#339900"),PorterDuff.Mode.MULTIPLY);

        // Create the ImageView showing current creature
        enImView = (ImageView) gameInit.findViewById(R.id.enemyImVi);
        
        // Create the text view showing the amount of currency
        currencyView = (TextView)gameInit.findViewById(R.id.currency);
        
        // Create the text view showing a players health
        playerHealthView = (TextView) gameInit.findViewById(R.id.playerHealth);

        
        /** Listeners for the five icons in the in-game menu.
         *  When clicked on, it's possible to place a tower
         *  on an empty space on the map. The first button
         *  is the normal/fast switcher. */
        final Button forward = (Button) gameInit.findViewById(R.id.forward);
        final Button play = (Button) gameInit.findViewById(R.id.play);

        forward.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		forward.setVisibility(View.GONE);
        		gameInit.gameLoop.setGameSpeed(4);
        		play.setVisibility(View.VISIBLE);
        	}
        });

        play.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		play.setVisibility(View.GONE);
        		gameInit.gameLoop.setGameSpeed(1);
        		forward.setVisibility(View.VISIBLE);
        	}
        });
        
        closeUpgrade.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Log.d("GUI", "Close Upgrade clicked!");

        		towerUpgrade.setVisibility(View.GONE);
           		towerbutton1.setVisibility(View.VISIBLE);
        		towerbutton2.setVisibility(View.VISIBLE);
        		towerbutton3.setVisibility(View.VISIBLE);
        		towerbutton4.setVisibility(View.VISIBLE);
        	}
        });
        
        towerbutton1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// A tower of type 1 has been chosen, where to put it?
        		gameInit.mGLSurfaceView.setTowerType(0);
        		openTowerBuildMenu(0);
        		hud.showGrid();
        	}
        });
        towerbutton2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// A tower of type 2 has been chosen, where to put it?
        		gameInit.mGLSurfaceView.setTowerType(1);
        		openTowerBuildMenu(1);
        		hud.showGrid();
        	}
        });
        towerbutton3.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// A tower of type 3 has been chosen, where to put it?
        		gameInit.mGLSurfaceView.setTowerType(2);
        		openTowerBuildMenu(2);
        		hud.showGrid();
        	}
        });
        towerbutton4.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// A tower of type 4 has been chosen, where to put it?
        		gameInit.mGLSurfaceView.setTowerType(3);
        		openTowerBuildMenu(3);
        		hud.showGrid();
        	}
        });

        // Button that removes towerInformation
        final Button inMenu6 = (Button) gameInit.findViewById(R.id.inmenu6);
        inMenu6.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		gameInit.mGLSurfaceView.setTowerType(-1);
        		towertext.setVisibility(View.GONE);
           		towerbutton1.setVisibility(View.VISIBLE);
        		towerbutton2.setVisibility(View.VISIBLE);
        		towerbutton3.setVisibility(View.VISIBLE);
        		towerbutton4.setVisibility(View.VISIBLE);
        		hud.hideGrid();
        	}
        });

    }
    
    
	/*
	 *  Creates all of our dialogs.
	 *  
	 *  Note: This functions is only called ONCE for each dialog.
	 *  If you need a dynamic dialog this code does NOT go here!
	 *  
	 */
	protected Dialog onCreateDialog(int id) {
		
		WindowManager.LayoutParams lp;
		
	    switch(id) {
	    
	    case DIALOG_NEXTLEVEL_ID:
	    	dialogNextLevel = new Dialog(gameInit,R.style.NextlevelTheme);
	    	dialogNextLevel.setContentView(R.layout.nextlevel);
	    	dialogNextLevel.setCancelable(true);
	    	// Info button
	    	Button infoButton = (Button) dialogNextLevel.findViewById(R.id.infobutton2);
	        infoButton.setOnClickListener(new OnClickListener() {
	        	
	        	public void onClick(View v) {
	        		Intent ShowInstr = new Intent(v.getContext(),InstructionWebView.class);
	        		gameInit.startActivity(ShowInstr);
	        	}
	        });
	    	
	    	// A button	    	
	    	Button butt = (Button) dialogNextLevel.findViewById(R.id.NextLevelButton);
	    	butt.setOnClickListener(
	    			new View.OnClickListener() {
	    				public void onClick(View v) {
	    					dialogNextLevel.dismiss();

				    }
				});
	    	
	    	dialogNextLevel.setOnDismissListener(
	    			new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							gameInit.gameLoop.dialogClick();
						}
	    			});
	    	return dialogNextLevel;
	    	//break;
	    	
	    case DIALOG_WON_ID:
	    	dialog = new Dialog(gameInit,R.style.NextlevelTheme);
	        dialog.setContentView(R.layout.levelwon);
	    	dialog.setCancelable(false);
	    	// First button
	    	Button buttonWon = (Button) dialog.findViewById(R.id.LevelWon_OK);
	        buttonWon.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		gameInit.gameLoop.dialogClick();
	        	}
	        });
	    	break;
	    	
	    case DIALOG_LOST_ID:
	    	dialog = new Dialog(gameInit,R.style.NextlevelTheme);
	        dialog.setContentView(R.layout.levellost);
	    	dialog.setCancelable(false);
	    	// First button
	    	Button buttonLost = (Button) dialog.findViewById(R.id.LevelLost_OK);
	        buttonLost.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		gameInit.gameLoop.dialogClick();
	        	}
	        });
	    	break;
	    	
	    case DIALOG_QUIT_ID:
	    	dialogQuit = new Dialog(gameInit,R.style.NextlevelTheme);
	    	dialogQuit.setContentView(R.layout.levelquit);
	    	dialogQuit.setCancelable(true);
	    	// First button
	    	Button quitYes = (Button) dialogQuit.findViewById(R.id.LevelQuit_Yes);
	        quitYes.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		gameInit.finish();
	        	}
	        });
	    	
	    	// Second button
	    	Button quitNo = (Button) dialogQuit.findViewById(R.id.LevelQuit_No);
	    	quitNo.setOnClickListener(
	    			new View.OnClickListener() {
	    				public void onClick(View v) {
	    					dialogQuit.dismiss();
				    }
				});
	    	
	    	// Dismiss-listener
	    	dialogQuit.setOnDismissListener(
	    			new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							// do nothing.
						}
	    			});
	    	
	        lp = dialogQuit.getWindow().getAttributes();
	        dialogQuit.getWindow().setAttributes(lp);
	        dialogQuit.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	    	
	    	return dialogQuit;
	    	//break;
	    	
	    case DIALOG_PAUSE_ID:
	    	dialogPause = new Dialog(gameInit, R.style.InGameMenu);
	        dialogPause.setContentView(R.layout.levelpause);
	    	dialogPause.setCancelable(true);
	    	
	    	// Continue button
	    	Button buttonPauseContinue = (Button) dialogPause.findViewById(R.id.LevelPause_Continue);
	    	buttonPauseContinue.setOnClickListener(
	    		new OnClickListener() {
	    			public void onClick(View v) {
	    				dialogPause.dismiss();
	    			}
	    		});
	    	
	    	// Continue button
	    	final ImageButton buttonPauseSound = (ImageButton) dialogPause.findViewById(R.id.LevelPause_Sound);
	    	buttonPauseSound.setOnClickListener(
	    		new OnClickListener() {
	    			public void onClick(View v) {
	    	    		if (gameInit.gameLoop.soundManager.playSound) {
	    	    			gameInit.gameLoop.soundManager.playSound = false;
	    	    			buttonPauseSound.setBackgroundResource(R.drawable.button_sound_off);
	    	    		} else {
	    	    			gameInit.gameLoop.soundManager.playSound = true;
	    	    			buttonPauseSound.setBackgroundResource(R.drawable.button_sound_on);
	    	    		}
	    			}
	    		});
	    		// And update the image to match the current setting.
			if (gameInit.gameLoop.soundManager.playSound)
				buttonPauseSound.setBackgroundResource(R.drawable.button_sound_on);
			else
				buttonPauseSound.setBackgroundResource(R.drawable.button_sound_off);
	    	
	    	// Help button
	    	Button buttonPauseHelp = (Button) dialogPause.findViewById(R.id.LevelPause_Help);
	    	buttonPauseHelp.setOnClickListener(
	    		new OnClickListener() {
	    			public void onClick(View v) {
	    	       		Intent ShowInstr = new Intent(v.getContext(),InstructionWebView.class);
	            		gameInit.startActivity(ShowInstr);
	    			}
	    		});
	    	
	    	// Quit button
	    	Button buttonPauseQuit = (Button) dialogPause.findViewById(R.id.LevelPause_Quit);
	    	buttonPauseQuit.setOnClickListener(
	    		new OnClickListener() {
	    			public void onClick(View v) {
	    	       		gameInit.showDialog(DIALOG_QUIT_ID);
	    			}
	    		});
	        
	    	// Dismiss-listener
	    	dialogPause.setOnDismissListener(
	    		new DialogInterface.OnDismissListener() {
					public void onDismiss(DialogInterface dialog) {
						GameLoop.unPause();
					}
	    		});
	    	
	    		// Makes the background of the dialog blurred.
	        lp = dialogPause.getWindow().getAttributes();
	        dialogPause.getWindow().setAttributes(lp);
	        dialogPause.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
	            WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

	    	return dialogPause;
	    	
	    case WAIT_OPPONENT_ID:
	    	dialogWait = new ProgressDialog(gameInit);
	    	dialogWait.setMessage("Waiting for opponent...");
	    	dialogWait.setIndeterminate(true);
	    	dialogWait.setCancelable(false);
	    	return dialogWait;
	    	
	    case LEVEL_SCORE:
	    	dialogScore = new Dialog(gameInit,R.style.NextlevelTheme);
	        dialogScore.setContentView(R.layout.multiplayer_score);
	    	dialogScore.setCancelable(false);
	    	
	    	Button closeScore = (Button) dialogScore.findViewById(R.id.scoreOK);
	        closeScore.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		dialogScore.dismiss();
	        	}
	        });
	        dialogScore.setOnDismissListener(
	    			new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							gameInit.gameLoop.dialogClick();
						}
	    			});
	        return dialogScore;
	        
	    case MULTIPLAYER_WON:
	    	dialogMpWon = new Dialog(gameInit,R.style.NextlevelTheme);
	    	dialogMpWon.setContentView(R.layout.multiplayer_won);
	    	dialogMpWon.setCancelable(false);
	    	
	    	TextView tv = (TextView) dialogMpWon.findViewById(R.id.wonText);
	    	String wonText = "The opponent is dead! You have won the battle!" + "<br><br>";
	    	wonText += 		"<b>Your score:<b> " + playerScore;
	    	CharSequence stText = Html.fromHtml(wonText);
		    tv.setText(stText);
	    	
	    	Button buttonMpWon = (Button) dialogMpWon.findViewById(R.id.MpWon_OK);
	        buttonMpWon.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		dialogMpWon.dismiss();
	        	}
	        });
	        dialogMpWon.setOnDismissListener(
	    			new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							gameInit.gameLoop.dialogClick();
						}
	    			});
	    	return dialogMpWon;
	    	
	    case MULTIPLAYER_LOST:
	    	dialogMpLost = new Dialog(gameInit,R.style.NextlevelTheme);
	    	dialogMpLost.setContentView(R.layout.multiplayer_lost);
	    	dialogMpLost.setCancelable(false);
	    	// First button
	    	Button buttonMpLost = (Button) dialogMpLost.findViewById(R.id.mpLost_OK);
	        buttonMpLost.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		dialogMpLost.dismiss();
	        	}
	        });
	        dialogMpLost.setOnDismissListener(
	    			new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							gameInit.gameLoop.dialogClick();
						}
	    			});
	    	return dialogMpLost;
	    case COMPARE_PLAYERS:
	    	dialogCompare = new Dialog(gameInit,R.style.NextlevelTheme);
	    	dialogCompare.setContentView(R.layout.multiplayer_compare);
	    	dialogCompare.setCancelable(false);
	    	// First button
	    	Button buttonCompare = (Button) dialogCompare.findViewById(R.id.mpCompare_OK);
	        buttonCompare.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		dialogCompare.dismiss();
	        	}
	        });
	        dialogCompare.setOnDismissListener(
	    			new DialogInterface.OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							gameInit.gameLoop.dialogClick();
						}
	    			});
	    	return dialogCompare;
	    	
	    default:
	    	Log.d("GAMEINIT", "onCreateDialog got unknown dialog id: " + id);
	        dialog = null;
	    }
	    return dialog;
	}

	
	/*
	 *  Creates our NextLevel-dialog.
	 *  
	 *  This is called every time a dialog is presented.
	 *  If you want dynamic dialogs, put your code here.
	 *  
	 */
	protected void onPrepareDialog(int id, Dialog dialog) {
	    switch(id) {
	    case DIALOG_NEXTLEVEL_ID:

	    	int currLvlnbr = gameInit.gameLoop.getLevelNumber() + 1;

	    	Level currLvl = gameInit.gameLoop.getLevelData();

	    	// Title:
	    	TextView title = (TextView) dialog.findViewById(R.id.NextLevelTitle);
	    	Typeface face = Typeface.createFromAsset(gameInit.getAssets(), "fonts/Sniglet.ttf");
	    	title.setTypeface(face);
	
	    	String titleText ="<b>Level " + currLvlnbr + "</b><br>" + currLvl.creepTitle +"<br>";
		    CharSequence styledText = Html.fromHtml(titleText);
	    	title.setText(styledText);
	    	
    		// And an icon.
	    	ImageView image = (ImageView) dialog.findViewById(R.id.NextLevelImage);
	    	image.setImageResource(currLvl.getDisplayResourceId());
	    	
	    	// Text for next level goes here.
	    	TextView text = (TextView) dialog.findViewById(R.id.NextLevelText);
	    	
	    	Player currPlayer = gameInit.gameLoop.getPlayerData();
	    	String lvlText ="<b>Number of creeps:</b> " + currLvl.nbrCreatures +"<br>";
	    	lvlText += 		"<b>Bounty:</b> " + currLvl.goldValue + "g/creep<br>";
	    	lvlText += 		"<b>Health:</b> " + (int)currLvl.getHealth() + "hp/creep<br>";
	    	lvlText += 		"<br>";
	    	lvlText += 		"<b>Special abillites:</b><br>";
	    	int tmpAbil = 0;
	    	if (currLvl.creatureFast) {
		    	lvlText += 		"<font color=yellow>Fast level</font><br>";
		    	tmpAbil++;
	    	}
		    if (currLvl.creatureFireResistant) {
		    	lvlText += 		"<font color=red>Fire resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (currLvl.creatureFrostResistant) {
		    	lvlText += 		"<font color=blue>Frost resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (currLvl.creaturePoisonResistant) {
		    	lvlText += 		"<font color=green>Posion resistant</font><br>";
		    	tmpAbil++;
		    }
		    if (tmpAbil == 0)
		    	lvlText += 		"No special abbilities<br>";
		    
		    if (currLvlnbr > 1) {
		    	lvlText += 		"<br><b>Previous level:</b><br>";
		    	lvlText += 		"Interest gained:" + currPlayer.getInterestGainedThisLvl() + "<br>";
		    	lvlText += 		"Health lost:" + currPlayer.getHealthLostThisLvl();		    	
		    }
		    else {
		    	lvlText += 		"<br><b>Tip:</b><br>";
		    	lvlText += 		"If you have trouble <br>understanding this game.<br> Use the information<br> button below or ingame";
		    }
		    styledText = Html.fromHtml(lvlText);
		    text.setText(styledText);

		    if (currLvl.creaturePoisonResistant && !currLvl.creatureFireResistant && !currLvl.creatureFrostResistant) {
		    	image.setColorFilter(Color.rgb(178, 255, 178),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (!currLvl.creaturePoisonResistant && currLvl.creatureFireResistant && !currLvl.creatureFrostResistant) {
		    	image.setColorFilter(Color.rgb(255, 178, 178),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (!currLvl.creaturePoisonResistant && !currLvl.creatureFireResistant && currLvl.creatureFrostResistant) {
		    	image.setColorFilter(Color.rgb(178, 178, 255),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (currLvl.creaturePoisonResistant && currLvl.creatureFrostResistant && !currLvl.creatureFireResistant) {
		    	image.setColorFilter(Color.rgb(178, 255, 255),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (currLvl.creaturePoisonResistant && !currLvl.creatureFrostResistant && currLvl.creatureFireResistant) {
		    	image.setColorFilter(Color.rgb(255, 255, 178),PorterDuff.Mode.MULTIPLY);
		    }
		    else if (!currLvl.creaturePoisonResistant && currLvl.creatureFrostResistant && currLvl.creatureFireResistant) {
		    	image.setColorFilter(Color.rgb(255, 178, 255),PorterDuff.Mode.MULTIPLY);
		    }
		    else 
		    	image.setColorFilter(Color.rgb(255, 255, 255),PorterDuff.Mode.MULTIPLY);
		    break;
	    case LEVEL_SCORE:
	    	TextView tv = (TextView) dialogScore.findViewById(R.id.scoreText);
	    	String scoreText = "<b>Score so far:</b> " + "<br>";
	    	scoreText += 		"You: " + playerScore + "<br>";
	    	scoreText += 		"Opponent: " + opponentScore + "<br>";
	    	CharSequence sText = Html.fromHtml(scoreText);
		    tv.setText(sText);
	        break;
	    case COMPARE_PLAYERS:
	    	TextView wL = (TextView) dialogCompare.findViewById(R.id.compareWinLoose);
	    	TextView cS = (TextView) dialogCompare.findViewById(R.id.compareScores);
	    	String winLoose;
	    	String compareScores;
	    	//Is player score better than opponents, if so player is the winner
	    	if(playerScore > opponentScore){
	    		winLoose = "<b>You win!</b>";
	    	}
	    	else if (playerScore < opponentScore){
	    		winLoose = "<b>You Loose!</b>";
	    	} 
	    	else {
	    		winLoose = "<b>It's a tie!</b>";
	    	}
	    	compareScores = "Your score: " + playerScore + "<br>";
	    	compareScores += "Opponent's score: " + opponentScore;
	    	CharSequence chS = Html.fromHtml(winLoose);
		    wL.setText(chS);
		    CharSequence chS2 = Html.fromHtml(compareScores);
		    cS.setText(chS2);
	    	break;		    
	    case DIALOG_PAUSE_ID:
	    	final ImageButton buttonPauseSound = (ImageButton) dialogPause.findViewById(R.id.LevelPause_Sound);
    		// And update the image to match the current setting.
			if (gameInit.gameLoop.soundManager.playSound)
				buttonPauseSound.setBackgroundResource(R.drawable.button_sound_on);
			else
				buttonPauseSound.setBackgroundResource(R.drawable.button_sound_off);

			break;
	    default:
	    	Log.d("GAMEINIT", "onPrepareDialog got unknown dialog id: " + id);
	        dialog = null;
	    }
	}

	
		// This is used to handle calls from the GameLoop to show
		// our dialogs.
	public Handler guiHandler = new Handler() {
	
	    @Override
	    public void handleMessage(Message msg) {

	        switch (msg.what) {
	        	 case DIALOG_NEXTLEVEL_ID:
	        		 SharedPreferences settings1 = gameInit.getSharedPreferences("Options", 0);
	        	     if (settings1.getBoolean("optionsNextLevel", true)
	        	    		 && !GameInit.multiplayerMode()) {
	        	    	 Log.d("GAMELOOPGUI", "Start next level dialog");
	        	    	 gameInit.showDialog(DIALOG_NEXTLEVEL_ID);
	        	     } else {
	        	    	 	// Simulate clicking the dialog.
	        	    	 Log.d("GAMELOOPGUI", "Simulate next level dialog");
	        	    	 gameInit.gameLoop.dialogClick();
	        	     }
	        		 break;
	        	 case DIALOG_WON_ID:
	        		 gameInit.showDialog(DIALOG_WON_ID);
	        		 break;
	        	 case DIALOG_LOST_ID:
	        		 gameInit.showDialog(DIALOG_LOST_ID);
	        		 break;
	        	 case DIALOG_HIGHSCORE_ID:
	        		 SharedPreferences settings2 = gameInit.getSharedPreferences("Options", 0);
	        	     if (settings2.getBoolean("optionsHighscore", false)) {
	        	    	 	// If ScoreNinja is enabled we show it to the player: 
	        	    	 gameInit.scoreNinjaAdapter.show(msg.arg1);
	        	     }
	        		 break;
	        		 
	        	 case GUI_PLAYERMONEY_ID:
	        		 // Update currencyView (MONEY)
	        		 currencyView.setText("" + msg.arg1);
	        		 break;
	        	 case GUI_PLAYERHEALTH_ID:
	        		 // Update player-health.
	        		 playerHealthView.setText("" + msg.arg1);
	        		 break;
	        	 case GUI_CREATUREVIEW_ID:
	        		 // Update Enemy-ImageView
	        		 enImView.setImageResource(msg.arg1);
	        		 break;
	        	 case GUI_CREATURELEFT_ID:
	        		 // update number of creatures still alive on GUI.
	        		 String tt = String.valueOf(msg.arg1);
	        		 if (msg.arg1 < 10)
	        			 tt = "  " + tt;
	        		 nrCreText.setText("" + tt);
	        		 break;
	        		 
	        	 case GUI_PROGRESSBAR_ID: // update progressbar with creatures health.
	        		 // The code below is used to change color of healthbar when health drops
	        		 if (msg.arg1 >= 66 && healthBarState == 1) {
	       				 healthBarDrawable.setColorFilter(Color.parseColor("#339900"),PorterDuff.Mode.MULTIPLY);
	        			 healthBarState = 3;
	        		 }
	        		 if (msg.arg1 <= 66 && healthBarState == 3) {
	        			 healthBarDrawable.setColorFilter(Color.parseColor("#FFBB00"),PorterDuff.Mode.MULTIPLY);
	        			 healthBarState = 2;
	        		 }
	        		 if (msg.arg1 <= 33 && healthBarState == 2) {
	        			 healthBarDrawable.setColorFilter(Color.parseColor("#CC0000"),PorterDuff.Mode.MULTIPLY);
	        			 healthBarState = 1;
	        		 }
	        		 healthProgressBar.setProgress(msg.arg1);
	        		 break;
	        		 
	        	 case GUI_NEXTLEVELINTEXT_ID: // This is used to show how long time until next lvl.
	        		 tt = String.valueOf(msg.arg1);
	        		 if (msg.arg1 < 10)
	        			 tt = "  " + tt;
	        		 nrCreText.setText("Next level in: " + tt);
	        		 break;
	        		 
	        	 case GUI_SHOWSTATUSBAR_ID:
	        		 //Show statusbar
		    			statusBar.setVisibility(View.VISIBLE);
		    			break;
	        	 case GUI_SHOWHEALTHBAR_ID:
	        		 //If we want to switch back to healthbar
        		 	 nrCreText.setText("");
	        		 healthProgressBar.setVisibility(View.VISIBLE);
	        		 enImView.setVisibility(View.VISIBLE);
	        		 break;
	        	 case GUI_HIDEHEALTHBAR_ID:
	        		 //If we want to use space in statusbar to show time to next level counter
	        		 healthProgressBar.setVisibility(View.GONE);
	        		 enImView.setVisibility(View.GONE);
	        		 break;
	        	 case WAIT_OPPONENT_ID:
	        		 gameInit.showDialog(WAIT_OPPONENT_ID);
	        		 break;
	        	 case CLOSE_WAIT_OPPONENT:
	        		 dialogWait.hide();
	        		 break;
	        	 case LEVEL_SCORE:
	        		 playerScore = msg.arg1;
	        		 gameInit.showDialog(LEVEL_SCORE);
	        		 break;
	        	 case MULTIPLAYER_WON:
	        		 playerScore = msg.arg1;
	        		 gameInit.showDialog(MULTIPLAYER_WON);
	        		 break;
	        	 case MULTIPLAYER_LOST:
	        		 gameInit.showDialog(MULTIPLAYER_LOST);
	    			 break;
	        	 case COMPARE_PLAYERS:
	        		 playerScore = msg.arg1;
	        		 gameInit.showDialog(COMPARE_PLAYERS);
	        		 break;
	        		 
	        	 case -1: // GAME IS DONE, CLOSE ACTIVITY.
	        		 gameInit.finish();
	        		 break;
	        	 case -2: // SAVE THE GAME.
	        		 	// arg 1 = save game, 2 = remove saved game.
	        		 gameInit.saveGame(msg.arg1);
	        		 break;
	        		 
	        	 default:
	                 Log.e("GAMELOOPGUI", "guiHandler error! msg.what: " + msg.what);
	                 break;
	        }
	    }
	};
	
	public void sendMessage(int i, int j, int k) {

		// TODO: remove this when done debugging msgs.
		//Log.d("GAMELOOPGUI", "sendMessage: " + i);
		
		Message msg = Message.obtain();
		msg.what = i;
		msg.arg1 = j;
		msg.arg2 = k;
		guiHandler.sendMessage(msg);
	}

	private void openTowerBuildMenu(int towerId) {
		//Tower info = gameInit.gameLoop.getTower(towerId);
		//String text =  info.getTitle() + "<b> Price:</b>" + info.getPrice() + "<br>";
		//text		+= "<b>Speed:</b> Fast <b>Range:</b> " + (int)info.getRange() + "<br>";
   		//text 		+= "<b> Damage:</b>" + (int)info.getMinDamage() + "-" + (int)info.getMaxDamage();
	    //CharSequence styledText = Html.fromHtml(text);
	    //if (towerId == 0) {
		    //tower1Information.setVisibility(View.GONE);
		    tower2Information.setVisibility(View.VISIBLE);
		    //tower3Information.setVisibility(View.GONE);
		    //tower4Information.setVisibility(View.GONE);
	    //}
		this.currentSelectedTower = towerId;
   		towerbutton1.setVisibility(View.GONE);
		towerbutton2.setVisibility(View.GONE);
		towerbutton3.setVisibility(View.GONE);
		towerbutton4.setVisibility(View.GONE);
		towertext.setVisibility(View.VISIBLE);
	}
	
	public void setOpponentScore(int score){
		this.opponentScore = score;
	}
	
	/** Method used to get the GameInit object from the multiplayer handler */
	public GameInit getGameInit(){
		return this.gameInit;
	}
	
	public void showTowerUpgrade(int typeResourceA, int typeResourceB) {
		towerUpgrade.setVisibility(View.VISIBLE);
		upgradeA.setBackgroundResource(typeResourceA);
		upgradeB.setBackgroundResource(typeResourceB);
		
		towerbutton1.setVisibility(View.GONE);
		towerbutton2.setVisibility(View.GONE);
		towerbutton3.setVisibility(View.GONE);
		towerbutton4.setVisibility(View.GONE);
	}
	
	public void hideTowerUpgrade() {
		towerUpgrade.setVisibility(View.GONE);
		
		towerbutton1.setVisibility(View.VISIBLE);
		towerbutton2.setVisibility(View.VISIBLE);
		towerbutton3.setVisibility(View.VISIBLE);
		towerbutton4.setVisibility(View.VISIBLE);
		
	}
	public void setUpgradeListeners(OnClickListener upgradeAListener,
									OnClickListener upgradeBListener, 
									OnClickListener sellListener){
		
		
		upgradeA.setOnClickListener(upgradeAListener);
		upgradeB.setOnClickListener(upgradeBListener);
		sellTower.setOnClickListener(sellListener);
		
	}
}