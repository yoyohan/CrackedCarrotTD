package com.crackedcarrot;

public class Player {

	private int difficulty;
	private int health;
	private int money = 0;
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
		money = (int)(money * 1.05);
	}
	
	public void damage(int dmg){
		health -= dmg;
	}
	
	public void addMoney(int amount){
		money += amount;
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
}
