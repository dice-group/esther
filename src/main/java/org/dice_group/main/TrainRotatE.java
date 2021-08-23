package org.dice_group.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.dice_group.embeddings.dictionary.DictionaryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrainRotatE {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainRotatE.class);

	public static void main(String[] args) {
		// process arguments
		Map<String, String> mapArgs = parseArguments(args); // TODO add null checks inside the method
		
		String dataFolderPath = mapArgs.get("-data");
		String rotateProjectPath = mapArgs.get("-rotate");
		
		int negSampleSize = Integer.valueOf(mapArgs.get("-n"));
		int batchSize = Integer.valueOf(mapArgs.get("-b"));
		int dim = Integer.valueOf(mapArgs.get("-dim"));
		int maxSteps = Integer.valueOf(mapArgs.get("-steps"));
		int testSize = Integer.valueOf(mapArgs.get("-test"));
		
		double gamma = Double.valueOf(mapArgs.get("-g"));
		double alpha = Double.valueOf(mapArgs.get("-a"));
		double lr = Double.valueOf(mapArgs.get("-lr"));
		
		// create dictionary and write it
		LOGGER.info("Indexing entities and relations...");
		DictionaryHelper dictHelper = new DictionaryHelper();
		dictHelper.createDictionary(dataFolderPath);
		dictHelper.saveDict2File(dataFolderPath);
		
		//Dictionary dict = dictHelper.getDictionary();

		// run kge on given dataset
		File validFile = getValidModelPath(rotateProjectPath);
		if(!trainEmbeddings(rotateProjectPath, negSampleSize, batchSize, dataFolderPath, gamma, dim, maxSteps, testSize, lr,
				alpha, validFile))
			return;

		// get the embeddings as arrays
//		LOGGER.info("Parsing CSV results to multi-dimentional arrays.");
//		CSVParser parser = new CSVParser();
//		double[][] entities = parser.readCSVFile(validFile.getAbsolutePath() + "/entity_embedding.csv",
//				dict.getEntCount(), 2 * dim);
//		double[][] relations = parser.readCSVFile(validFile.getAbsolutePath() + "/relation_embedding.csv",
//				dict.getRelCount(), dim);

	}

	/**
	 * Returns a non-existing file name
	 * 
	 * @param rotateProjectPath
	 * @return
	 */
	public static File getValidModelPath(String rotateProjectPath) {
		String savedModelPath = rotateProjectPath + "models/RotatE";
		File file = new File(savedModelPath);
		for (int i = 0; file.exists(); i++) {
			file = new File(savedModelPath + i);
		}
		return file;
	}

	/**
	 * Runs RotatE implementation:
	 * https://github.com/DeepGraphLearning/KnowledgeGraphEmbedding
	 * 
	 * @param rotateProjectPath
	 * @param negSampleSize
	 * @param batchSize
	 * @param dataFolderPath
	 * @param gamma
	 * @param dim
	 * @param maxSteps
	 * @param testSize
	 * @param lr
	 * @param alpha
	 * @param file
	 */
	public static boolean trainEmbeddings(String rotateProjectPath, int negSampleSize, int batchSize,
			String dataFolderPath, double gamma, int dim, int maxSteps, int testSize, double lr, double alpha,
			File file) {
		try {
			LOGGER.info("Training RotatE embeddings."); // TODO: there might be a better way of doing this
			String cmd = "python3.6 -u " + rotateProjectPath + "codes/run.py --do_train --data_path " + dataFolderPath
					+ " --model RotatE -n " + negSampleSize + " -b " + batchSize + " -d " + dim + " -g " + gamma
					+ " -a " + alpha + " -adv -lr " + lr + " --max_steps " + maxSteps + " -save "
					+ file.getAbsolutePath() + " --test_batch_size " + testSize + " -de";
			LOGGER.info("Command: " + cmd);
			LOGGER.info("Saving model under: "+file.getAbsolutePath());
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd).inheritIO();
			Process p = pb.start();
			int exitVal = p.waitFor();

			if (exitVal != 0) {
				int len = p.getErrorStream().available();
				if (len > 0) {
					byte[] buf = new byte[len];
					p.getErrorStream().read(buf);
					LOGGER.error(new String(buf));
				}
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private static Map<String, String> parseArguments(String[] args) {
		Map<String, String> mapArgs = new HashMap<String, String>();
		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				String param = args[i];
				if ((i + 1) < args.length) {
					String value = args[i + 1];
					if (param.equalsIgnoreCase("-data")) {
						mapArgs.put("-data", value);
					} else if (param.equalsIgnoreCase("-rotate")) {
						mapArgs.put("-rotate", value);
					} else if (param.equalsIgnoreCase("-n")) {
						mapArgs.put("-n", value);
					} else if (param.equalsIgnoreCase("-b")) {
						mapArgs.put("-b", value);
					} else if (param.equalsIgnoreCase("-dim")) {
						mapArgs.put("-dim", value);
					} else if (param.equalsIgnoreCase("-g")) {
						mapArgs.put("-g", value);
					} else if (param.equalsIgnoreCase("-a")) {
						mapArgs.put("-a", value);
					} else if (param.equalsIgnoreCase("-lr")) {
						mapArgs.put("-lr", value);
					} else if (param.equalsIgnoreCase("-steps")) {
						mapArgs.put("-steps", value);
					} else if (param.equalsIgnoreCase("-test")) {
						mapArgs.put("-test", value);
					}
				}
			}
		}
		return mapArgs;
	}

//	Model model = ModelFactory.createDefaultModel();
//	model.read(args[0]);
//	Model cleanedUpModel = new GraphCleaner().cleanGraph(model);
//	LineGraph lineGraph = new LineGraph(cleanedUpModel);
//	lineGraph.getGraph(); 
}
