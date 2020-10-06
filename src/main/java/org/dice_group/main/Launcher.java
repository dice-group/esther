package org.dice_group.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.embeddings.dictionary.DictionaryHelper;
import org.dice_group.fact_check.path.scorer.NPMICalculator;
import org.dice_group.fact_check.path.scorer.OccurrencesCounter;
import org.dice_group.fact_check.path.scorer.ResultWriter;
import org.dice_group.graph_search.modes.IrrelevantDR;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.graph_search.modes.NotDisjointDR;
import org.dice_group.graph_search.modes.StrictDR;
import org.dice_group.graph_search.modes.SubsumedDR;
import org.dice_group.models.DensE;
import org.dice_group.models.EmbeddingModel;
import org.dice_group.models.RotatE;
import org.dice_group.models.TransE;
import org.dice_group.path.Graph;
import org.dice_group.path.PathCreator;
import org.dice_group.path.property.Property;
import org.dice_group.util.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) {
		Map<String, String> mapArgs = parseArguments(args);

		// folder where the dictionary is saved
		String folderPath = mapArgs.get("-data");

		String serviceRequestURL = "http://localhost:8890/sparql";

		// get matrix type from user
		String type = mapArgs.get("-matrix");

		// testing data
		LOGGER.info("Reading data from file: " + folderPath);
		Model testData = ModelFactory.createDefaultModel();
		testData.read(folderPath + "/ns_test.nt");

		// read dictionary from file
		DictionaryHelper dictHelper = new DictionaryHelper();
		dictHelper.readDictionary(folderPath);
		Dictionary dict = dictHelper.getDictionary();

		// read embeddings from file
		// double[][] entities = CSVParser.readCSVFile(folderPath +
		// "/entity_embedding.csv", dict.getEntCount(), 2);
		double[][] relations = CSVParser.readCSVFile(folderPath + "/relation_embedding.csv", dict.getRelCount(), 1);

		// read and create ontology with OWL inference //TODO move this to endpoint?
//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
//		ontModel.read(folderPath + "/fb_ontology.nt");

		Set<Graph> graphs = new HashSet<Graph>();

		// create edge adjacency matrix
		Matrix matrix;
		if (type.equals("ND")) {
			matrix = new NotDisjointDR(serviceRequestURL, dict);
		} else if (type.equals("S")) {
			matrix = new StrictDR(serviceRequestURL, dict);
		} else if (type.equals("SS")) {
			matrix = new SubsumedDR(serviceRequestURL, dict);
		} else {
			matrix = new IrrelevantDR(serviceRequestURL, dict);
		}
		LOGGER.info("Matrix type: " + matrix.toString());
		matrix.populateMatrix();

		// check each statement
		StmtIterator checkStmts = testData.listStatements();
		int i = 1;
		while (checkStmts.hasNext()) {
			Statement curStmt = checkStmts.next();

			// TODO this can be moved outside the loop
			String embModel = mapArgs.get("-model");
			EmbeddingModel eModel;
			if (embModel.equals("R")) {
				eModel = new RotatE(new double[1][1], relations,
						dict.getRelations2ID().get(curStmt.getPredicate().toString()));
			} else if (embModel.equals("D")) {
				eModel = new DensE(new double[1][1], relations,
						dict.getRelations2ID().get(curStmt.getPredicate().toString()));
			} else {
				eModel = new TransE(new double[1][1], relations,
						dict.getRelations2ID().get(curStmt.getPredicate().toString()));
			}
			// LOGGER.info("Embedding model: " + eModel.toString());

			// find property combinations in graph
			PathCreator creator = new PathCreator(dict, eModel);
			Set<Property> p = creator.findPropertyPaths(curStmt, matrix);

			/*
			 * remove if property path not present in graph p.removeIf(curProp ->
			 * !SparqlHelper.askModel(serviceRequestURL, SparqlHelper.getAskQuery(
			 * PropertyHelper.getPropertyPath(curProp, dict.getId2Relations()),
			 * curStmt.getSubject().toString(), curStmt.getObject().toString())));
			 */
			OccurrencesCounter c = new OccurrencesCounter(curStmt, serviceRequestURL, false);
			c.count();

			// calculate npmi for each path found
			for (Property path : p) {
				if (c.getSubjectTypes().isEmpty() || c.getObjectTypes().isEmpty()) {
					graphs.add(new Graph(p, 0, curStmt));
					continue;
				}
				NPMICalculator cal = new NPMICalculator(path, dict.getId2Relations(), c);
				try {
					cal.calculatePMIScore();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			// no paths found
			if (p.isEmpty()) {
				graphs.add(new Graph(p, 0, curStmt));
				continue;
			}
			double[] scores = new double[p.size()];
			scores = p.stream().mapToDouble(k -> k.getFinalScore()).toArray();

			// aggregate the path's scores into one triple veracity score
			// TODO move to another class like in copaal
			double score = 1.0;
			for (int s = scores.length - 1; s >= 0; s--) {
				if (scores[s] > 1)
					continue;
				score = score * (1 - scores[s]);
			}
			double f = 1 - score;
			LOGGER.info(++i + "/" + testData.size() + " : " + f + " - " + curStmt.toString());
			graphs.add(new Graph(p, f, curStmt));
		}

		ResultWriter results = new ResultWriter();
		results.addResults(graphs);
		results.printToFile(matrix.toString() + "_results.nt");
	}

	/**
	 * 
	 * -matrix: type of matrix generation
	 * 
	 * -data: data folder path
	 * 
	 * -model embedding model used
	 * 
	 * @param args
	 * @return
	 */
	private static Map<String, String> parseArguments(String[] args) {
		Map<String, String> mapArgs = new HashMap<String, String>();
		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				String param = args[i];
				if ((i + 1) < args.length) {
					String value = args[i + 1];
					if (param.equalsIgnoreCase("-emb")) {
						mapArgs.put("-emb", value);
					} else if (param.equalsIgnoreCase("-matrix")) {
						mapArgs.put("-matrix", value);
					} else if (param.equalsIgnoreCase("-data")) {
						mapArgs.put("-data", value);
					} else if (param.equalsIgnoreCase("-model")) {
						mapArgs.put("-model", value);
					}
				}
			}
		}
		return mapArgs;
	}

}
