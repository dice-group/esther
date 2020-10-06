package org.dice_group.graph_search;

import org.junit.Test;

public class PropertyComboTest {
	//private static final Logger LOGGER = LoggerFactory.getLogger(PropertyComboTest.class);

	//private final String PREFIX_NS = "www.example.com";

	@Test
	public void testPropertyPaths() {
//		OntModel ontModel = buildTestOntology();
//		Model model = ModelFactory.createDefaultModel();
//		model.read("graph2.n3");

//		DictionaryHelper help = new DictionaryHelper();
//		Dictionary dict = help.createDictionary(model);
//		
//		Statement fact = ResourceFactory.createStatement(ResourceFactory.createResource(PREFIX_NS+":a"),
//				ResourceFactory.createProperty(PREFIX_NS+":z"), 
//				ResourceFactory.createResource(PREFIX_NS+":o"));
//
//		String requestURL = "";
//		Matrix matrix = new StrictDR(requestURL, dict);
//
//		Path resourceDirectory = Paths.get("src", "test", "resources");
//		String absolutePath = resourceDirectory.toFile().getAbsolutePath();
//		
//		double[][] relations = CSVParser.readCSVFile(absolutePath + "/rotate/relation_embedding.csv", 10, 2);
//		double[][] entities = CSVParser.readCSVFile(absolutePath + "/rotate/entity_embedding.csv", 10, 1);
//
//		String predicate = PREFIX_NS + ":z";
//		//int pID = dict.getRelations2ID().get(predicate);
//		
//		EmbeddingModel eModel = new RotatE(entities, relations, dict.getRelations2ID().get(predicate));
//
//		PathCreator creator = new PathCreator(dict, eModel);
//		Set<Property> p = creator.findPropertyPaths(fact, matrix); 
//		
//		LOGGER.info(p.toString());
	}

//	private OntModel buildTestOntology() {
//		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
//		OntProperty b = ontModel.createOntProperty(PREFIX_NS + ":b");
//		b.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
//		b.addRange(ResourceFactory.createResource(PREFIX_NS + ":c3"));
//
//		OntProperty d = ontModel.createOntProperty(PREFIX_NS + ":d");
//		d.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c3"));
//		d.addRange(ResourceFactory.createResource(PREFIX_NS + ":c4"));
//
//		OntProperty m = ontModel.createOntProperty(PREFIX_NS + ":m");
//		m.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c4"));
//		m.addRange(ResourceFactory.createResource(PREFIX_NS + ":c5"));
//
//		OntProperty g = ontModel.createOntProperty(PREFIX_NS + ":g");
//		g.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
//		g.addRange(ResourceFactory.createResource(PREFIX_NS + ":c3"));
//
//		OntProperty h = ontModel.createOntProperty(PREFIX_NS + ":h");
//		h.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c3"));
//		h.addRange(ResourceFactory.createResource(PREFIX_NS + ":c6"));
//
//		OntProperty l = ontModel.createOntProperty(PREFIX_NS + ":l");
//		l.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c4"));
//		l.addRange(ResourceFactory.createResource(PREFIX_NS + ":c6"));
//
//		OntProperty t = ontModel.createOntProperty(PREFIX_NS + ":t");
//		t.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
//		t.addRange(ResourceFactory.createResource(PREFIX_NS + ":c1"));
//
//		OntProperty z = ontModel.createOntProperty(PREFIX_NS + ":z");
//		z.addDomain(ResourceFactory.createResource(PREFIX_NS + ":c1"));
//		z.addRange(ResourceFactory.createResource(PREFIX_NS + ":c5"));
//
//		return ontModel;
//	}

}
