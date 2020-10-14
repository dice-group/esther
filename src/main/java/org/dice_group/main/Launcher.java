package org.dice_group.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
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
import org.dice_group.util.CSVUtils;
import org.dice_group.util.QueryExecutioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

public class Launcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) {
		ProgramArgs pArgs = new ProgramArgs();
		JCommander.newBuilder().addObject(pArgs).build().parse(args);

		LOGGER.info("Reading data from file: " + pArgs.folderPath);
		LOGGER.info("Embeddings Model: " + pArgs.eModel);
		LOGGER.info("k: " + pArgs.k);
		
		QueryExecutioner sparqlExec = new QueryExecutioner(pArgs.serviceRequestURL);

		// read testing data - facts to check
		Model testData = ModelFactory.createDefaultModel();
		testData.read(pArgs.folderPath + "/reduced.nt");//ns_test mixed_false_triples

		// read dictionary from file
		DictionaryHelper dictHelper = new DictionaryHelper();
		dictHelper.readDictionary(pArgs.folderPath);
		Dictionary dict = dictHelper.getDictionary();

		// read embeddings from file
		// double[][] entities = CSVUtils.readCSVFile(folderPath+"/entity_embedding.csv");
		double[][] relations = CSVUtils.readCSVFile(pArgs.folderPath + "/relation_embedding.csv");

		Set<Graph> graphs = new HashSet<Graph>();

		// create d/r edge adjacency matrix
		Matrix matrix;
		if (pArgs.type.equals("ND")) {
			matrix = new NotDisjointDR(sparqlExec, dict);
		} else if (pArgs.type.equals("S")) {
			matrix = new StrictDR(sparqlExec, dict);
		} else if (pArgs.type.equals("SS")) {
			matrix = new SubsumedDR(sparqlExec, dict);
		} else {
			matrix = new IrrelevantDR(sparqlExec, dict);
		}
		LOGGER.info("Matrix type: " + matrix.toString());
		matrix.populateMatrix();

		// get embedding model
		EmbeddingModel eModel;
		if (pArgs.eModel.equals("R")) {
			eModel = new RotatE(null, relations);
		} else if (pArgs.eModel.equals("D")) {
			eModel = new DensE(null, relations);
		} else {
			eModel = new TransE(null, relations);
		}
		
		

		// check each statement
		StmtIterator checkStmts = testData.listStatements();
		int i = 1;
		while (checkStmts.hasNext()) {
			Statement curStmt = checkStmts.next();

			// set score function's target edge as current
			eModel.updateScorer(dict.getRelations2ID().get(curStmt.getPredicate().toString()));

			// find property combinations in graph
			PathCreator creator = new PathCreator(dict, eModel);
			Set<Property> p = creator.findPropertyPaths(curStmt, matrix, pArgs.k);

			// no paths found
			if (p.isEmpty()) {
				graphs.add(new Graph(p, 0, curStmt));
				continue;
			}

			/*
			 * remove if property path not present in graph 
			 */ 
//			p.removeIf(curProp ->
//			  !SparqlHelper.askModel(pArgs.serviceRequestURL, SparqlHelper.getAskQuery(
//			  PropertyHelper.getPropertyPath(curProp, dict.getId2Relations()),
//			  curStmt.getSubject().toString(), curStmt.getObject().toString())));
			 

			OccurrencesCounter c = new OccurrencesCounter(curStmt, sparqlExec, false);
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

			double[] scores = new double[p.size()];
			scores = p.stream().mapToDouble(s -> s.getFinalScore()).toArray();

			// aggregate the path's scores into one triple veracity score
			double score = 1.0;
			for (int s = scores.length - 1; s >= 0; s--) {
				if (scores[s] > 1)
					continue;
				score = score * (1 - scores[s]);
			}
			double f = 1 - score;
			LOGGER.info(i++ + "/" + testData.size() + " : " + f + " - " + curStmt.toString());
			graphs.add(new Graph(p, f, curStmt));
		}
		
		StringBuilder builder = new StringBuilder();
		for(Graph g : graphs) {
			builder.append(g.getPrintableResults(dict.getId2Relations()));
		}
		
		File file = new File("paths.txt");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
		    writer.write(builder.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// write results in gerbil's format
		ResultWriter results = new ResultWriter(0);
		results.addResults(graphs);
		results.printToFile(pArgs.folderPath + "/Results/" + matrix.toString() + results.getCurID()+"reduced_pos_results.nt");
	}
}

