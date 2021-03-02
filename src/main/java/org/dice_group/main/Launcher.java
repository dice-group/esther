package org.dice_group.main;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.dice_group.datasets.Dataset;
import org.dice_group.datasets.Freebase;
import org.dice_group.datasets.Wordnet;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.embeddings.dictionary.DictionaryHelper;
import org.dice_group.fact_check.FactChecker;
import org.dice_group.fact_check.path.scorer.ResultWriter;
import org.dice_group.graph_search.modes.IrrelevantDR;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.graph_search.modes.NDSubsumedDR;
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
import org.dice_group.util.LogUtils;
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
		Dictionary dict = dictHelper.readDictionary(pArgs.folderPath, getDataset(pArgs.dataset));

		// create d/r edge adjacency matrix
		LOGGER.info("Creating edge adjacency matrix from d/r");
		QueryExecutioner sparqlExec = new QueryExecutioner(pArgs.serviceRequestURL);
		Matrix matrix = getMatrixType(pArgs.type, sparqlExec, dict);
		Instant startTime = Instant.now();
		matrix.populateMatrix();

		// preprocess the meta-paths for all properties
		LOGGER.info("Preprocessing meta-paths");
		EmbeddingModel eModel = getModel(pArgs.eModel, pArgs.folderPath);
		PathCreator creator = new PathCreator(dict, eModel, matrix, pArgs.k);
		Map<String, Set<Property>> metaPaths = creator.getMultipleMetaPaths(dict.getRelations2ID().keySet(), pArgs.max_length, pArgs.isLoopsAllowed);
		LogUtils.printTextToLog("Meta-paths generated in " + Duration.between(Instant.now(), startTime).toSeconds());

		// check each fact
		LOGGER.info("Applying meta-paths to KG");
		FactChecker checker = new FactChecker(pArgs.folderPath + pArgs.testData, sparqlExec);
		Instant startFactTime = Instant.now();
		Set<Graph> graphs = checker.checkFacts(metaPaths, dict.getId2Relations());
		checker = new FactChecker(pArgs.folderPath + pArgs.negTestData, sparqlExec);
		Set<Graph> negGraphs = checker.checkFacts(metaPaths, dict.getId2Relations());
		LogUtils.printTextToLog("Facts checked in " + Duration.between(Instant.now(), startFactTime).toSeconds());

		// write results
		ResultWriter results = new ResultWriter(0, graphs);
		ResultWriter negResults = new ResultWriter(graphs.size(), negGraphs);
		results.printToFile(pArgs.folderPath + pArgs.savePath);
		negResults.appendToFile(pArgs.folderPath + pArgs.savePath);
		results.printPathsToFile(pArgs.folderPath + "paths_" + pArgs.savePath,
				results.getPaths(dict.getId2Relations()).append(negResults.getPaths(dict.getId2Relations())));
	}

	private static Dataset getDataset(String type) {
		Dataset dataset;
		switch (type) {
		case "WN":
			dataset = new Wordnet();
			break;
		case "FB":
			dataset = new Freebase();
			break;
		default:
			dataset = new Dataset();
			break;
		}
		return dataset;
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
		case "NDS":
			matrix = new NDSubsumedDR(sparqlExec, dict);
			break;
		default:
			matrix = new IrrelevantDR(sparqlExec, dict);
			break;
		}
		return matrix;
	}

	private static EmbeddingModel getModel(String model, String folderPath) {
		EmbeddingModel eModel = null;
		switch (model) {
		case Constants.ROTATE_STRING:
			double[][] relations = CSVUtils.readCSVFile(folderPath + Constants.REL_EMB_FILE);
			eModel = new RotatE(relations);
			break;
		case Constants.DENSE_STRING:
			double[][] relW = CSVUtils.readCSVFile(folderPath + Constants.REL_W_FILE);
			double[][] relX = CSVUtils.readCSVFile(folderPath + Constants.REL_X_FILE);
			double[][] relY = CSVUtils.readCSVFile(folderPath + Constants.REL_Y_FILE);
			double[][] relZ = CSVUtils.readCSVFile(folderPath + Constants.REL_Z_FILE);
			eModel = new DensE(relW, relX, relY, relZ);
			break;
		default:
			double[][] relations2 = CSVUtils.readCSVFile(folderPath + Constants.REL_EMB_FILE);
			eModel = new TransE(relations2);
			break;
		}
		return eModel;
	}
}
