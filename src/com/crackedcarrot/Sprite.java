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
    // color and opacity and for this sprite.
    public float r = 1.0f;
    public float g = 1.0f;
    public float b = 1.0f;
    public float opacity = 1.0f;
    // Size.
    protected float width;
    protected float height;
    //Scale.
    public float scale = 1.0f;
    // The OpenGL ES texture handle to draw.
    private int mTextureName;
    // The id of the original resource that mTextureName is based on.
    private int mResourceId;
    //this field tracks the index of this sprite in the renders internal structures.
    
    public Sprite() {
    }
    
    public Sprite(int resourceId) {
        mResourceId = resourceId;
    }
    
    public void setResourceId(int id) {
        mResourceId = id;
    }
    
    public int getResourceId() {
        return mResourceId;
    }

	public void setTextureName(int mTextureName) {
		this.mTextureName = mTextureName;
	}

	public int getTextureName() {
		return mTextureName;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getWidth() {
		return width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getHeight() {
		return height;
	}
    
}
