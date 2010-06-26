package com.crackedcarrot.multiplayer;

import java.util.Random;
import java.util.concurrent.Semaphore;

import android.os.SystemClock;
import android.util.Log;

import com.crackedcarrot.Coords;
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
	private boolean opponentLife = true;
	//private boolean hurtOpponent;
	
	//The variable representing the shield in a multiplayer game
	public boolean multiplayerShield = false;

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
		
		gui.sendMessage(gui.GUI_UPDATELVLNBRTEXT_ID, this.lvlNbr+1, 0);
		
		mLastTime = 0;
		// Reset gamespeed between levels?
		// gameSpeed = 1;
    	
		// Remove healthbar until game begins.
		gui.sendMessage(gui.GUI_HIDEHEALTHBAR_ID, 0, 0);

		player.setTimeUntilNextLevel(player.getTimeBetweenLevels());

		// Initialize the status, displaying how long left until level starts
		gui.sendMessage(gui.GUI_NEXTLEVELINTEXT_ID, (int) player.getTimeUntilNextLevel(), 0);
		
		// We wait to show the status bar until everything is updated
		gui.sendMessage(gui.GUI_SHOWSTATUSBAR_ID, 0, 0);
		
		//The dialog showing the players score is shown right after next level dialog
		gui.sendMessage(gui.LEVEL_SCORE, player.getScore(), 0);
		
		//Make the five multiplayer buttons visible for the current level
		gui.sendMessage(gui.SETMULTIPLAYERVISIBLE, 0, 0);
		
		waitForDialogClick();
		
		//When player clicked ok, send message to opponent that it's done
		String message2 = "synchLevel";
		byte[] send2 = message2.getBytes();
		mMultiplayerService.write(send2);
		
		//Show "Waiting for opponent" message
		gui.sendMessage(gui.WAIT_OPPONENT_ID, 0, 0);
		
		// Wait for the opponent
		try {
			synchLevelSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			synchLevelSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchLevelSemaphore.release();
		
		//Close "Waiting for opponent" message
		gui.sendMessage(gui.CLOSE_WAIT_OPPONENT, 0, 0);

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

    	 Log.d("GAMELOOP","INIT" + this.getClass().getName());
    	
	    initializeDataStructures();
	    
	    gameSpeed = 1;

	    while(run){
    		initializeLvl();
    		int lastTime = (int) player.getTimeUntilNextLevel();

    		while(remainingCreaturesALL > 0 && run){
    			
				final long time = SystemClock.uptimeMillis();

				if (pause) {
	    			try { pauseSemaphore.acquire(); }
	    			catch (InterruptedException e1) { }
	    			pauseSemaphore.release();
				}
    			
	            // Used to calculate creature movement.
				long timeDelta = time - mLastTime;
	            // To save some cpu we will sleep the
	            // gameloop when not needed. GOAL 60fps
	            if (timeDelta <= 16) {
	            	int naptime = (int) (16 - timeDelta);
		            try {
						Thread.sleep(naptime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            } else 				
	            	timeDelta = timeDelta > 100 ? 100 : timeDelta;

				final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? (timeDelta / 1000.0f) * gameSpeed : 0.0f;
	            mLastTime = time;
	            
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
	        	for (int x = 0; x < mTower.length; x++) {
	        		if(mTower[x].draw == true)
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
            	
            	//Send info to opponent that player is dead
            	String message = "Dead";
    			byte[] send = message.getBytes();
    			mMultiplayerService.write(send);
            	
    			//Is the opponent still alive?
    			if(this.opponentLife){
    				// Send the synch message so opponent won't wait for eternity
    				String lastMessage = "synchLevel";
            		byte[] sendMessage = lastMessage.getBytes();
            		mMultiplayerService.write(sendMessage);
    				// Show the "You Lost"-dialog.
                	gui.sendMessage(gui.MULTIPLAYER_LOST, 0, 0);
            		waitForDialogClick();
                	run = false;
    			} else {
    				//The one who dies first is the looser, so this player has won
    				gui.sendMessage(gui.MULTIPLAYER_WON, player.getScore(), 0);
    				waitForDialogClick();
    				run = false;
    			}
        	} 
            else if (remainingCreaturesALL < 1) {
            	//Send players score to opponent
            	String message = "Score" + player.getScore();
    			byte[] send = message.getBytes();
    			mMultiplayerService.write(send);	
            	Log.d("GAMETHREAD", "Wave complete");
        		lvlNbr++;
        		/**
        		
        		*/
        		
        		//Is the opponent dead, in that case you've won the game
        		if(!this.opponentLife){
        			gui.sendMessage(gui.MULTIPLAYER_WON, player.getScore(), 0);
        			waitForDialogClick();
        			run = false;
        		} else {
        			//Show "Waiting for opponent" message
            		gui.sendMessage(gui.WAIT_OPPONENT_ID, 0, 0);
        			
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            		
            		String me = "synchLevel";
            		byte[] sendThis = me.getBytes();
            		mMultiplayerService.write(sendThis);
            		
            		Log.d("ZZZZZZZ", "Before first synchlevel");
            		// Wait for the opponent
            		try {
            			synchLevelSemaphore.acquire();
            		} catch (InterruptedException e) {
            			e.printStackTrace();
            		}
            		Log.d("ZZZZZZZ", "Before second synchlevel");
            		try {
            			synchLevelSemaphore.acquire();
            		} catch (InterruptedException e) {
            			e.printStackTrace();
            		}
            		synchLevelSemaphore.release();
            		
            		Log.d("ZZZZZZZ", "close wait for....");
            		//Close "Waiting for opponent" message
            		gui.sendMessage(gui.CLOSE_WAIT_OPPONENT, 0, 0);
	        		// The game is not totally completed
	        		if (lvlNbr < mLvl.length) {
	        			//Do nothing here
	        		}
	        		else {
	                	Log.d("GAMETHREAD", "You have completed this map");
	                	//Both players have survived all the enemy waves
	            		gui.sendMessage(gui.COMPARE_PLAYERS, player.getScore(), 0);
	            		waitForDialogClick();
	            		run = false;
	        		}
        		}
        	}
	    }
    	Log.d("GAMETHREAD", "dead thread");
    	// Close activity/gameview.
    	gui.sendMessage(-1, 0, 0); // gameInit.finish();
    }
    
    /** Release the synchronization semaphore from outside this class */
    //public static void synchLevelClick() {
    public void synchLevelClick() {
    	synchLevelSemaphore.release();
    }
    
    /** When handler receives info about opponent life, update through this method */
    public void setOpponentLife(boolean bool){
    	this.opponentLife = bool;
    }
    
    /** This method is called when the player wants to increase the speed 
     * and health of one of the opponents enemies. The method can only be
     * called once every level */
    public boolean increaseEnemySpeed(){
		if(player.getMoney() >= 20){
			player.moneyFunction(-20);
			//send message over Bluetooth
			String increaseEnemySpeed = "incEnSp";
			byte[] sendIncEnSp = increaseEnemySpeed.getBytes();
			mMultiplayerService.write(sendIncEnSp);
			updateCurrency();
			return true;
		} else {
			//Not enough money
			return false;
		}
    }
    
    /** This method is called when the player wants to decrease 
     * the health of the opponent. The method can only be called once 
     * every level */
    public boolean decreaseOppLife(){
		if(player.getMoney() >= 20){
			player.moneyFunction(-20);
			//send message over Bluetooth
			String decOppLife = "decOppLife";
			byte[] sendDecOppL = decOppLife.getBytes();
			mMultiplayerService.write(sendDecOppL);
			updateCurrency();
			return true;
		} else {
			//Not enough money
			return false;
		}
    }
    
    /** This method is called when the player wants to destroy 
     * one of the opponents random towers The method can only be called once 
     * every level */
    public boolean destroyTower(){
		if(player.getMoney() >= 20){
			player.moneyFunction(-20);
			//send message over Bluetooth
			String desTower = "desTower";
			byte[] sendDesTow = desTower.getBytes();
			mMultiplayerService.write(sendDesTow);
			updateCurrency();
			return true;
		} else {
			//Not enough money
			return false;
		}
    }
    
    /** This method is called when the player wants to make
     *  all the opponents enemies gain special ability (fast
     *  or resistance) */
    public boolean makeElemental(){
		if(player.getMoney() >= 20){
			player.moneyFunction(-20);
			//send message over Bluetooth
			String mkElem = "mkElem";
			byte[] sendMkElem = mkElem.getBytes();
			mMultiplayerService.write(sendMkElem);
			updateCurrency();
			return true;
		} else {
			//Not enough money
			return false;
		}
    }
    
    /** This method is called when the player wants to make
     *  a shield to protect from the opponents nasty multiplayer-
     *  manipulations */
    public boolean makeShield(){
		if(player.getMoney() >= 20){
			player.moneyFunction(-20);
			mkShield();
			//send message over Bluetooth
			/*
			String mkShield = "mkShield";
			byte[] sendMkShield = mkShield.getBytes();
			mMultiplayerService.write(sendMkShield);
			*/
			updateCurrency();
			return true;
		} else {
			//Not enough money
			return false;
		}
    }
    
    /**
     * The five help functions for the multiplayer gameplay
     */
    public void incEnSp(int nbr){
    	
    	// If we fail to find a random creature in nbrCreatures tries. We will try to get
    	// the first living creature and add special abillites to him.
    	if (nbr >= mLvl[lvlNbr].nbrCreatures) {
        	for (int z = 0; z < mLvl[lvlNbr].nbrCreatures; z++) {
        		if (mCreatures[z].draw && mCreatures[z].getHealth() > 0) {
        	    	mCreatures[z].creatureFast = true;
        	    	mCreatures[z].setVelocity(mCreatures[z].getVelocity()*1.5f);        	    	
        	    	mCreatures[z].setHealth(mLvl[lvlNbr].getHealth() * 4); 
        	    	updateCreatureProgress(0);
        			break;
        		}
        	}
        	return;
    	}
    	
    	// Tries to find a random living creature and speed and health to him
    	Random rand = new Random();
    	int tmp = rand.nextInt(mLvl[lvlNbr].nbrCreatures);
		if (mCreatures[tmp].draw && mCreatures[tmp].getHealth() > 0) {
	    	mCreatures[tmp].creatureFast = true;
	    	mCreatures[tmp].setVelocity(mCreatures[tmp].getVelocity()*1.5f);        	    	
	    	mCreatures[tmp].setHealth(mLvl[lvlNbr].getHealth() * 4); 
	    	updateCreatureProgress(0);
	    }
		else {
		    incEnSp(nbr++);
		}
    }
    public void decOppLife(){
    	player.damage(5);
    	updatePlayerHealth();
    }
    public void desTower(int nbr){
    	if (nbr >= mTower.length) {
        	for (int z = 0; z < mTower.length; z++) {
        		if (mTower[z].draw) {
        			mTower[z].draw = false;
        			mTower[z].relatedShot.draw = false;
        			break;
        		}
        	}
        	return;
    	}
    	
    	Random rand = new Random();
    	int tmp = rand.nextInt(mTower.length);

    	if (mTower[tmp].draw) {
	    	mTower[tmp].draw = false;
			mTower[tmp].relatedShot.draw = false;
    	}
    	else desTower(nbr++);
    }
    public void mkElem(){
    	Random rand = new Random();
    	int tmp = rand.nextInt(3);
    	
    	boolean fast = false;
    	boolean fireResistant = false;
    	boolean frostResistant = false;
    	boolean poisonResistant = false;
    	
    	if (tmp == 0) {
        	fast = true;
    	}
    	if (tmp == 1) {
        	fireResistant = true;
    	}
    	if (tmp == 2) {
        	frostResistant = true;
    	}
    	else {
        	poisonResistant = true;
    	}
    		
    	Log.d("ELEMENTAL",""+tmp);
    	
    	for (int z = 0; z < mLvl[lvlNbr].nbrCreatures; z++) {
    		mCreatures[z].setCreatureSpecials(fast,fireResistant,frostResistant,poisonResistant);
    	}
    }

    
    public void mkShield(){
    	this.multiplayerShield = true;
    }

}
