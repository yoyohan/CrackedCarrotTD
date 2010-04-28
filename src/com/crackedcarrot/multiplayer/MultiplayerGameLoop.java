package com.crackedcarrot.multiplayer;

import android.os.SystemClock;
import android.util.Log;

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

	public MultiplayerGameLoop(NativeRender renderHandle, Map gameMap,
			Level[] waveList, Tower[] tTypes, Player p, GameLoopGUI gui,
			SoundManager sm) {
		super(renderHandle, gameMap, waveList, tTypes, p, gui, sm);
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

}
