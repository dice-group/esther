package org.dice_group.path;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.fact_check.path.scorer.NPMICalculator;
import org.dice_group.fact_check.path.scorer.OccurrencesCounter;
import org.dice_group.graph_search.algorithms.PropertySearch;
import org.dice_group.graph_search.algorithms.SearchAlgorithm;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.models.EmbeddingModel;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyHelper;
import org.dice_group.util.LogUtils;
import org.dice_group.util.PrintToFileUtils;
import org.dice_group.util.QueryExecutioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PathCreator.class);

	private EmbeddingModel emodel;

	private Dictionary dictionary;

	private Matrix matrix;

	private int k;
	
	private QueryExecutioner sparqlExec;
	
	private static final int MAX_THREADS = 16;

	public PathCreator(Dictionary dictionary, EmbeddingModel emodel, Matrix matrix, int k, QueryExecutioner sparqlExec) {
		this.dictionary = dictionary;
		this.emodel = emodel;
		this.matrix = matrix;
		this.k = k;
		this.sparqlExec = sparqlExec;
	}

	/**
	 * Calculates the meta-paths for a single edge
	 * 
	 * @param edge
	 * @return the paths found
	 */
	public Set<Property> getMetaPaths(String edge, int l, boolean isLoopsAllowed, int targetID) {
		Map<String, Integer> rel2ID = dictionary.getRelations2ID();

		// get corresponding ids for embeddings
		int edgeID = rel2ID.getOrDefault(edge, -1);

		// if any isn't found, return. It's an error
		if (edgeID < 0) {
			throw new IllegalArgumentException("Could not find the given predicate's embedding");
		}

		// search for property combos
		SearchAlgorithm propertyCombos = new PropertySearch(matrix, emodel);
		Set<Property> propertyPaths = propertyCombos.findPaths(edgeID, k, l, isLoopsAllowed, targetID);

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
		ConcurrentMap<String, Set<Property>> metaPaths = new ConcurrentHashMap<String, Set<Property>>();
		StringBuffer buffer = new StringBuffer();
		edges.parallelStream().forEach(edge -> {
			Set<Property> propertyPaths = getMetaPaths(edge, l, isLoopsAllowed, rel2ID.get(edge));
			
			// calculate pnpmi for each meta-path
			org.apache.jena.rdf.model.Property edgeProp = ResourceFactory.createProperty(edge);
			
			OccurrencesCounter c = new OccurrencesCounter(edgeProp, sparqlExec, false);
			ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
			for (Property path : propertyPaths) {
				if (c.getSubjectTypes().isEmpty() || c.getObjectTypes().isEmpty()) {
					path.setPathNPMI(0);
				}
				Runnable task = new NPMICalculator(path, c, dictionary.getId2Relations());
				executor.execute(task);
			}
			executor.shutdown();
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			metaPaths.put(edge, propertyPaths);
			LOGGER.info("Processed meta-path: " + edge);
			buffer.append("\nPredicate:").append("\t").append(edge);
			addPrintableMetaPaths(buffer, propertyPaths);
		});
		print(buffer.toString());
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

	public void addPrintableMetaPaths(StringBuffer builder, Set<Property> propertyPaths) {
		List<Property> result = propertyPaths.stream()
				.sorted(Comparator.comparingDouble(Property::getPathLength).thenComparing(Property::getPathCost))
				.collect(Collectors.toList());

		builder.append("\nLength").append("\t").append("A* score").append("\t").append("Path(s)");
		for (Property path : result) {
			builder.append("\n").append(path.getPathLength()).append("\t").append(path.getPathCost()).append("\t")
					.append(PropertyHelper.translate2DirectedIRI(path, dictionary.getId2Relations()));
		}
	}

	public void print(String string) {
		int index = 1;
		String fileName = index + "_metapaths_" + k + ".txt";
		File file = new File(fileName);
		while (file.exists()) {
			String newFileName = ++index + "_metapaths_" + k + ".txt";
			file = new File(newFileName);
		}
		LogUtils.printTextToLog(file.getAbsolutePath());
		PrintToFileUtils.printStringToFile(string, file);
	}

}
