package org.dice_group.util;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class ReferenceDatasetCreator {

	private static final String TRUTH_VALUE_STR = "http://swc2017.aksw.org/hasTruthValue";

	public static void main(String[] args) {
		boolean isTrue = true;
		String fileName = args[0];
		
		RDFNode truthValue;
		double t = 0.0;
		if (isTrue) {
			t = 1.0;
		}
		truthValue = ResourceFactory.createTypedLiteral(String.valueOf(t), XSDDatatype.XSDdouble);

		Model model = ModelFactory.createDefaultModel();
		model.read(fileName);
		ResIterator subjects = model.listSubjects();
		
		Model reference = ModelFactory.createDefaultModel();
		while (subjects.hasNext()) {
			Resource cur = subjects.next();
			reference.add(
					ResourceFactory.createStatement(cur, ResourceFactory.createProperty(TRUTH_VALUE_STR), truthValue));
		}
	}

}
