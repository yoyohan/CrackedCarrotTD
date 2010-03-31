package com.crackedcarrot;

import java.util.Enumeration;
import java.util.Hashtable;

public class Tracker {
	private TrackerData[] allSpots;
	private int gridWidth;

	public Hashtable<Integer, Creature> inRangeCreeps;
	private float loadFactor = 0.5f;
	
	// Defines the constructor for the tracker. It requires the number of
	// gridspots on the map and the width of the map
	public Tracker(int gridWidth, int gridHeight, int maxNbrOfCreatures) {
		maxNbrOfCreatures = maxNbrOfCreatures*2 +1;
		
		int nbrOfSpots = (gridHeight) * (gridWidth);
		this.gridWidth = gridWidth;
		allSpots = new TrackerData[nbrOfSpots];
		inRangeCreeps = new Hashtable<Integer, Creature>(maxNbrOfCreatures,loadFactor);
		for (int x = 0; x < nbrOfSpots; x++) {
			allSpots[x] = new TrackerData(maxNbrOfCreatures,loadFactor);
		}
	}
	// When a creature dies or leaves the map it will update the tracker.
	public void removeCreature(Creature creep, int creepIndex, int lastGridLocation) {
		allSpots[lastGridLocation].remove(creep,creepIndex);
	}

	// When the game begins we have to add creature.
	public void addCreature(Creature creep, int creepIndex, int gridLocation) {
		allSpots[gridLocation].add(creep,creepIndex);
	}
		
	// When a creature enters a new gridspot it will update the tracker.
	public void UpdatePosition(Creature creep, int creepIndex, int lastGridLocation, int newGridLocation) {
		allSpots[lastGridLocation].remove(creep,creepIndex);
		allSpots[newGridLocation].add(creep,creepIndex);		
	}
	
	// A tower will ask the tracker for all creatures in close range to him
	public Enumeration<Creature> getCreaturesInRange(int l_col, int r_col, int u_row, int b_row) {
		inRangeCreeps.clear();
		for (int y = b_row; y <= u_row; y++) {
			for (int x = l_col; x <= r_col; x++) {
				int tmp = (y*gridWidth)+x;
				inRangeCreeps.putAll(allSpots[tmp].getAll());
			}
		}
		return inRangeCreeps.elements();
	}
}
