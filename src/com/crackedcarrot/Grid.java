package com.crackedcarrot;

public class Grid extends Sprite {
	public Grid(int resourceId, Scaler s){
		super(resourceId);
		this.x = 0; this.y = 0; this.z = 0;
		this.setWidth(s.getScreenResolutionX());
        this.setHeight(s.getScreenResolutionY());
        this.draw = false;
        this.opacity = 0.0f;
	}
}
