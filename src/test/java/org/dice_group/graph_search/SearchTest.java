package org.dice_group.graph_search;

import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

public class SearchTest {

	@Test
	public void testSearch() {
		OntModel ontModel = ModelFactory.createOntologyModel();
		ontModel.read("/home/ana-silva/Work/esther/dbpedia_data/dbpedia_2016-10.owl");
		
		Set<OntProperty> properties = ontModel.listAllOntProperties().toSet();
		System.out.println(properties.size());
		System.out.println();
	}

}
