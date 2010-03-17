package com.crackedcarrot;

public class Player {

	public int difficulty;
	public int health;
	public int money = 0;
	public int timeUntilNextLevel;
	public double timeBetweenLevels;
	
	public Player() {
	}
	
	public void calculateInterest() {
		// Formula for calculating interest.
		money = (int)(money * 1.05);
	}
}
