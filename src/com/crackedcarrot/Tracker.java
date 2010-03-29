package com.crackedcarrot;

public class Tracker {
	private TrackerData[] allSpots;
	private Scaler mScaler;
	private int gridWidth;
	private Creature[] inRangeCreeps;
	
	public Tracker(int nbrOfSpots, int MaxNbrOfCreatures, Scaler mScaler) {
		this.mScaler = mScaler;
		gridWidth = mScaler.getGridWidth();
		allSpots = new TrackerData[nbrOfSpots];
		inRangeCreeps = new Creature[MaxNbrOfCreatures];
		for (int x = 0; x <= nbrOfSpots; x++) {
			allSpots[x] = new TrackerData(MaxNbrOfCreatures);
		}
	}

	public int UpdatePosition(Creature creep, int lastGridLocation, int gridIndex) {
		Coords tmp = mScaler.getGridXandY((int)creep.x,(int)creep.y);
		int gridLocation = tmp.x + gridWidth*tmp.y;
		if (lastGridLocation != gridLocation) {
			allSpots[lastGridLocation].remove(creep,gridIndex);
			creep.gridIndex = allSpots[gridLocation].add(creep);
			return gridLocation;
		}
		return lastGridLocation;
	}
	
	public Creature[] getCreaturesInRange(int towerSpotInAllSpots, int size) {
		for (int i=0; i < ;i++)
		
			size + towerSpotInAllSpots/gridWidth
		
		return inRangeCreeps;
	}

}
