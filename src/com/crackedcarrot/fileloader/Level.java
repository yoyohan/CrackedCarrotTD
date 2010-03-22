package com.crackedcarrot.fileloader;

import com.crackedcarrot.Creature;

// Class contains level information. Number of creatures creature type etc.
public class Level extends Creature {
	public int nbrCreatures;
	
	//Constructor
    public Level(int resourceId){
		super(resourceId);
	}
    public int getHealth() {
		return health;
	}
	
	public void setHealth(int health){
		this.health = health;
	}
	
	public void setVelocity(int velocity){
		this.velocity = velocity;
	}
	
	public void setGoldValue(int goldValue) {
		this.goldValue = goldValue;
	}

	public void setCreatureFast(boolean creatureFast) {
		this.creatureFast = creatureFast;
	}

	public void setCreatureFrostResistant(boolean creatureFrostResistant) {
		this.creatureFrostResistant = creatureFrostResistant;
	}
	
	public void setCreatureFireResistant(boolean creatureFireResistant) {
		this.creatureFireResistant = creatureFireResistant;
	}

	public void setCreaturePoisonResistant(boolean creaturePoisonResistant) {
		this.creaturePoisonResistant = creaturePoisonResistant;
	}
	
	public void setDeadResourceId(int mDeadResourceId) {
		this.mDeadResourceId = mDeadResourceId;
	}
    
	public int getDeadResourceId() {
		return mDeadResourceId;
	}
	
	public void setDeadTextureName(int mDeadTextureName) {
		this.mDeadTextureName = mDeadTextureName;
	}
    
}