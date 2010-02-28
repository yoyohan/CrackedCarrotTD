package com.crackedcarrot;

import android.util.Log;

public class Player {

	public int difficulty;
	public int health;
	public int money;
	
	public Player() {
		// nothing?
	}
	
	public void calculateInterest() {
		// Formula for calculating interest.
		Log.d("PLAYER", "Interest1: " + money);
		if (money > 10) {
			int interest = (money / 20) * (4-difficulty);
			money = money + interest;
		}
		Log.d("PLAYER", "Interest2: " + money);
	}

}
