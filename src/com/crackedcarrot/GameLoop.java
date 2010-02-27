package com.crackedcarrot;

import com.crackedcarrot.fileloader.Level;

import android.os.SystemClock;
import android.util.Log;

/**
 * A runnable that updates the position of each creature and projectile
 * every frame by applying a simple movement simulation. Also keeps
 * track of player life, level count etc.
 */
public class GameLoop implements Runnable {
    private Creature[] mCreatures;
    private Level[] mLvl;
    private Tower[] mTower;
    private long mLastTime;
    private int lvlNbr;
    private int remainingCreatures;
    private Coords[] wayP;
    public volatile boolean run = true;
    private long gameSpeed;
    private SoundManager soundManager;
    private Player player;
    
    public void run() { 
    	lvlNbr = 0;
	    gameSpeed = 1;

    	while(run){
    		Log.d("GAMELOOP","INIT GAMELOOP");
        	final long starttime = SystemClock.uptimeMillis();
    		
    		//The following line contains the code for initiating every level
    		/////////////////////////////////////////////////////////////////
    		remainingCreatures = mLvl[lvlNbr].nbrCreatures;

    		for (int z = 0; z < remainingCreatures; z++) {
    			// The following line is used to add the following wave of creatures to the list of creatures.
        		mCreatures[z].cloneCreature(mLvl[lvlNbr]);
    			// In some way we have to determine when to spawn the creature. Since we dont want to spawn them all at once.
        		mCreatures[z].x = wayP[0].x;
        		mCreatures[z].y = wayP[0].y;
        		mCreatures[z].spawndelay = z * (int)(mCreatures[z].velocity * mCreatures[z].height * gameSpeed);
    		}
    		
			// The LEVEL loop. Will run until all creatures are dead or done or player are dead.
    		while(remainingCreatures > 0 && run){
	    		//Systemclock. Used to help determine speed of the game. 
				final long time = SystemClock.uptimeMillis();
				
	            // Used to calculate creature movement.
				final long timeDelta = time - mLastTime;
	            final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
	            mLastTime = time;
	            
	            //Calls the method that moves the creature.
	            moveCreature(timeDeltaSeconds,time-starttime);
	            //Calls the method that handles the monsterkilling.
	            killCreature(timeDeltaSeconds);

	            
	            // Check if the GameLoop are to run the level loop one more time.
	            if (player.health < 1) {
            		//If you have lost all your lives then the game ends.
	            	run = false;
            	} 
	        }

    		
    		player.calculateInterest();
    		Log.d("GAMELOOP", "Money: " + player.money);


    		// Check if the GameLoop are to run the level loop one more time.
            if (player.health < 1) {
        		//If you have lost all your lives then the game ends.
            	Log.d("GAMETHREAD", "You are dead");
            	run = false;
        	} 
        	else if (remainingCreatures < 1) {
        		//If you have survied the entire wave without dying. Proceed to next next level.
            	Log.d("GAMETHREAD", "Level " + lvlNbr + "complete");
        		lvlNbr++;
        		if (lvlNbr > mLvl.length) {
        			// You have completed this map
                	Log.d("GAMETHREAD", "You have completed this map");
        			run = false;
        		}
        	}
	    }
    	Log.d("GAMETHREAD", "dead thread");
    }

    
	/**
	 * Will go through all of the creatures from this level and
	 * calculate the movement. 
	 * <p>
	 * This method runs every loop the gameLoop takes. 
	 *
	 * @param  timeDeltaSeconds  	Time since last GameLoop lap 
	 * @param  time	 				Time since this level started
	 * @return      				void
	 */
    public void moveCreature(float timeDeltaSeconds, long time) {
    	// If the list of creatures is empty we will end this method
    	if (mCreatures == null) {
    		return;
    	}
    	for (int x = 0; x < mLvl[lvlNbr].nbrCreatures; x++) {
    		Creature object = mCreatures[x];
    		// Check to see if a not existing creature is supposed to spawn on to the map
			if (time > object.spawndelay && wayP[0].x == object.x && wayP[0].y == object.y) {
				object.draw = true;
			}	            	
			// If the creature is living start movement calculations.
			if (object.draw && object.opacity == 1.0f) {
	    		Coords co = wayP[object.nextWayPoint];
	    		// Creature is moving left.
				if(object.x > co.x){
		    		object.direction = Creature.LEFT;
		    		object.x = object.x - (object.velocity * timeDeltaSeconds * gameSpeed);
		    		if(!(object.x > co.x)){
		    			object.x = co.x;
		    		}
		    	}
	    		// Creature is moving right.
				else if (object.x < co.x) {
		    		object.direction = Creature.RIGHT;
		    		object.x = object.x + (object.velocity * timeDeltaSeconds * gameSpeed);
		    		if(!(object.x < co.x)){
		    			object.x = co.x;
		    		}
		    	}
	    		// Creature is moving down.
				else if(object.y > co.y){
		    		object.direction = Creature.DOWN;
		    		object.y = object.y - (object.velocity * timeDeltaSeconds * gameSpeed);
		    		if(!(object.y > co.y)){
		    			object.y = co.y;
		    		}
		    	}
	    		// Creature is moving up.
		    	else if (object.y < co.y) {
		    		object.direction = Creature.UP;
		    		object.y = object.y + (object.velocity * timeDeltaSeconds * gameSpeed);
		    		if(!(object.y < co.y)){
		    			object.y = co.y;
		    		}
		    	}
				// Creature has reached a WayPoint. Update
		    	if (object.y == co.y && object.x == co.x){
		    		object.updateWayPoint();
		    	}
		    	// Creature has reached is destination without being killed
		    	if (object.nextWayPoint >= wayP.length){
		    		object.draw = false;
		    		player.health --;
		    		remainingCreatures --;
		    	}
		    	
		    	// Creature is dead and fading...
			} else if (object.draw && object.opacity > 0.0f) {
					// If we divide by 10 the creature stays on the screen a while longer...
				object.opacity = object.opacity - (timeDeltaSeconds/10 * gameSpeed);
				if (object.opacity <= 0.0f) {
					object.draw = false;
				}
			}
    	}
    }

    
	/**
	 * Will go through all of the towews and try to find targets
	 * <p>
	 * This method runs every loop the gameLoop takes. 
	 *
	 * @param  timeDeltaSeconds  	Time since last GameLoop lap 
	 * @return      				void
	 */
    public void killCreature(float timeDeltaSeconds) {
    	// If the list of shots is empty we will end this method
    	if (mTower == null) {
    		return;
    	}
    	//TODO: If we would use mTower.length.. The game will try to check all
    	//towers but since we only use one and don't have enabled buying
    	//we will wait with this loop.
    	for (int x = 0; x < 1; x++) {
    		
    		Tower towerObject = mTower[x];
    		// Decrease the coolDown variable and check if it has reached zero
    		towerObject.tmpCoolDown = towerObject.tmpCoolDown - (timeDeltaSeconds * gameSpeed);
    		if (!towerObject.relatedShot.draw && (towerObject.tmpCoolDown <= 0)) {
    			// If the tower/shot is existing start calculations.
    			towerObject.trackEnemy(mCreatures);
    			if (towerObject.targetCreature != null) {
    					// play shot1.mp3
    					soundManager.playSound(0);
    					towerObject.tmpCoolDown = towerObject.coolDown;
    					towerObject.relatedShot.draw = true;
    			}
    		}
    		// if the creature is still alive or have not reached the goal
    		if (towerObject.relatedShot.draw && towerObject.targetCreature.draw) {
    			Creature targetCreature = towerObject.targetCreature;

    			float yDistance = (targetCreature.y+(targetCreature.height/2)) - towerObject.relatedShot.y;
    			float xDistance = (targetCreature.x+(targetCreature.width/2)) - towerObject.relatedShot.x;
    			double xyMovement = (towerObject.velocity * timeDeltaSeconds * gameSpeed);
    			
 //   			Log.d("FEL",""+towerObject.relatedShot.x);
    			
    			if ((Math.abs(yDistance) <= xyMovement) && (Math.abs(xDistance) <= xyMovement)) {
		    		towerObject.relatedShot.draw = false;
		    		towerObject.resetShotCordinates();
		    		//Basic way of implementing damage
		    		targetCreature.health = targetCreature.health - towerObject.createDamage();
		    		if (targetCreature.health <= 0) {
		    			//object.cre.draw = false;
		    			targetCreature.opacity = targetCreature.opacity - 0.1f;
		    			remainingCreatures --;
		    			player.money = player.money + targetCreature.goldValue;
		    			Log.d("LOOP","Creature killed");
		    			// play died1.mp3
		    			soundManager.playSound(10);
		    		}
    			}
    			else {
        			double radian = Math.atan2(yDistance, xDistance);
        			towerObject.relatedShot.x += Math.cos(radian) * xyMovement;
        			towerObject.relatedShot.y += Math.sin(radian) * xyMovement;
    			}
			}
    		else {
    			towerObject.relatedShot.draw = false;
	    		towerObject.resetShotCordinates();
    		}
    	}
	}
    
    
	/**
	 * Will set the list of creatures moving over the map.
	 * <p>
	 * This method is called before GameLoop is started. 
	 *
	 * @param  creature  	List of Creature. 
	 * @return      		void
	 */    
    public void setCreatures(Creature[] creatures) {
        this.mCreatures = creatures;
    }
    
	/**
	 * Will give level information to the GameLoop.
	 * calculate the movement. 
	 * <p>
	 * This method is called before GameLoop is started. 
	 *
	 * @param  lvl			List of type Levels
	 * @return      		void
	 */    
    public void setLevels(Level[] lvl) {
        this.mLvl = lvl;
    }
    
	/**
	 * Will set the WayPoints for this map to  the GameLoop.
	 * <p>
	 * This method is called before GameLoop is started. 
	 *
	 * @param  wp			Object of type WayPoints
	 * @return     			void
	 */    
    public void setWP(Waypoints wayP){
    	this.wayP = wayP.getCoords();
    }

    /**
	 * Will set the Shots(Tower projectiles) for this map to  the GameLoop.
	 * <p>
	 * This method is called before GameLoop is started. 
	 *
	 * @param  sh			List of type Shot
	 * @return     			void
	 */    
    public void setTowers(Tower[] tw){
    	this.mTower = tw;
    }
    
    public void setSoundManager(SoundManager sm) {
    	this.soundManager = sm;
    }
    
    public void setPlayer(Player p) {
    	this.player = p;
    }
    
}