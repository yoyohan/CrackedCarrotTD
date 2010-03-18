package com.crackedcarrot.fileloader;

import com.crackedcarrot.Creature;

// Class contains level information. Number of creatures creature type etc.
public class Level extends Creature {
	public int nbrCreatures;
	
	//Constructor
    public Level(int resourceId){
		super(resourceId);
	}
    
}