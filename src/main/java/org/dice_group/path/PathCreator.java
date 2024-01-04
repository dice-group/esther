package org.dice_group.path;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
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

	public PathCreator(Dictionary dictionary, EmbeddingModel emodel, Matrix matrix, int k,
			QueryExecutioner sparqlExec) {
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
	 * @throws InterruptedException 
	 */
	public Map<String, Set<Property>> getMultipleMetaPaths(Set<String> edges, int l, boolean isLoopsAllowed) {
		Map<String, Integer> rel2ID = dictionary.getRelations2ID();
		ConcurrentMap<String, Set<Property>> metaPaths = new ConcurrentHashMap<String, Set<Property>>();
		String endSignal = "stop";
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		Thread writingThread = createAndRunWritingThread(endSignal, "metapaths.txt", queue);

		edges.parallelStream().forEach(edge -> {
			LOGGER.info("Processing meta-path: " + edge);
			Set<Property> propertyPaths = getMetaPaths(edge, l, isLoopsAllowed, rel2ID.get(edge));
			
			// calculate pnpmi for each meta-path
			org.apache.jena.rdf.model.Property edgeProp = ResourceFactory.createProperty(edge);

			OccurrencesCounter c = new OccurrencesCounter(edgeProp, sparqlExec, false);
			for (Property path : propertyPaths) {
				if (c.getSubjectTypes().isEmpty() || c.getObjectTypes().isEmpty()) {
					path.setPathNPMI(0);
				}
				NPMICalculator task = new NPMICalculator(path, c, dictionary.getId2Relations());
				try {
					task.calculatePMIScore();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			metaPaths.put(edge, propertyPaths);
			LOGGER.info(metaPaths.size() + " - Processed meta-path: " + edge);
			StringBuilder builder = new StringBuilder();
			builder.append("\nPredicate:").append("\t").append(edge);
			addPrintableMetaPaths(builder, propertyPaths);
			try {
				queue.put(builder.toString());
			} catch (InterruptedException e) {
				// just skip it if something goes wrong
				return;
			}
		});
		
		// wait for the thread to finish
		try {
			queue.put(endSignal);
			writingThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return metaPaths;
	}

	/**
	 * Creates a thread on an infinite loop responsible for writing the results to
	 * file until an endSignal is received.
	 * 
	 * @param endSignal String to signal the thread to stop
	 * @return
	 */
	public Thread createAndRunWritingThread(String endSignal, String destinationPath, BlockingQueue<String> queue) {
		Thread consumerThread = new Thread(() -> {
			try (FileWriter fileWriter = new FileWriter(destinationPath, true)) {
				while (true) {
					String message = queue.take();
					if (message.equals(endSignal)) {
						break;
					}
					fileWriter.write(message + "\n");
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});
		consumerThread.start();
		return consumerThread;
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
