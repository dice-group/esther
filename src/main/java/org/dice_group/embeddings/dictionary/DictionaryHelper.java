package org.dice_group.embeddings.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.dice_group.util.Constants;

public class DictionaryHelper {

	private Dictionary dictionary;
	
	private final static String FB_NS = "http://rdf.freebase.com/ns/";

	public DictionaryHelper() {
		this.dictionary = new Dictionary();
	}

	/**
	 * Reads the dictionary from a given file path
	 * 
	 * @param dataFolderPath
	 */
	public Dictionary readDictionary(String dataFolderPath) {

		// read id2entities and id2relations from file
		Map<Integer, String> id2entities = readMap(dataFolderPath + Constants.ENT_DICT_FILE);
		Map<Integer, String> id2relations = readMap(dataFolderPath + Constants.REL_DICT_FILE);

		// compute ent2id and rel2id
		Map<String, Integer> ent2ID = id2entities.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		Map<String, Integer> rel2ID = id2relations.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		return new Dictionary(ent2ID, rel2ID, id2entities, id2relations);

	}

	/**
	 * Reads a single model from path and creates the corresponding dictionary
	 * 
	 * @param filePath
	 * @return 
	 */
	public Dictionary createDictionary(String filePath) {
		Model model = ModelFactory.createDefaultModel();
		model.read(filePath+ "/train.txt", "TTL");
		return createDictionary(model);
	}

	/**
	 * Creates the entities and relations dictionary for a given model
	 * 
	 * @param model
	 * @return 
	 */
	public Dictionary createDictionary(Model model) {
		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			Statement curStmt = stmtIterator.next();

			String subject = curStmt.getSubject().toString();
			String predicate = curStmt.getPredicate().toString();
			RDFNode object = curStmt.getObject();

			if (object.isResource()) {
				dictionary.addEntity(subject);
				dictionary.addRelation(predicate);
				dictionary.addEntity(object.asResource().toString());
			} 				
		}
		return dictionary;
	}

	/**
	 * Reads a map from file, with key value pairs separated by \t
	 * 
	 * @param filePath
	 * @return
	 */
	public Map<Integer, String> readMap(String filePath) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			lines.filter(line -> line.contains("\t")) // TODO this change is only applicable for freebase dataset! change accordingly
					.forEach(line -> map.putIfAbsent(Integer.valueOf(line.split("\t")[0]), FB_NS+line.split("\t")[1].replace("/", ".").substring(1)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Writes the dictionary to file
	 * 
	 * @param dictionary
	 * @param dataFolderPath
	 */
	public void saveDict2File(String dataFolderPath) {
		printMap(dictionary.getId2Entities(), dataFolderPath + Constants.ENT_DICT_FILE);
		printMap(dictionary.getId2Relations(), dataFolderPath + Constants.REL_DICT_FILE);
	}

	/**
	 * Writes a map to a file: each key value pair in a new line separated by \t
	 * 
	 * @param dictionary
	 * @param savedFile
	 */
	public void printMap(Map<?, ?> dictionary, String savedFile) {
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
	public Model readDatasetFolder(String dataFolderPath) {
		Model model = ModelFactory.createDefaultModel();
		File folder = new File(dataFolderPath);
		if (folder != null && folder.isDirectory() && folder.listFiles().length > 0) {
			for (String fileName : folder.list()) {
				model.read(dataFolderPath + "/" + fileName);
			}
		}
		return model;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

}
