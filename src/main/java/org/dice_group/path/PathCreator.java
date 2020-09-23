package org.dice_group.path;

import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Statement;
import org.dice_group.graph_search.ComplexL1;
import org.dice_group.graph_search.Distance;
import org.dice_group.graph_search.algorithms.AStarSearch;
import org.dice_group.graph_search.algorithms.SearchAlgorithm;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertySearch;

public class PathCreator {
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(PathCreator.class);

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

	/**
	 * 
	 * @param source      source node
	 * @param edge
	 * @param destination goal node
	 * @return
	 */
	public Set<Node> findOtherPaths(Statement stmt, Matrix matrix) {
		Map<String, Integer> entities2ID = graph.getDictionary().getEntities2ID();
		Map<String, Integer> rel2ID = graph.getDictionary().getRelations2ID();

		String source = stmt.getSubject().toString();
		String edge = stmt.getPredicate().toString();
		String destination = stmt.getObject().toString();

		// get corresponding ids for embeddings, not graph
		int sourceID = entities2ID.getOrDefault(source, -1);
		int destID = entities2ID.getOrDefault(destination, -1);
		int edgeID = rel2ID.getOrDefault(edge, -1);

		// if any isn't found, return. It's an error
		if (sourceID < 0 || destID < 0 || edgeID < 0) {
			throw new IllegalArgumentException("Could not find the given resources' embeddings");
		}

		// search for property combos
		SearchAlgorithm propertyCombos = new PropertySearch(matrix);
		Set<?> propertyPaths = propertyCombos.findPaths(graph, sourceID, edgeID, destID, relations, null);
		
		// search for paths based on the property combos
		AStarSearch search = new AStarSearch(matrix);
		Distance scorer = new ComplexL1(relations[edgeID]);
		return search.findPaths(graph, sourceID, edgeID, destID, relations, scorer);

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
