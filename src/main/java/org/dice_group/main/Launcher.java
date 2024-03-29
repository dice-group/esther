package org.dice_group.main;

import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.dice_group.datasets.Dataset;
import org.dice_group.datasets.Freebase;
import org.dice_group.datasets.Wordnet;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.embeddings.dictionary.DictionaryHelper;
import org.dice_group.fact_check.FactChecker;
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
		Dictionary dict = DictionaryHelper.readDictionary(pArgs.folderPath, getDataset(pArgs.dataset));
		
		// create d/r edge adjacency matrix
		LOGGER.info("Creating edge adjacency matrix from d/r");
		QueryExecutioner sparqlExec = new QueryExecutioner(pArgs.serviceRequestURL);
		Matrix matrix = getMatrixType(pArgs.type, sparqlExec, dict);
		long startTime = System.currentTimeMillis();
		matrix.populateMatrix();

		// preprocess the meta-paths for all properties
		LOGGER.info("Preprocessing meta-paths");
		EmbeddingModel eModel = getModel(pArgs.eModel, pArgs.folderPath);
		PathCreator creator = new PathCreator(dict, eModel, matrix, pArgs.k, sparqlExec, pArgs.savePath);
		FactChecker checker = new FactChecker(pArgs.facts, sparqlExec);
		Map<String, Set<Property>> metaPaths = creator.getMultipleMetaPaths(checker.getExistingPropertiesFromFile(), pArgs.max_length, pArgs.isLoopsAllowed);
		LogUtils.printTextToLog("Meta-paths generated in " + (System.currentTimeMillis()-startTime)/1000);

		// check facts and save to file
		LOGGER.info("Applying meta-paths to KG");
		long startFactTime = System.currentTimeMillis();
		checker.checkFactsParallel(metaPaths, dict.getId2Relations(), pArgs.savePath);
		LogUtils.printTextToLog("Facts checked in " + (System.currentTimeMillis()-startFactTime)/1000);
		
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
			double[][] relations = CSVUtils.readCSVFile(folderPath + Constants.REL_EMB_FILE, CSVFormat.DEFAULT);
			eModel = new RotatE(relations);
			break;
		case Constants.DENSE_STRING:
			double[][] relW = CSVUtils.readCSVFile(folderPath + Constants.REL_W_FILE, CSVFormat.DEFAULT);
			double[][] relX = CSVUtils.readCSVFile(folderPath + Constants.REL_X_FILE, CSVFormat.DEFAULT);
			double[][] relY = CSVUtils.readCSVFile(folderPath + Constants.REL_Y_FILE, CSVFormat.DEFAULT);
			double[][] relZ = CSVUtils.readCSVFile(folderPath + Constants.REL_Z_FILE, CSVFormat.DEFAULT);
			eModel = new DensE(relW, relX, relY, relZ);
			break;
		case Constants.TRANSE_STRING:
			double[][] relations2 = CSVUtils.readCSVFile(folderPath + Constants.REL_EMB_FILE, CSVFormat.DEFAULT);
			eModel = new TransE(relations2);
			break;
		default:
			double[][] relations3 = CSVUtils.readCSVFile(folderPath + Constants.REL_EMB_FILE, CSVFormat.TDF);
			eModel = new TransE(relations3);
			break;
		}
		return eModel;
	}
}
