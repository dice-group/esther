package org.dice_group.main;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.embeddings.dictionary.DictionaryHelper;
import org.dice_group.path.Graph;
import org.dice_group.path.PathCreator;
import org.dice_group.util.CSVParser;

public class Launcher {

	public static void main(String[] args) {
		// TODO need to put in the same workspace
		String rdfPath = "";
		String dictFilePath = "";
		String modelFilePath = "";

		// testing data 
		String subject = "www.example.com:a";
		String predicate = "www.example.com:h";
		String object = "www.example.com:e";
		
		// read model - TODO change this to a sparql endpoint instead 
		Model model = ModelFactory.createDefaultModel();
		model.read(rdfPath, "TTL");

		// read dictionary from file
		DictionaryHelper dictHelper = new DictionaryHelper();
		dictHelper.readDictionary(dictFilePath);
		Dictionary dict = dictHelper.getDictionary();

		// read embeddings from file
		int dim = 5; //TODO read number of dimensions from file
		CSVParser parser = new CSVParser();
		double[][] entities = parser.readCSVFile(modelFilePath + "/entity_embedding.csv", dict.getEntCount(), 2 * dim);
		double[][] relations = parser.readCSVFile(modelFilePath + "/relation_embedding.csv", dict.getRelCount(), dim);

		// find paths
		Graph graph = new Graph(model, dict);
		PathCreator creator = new PathCreator(graph, entities, relations);
		creator.findOtherPaths(subject, predicate, object); //TODO 
		
		//TODO still needs to read the ontology
		//TODO 
	}

}
