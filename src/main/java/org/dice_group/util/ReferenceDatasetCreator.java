package org.dice_group.util;

import java.io.FileWriter;
import java.io.IOException;

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
		String fileName = args[0];
		String fileName2 = args[1];
		
		RDFNode truthValue = ResourceFactory.createTypedLiteral(String.valueOf(1.0), XSDDatatype.XSDdouble);
		RDFNode truthValueFalse = ResourceFactory.createTypedLiteral(String.valueOf(0.0), XSDDatatype.XSDdouble);

		Model model = ModelFactory.createDefaultModel();
		model.read(fileName);
		
		Model falseFacts = ModelFactory.createDefaultModel();
		falseFacts.read(fileName2);
		
		Model reference = ModelFactory.createDefaultModel();
		addFacts(reference, model.listSubjects(), truthValue);
		addFacts(reference, falseFacts.listSubjects(), truthValueFalse);
		
		try (FileWriter out = new FileWriter("reference_dataset.nt")) {
			reference.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void addFacts(Model reference, ResIterator subjects, RDFNode truthValue) {
		while (subjects.hasNext()) {
			Resource cur = subjects.next();
			reference.add(
					ResourceFactory.createStatement(cur, ResourceFactory.createProperty(TRUTH_VALUE_STR), truthValue));
		}
	}

}
