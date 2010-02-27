package com.crackedcarrot;

/**
* Class defining a shot in the game
*/
public class Shot extends Sprite{
    // The tower object which the shot belongs to
    public Tower tower;
    
	public Shot(int resourceId, Tower tower){
		super(resourceId);
		this.tower = tower;
		super.draw = false;
	}
}