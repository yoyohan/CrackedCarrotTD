package com.crackedcarrot;

public class TrackerData {
	public Creature first;
	public Creature last;

	
	public TrackerData() {
		first = new Creature(0, 0, null, null, null, null, 0, null);
		last = new Creature(0, 0, null, null, null, null, 0, null);
		first.nextCreature = last;
		last.previousCreauture = first;
	}
	
	public void addCreatureToList(Creature creature) {
		creature.nextCreature = first.nextCreature;
		creature.previousCreauture = first;
		Creature tmp = first.nextCreature;
		first.nextCreature = creature;
		tmp.previousCreauture = creature;
	}
	public void removeCreatureFromList(Creature creature) {
		Creature tmp = creature.previousCreauture;
		creature.previousCreauture.nextCreature = creature.nextCreature;
		creature.nextCreature.previousCreauture = tmp;
	}

}
