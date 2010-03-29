package com.crackedcarrot;

/**
* Class defining a shot in the game
*/
public class Shot extends Sprite{
    // The tower object which the shot belongs to
    public Tower tower;
    
	public Shot(int resourceId, Tower tower, int type){
		super(resourceId);
		this.tower = tower;
		super.draw = false;
		setType(NativeRender.SHOT, type);

	}
}