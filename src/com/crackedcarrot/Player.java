package com.crackedcarrot;

public class Player {

	private int difficulty;
	private int health;
	private int money;
	private int interestGainedLatestLvl;
	private int score;
	private int healthLost;
		// TODO: Är inte dom här två nedan samma saker?
	private float timeUntilNextLevel;
	private float timeBetweenLevels;
	
	public Player(int difficulty, int health, int money, int timeBetweenLevels) { 
		this.difficulty = difficulty;
		this.health = health;
		this.money = money;
		this.timeBetweenLevels = timeBetweenLevels;
	}
	
	public void calculateInterest() {
		// Formula for calculating interest.
		interestGainedLatestLvl = (int)(money * 0.10);
		score += interestGainedLatestLvl;
		money = (int)(money * 1.10);
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
		
			// Punish bad players who let creatures through.
		score = score - dmg * (difficulty + 1);
		if (score < 0) {
			score = 0;
		}
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

	public float getTimeBetweenLevels() {
		return timeBetweenLevels;
	}

	public void setTimeUntilNextLevel(float f) {
		this.timeUntilNextLevel = f;
	}

	public float getTimeUntilNextLevel() {
		return timeUntilNextLevel;
	}

	public int getHealth() {
		return health;
	}

	public int getDifficulty() {
		return difficulty;
	}
	
	public int getScore() {

			// Difference in difficulty should reflect the final score.
		float d = 0;
		if (difficulty == 0)
			d = (float) 0.8;
		else if (difficulty == 1)
			d = (float) 1.0;
		else if (difficulty == 2)
			d = (float) 1.2;

		// Times 10 just so we get a little cooler highscore.
		return this.score * 10 * (int) d;
	}

}
