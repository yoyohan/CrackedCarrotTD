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


/**
 * This is the OpenGL ES version of a sprite.  It is more complicated than the
 * CanvasSprite class because it can be used in more than one way.  This class
 * can draw using a grid of verts, a grid of verts stored in VBO objects, or
 * using the DrawTexture extension.
 */
public class Sprite {
    // Position.
    public float x;
    public float y;
    public float z;
    
    // Is the sprite going to be draw'd or not?
    public boolean draw = true;
    
    // Opacity for this sprite.
    public float opacity = 1.0f;


    //Velocity
    /*public float velocityX;
    public float velocityY;
    public float velocityZ;*/
    

    // Size.
    public float width;
    public float height;
	
    // The OpenGL ES texture handle to draw.
    public int mTextureName;
    // The id of the original resource that mTextureName is based on.
    public int mResourceId;
    
    public Sprite() {
    	
    }
    
    public Sprite(int resourceId) {
        mResourceId = resourceId;
    }
    
    public void setTextureName(int name) {
        mTextureName = name;
    }
    
    public int getTextureName() {
        return mTextureName;
    }
    
    public void setResourceId(int id) {
        mResourceId = id;
    }
    
    public int getResourceId() {
        return mResourceId;
    }
    
}
