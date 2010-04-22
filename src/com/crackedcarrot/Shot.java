package com.crackedcarrot;

/**
* Class defining a shot in the game
*/
public class Shot extends Sprite{
    // The tower object which the shot belongs to
    public Tower tower;
    
	public Shot(int resourceId,  int type, Tower tower){
		super(resourceId, SHOT, type);
		this.tower = tower;
		super.draw = false;
	}
}