package com.crackedcarrot;

public class TrackerData {
	public Creature[] mCreatures;
	
	public TrackerData(int maxNbrCreatures) {
		mCreatures = new Creature[maxNbrCreatures];
	}
	
	// Remove creature from this grid position
	public void remove(Creature creep,int gridIndex) {
		mCreatures[gridIndex] = null;
	}

	// Add creature to first space in list at this gridspot
	public int add(Creature creep) {
		for (int i=0; i <= mCreatures.length; i++) {
			if (mCreatures[i] == null) {
				mCreatures[i] = creep;
				return i;
			}
		}
		return 0;
	}
}