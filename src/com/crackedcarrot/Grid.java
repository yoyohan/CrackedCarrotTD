package com.crackedcarrot;

import com.crackedcarrot.menu.R;

public class Grid extends Sprite {
	public Grid(int resourceId, Scaler s){
		super(resourceId);
		this.x = 0; this.y = 0; this.z = 0;
		this.width = s.getScreenResolutionX();
        this.height = s.getScreenResolutionY();
        this.draw = false;
        this.opacity = 0.0f;
	}
}
