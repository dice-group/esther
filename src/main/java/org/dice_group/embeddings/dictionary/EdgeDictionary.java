package org.dice_group.embeddings.dictionary;

import java.util.HashMap;
import java.util.Map;

import grph.DefaultIntSet;

public class EdgeDictionary {
	
	private Map<Integer, DefaultIntSet> prop2edges;
	private Map<Integer, Integer> edge2Prop;
	
	public EdgeDictionary() {
		prop2edges = new HashMap<Integer, DefaultIntSet>();
		edge2Prop = new HashMap<Integer, Integer>();
	}

	public Map<Integer, DefaultIntSet> getProp2edges() {
		return prop2edges;
	}

	public void setProp2edges(Map<Integer, DefaultIntSet> prop2edges) {
		this.prop2edges = prop2edges;
	}

	public Map<Integer, Integer> getEdges2Prop() {
		return edge2Prop;
	}

	public void setEdges2Prop(Map<Integer, Integer> edges2Prop) {
		this.edge2Prop = edges2Prop;
	}
	
	public void addEdge(int edge, Integer prop) {
		prop2edges.putIfAbsent(prop, new DefaultIntSet(0));
		prop2edges.get(prop).add(edge);
		edge2Prop.put(edge, prop);
	}
	
	

}
