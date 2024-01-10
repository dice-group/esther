package org.dice_group.embeddings.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.dice_group.datasets.Dataset;
import org.dice_group.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helper class to help the reading, creation and saving of the relation dictionary.
 * 
 * @author Ana Silva
 *
 */
public class DictionaryHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryHelper.class);

	/**
	 * Reads the dictionary from a given file path
	 * 
	 * @param dataFolderPath
	 */
	public static Dictionary readDictionary(String folder, Dataset dataset) {		
		String filePath = folder + Constants.REL_EMB_FILE;
		String dictPath = folder + Constants.REL_DICT_FILE;
		
		// create dictionary and save it to file if the file doesn't exist
		if(!new File(dictPath).isFile()) {
			LOGGER.info("Dictionary not found, creating it at "+dictPath);
			Dictionary dict = DictionaryHelper.createDictionaryFromTSV(filePath);
			DictionaryHelper.saveDict2File(dict, dictPath);
			return dict;
		}
		
		// read id2relations from file, infer relations2id
		Map<Integer, String> id2relations = dataset.readMap(dictPath);
		Map<String, Integer> rel2ID = id2relations.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		return new Dictionary(rel2ID, id2relations);

	}

	/**
	 * Reads a single model from path and creates the corresponding dictionary.
	 *
	 * 
	 * @param filePath
	 * @return 
	 */
	public static Dictionary createDictionaryFromModel(String filePath) {
		Dictionary dictionary = new Dictionary();
		Model model = ModelFactory.createDefaultModel();
		model.read(filePath, "TTL");
		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			Statement curStmt = stmtIterator.next();
			String predicate = curStmt.getPredicate().toString();
			dictionary.addRelation(predicate);				
		}
		return dictionary;
	}

	/**
	 * Creates the relations dictionary from a TSV
	 * 
	 * @param filePath
	 * @return 
	 */
	public static Dictionary createDictionaryFromTSV(String filePath) {
		LOGGER.info("Creating dictionary from TSV in: " + filePath);
		Dictionary dictionary = new Dictionary();
		try (Reader reader = new FileReader(filePath); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
			for (CSVRecord csvRecord : csvParser) {
				String uri = csvRecord.get(0).substring(1,csvRecord.get(0).length()-1);
				dictionary.addRelation(uri);
			}
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}

		return dictionary;
		
	}

	/**
	 * Writes the dictionary to file
	 * 
	 * @param dictionary
	 * @param dataFolderPath
	 */
	public static void saveDict2File(Dictionary dict, String path) {
		writeMapToFile(dict.getId2Relations(), path);
	}

	/**
	 * Writes a map to a file as a TSV
	 * 
	 * @param dictionary
	 * @param savedFile
	 */
	public static void writeMapToFile(Map<?, ?> dictionary, String savedFile) {
		StringBuilder builder = new StringBuilder();
		dictionary.forEach((k, v) -> {
			builder.append(k).append("\t").append(v.toString()).append("\n");
		});

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(savedFile))) {
			bw.append(builder);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads an entire folder contents to a single model
	 * 
	 * @param dataFolderPath
	 * @return
	 */
	public static Model readDatasetFolder(String dataFolderPath) {
		Model model = ModelFactory.createDefaultModel();
		File folder = new File(dataFolderPath);
		if (folder != null && folder.isDirectory() && folder.listFiles().length > 0) {
			for (String fileName : folder.list()) {
				model.read(dataFolderPath + "/" + fileName);
			}
		}
		return model;
	}

}
