package com.crackedcarrot;

/**
* Class defining a shot in the game
*/
public class Shot extends Sprite{
    // The tower object which the shot belongs to
    public Tower tower;
    
	public Shot(int resourceId,  int type, int frames, Tower tower){
		super(resourceId, NativeRender.SHOT, type, frames);
		this.tower = tower;
		super.draw = false;
	}
}