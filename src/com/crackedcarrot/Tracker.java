package com.crackedcarrot;

import java.util.Collection;

public class Tracker {
	private TrackerData[] allSpots;
	private int gridWidth;
	private Collection<Creature> inRangeCreeps;
	private int maxNbrOfCreatures = 41;
	private float loadFactor = 0.5f;
	
	
	// Defines the constructor for the tracker. It requires the number of
	// gridspots on the map and the width of the map
	public Tracker(int nbrOfSpots, int gridWidth) {
		this.gridWidth = gridWidth;
		allSpots = new TrackerData[nbrOfSpots];
		for (int x = 0; x <= nbrOfSpots; x++) {
			allSpots[x] = new TrackerData(maxNbrOfCreatures,loadFactor);
		}
	}
	
	// When a creature enters a new gridspot it will update the tracker.
	public void UpdatePosition(Creature creep, int creepIndex, int lastGridLocation, int newGridLocation) {
		allSpots[lastGridLocation].remove(creep,creepIndex);
		allSpots[newGridLocation].add(creep,creepIndex);		
	}
	
	// A tower will ask the tracker for all creatures in close range to him
	public Creature[] getCreaturesInRange(int l_col, int r_col, int u_row, int b_row) {
		for (int y = b_row; y <= u_row; y++) {
			for (int x = l_col; x <= r_col; x++) {
				int tmp = y*gridWidth+x;
				inRangeCreeps.addAll(allSpots[tmp].getAll());
			}
		}
		return (Creature[]) inRangeCreeps.toArray();
	}
}
