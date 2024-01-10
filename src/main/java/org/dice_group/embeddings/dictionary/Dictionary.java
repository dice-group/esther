package org.dice_group.embeddings.dictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Relation Dictionary
 * 
 * @author Ana Silva
 *
 */
public class Dictionary {

	// Relation String to an Integer ID
	private Map<String, Integer> relations2ID;

	// Relation ID to the String relation
	private Map<Integer, String> id2Relations;

	/**
	 * Empty constructor.
	 */
	public Dictionary() {
		relations2ID = new HashMap<String, Integer>();
		id2Relations = new HashMap<Integer, String>();
	}

	/**
	 * Constructor.
	 * 
	 * @param relations2id Relation Strings to Integer IDs
	 * @param id2Relations Relation IDs to String relations
	 */
	public Dictionary(Map<String, Integer> relations2id, Map<Integer, String> id2Relations) {
		this.relations2ID = relations2id;
		this.id2Relations = id2Relations;
	}
	
	/**
	 * Adds relation to the maps
	 * 
	 * @param relToBeAdded
	 */
	public void addRelation(String relToBeAdded) {
		int relCount = id2Relations.size();
		if(!relations2ID.containsKey(relToBeAdded)) {
			relations2ID.put(relToBeAdded, relCount);
			id2Relations.put(relCount, relToBeAdded);
		}
	}

	public Map<String, Integer> getRelations2ID() {
		return relations2ID;
	}

	public void setRelations2ID(Map<String, Integer> relations2id) {
		relations2ID = relations2id;
	}

	public Map<Integer, String> getId2Relations() {
		return id2Relations;
	}

	public void setId2Relations(Map<Integer, String> id2Relations) {
		this.id2Relations = id2Relations;
	}

	/**
	 * 
	 * @return Number of relations in the dictionary
	 */
	public int getRelCount() {
		return id2Relations.size();
	}

}
