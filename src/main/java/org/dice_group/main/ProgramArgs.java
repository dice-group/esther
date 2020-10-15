package org.dice_group.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

public class ProgramArgs {
	@Parameter(names = { "--data", "-d" }, description = "Folder path", required = true)
	String folderPath;

	@Parameter(names = { "--topk", "-k" }, description = "The number of optimal paths we want to find. k=100 is the default value.")
	int k = 100;

	@Parameter(names = { "--matrix",
			"-m" }, description = "The matrix type, it can be S (Strict), SS (Subsumed), ND (Intersecting), or I (Irrelevant). I is the default value.")
	String type = "I";

	@Parameter(names = { "--emb-model", "-e" }, description = "The embedding model used: RotatE, TransE or DensE. TransE is the default value.")
	String eModel = "TransE";

	@Parameter(names = { "--sparql-endpoint", "-se" }, description = "The sparql endpoint ", required = true)
	String serviceRequestURL;
	
	@Parameter(names = { "--save", "-s" }, description = "Desired result file name ")
	String savePath;
	
	@Parameter(names = { "--test", "-t" }, description = "Filename of the facts under folder path")
	String testData = "ns_test.nt";
	
	@Parameter(names = { "-id"}, description = "Initial ID for the result files")
	int initID = 0;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgramArgs.class);
	
	public void printArgs() {
		LOGGER.info("Reading data from folder: " + folderPath);
		LOGGER.info("Embeddings Model: " + eModel);
		LOGGER.info("k: " + k);
		LOGGER.info("Matrix type: " + type);
		LOGGER.info("Endpoint: " + serviceRequestURL);
		LOGGER.info("Test data: " + testData);
		LOGGER.info("Init id: " + initID);
		LOGGER.info("Saving to: " + savePath);
	}
}
