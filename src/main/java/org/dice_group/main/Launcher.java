package org.dice_group.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.embeddings.dictionary.DictionaryHelper;
import org.dice_group.fact_check.path.scorer.NPMICalculator;
import org.dice_group.fact_check.path.scorer.OccurrencesCounter;
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
import org.dice_group.util.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) {
		Map<String, String> mapArgs = parseArguments(args);

		// embeddings file path
		String modelFilePath = mapArgs.get("-emb");

		// folder where the model and dictionary are saved
		String folderPath = mapArgs.get("-data");

		// testing data
		String subject = "www.example.com:a";
		String predicate = "www.example.com:h";
		String object = "www.example.com:e";

		Statement fact = ResourceFactory.createStatement(ResourceFactory.createResource(subject),
				ResourceFactory.createProperty(predicate), ResourceFactory.createResource(object));

		// read model - TODO change this to a sparql endpoint instead
		Model model = ModelFactory.createDefaultModel();
		model.read(folderPath + "/train.txt", "TTL");

		// read dictionary from file
		DictionaryHelper dictHelper = new DictionaryHelper();
		dictHelper.readDictionary(folderPath);
		Dictionary dict = dictHelper.getDictionary();

		// read embeddings from file
		double[][] entities = CSVParser.readCSVFile(modelFilePath + "/entity_embedding.csv", dict.getEntCount(), 2);
		double[][] relations = CSVParser.readCSVFile(modelFilePath + "/relation_embedding.csv", dict.getRelCount(), 1);

		// read and create ontology with OWL inference
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
		ontModel.read(folderPath+"/ontology.owl");

		// get matrix type from user
		String type = mapArgs.get("-matrix");
		Matrix matrix;
		if (type.equals("ND")) {
			matrix = new NotDisjointDR(ontModel, dict);
		} else if (type.equals("S")) {
			matrix = new StrictDR(ontModel, dict);
		} else if (type.equals("SS")) {
			matrix = new SubsumedDR(ontModel, dict);
		} else {
			matrix = new IrrelevantDR(ontModel, dict);
		}
		LOGGER.info("Matrix type: "+matrix.toString());
		
		String embModel = mapArgs.get("-model");
		EmbeddingModel eModel;
		if(embModel.equals("R")) {
			eModel = new RotatE(entities, relations, dict.getRelations2ID().get(predicate));
		} else if (embModel.equals("D")) {
			eModel = new DensE(entities, relations, dict.getRelations2ID().get(predicate));
		} else {
			eModel = new TransE(entities, relations, dict.getRelations2ID().get(predicate));
		}
		LOGGER.info("Embedding model: " + eModel.toString());
		
		// find property combinations in graph
		Graph graph = new Graph(model, dict);
		PathCreator creator = new PathCreator(graph, eModel);
		Set<Property> p = creator.findPropertyPaths(fact, matrix, model); 
		
		// remove if property path not present
		// p.removeIf(k -> SparqlHelper.askModel(model, SparqlHelper.getAskQuery(k.toString(), subject, object)));
		
		Set<NPMICalculator> calcSet = new HashSet<NPMICalculator>();
		String serviceRequestURL = "";
		for(Property path: p) {
			NPMICalculator c = new NPMICalculator(path, dict.getId2Relations(), new OccurrencesCounter(fact, serviceRequestURL), false);
			try {
				c.calculatePMIScore();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			calcSet.add(c);
		}
		
		
		
	}

	/**
	 * -emb: embeddings full file path
	 * 
	 * -matrix: type of matrix generation
	 * 
	 * -data: data folder path
	 * 
	 * -model embedding model used
	 * 
	 * @param args
	 * @return
	 */
	private static Map<String, String> parseArguments(String[] args) {
		Map<String, String> mapArgs = new HashMap<String, String>();
		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				String param = args[i];
				if ((i + 1) < args.length) {
					String value = args[i + 1];
					if (param.equalsIgnoreCase("-emb")) {
						mapArgs.put("-emb", value);
					} else if (param.equalsIgnoreCase("-matrix")) {
						mapArgs.put("-matrix", value);
					} else if (param.equalsIgnoreCase("-data")) {
						mapArgs.put("-data", value);
					} else if (param.equalsIgnoreCase("-model")) {
						mapArgs.put("-model", value);
					}
				}
			}
		}
		return mapArgs;
	}

}
