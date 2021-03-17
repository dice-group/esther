package org.dice_group.main;

import java.util.Date;

import org.dice_group.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

/**
 * Program arguments
 *
 */
public class ProgramArgs {
	@Parameter(names = { "--data", "-d" }, description = "Folder path", required = true)
	String folderPath;

	@Parameter(names = { "--topk",
			"-k" }, description = "The number of meta-paths we want to find. 100 is the default value.")
	int k = 100;

	@Parameter(names = { "--matrix",
			"-m" }, description = "The matrix type, it can be S (Strict), SS (Subsumed), ND (Intersecting), or I (Irrelevant). I is the default value.")
	String type = "I";

	@Parameter(names = { "--emb-model",
			"-e" }, description = "The embedding model used: RotatE, TransE or DensE. TransE is the default value.")
	String eModel = "TransE";

	@Parameter(names = { "--sparql-endpoint", "-se" }, description = "The sparql endpoint ", required = true)
	String serviceRequestURL;

	@Parameter(names = { "--save", "-s" }, description = "Desired result file name ")
	String savePath;

	@Parameter(names = { "--test", "-t" }, description = "Filename of the facts under folder path")
	String testData = "true_triples_750.nt";
	
	@Parameter(names = { "--neg", "-n" }, description = "Filename of the neg facts under folder path")
	String negTestData = "false_triples_750.nt";

	@Parameter(names = { "-id" }, description = "Initial ID for the result files")
	int initID = 0;

	@Parameter(names = { "-l" }, description = "Maximum Path length")
	int max_length = 3;
	
	@Parameter(names = { "-ds" }, description = "Dataset")
	String dataset = "";

	@Parameter(names = { "-loops" }, description = "Are loops allowed?")
	boolean isLoopsAllowed = false;

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgramArgs.class);

	public void printArgs() {
		StringBuilder builder = new StringBuilder();
		builder.append(new Date());
		builder.append("\nReading data from folder: ").append(folderPath);
		builder.append("\nDataset: ").append(dataset);
		builder.append("\nEmbeddings Model: new ").append(eModel);
		builder.append("\nk: ").append(k);
		builder.append("\nMax Path length: ").append(max_length);
		builder.append("\nMatrix type: ").append(type);
		builder.append("\nEndpoint: ").append(serviceRequestURL);
		builder.append("\nTest data: ").append(testData);
		builder.append("\nNeg test data: ").append(negTestData);
		builder.append("\nInit id: ").append(initID);
		builder.append("\nSaving to: ").append(savePath);
		builder.append("\nLoops allowed: ").append(isLoopsAllowed);
		builder.append("\n");
		LOGGER.info(builder.toString());
		LogUtils.printTextToLog(builder.toString());
	}
}
