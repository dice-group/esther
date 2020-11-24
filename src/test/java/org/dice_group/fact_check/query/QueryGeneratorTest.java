package org.dice_group.fact_check.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyBackPointer;
import org.dice_group.path.property.PropertyHelper;
import org.dice_group.util.SparqlHelper;
import org.junit.Test;

public class QueryGeneratorTest {

	private static final String DUMMY_NS = "http://www.example.com/";

	@Test
	public void testQueryGeneration() {
		Resource givenPredicate = ResourceFactory.createResource(DUMMY_NS+6);
		Model model = ModelFactory.createDefaultModel();
		model.add(ResourceFactory.createStatement(givenPredicate, RDFS.domain, ResourceFactory.createResource(DUMMY_NS+"type0")));
		model.add(ResourceFactory.createStatement(givenPredicate, RDFS.range, ResourceFactory.createResource(DUMMY_NS+"type1")));
		
		Set<Node> subjectTypes = SparqlHelper.getDomainFromModel(model, givenPredicate.getURI());
		Set<Node> objectTypes = SparqlHelper.getRangeFromModel(model, givenPredicate.getURI());
		
		Set<Property> paths = new HashSet<Property>();
		
		Property singleProp = new Property(0); // 0
		Property property2 = new Property(1, new PropertyBackPointer(singleProp), false); // 0 - 1
		Property property3 = new Property(2, new PropertyBackPointer(property2), false);  // 0 - 1 - 2
		Property property4 = new Property(3, new PropertyBackPointer(property3), false);  // 0 - 1 - 2 - 3
		Property property5 = new Property(4+6, new PropertyBackPointer(property4), true); // 0 - 1 - 2 - 3 - 4⁻¹(10)
		Property property6 = new Property(5, new PropertyBackPointer(property5), false);  // 0 - 1 - 2 - 3 - 4⁻¹(10) - 5

		paths.add(singleProp);
		paths.add(property2);
		paths.add(property3);
		paths.add(property4);
		paths.add(property5);
		paths.add(property6);

		Map<Integer, String> id2rel = new HashMap<Integer, String>();
		for (int i = 0; i < 6; i++) {
			id2rel.put(i, DUMMY_NS + i);
		}

		QueryGenerator generator = new CountApproximatingQueryGenerator();
		for (Property path : paths) {
			String query = generator.createCountQuery(path.getProperties(), PropertyHelper.translate2IRIArray(path, id2rel), subjectTypes, objectTypes);
			System.out.println("#############");
			System.out.println(path);
			System.out.println(query);
		}

	}
	
	

}
