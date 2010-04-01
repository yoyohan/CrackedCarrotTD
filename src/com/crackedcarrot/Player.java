package com.crackedcarrot;

public class Player {

	private int difficulty;
	private int health;
	private int money;
	private int interestGainedLatestLvl;
	private int interestGainedEntireGame;
	private int healthLost;
	private int timeUntilNextLevel;
	private double timeBetweenLevels;
	
	public Player(int difficulty, int health, int money, int timeBetweenLevels) { 
		this.difficulty = difficulty;
		this.health = health;
		this.money = money;
		this.timeBetweenLevels = timeBetweenLevels;
	}
	
	public void calculateInterest() {
		// Formula for calculating interest.
		interestGainedLatestLvl = (int)(money * 0.05);
		interestGainedEntireGame+=interestGainedLatestLvl;
		money = (int)(money * 1.05);
	}
	
	public int getInterestGainedThisLvl() {
		return interestGainedLatestLvl;
	}
	
	public int getHealthLostThisLvl() {
		int tmp = healthLost;
		healthLost = 0;
		return tmp;
	}	
	
	public void damage(int dmg){
		healthLost++;
		health -= dmg;
	}
	
	public void moneyFunction(int value) {
		// Should be fine for negative values as well.
		money = money + value;
		
		if (money < 0) {
			money = 0;
		}
	}
	
	public int getMoney() {
		return money;
	}

	public double getTimeBetweenLevels() {
		return timeBetweenLevels;
	}

	public void setTimeUntilNextLevel(int timeUntilNextLevel) {
		this.timeUntilNextLevel = timeUntilNextLevel;
	}

	public int getTimeUntilNextLevel() {
		return timeUntilNextLevel;
	}

	public int getHealth() {
		return health;
	}

	public int getDifficulty() {
		return difficulty;
	}

}
