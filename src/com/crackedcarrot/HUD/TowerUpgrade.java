package com.crackedcarrot.HUD;

import com.crackedcarrot.Coords;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.menu.R;

public class TowerUpgrade extends Sprite{
	public TowerUpgrade(Scaler s){
		//The grid only has one subtype, and one frame. Magical constants for the win.
		super(R.drawable.range_indicator, UI, 0);
		this.x = 0; this.y = 0; this.z = 0;
		Coords co = s.scale(60, 60);
		this.setWidth(co.getX());
        this.setHeight(co.getY());
        this.draw = false;
        
        this.r = 1.0f;
        this.g = 0.0f;
        this.b = 0.0f;
        this.opacity = 0.0f;
	}
}
