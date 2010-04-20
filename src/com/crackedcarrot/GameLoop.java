package com.crackedcarrot;

import java.util.Random;
import java.util.concurrent.Semaphore;

import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.crackedcarrot.HUD.Grid;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.menu.R;
import com.crackedcarrot.textures.TextureData;

/**
 * A runnable that updates the position of each creature and projectile
 * every frame by applying a simple movement simulation. Also keeps
 * track of player life, level count etc.
 */
public class GameLoop implements Runnable {

    public  NativeRender renderHandle;
    public  SoundManager soundManager;  // We need to reach this to be able to turn off sound.
    
    private GameLoopGUI  gui;
    private Scaler       mScaler;
    private Semaphore    dialogSemaphore = new Semaphore(1);
    private Tracker      mTracker;

    private Map mGameMap;
    private Player player;

    private boolean run = true;
    
    private long mLastTime;
    
    	// TODO: Maybe we can remove this string thingie-completely...?
    private String resumeTowers = null;

    private float startCreatureHealth;
    private float currentCreatureHealth;
    
    private int lvlNbr = 0;
    private int gameSpeed;
    private int remainingCreaturesALIVE;
    private int remainingCreaturesALL;
    private int totalNumberOfTowers = 0;

    private Creature[] mCreatures;
    private Level[]    mLvl;
    private Shot[]     mShots;
    private Sprite[]   mGrid;
    private Tower[]    mTower;
    private Tower[][]  mTowerGrid;
    private Tower[]    mTTypes;
    
    public Message     msgCreatureLeft = new Message();
    public Message     msgMoney = new Message();
    public Message     msgPlayerHealth = new Message();
    public Message     msgProgressbar = new Message();
    
    private long       msgCreatureLeftTime = 0;
    private long       msgMoneyTime = 0;
    private long       msgPlayerHealthTime = 0;
    private long       msgProgressbarTime = 0;
    
    public GameLoop(NativeRender renderHandle, Map gameMap, Level[] waveList, Tower[] tTypes,
			Player p, GameLoopGUI gui, SoundManager sm){
    	this.renderHandle = renderHandle;
		this.mGameMap = gameMap;
   		this.mTowerGrid = gameMap.getTowerGrid();
   		this.mScaler = gameMap.getScaler();
		this.mTTypes = tTypes;
        this.mLvl = waveList;
    	this.soundManager = sm;
    	this.player = p;
    	this.gui = gui;
    	this.mTracker = new Tracker(mScaler.getGridWidth(),mScaler.getGridHeight(), 20);
    }
    
