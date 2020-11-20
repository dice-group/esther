package org.dice_group.path;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.graph_search.algorithms.PropertySearch;
import org.dice_group.graph_search.algorithms.SearchAlgorithm;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.models.EmbeddingModel;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyHelper;
import org.dice_group.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PathCreator.class);

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
	 * 
	 * @param edge
	 * @return the paths found
	 */
	public Set<Property> getMetaPaths(String edge, int l, boolean isLoopsAllowed) {
		Map<String, Integer> rel2ID = dictionary.getRelations2ID();

		// get corresponding ids for embeddings
		int edgeID = rel2ID.getOrDefault(edge, -1);

		// if any isn't found, return. It's an error
		if (edgeID < 0) {
			throw new IllegalArgumentException("Could not find the given predicate's embedding");
		}

		// search for property combos
		SearchAlgorithm propertyCombos = new PropertySearch(matrix, emodel);
		Set<Property> propertyPaths = propertyCombos.findPaths(edgeID, k, l, isLoopsAllowed);

		return propertyPaths;
	}

	/**
	 * Calculates the meta-paths for multiple edges
	 * 
	 * @param edges
	 * @return the edge to paths found map
	 */
	public Map<String, Set<Property>> getMultipleMetaPaths(Set<String> edges, int l, boolean isLoopsAllowed) {
		Map<String, Integer> rel2ID = dictionary.getRelations2ID();
		Map<String, Set<Property>> metaPaths = new HashMap<String, Set<Property>>();
		int count = 0;
		StringBuilder builder = new StringBuilder();
		for (String edge : edges) {
			//String edge = "http://rdf.freebase.com/ns/people.person.nationality";
			emodel.updateTargetEdge(rel2ID.get(edge));
			Set<Property> propertyPaths = getMetaPaths(edge, l, isLoopsAllowed);
			metaPaths.put(edge, propertyPaths);
			LOGGER.info(++count + "/" + edges.size() + " meta-paths processed.");

			// prepare meta-paths to be printed
			builder.append("\nPredicate:").append("\t").append(edge);
			addPrintableMetaPaths(builder, propertyPaths);
		}
		print(builder);
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

	public void addPrintableMetaPaths(StringBuilder builder, Set<Property> propertyPaths) {
		List<Property> result = propertyPaths.stream()
				.sorted(Comparator.comparingDouble(Property::getPathLength).thenComparing(Property::getPathCost))
				.collect(Collectors.toList());

		builder.append("\nLength").append("\t").append("A* score").append("\t").append("Path(s)");
		for (Property path : result) {
			builder.append("\n").append(path.getPathLength()).append("\t").append(path.getPathCost()).append("\t")
					.append(PropertyHelper.translate2DirectedIRI(path, dictionary.getId2Relations()));
		}
	}

	public void print(StringBuilder builder) {
		int index = 1;
		String fileName = index + "_metapaths_" + k + ".txt";
		File file = new File(fileName);
		while (file.exists()) {
			String newFileName = ++index + "_metapaths_" + k + ".txt";
			file = new File(newFileName);
		}
		
		LogUtils.printTextToLog(file.getAbsolutePath());
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(builder.toString());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
