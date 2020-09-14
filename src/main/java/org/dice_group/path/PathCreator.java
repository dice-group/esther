package org.dice_group.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dice_group.graph_search.algorithms.AStarSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PathCreator.class);

	private Graph graph;
	private double[][] entities;
	private double[][] relations;

	public PathCreator() {
		graph = new Graph();
	}

	public PathCreator(Graph graph, double[][] entities, double[][] relations) {
		this.graph = graph;
		this.entities = entities;
		this.relations = relations;
	}

	public Set<Node> findOtherPaths(String source, String edge, String destination) {
		Map<String, Integer> entities2ID = graph.getDictionary().getEntities2ID();
		Map<String, Integer> rel2ID = graph.getDictionary().getRelations2ID();

		// get corresponding ids
		int sourceID = entities2ID.getOrDefault(source, -1);
		int destID = entities2ID.getOrDefault(destination, -1);
		int edgeID = rel2ID.getOrDefault(edge, -1);

		// if any isn't found, return. It's an error
		if (sourceID < 0 || destID < 0 || edgeID < 0)
			throw new IllegalArgumentException("Could not find the given resources' embedding");

		// search for paths
		AStarSearch search = new AStarSearch();
		return search.findPaths(graph, sourceID, edgeID, destID, relations);
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public double[][] getEntities() {
		return entities;
	}

	public void setEntities(double[][] entities) {
		this.entities = entities;
	}

	public double[][] getRelations() {
		return relations;
	}

	public void setRelations(double[][] relations) {
		this.relations = relations;
	}
	
	

}