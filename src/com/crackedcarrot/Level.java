package com.crackedcarrot;

// Class contains level information. Number of creatures creature type etc.
public class Level {
	public Creature cr;
	public int nrCr;
	
	//Constructor
	public Level(Creature cr, int nrCr) {
		this.cr = cr;
		this.nrCr = nrCr;
	}
}