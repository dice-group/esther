package org.dice_group.path;

import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.dice_group.graph_search.algorithms.PropertySearch;
import org.dice_group.graph_search.algorithms.SearchAlgorithm;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.models.EmbeddingModel;
import org.dice_group.path.property.Property;
import org.dice_group.util.SparqlHelper;

public class PathCreator {
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(PathCreator.class);

	private Graph graph;
	private EmbeddingModel emodel;

	public PathCreator(Graph graph, EmbeddingModel emodel) {
		this.graph = graph;
		this.emodel = emodel;
	}

	/**
	 * 
	 * @param source      source node
	 * @param edge
	 * @param destination goal node
	 * @return
	 */
	public Set<Property> findPropertyPaths(Statement stmt, Matrix matrix, Model model) {
		Map<String, Integer> rel2ID = graph.getDictionary().getRelations2ID();

		String source = stmt.getSubject().toString();
		String edge = stmt.getPredicate().toString();
		String destination = stmt.getObject().toString();

		// get corresponding ids for embeddings
		int edgeID = rel2ID.getOrDefault(edge, -1);

		// if any isn't found, return. It's an error
		if (edgeID < 0) {
			throw new IllegalArgumentException("Could not find the given resources' embeddings");
		}

		// search for property combos
		SearchAlgorithm propertyCombos = new PropertySearch(matrix, emodel.getScorer());
		Set<Property> propertyPaths = propertyCombos.findPaths(edgeID, emodel.getRelations());

		// remove if not present in model
		//propertyPaths.removeIf(k -> SparqlHelper.askModel(model, SparqlHelper.getAskQuery(k.toString(), source, destination)));

		return propertyPaths;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}
}
