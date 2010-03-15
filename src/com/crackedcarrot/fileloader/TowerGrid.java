package com.crackedcarrot.fileloader;

/**
* Class defining the towerGrid in the game
*/
public class TowerGrid {
	// The current tower for this gridpos
	public int tower;
	// boolean defining if spot has a tower avaible or not
	public boolean empty;
    
	public TowerGrid(){
		tower = -1;
		empty = true;
	}
}