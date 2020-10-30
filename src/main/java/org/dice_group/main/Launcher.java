package org.dice_group.main;

import java.util.Map;
import java.util.Set;

import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.embeddings.dictionary.DictionaryHelper;
import org.dice_group.fact_check.FactChecker;
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
import org.dice_group.util.Constants;
import org.dice_group.util.QueryExecutioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

public class Launcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) {
		ProgramArgs pArgs = new ProgramArgs();
		JCommander.newBuilder().addObject(pArgs).build().parse(args);
		pArgs.printArgs();

		// read dictionary from file
		LOGGER.info("Reading data from file");
		DictionaryHelper dictHelper = new DictionaryHelper();
		Dictionary dict = dictHelper.readDictionary(pArgs.folderPath);

		// read embeddings from file
		double[][] entities = null;
		if (pArgs.eModel.equals(Constants.DENSE_STRING))
			entities = CSVUtils.readCSVFile(pArgs.folderPath + Constants.ENT_EMB_FILE);
		double[][] relations = CSVUtils.readCSVFile(pArgs.folderPath + Constants.REL_EMB_FILE);

		// create d/r edge adjacency matrix
		LOGGER.info("Creating edge adjacency matrix from d/r");
		QueryExecutioner sparqlExec = new QueryExecutioner(pArgs.serviceRequestURL);
		Matrix matrix = getMatrixType(pArgs.type, sparqlExec, dict);
		matrix.populateMatrix();

		// preprocess the meta-paths for all existing properties
		LOGGER.info("Preprocessing meta-paths");
		EmbeddingModel eModel = getModel(pArgs.eModel, relations, entities);
		PathCreator creator = new PathCreator(dict, eModel, matrix, pArgs.k);
		Map<String, Set<Property>> metaPaths = creator.getMultipleMetaPaths(dict.getRelations2ID().keySet(), pArgs.max_length, false);
		
		// check each fact
		LOGGER.info("Applying meta-paths to KG");
		FactChecker checker = new FactChecker(pArgs.folderPath + pArgs.testData, sparqlExec);
		Set<Graph> graphs = checker.checkFacts(metaPaths, dict.getId2Relations());

		// write results
		ResultWriter results = new ResultWriter(pArgs.initID, graphs);
		results.printToFile(pArgs.folderPath + "pos_" + pArgs.savePath);
		results.printPathsToFile(pArgs.folderPath + "paths_" + pArgs.savePath, results.getPaths(dict.getId2Relations()));
	}

	private static Matrix getMatrixType(String type, QueryExecutioner sparqlExec, Dictionary dict) {
		Matrix matrix;
		switch (type) {
		case "ND":
			matrix = new NotDisjointDR(sparqlExec, dict);
			break;
		case "S":
			matrix = new StrictDR(sparqlExec, dict);
			break;
		case "SS":
			matrix = new SubsumedDR(sparqlExec, dict);
			break;
		default:
			matrix = new IrrelevantDR(sparqlExec, dict);
			break;
		}
		return matrix;
	}

	private static EmbeddingModel getModel(String model, double[][] relations, double[][] entities) {
		EmbeddingModel eModel;
		switch (model) {
		case Constants.ROTATE_STRING:
			eModel = new RotatE(null, relations);
			break;
		case Constants.DENSE_STRING:
			eModel = new DensE(entities, relations);
			break;
		default:
			eModel = new TransE(null, relations);
			break;
		}
		return eModel;
	}
}
