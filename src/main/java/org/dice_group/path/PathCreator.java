package org.dice_group.path;

import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Statement;
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

	public PathCreator(Dictionary dictionary, EmbeddingModel emodel) {
		this.dictionary = dictionary;
		this.emodel = emodel;
	}

	/**
	 * 
	 * @param k 
	 * @param source      source node
	 * @param edge
	 * @param destination goal node
	 * @return
	 */
	public Set<Property> findPropertyPaths(Statement stmt, Matrix matrix, int k) {
		Map<String, Integer> rel2ID = dictionary.getRelations2ID();

		String edge = stmt.getPredicate().toString();

		// get corresponding ids for embeddings
		int edgeID = rel2ID.getOrDefault(edge, -1);

		// if any isn't found, return. It's an error
		if (edgeID < 0) {
			throw new IllegalArgumentException("Could not find the given predicate's embedding");
		}

		// search for property combos
		SearchAlgorithm propertyCombos = new PropertySearch(matrix, emodel.getScorer());
		Set<Property> propertyPaths = propertyCombos.findPaths(edgeID, emodel.getRelations(),k);

		return propertyPaths;
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
