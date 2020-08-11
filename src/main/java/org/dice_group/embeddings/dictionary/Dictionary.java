package org.dice_group.embeddings.dictionary;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {

	private Map<String, Integer> entities2ID;
	private Map<String, Integer> relations2ID;

	private Map<Integer, String> id2Entities;
	private Map<Integer, String> id2Relations;

	private int entCount;
	private int relCount;

	public Dictionary() {
		entities2ID = new HashMap<String, Integer>();
		relations2ID = new HashMap<String, Integer>();

		id2Entities = new HashMap<Integer, String>();
		id2Relations = new HashMap<Integer, String>();
	}

	public Dictionary(Map<String, Integer> entities2id, Map<String, Integer> relations2id,
			Map<Integer, String> id2Entities, Map<Integer, String> id2Relations) {
		this.entities2ID = entities2id;
		this.relations2ID = relations2id;

		this.id2Entities = id2Entities;
		this.id2Relations = id2Relations;

		this.entCount = id2Entities.size();
		this.relCount = id2Relations.size();
	}
	
	public void addEntity(String entToBeAdded) {
		if(!entities2ID.containsKey(entToBeAdded)) {
			entities2ID.put(entToBeAdded, entCount);
			id2Entities.put(entCount, entToBeAdded);
			entCount++;
		}
	}
	
	public void addRelation(String relToBeAdded) {
		if(!relations2ID.containsKey(relToBeAdded)) {
			relations2ID.put(relToBeAdded, entCount);
			id2Relations.put(entCount, relToBeAdded);
			relCount++;
		}
	}

	public Map<String, Integer> getEntities2ID() {
		return entities2ID;
	}

	public void setEntities2ID(Map<String, Integer> entities2id) {
		entities2ID = entities2id;
	}

	public Map<String, Integer> getRelations2ID() {
		return relations2ID;
	}

	public void setRelations2ID(Map<String, Integer> relations2id) {
		relations2ID = relations2id;
	}

	public Map<Integer, String> getId2Entities() {
		return id2Entities;
	}

	public void setId2Entities(Map<Integer, String> id2Entities) {
		this.id2Entities = id2Entities;
	}

	public Map<Integer, String> getId2Relations() {
		return id2Relations;
	}

	public void setId2Relations(Map<Integer, String> id2Relations) {
		this.id2Relations = id2Relations;
	}

	public int getEntCount() {
		return entCount;
	}

	public void setEntCount(int entCount) {
		this.entCount = entCount;
	}

	public int getRelCount() {
		return relCount;
	}

	public void setRelCount(int relCount) {
		this.relCount = relCount;
	}

}
