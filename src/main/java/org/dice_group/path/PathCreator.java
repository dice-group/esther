package org.dice_group.path;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.graph_search.algorithms.PropertySearch;
import org.dice_group.graph_search.algorithms.SearchAlgorithm;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.models.EmbeddingModel;
import org.dice_group.path.property.Property;

public class PathCreator {
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(PathCreator.class);

	private EmbeddingModel emodel;

	private Dictionary dictionary;

	private Matrix matrix;

	private int k;

	public PathCreator(Dictionary dictionary, EmbeddingModel emodel, Matrix matrix, int k) {
		this.dictionary = dictionary;
		this.emodel = emodel;
		this.matrix = matrix;
		this.k = k;
	}

	/**
	 * Calculates the meta-paths for a single edge
	 * @param edge
	 * @return the paths found
	 */
	public Set<Property> getMetaPaths(String edge) {
		Map<String, Integer> rel2ID = dictionary.getRelations2ID();

		// get corresponding ids for embeddings
		int edgeID = rel2ID.getOrDefault(edge, -1);

		// if any isn't found, return. It's an error
		if (edgeID < 0) {
			throw new IllegalArgumentException("Could not find the given predicate's embedding");
		}

		// search for property combos
		SearchAlgorithm propertyCombos = new PropertySearch(matrix, emodel.getScorer());
		Set<Property> propertyPaths = propertyCombos.findPaths(edgeID, emodel.getRelations(), k);

		return propertyPaths;
	}

	/**
	 * Calculates the meta-paths for multiple edges
	 * @param edges
	 * @return the edge to paths found map
	 */
	public Map<String, Set<Property>> getMultipleMetaPaths(Set<String> edges) {
		Map<String, Integer> rel2ID = dictionary.getRelations2ID();
		Map<String, Set<Property>> metaPaths = new HashMap<String, Set<Property>>();
		for (String edge : edges) {
			emodel.updateScorer(rel2ID.get(edge));
			Set<Property> propertyPaths = getMetaPaths(edge);
			metaPaths.put(edge, propertyPaths);
		}
		return metaPaths;
	}

	public EmbeddingModel getEmodel() {
		return emodel;
	}

	public void setEmodel(EmbeddingModel emodel) {
		this.emodel = emodel;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

}
