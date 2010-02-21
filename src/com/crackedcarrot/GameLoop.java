/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crackedcarrot;

import android.os.SystemClock;
import android.util.Log;

/**
 * A simple runnable that updates the position of each sprite on the screen
 * every frame by applying a very simple gravity and bounce simulation.  The
 * sprites are jumbled with random velocities every once and a while.
 */
public class GameLoop implements Runnable {
    private Creature[] mCreatures;
    private Level[] mLvl;
    private long mLastTime;
    private int lvlNbr;
    private int playerHealth;
    private int remainingCreatures;
    private Coords[] wayP;
    private boolean run = true;
    
    public void run() { 
    	final long starttime = SystemClock.uptimeMillis();
    	lvlNbr = 0;
		playerHealth = 60;

    	while(run){
    		//Start new LEVEL
    		Log.d("GAMELOOP","INITGAMELOOP");

    		remainingCreatures = mLvl[lvlNbr].nrCr;
    		for (int z = 0; z < remainingCreatures; z++) {
    			mCreatures[z].cloneCreature(mLvl[lvlNbr].cr);
    			// In some way we have to determine when to spawn the creature. But we have to use better way than this one
    			mCreatures[z].spawndelay = z * (int)mCreatures[z].velocity;
    		}
    		
			while(remainingCreatures > 0 && playerHealth > 0){
	    		//Systemclock
				final long time = SystemClock.uptimeMillis();
				if ((time-starttime) > 60000) {
	            	run = false;
	            	break;
	            }

	            final long timeDelta = time - mLastTime;
	            final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
	            mLastTime = time;
	            
	            //Calls the method that moves the creature
	            moveCreature(timeDeltaSeconds,time-starttime);

	            
	            if (playerHealth < 1) {
            		//If you have lost all your lives then end game
                	Log.d("GAMETHREAD", "You are dead");
	            	run = false;
            	} 
            	else if (remainingCreatures < 1) {
            		//If you have survied the entire wave without dying. Proceed to next next level
                	Log.d("GAMETHREAD", "Level " + lvlNbr + "complete");
            		lvlNbr++;
            		if (lvlNbr > mLvl.length) {
            			// You have completed this map
            			run = false;
            		}
            	}
	        }
	    }
    	Log.d("GAMETHREAD", "dead tthread");
    }

    public void moveCreature(float timeDeltaSeconds, long time) {
    	if (mCreatures == null) {
    		return;
    	}
    	for (int x = 0; x < mLvl[lvlNbr].nrCr; x++) {
    		Creature object = mCreatures[x];
    		
			if (time > object.spawndelay && wayP[0].x == object.x && wayP[0].y == object.y) {
	    		//Log.d("START",object.toString());
				object.draw = true;
			}	            	
			
			if (object.draw) {
	    		Coords co = wayP[object.nextWayPoint];

				if(object.x > co.x){
		    		object.direction = Creature.LEFT;
		    		object.x = object.x - (object.velocity * timeDeltaSeconds);
		    		if(!(object.x > co.x)){
		    			object.x = co.x;
		    		}
		    	}
		    	else if (object.x < co.x) {
		    		object.direction = Creature.RIGHT;
		    		object.x = object.x + (object.velocity * timeDeltaSeconds);
		    		if(!(object.x < co.x)){
		    			object.x = co.x;
		    		}
		    	}
		    	else if(object.y > co.y){
		    		object.direction = Creature.DOWN;
		    		object.y = object.y - (object.velocity * timeDeltaSeconds);
		    		if(!(object.y > co.y)){
		    			object.y = co.y;
		    		}
		    	}
		    	else if (object.y < co.y) {
		    		object.direction = Creature.UP;
		    		object.y = object.y + (object.velocity * timeDeltaSeconds);
		    		if(!(object.y < co.y)){
		    			object.y = co.y;
		    		}
		    	}
		    	if (object.y == co.y && object.x == co.x){
		    		object.updateWayPoint();
		    	}
		    	// Creature has reached is destination without being killed
		    	if (object.nextWayPoint >= wayP.length){
		    		object.draw = false;
		    		//Log.d("KLAR",object.toString());
		    		playerHealth--;
		    		remainingCreatures--;
		    	}
			}
    	}
    }
    
    
    public void setCreatures(Creature[] creat) {
        this.mCreatures = creat;
    }
    public void setLevels(Level[] lvl) {
        this.mLvl = lvl;
    }
    
    public void setWP(WayPoints wp){
    	this.wayP = wp.getCoords();
    }
}