	private void initializeDataStructures() {
		//this allocates the space we need for shots towers and creatures.
	    this.mTower = new Tower[60];
	    this.mShots = new Shot[60];
	    this.mCreatures = new Creature[50];
		this.mGrid = new Grid[1]; 
		mGrid[0]  = new Grid(R.drawable.grid4px, mScaler);
		
	    //Initialize the all the elements in the arrays with garbage data
	    for (int i = 0; i < mTower.length; i++) {

	    	mTower[i] = new Tower(R.drawable.tower1, 0, mCreatures, soundManager,mTracker);
	    	mShots[i] = new Shot(R.drawable.cannonball,0, mTower[i]);
	    	mTower[i].setHeight(this.mTTypes[0].getHeight());
	    	mTower[i].setWidth(this.mTTypes[0].getWidth());
	    	mTower[i].relatedShot = mShots[i];
	    	mTower[i].relatedShot.setHeight(this.mTTypes[0].relatedShot.getHeight());
	    	mTower[i].relatedShot.setWidth(this.mTTypes[0].relatedShot.getWidth());
	    	mTower[i].draw = false;
	    	mShots[i].draw = false;
	    } 

	    Random rand = new Random();	    
	    //same as for the towers and shots.
	    for (int i = 0; i < mCreatures.length; i++) {

	    	mCreatures[i] = new Creature(R.drawable.bunny_pink_alive, 
	    								0,1,player, soundManager, 
	    								mGameMap.getWaypoints().getCoords(), 
	    								this,
	    								i,
	    								mTracker);

	    	mCreatures[i].draw = false;
	    	int tmpOffset = rand.nextInt(10) - 5;
	    	Coords tmpCoord = mScaler.scale(tmpOffset,0);
	    	mCreatures[i].setOffset(tmpCoord.getX());
	    }
	    //Set grid attributes.
	    //Free all allocated data in the render
	    //Not needed really.. but now we know for sure that
	    //we don't have any garbage anywhere.
		try {
			renderHandle.freeSprites();
			renderHandle.preloadTextureLibrary();
			//Ok, here comes something superduper mega important.
			//The folowing looks up what names the render assigned
			//To every texture from their resource ids 
			//And assigns that id to the template objects for
			//Towers shots and creatures.
			
			for(int i = 0; i < mTTypes.length; i++){
				mTTypes[i].setCurrentTexture(
						renderHandle.getTexture(mTTypes[i].getResourceId()));
				
				mTTypes[i].relatedShot.setCurrentTexture(
						renderHandle.getTexture(mTTypes[i].relatedShot.getResourceId()));
				
			}
			
			for(int i = 0; i < mLvl.length; i++){
				TextureData test = renderHandle.getTexture(mLvl[i].getResourceId());
				Log.d("INIT", ""+mLvl[i].getResourceId());
				
				Log.d("INIT", ""+test.mTextureName);
				mLvl[i].setCurrentTexture(test);
				mLvl[i].setDeadTexture(renderHandle.getTexture(mLvl[i].getDeadResourceId()));
			}
			
						
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Sends an array with sprites to the renderer
		
		//UGLY HACK!!
		mGameMap.getBackground()[0].setType(NativeRender.BACKGROUND, 0);
		//END UGLY HACK!!
		
		renderHandle.setSprites(mGameMap.getBackground(), NativeRender.BACKGROUND);
		renderHandle.setSprites(mCreatures, NativeRender.CREATURE);
		renderHandle.setSprites(mTower, NativeRender.TOWER);
		renderHandle.setSprites(mShots, NativeRender.SHOT);
		//renderHandle.setSprites(mGrid, NativeRender.HUD);
		
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
		Runtime r = Runtime.getRuntime();
        r.gc();
		
	}
    
	private void initializeLvl() {
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

    	int reverse = remainingCreaturesALL; 
		for (int z = 0; z < remainingCreaturesALL; z++) {
			reverse--;
			int special = 1;
    		if (mCreatures[z].isCreatureFast())
    			special = 2;
    		mCreatures[z].setSpawndelay((player.getTimeBetweenLevels() + ((reverse/special)*2)));
		}
	}

    public void run() {
    	
    	Looper.prepare();
    	
	    initializeDataStructures();

	    	// Resuming an old game? Rebuild all the old towers.
	    if ((resumeTowers != null) && (resumeTowers.compareTo("") != 0)) {
	    	String[] towers = resumeTowers.split("-");
	    	
	    	for (int i = 0; i < towers.length; i ++) {
	    		String[] tower = towers[i].split(",");
	    		Coords c = new Coords(Integer.parseInt(tower[1]), Integer.parseInt(tower[2]));
	    		Log.d("GAMELOOP", "Resume CreateTower Type: " + tower[0]);
	    		createTower(c, Integer.parseInt(tower[0]));
	    	}
	    }
	    
	    Log.d("GAMELOOP","INIT GAMELOOP");

	    while(run){
	    	
	    	//It is important that ALL SIZES OF SPRITES ARE SET BEFORE! THIS!
    		//OR they will be infinitely small.
    		initializeLvl();

    		// This is used to know when the time has changed or not
    		int lastTime = 0;

    		// The LEVEL loop. Will run until all creatures are dead or done or player are dead.
    		while(remainingCreaturesALL > 0 && run){
    			
    			//Systemclock. Used to help determine speed of the game. 
				final long time = SystemClock.uptimeMillis();
    			
    			try {
    	    		GameInit.pauseSemaphore.acquire();
    			} catch (InterruptedException e1) {}
    			GameInit.pauseSemaphore.release();
    			
    			//Get the time after an eventual pause and add this to the mLastTime variable
    			final long time2 = SystemClock.uptimeMillis();
    			final long pauseTime = time2 - time;

	            // Used to calculate creature movement.
				final long timeDelta = time - mLastTime;
	            final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? (timeDelta / 1000.0f) * gameSpeed : 0.0f;
	            mLastTime = time + pauseTime;
	            
	            // Shows how long it is left until next level
	            if (player.getTimeUntilNextLevel() > 0) {
	            	if ((player.getTimeUntilNextLevel() - timeDeltaSeconds) <= 0) {
		            	// Show healthbar again.
	            		gui.sendMessage(gui.GUI_SHOWHEALTHBAR_ID, 0, 0);
	            	    
	            		// Tell the creature counter to stop showing time and start showing nbr of creatures
	            		// TODO: removed this, we dont need to call it at all times really do we?
	            		creatureDiesOnMap(0);
	            	}
	            	else 
	            		player.setTimeUntilNextLevel(player.getTimeUntilNextLevel() - timeDeltaSeconds);
	            	if (lastTime != (int) player.getTimeUntilNextLevel()) {
	            		lastTime = (int) player.getTimeUntilNextLevel();
	            		gui.sendMessage(gui.GUI_NEXTLEVELINTEXT_ID, lastTime, 0);

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
                	gui.sendMessage(gui.DIALOG_HIGHSCORE_ID, player.getInterestGainedEntireGame(), 0);
                	
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

    public boolean createTower(Coords TowerPos, int towerType) {
		if (mTTypes.length > towerType && totalNumberOfTowers < mTower.length) {
			if (!mScaler.insideGrid(TowerPos.x,TowerPos.y)) {
				//You are trying to place a tower on a spot outside the grid
				return false;
			}
			if (player.getMoney() < mTower[totalNumberOfTowers].getPrice()) {
				// Not enough money to build this tower.
				return false;
			}
			Coords tmpC = mScaler.getGridXandY(TowerPos.x,TowerPos.y);
			int tmpx = tmpC.x;
			int tmpy = tmpC.y;
			
			if (mTowerGrid[tmpx][tmpy] != null && !mTowerGrid[tmpx][tmpy].draw) {
				Coords towerPlacement = mScaler.getPosFromGrid(tmpx, tmpy);
				mTower[totalNumberOfTowers].createTower(mTTypes[towerType], towerPlacement, mScaler);
				mTowerGrid[tmpx][tmpy] = mTower[totalNumberOfTowers];
				player.moneyFunction(-mTower[totalNumberOfTowers].getPrice());
				
				try {
					TextureData tex = renderHandle.getTexture(mTower[totalNumberOfTowers].getResourceId());
					mTower[totalNumberOfTowers].setCurrentTexture(tex);
					TextureData tex2 = renderHandle.getTexture(mTower[totalNumberOfTowers].relatedShot.getResourceId());
					mTower[totalNumberOfTowers].relatedShot.setCurrentTexture(tex2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				totalNumberOfTowers++;
				soundManager.playSound(20);
				
				return true;
			} else {
			/*	
			 * TODO: Maybe someone can use this to upgrade towers, comment left intact.
			 *
			    // User clicked on an existing tower. Show upgrade window.
				Message msg = new Message();
				msg.what = 4;
				nextLevelHandler.sendMessage(msg);
			  */
			}
		}
		return false;
    }
    
    // When a creature is dead and have faded away we will remove it from the gameloop
    public void creatureLeavesMAP(int n){
    	this.remainingCreaturesALL -= n;
    }
    
    // When the player decreases in health, we will notify the status bar
    public void updatePlayerHealth(){
		//gui.sendMessage(gui.GUI_PLAYERHEALTH_ID, player.getHealth(), 0);
    	if (SystemClock.uptimeMillis() > msgPlayerHealthTime + 1000) {
    		msgPlayerHealthTime = SystemClock.uptimeMillis();
    		
    		msgPlayerHealth.what = gui.GUI_PLAYERHEALTH_ID;
    		msgPlayerHealth.arg1 = player.getHealth();
    		gui.pushMessage(msgPlayerHealth);
    		
    		Log.d("GAMELOOP", "push'd msgPlayerHealth");
    	}
    }

    // When a creature is dead we will notify the status bar
    public void creatureDiesOnMap(int n) {
    	this.remainingCreaturesALIVE -= n;
    	if (remainingCreaturesALIVE <= 0) 
    		for (int x = 0; x < mLvl[lvlNbr].nbrCreatures; x++)
    			mCreatures[x].setAllDead(true);
		// Update the status, displaying how many creatures that are still alive
    	//gui.sendMessage(gui.GUI_CREATURELEFT_ID, remainingCreaturesALIVE, 0);
    	//if (!gui.guiHandler.hasMessages(gui.GUI_CREATURELEFT_ID)) {
    	if (SystemClock.uptimeMillis() > msgCreatureLeftTime + 1000) {
    		msgCreatureLeftTime = SystemClock.uptimeMillis();
    		
    		msgCreatureLeft.what = gui.GUI_CREATURELEFT_ID;
    		msgCreatureLeft.arg1 = remainingCreaturesALIVE;
    		gui.pushMessage(msgCreatureLeft);
    		
    		Log.d("GAMELOOP", "push'd msgCreatureLeft");
    	}
    }
    
    public void updateCreatureProgress(float dmg) {
    	// Update the status, displaying total health of all creatures
    	this.currentCreatureHealth -= dmg;
		//gui.sendMessage(gui.GUI_PROGRESSBAR_ID, (int)(100*(currentCreatureHealth/startCreatureHealth)), 0);
    	//if (!gui.guiHandler.hasMessages(gui.GUI_PROGRESSBAR_ID)) {
    	if (SystemClock.uptimeMillis() > msgProgressbarTime + 1000) {
    		msgProgressbarTime = SystemClock.uptimeMillis();

    		msgProgressbar.what = gui.GUI_PROGRESSBAR_ID;
    		msgProgressbar.arg1 = (int) (100*(currentCreatureHealth/startCreatureHealth));
    		gui.pushMessage(msgProgressbar);
    		
    		Log.d("GAMELOOP", "push'd msgProgressbar");
    	}
    }
    
    // Update the status when the players money increases.
    public void updateCurrency(int currency) {
		//gui.sendMessage(gui.GUI_PLAYERMONEY_ID, player.getMoney(), 0);
    	//if (!gui.guiHandler.hasMessages(gui.GUI_PLAYERMONEY_ID)) {
    	if (SystemClock.uptimeMillis() > msgMoneyTime + 1000) {
    		msgMoneyTime = SystemClock.uptimeMillis();

    		msgMoney.what = gui.GUI_PLAYERMONEY_ID;
    		msgMoney.arg1 = player.getMoney();
    		gui.pushMessage(msgMoney);
    		
    		Log.d("GAMELOOP", "push'd msgMoney");
    	}
    }
    
    public void stopGameLoop(){
    	run = false;
    }
    
    public void dialogClick() {
    	dialogSemaphore.release();
    }
    
    public Level getLevelData() {
    	return mLvl[lvlNbr];
    }
    public Player getPlayerData() {
    	return player;
    }
    public int getLevelNumber() {
    	return lvlNbr;
    }
    
    public void resumeSetLevelNumber(int i) {
    	this.lvlNbr = i;
    }

    	// This is used by the savegame-function to remember all the towers.
    	// TODO: We need to get the correct "version" of the tower too, e.g.
    	// any upgrades purchased, etc....
    public String resumeGetTowers() {
    	String s = "";
    	for (int i = 0; i < totalNumberOfTowers; i++) {
    		Tower t = mTower[i];
    		s = s + t.getTowerTypeId() + "," + (int) t.x + "," + (int) t.y + "-";
    	}
    	
    	Log.d("GAMELOOP", "resumeTowers: " + s);

    	return s;
    }
    
    public void resumeSetTowers(String s) {
    	this.resumeTowers = s;
    }
    
	public void setGameSpeed(int i) {
		this.gameSpeed = i;
	}

}