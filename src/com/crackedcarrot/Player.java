package com.crackedcarrot;

public class Player {

	public int difficulty;
	public int health;
	public int money = 0;
	
	public Player() {
	}
	
	public void calculateInterest() {
		// Formula for calculating interest.
		// Found some problems with allocations, Removed until solved
		money = (int)(money * 1.05);
	}
}
