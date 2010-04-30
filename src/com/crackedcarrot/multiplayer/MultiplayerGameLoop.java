package com.crackedcarrot.multiplayer;

import java.util.concurrent.Semaphore;

import android.os.SystemClock;
import android.util.Log;

import com.crackedcarrot.Coords;
import com.crackedcarrot.GameInit;
import com.crackedcarrot.GameLoop;
import com.crackedcarrot.GameLoopGUI;
import com.crackedcarrot.NativeRender;
import com.crackedcarrot.Player;
import com.crackedcarrot.SoundManager;
import com.crackedcarrot.Tower;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;

public class MultiplayerGameLoop extends GameLoop {
	
	private static Semaphore synchLevelSemaphore = new Semaphore(1);
	private MultiplayerService mMultiplayerService;

	public MultiplayerGameLoop(NativeRender renderHandle, Map gameMap,
			Level[] waveList, Tower[] tTypes, Player p, GameLoopGUI gui,
			SoundManager sm, MultiplayerService mpS) {
		super(renderHandle, gameMap, waveList, tTypes, p, gui, sm);
		this.mMultiplayerService = mpS;
	}
	
	/** Overriding initializeLevel from super class to control multiplayer
	 * synchronization 
	 */
	protected void initializeLvl() {
		try {
			//Free last levels sprites to clear the video mem and ram from
			//Unused creatures and settings that are no longer valid.
			renderHandle.freeSprites();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	
    	//Set the creatures texture size and other atributes.
    	remainingCreaturesALL = mLvl[lvlNbr].nbrCreatures;
    	remainingCreaturesALIVE = mLvl[lvlNbr].nbrCreatures;
    	currentCreatureHealth = mLvl[lvlNbr].getHealth() * remainingCreaturesALL;
    	startCreatureHealth = mLvl[lvlNbr].getHealth() * remainingCreaturesALL;
    	
    	//Need to reverse the list for to draw correctly.
    	for (int z = 0; z < remainingCreaturesALL; z++) {
			// The following line is used to add the following wave of creatures to the list of creatures.
			mLvl[lvlNbr].cloneCreature(mCreatures[z]);
	    	//This is defined by the scale of this current lvl
			Coords tmpCoord = mScaler.scale(14,0);
	    	mCreatures[z].setYOffset((int)(tmpCoord.getX()*mCreatures[z].scale));
	    	
    	}
		try {
			//Finally send of the sprites to the render to be allocated
			//And after that drawn.
			renderHandle.finalizeSprites();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Initialize the status, displaying the amount of currency
		gui.sendMessage(gui.GUI_PLAYERHEALTH_ID, player.getHealth(), 0);
		// Initialize the status, displaying the players health
		gui.sendMessage(gui.GUI_PLAYERMONEY_ID, player.getMoney(), 0);
		// Initialize the status, displaying the creature image
		gui.sendMessage(gui.GUI_CREATUREVIEW_ID, mLvl[lvlNbr].getDisplayResourceId(), 0);
				
		// Show the NextLevel-dialog and waits for user to click ok
		// via the semaphore.
    	try {
			dialogSemaphore.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//Also wait for the opponent to click ok via semaphore
		try {
			synchLevelSemaphore.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		gui.sendMessage(gui.DIALOG_NEXTLEVEL_ID, 0, 0);
		
    	// This is a good time to save the current progress of the game.
			// -2 = call the SaveGame-function.
			// 1  = ask SaveGame to save all data.
			// 0  = not used.
		gui.sendMessage(-2, 1, 0);

		// Initialize the status, displaying the amount of currency
		gui.sendMessage(gui.GUI_PLAYERMONEY_ID, player.getMoney(), 0);
		// Initialize the status, displaying the players health
		gui.sendMessage(gui.GUI_PLAYERHEALTH_ID, player.getHealth(), 0);
		// Initialize the status, displaying the creature image
		gui.sendMessage(gui.GUI_CREATUREVIEW_ID, mLvl[lvlNbr].getDisplayResourceId(), 0);

		// And set the progressbar with creature health to full again.
		gui.sendMessage(gui.GUI_PROGRESSBAR_ID, 100, 0);
		// And reset our internal counter for the creature health progress bar ^^
		progressbarLastSent = 100;
		
		// Fredrik: this was added by akerberg 2010-04-05, survied commit.
		// If we dont reset this variable each wave. The timeDelta will be fucked up
		// And creatures will try to move to second waypoint insteed.
		mLastTime = 0;
		// Reset gamespeed between levels?
		gameSpeed = 1;
    	
		// Remove healthbar until game begins.
		gui.sendMessage(gui.GUI_HIDEHEALTHBAR_ID, 0, 0);

		player.setTimeUntilNextLevel(player.getTimeBetweenLevels());

		// Initialize the status, displaying how long left until level starts
		gui.sendMessage(gui.GUI_NEXTLEVELINTEXT_ID, (int) player.getTimeUntilNextLevel(), 0);
		
		// We wait to show the status bar until everything is updated
		gui.sendMessage(gui.GUI_SHOWSTATUSBAR_ID, 0, 0);
		
		// Code to wait for the user to click ok on NextLevel-dialog.
		try {
			dialogSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dialogSemaphore.release();
		
		//When player clicked ok, send message to opponent that it's done
		String message = "synchLevel";
		byte[] send = message.getBytes();
		mMultiplayerService.write(send);
		
		//Show "Waiting for opponent" message
		//gui.sendMessage(gui.WAIT_OPPONENT_ID, 0, 0);
		
		// Wait for the opponent to click on the NextLevel-dialog.
		try {
			synchLevelSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchLevelSemaphore.release();
		
		//Close "Waiting for opponent" message

    	int reverse = remainingCreaturesALL; 
		for (int z = 0; z < remainingCreaturesALL; z++) {
			reverse--;
			int special = 1;
    		if (mCreatures[z].isCreatureFast())
    			special = 2;
    		mCreatures[z].setSpawndelay((player.getTimeBetweenLevels() + ((reverse*1.5f)/special)));
		}
	}
	
	/** Overriding the run method from super class GameLoop */
    public void run() {

    	Log.d("GAMELOOP","INIT GAMELOOP");
   	
	    initializeDataStructures();
	    
	    Log.d("GAMELOOP","INIT" + this.getClass().getName());

	    while(run){
    		initializeLvl();
    		int lastTime = (int) player.getTimeUntilNextLevel();

    		while(remainingCreaturesALL > 0 && run){
    			
				final long time = SystemClock.uptimeMillis();

				if(GameInit.pause){
	    			try {
	    	    		GameInit.pauseSemaphore.acquire();
	    			} catch (InterruptedException e1) {}
	    			GameInit.pauseSemaphore.release();
				}
    			
				//Get the time after an eventual pause and add this to the mLastTime variable
    			final long time2 = SystemClock.uptimeMillis();
    			final long pauseTime = time2 - time;

	            // Used to calculate creature movement.
				final long timeDelta = time - mLastTime;
	            final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? (timeDelta / 1000.0f) * gameSpeed : 0.0f;
	            mLastTime = time + pauseTime;

	            // To save some cpu we will sleep the
	            // gameloop when not needed. GOAL 60fps
	            if (timeDelta <= 16) {
	            	int naptime = (int)(16-timeDelta);
		            try {
						Thread.sleep(naptime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            }
	            
	            
	            // Displays the Countdown-to-next-wave text.
	            if (player.getTimeUntilNextLevel() > 0) {
	            	
		    		// So we eventually reach the end of the countdown...
	        		player.setTimeUntilNextLevel(player.getTimeUntilNextLevel() - timeDeltaSeconds);
	            	
	            	if (player.getTimeUntilNextLevel() < 0) {
		            	// Show healthbar again.
	            		gui.sendMessage(gui.GUI_SHOWHEALTHBAR_ID, 0, 0);
	
	            			// Force the GUI to repaint the #-of-creatures-alive-counter.
	            		creatureDiesOnMap(0);
	
	            		player.setTimeUntilNextLevel(0);
	            	} else {
		        		// Update the displayed text on the countdown.
		            	if (lastTime - player.getTimeUntilNextLevel() > 0.5) {
		            		lastTime = (int) player.getTimeUntilNextLevel();
		            		gui.sendMessage(gui.GUI_NEXTLEVELINTEXT_ID, lastTime, 0);
		            	}
	            	}
	            }

	            
	            //Calls the method that moves the creature.
	        	for (int x = 0; x < mLvl[lvlNbr].nbrCreatures; x++) {
	        		mCreatures[x].update(timeDeltaSeconds);
	        	}       	
	            //Calls the method that handles the monsterkilling.
	        	for (int x = 0; x <= totalNumberOfTowers; x++) {
	        		mTower[x].attackCreatures(timeDeltaSeconds,mLvl[lvlNbr].nbrCreatures);
	        	}	            
	            // Check if the GameLoop are to run the level loop one more time.
	            if (player.getHealth() < 1) {
            		//If you have lost all your lives then the game ends.
	            	run = false;
            	}
	        }
    		
    		player.calculateInterest();

    		// Check if the GameLoop are to run the level loop one more time.
            if (player.getHealth() < 1) {
        		//If you have lost all your lives then the game ends.
            	Log.d("GAMETHREAD", "You are dead");

            		// Show the You Lost-dialog.
            	gui.sendMessage(gui.DIALOG_LOST_ID, 0, 0);
            	// This is a good time clear all savegame data.
            		// -2 = call the SaveGame-function.
            		// 2  = ask SaveGame to clear all data.
            		// 0  = not used.
            	gui.sendMessage(-2, 2, 0);
            	
        		// Code to wait for the user to click ok on YouLost-dialog.
        		try {
        			dialogSemaphore.acquire();
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
        		
        		try {
        			dialogSemaphore.acquire();
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
        		dialogSemaphore.release();

            	run = false;
        	} 
        	else if (remainingCreaturesALL < 1) {
        		//If you have survied the entire wave without dying. Proceed to next next level.
            	Log.d("GAMETHREAD", "Wave complete");
        		lvlNbr++;
        		if (lvlNbr >= mLvl.length) {
        			// You have completed this map
                	Log.d("GAMETHREAD", "You have completed this map");
                	
            		// Show the You Won-dialog.
                	gui.sendMessage(gui.DIALOG_WON_ID, 0, 0);

                	// This is a good time clear all savegame data.
            			// -2 = call the SaveGame-function.
            			// 2  = ask SaveGame to clear all data.
            			// 0  = not used.
                	gui.sendMessage(-2, 2, 0);
                	
                	// Show Ninjahighscore-thingie.
                	gui.sendMessage(gui.DIALOG_HIGHSCORE_ID, player.getScore(), 0);
                	
            		// Code to wait for the user to click ok on YouWon-dialog.
            		try {
            			dialogSemaphore.acquire();
            		} catch (InterruptedException e) {
            			e.printStackTrace();
            		}
            		
            		try {
            			dialogSemaphore.acquire();
            		} catch (InterruptedException e) {
            			e.printStackTrace();
            		}
            		dialogSemaphore.release();

        			run = false;
        		}
        	}
	    }
    	Log.d("GAMETHREAD", "dead thread");
    	// Close activity/gameview.
    	gui.sendMessage(-1, 0, 0); // gameInit.finish();
    }
    
    public static void synchLevelClick() {
    	synchLevelSemaphore.release();
    }

}
