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
    private Sprite[] mRenderables;
    private long mLastTime;
    //private long mLastJumbleTime;
    private int mViewWidth;
    private int mViewHeight;
    private boolean run = true;
    static final float COEFFICIENT_OF_RESTITUTION = 0.75f;
    static final float SPEED_OF_GRAVITY = 150.0f;
    static final long JUMBLE_EVERYTHING_DELAY = 15 * 1000;
    static final float MAX_VELOCITY = 8000.0f;
    
    public void run() { 
        Log.d("GAMETHREAD", "start tthread");
    	final long starttime = SystemClock.uptimeMillis();
    	
    	while(run){
    	// Perform a single simulation step.
        if (mRenderables != null) {

        	final long time = SystemClock.uptimeMillis();
            if ((time-starttime) > 60000) {
            	run = false;            	
            }
        	
        	final long timeDelta = time - mLastTime;
            final float timeDeltaSeconds = 
                mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
            mLastTime = time;
           
            for (int x = 1; x < mRenderables.length; x++) {
            	Creature object = (Creature)mRenderables[x];
            	
            	if (object.velocityX > 0) {
                    object.x = object.x + (object.velocityX * timeDeltaSeconds);
            	}
            	if (object.velocityY > 0) {
                    object.y = object.y + (object.velocityY * timeDeltaSeconds);
            	}
                
            	if ((object.x < 0.0f && object.velocityX < 0.0f) || (object.x > mViewWidth - object.width && object.velocityX > 0.0f)) {
                    Log.d("robot","krock x led");
            		object.velocityX = 0.0f;
                    object.velocityY = 50f;
                    object.x = Math.max(0.0f,Math.min(object.x, mViewWidth - object.width));
                    if (object.x < 0.0f && object.velocityX < 0.0f) {
                        object.velocityY = -50f;
                    }
            	}
                
                if ((object.y < 0.0f && object.velocityY < 0.0f) || (object.y > mViewHeight - object.height && object.velocityY > 0.0f)) {
                    Log.d("robot","krock y led");
                    object.velocityY = 0.0f;
                    object.velocityX = -50f;
                    object.y = Math.max(0.0f, Math.min(object.y, mViewHeight - object.height));
                    if (object.y < 0.0f && object.velocityY < 0.0f) {
	                 	object.velocityX = 50f;
                    }
                }
            	
                
                
                
            }
        }
	}
    Log.d("GAMETHREAD", "dead tthread");
    }
    
    public void setRenderables(Sprite[] renderables) {
        mRenderables = renderables;
    }
    public void setViewSize(int width, int height) {
        mViewHeight = height;
        mViewWidth = width;
    }
}
