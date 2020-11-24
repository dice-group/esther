package org.dice_group.fact_check.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyBackPointer;
import org.dice_group.path.property.PropertyHelper;
import org.dice_group.util.QueryExecutioner;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryGeneratorTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryGeneratorTest.class);
	private static final String DUMMY_NS = "http://www.example.com/";

	@Test
	public void testQueryGeneration() {
		org.apache.jena.rdf.model.Property givenPredicate = ResourceFactory.createProperty(DUMMY_NS+6);
		
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

		QueryExecutioner exec = new QueryExecutioner("http://lemming.cs.uni-paderborn.de:8890/sparql");
		QueryGenerator generator = new CountApproximatingQueryGenerator(exec);
		for (Property path : paths) {
			String query = generator.createCountQuery(path.getProperties(), PropertyHelper.translate2IRIArray(path, id2rel), givenPredicate);
			LOGGER.info("#############");
			LOGGER.info(path.toString());
			LOGGER.info(query);
			// TODO 
			exec.selectDoubleVar(query, "?sum");
		}
		
		QueryGenerator pairGenerator = new PairCountingQueryGenerator();
		for (Property path : paths) {
			String query = pairGenerator.createCountQuery(path.getProperties(), PropertyHelper.translate2IRIArray(path, id2rel), givenPredicate);
			LOGGER.info("#############");
			LOGGER.info(path.toString());
			LOGGER.info(query);
			exec.selectDoubleVar(query, "?sum");
		}
	}
	
	

}
