package com.crackedcarrot;

import java.util.Collection;
import java.util.Hashtable;

public class TrackerData {
	public Hashtable<Integer, Creature> mCreeps;
	
	public TrackerData(int maxNbrCreatures,float loadFactor) {
		mCreeps = new Hashtable<Integer, Creature>(maxNbrCreatures,loadFactor);
	}
	
	// Remove creature from this grid position
	public void remove(Creature creep,int creepIndex) {
		mCreeps.remove(creepIndex);
	}

	// Add creature to to this grid position
	public void add(Creature creep,int creepIndex) {
		mCreeps.put(creepIndex, creep);
	}
	
	// return all creatures in this grid position
	public Collection<Creature> getAll() {
		return mCreeps.values();
	}
}