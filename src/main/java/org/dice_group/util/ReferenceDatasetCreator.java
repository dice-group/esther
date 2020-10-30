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
		String fileName2 = "/media/ana-silva/Storage/models/freebase_transe/strict_final_neg_t20_fb.nt";
		String fileName = "/home/ana-silva/Work/esther/strict_final_pos_t20_fb.nt";
		
		RDFNode truthValue = ResourceFactory.createTypedLiteral(String.valueOf(1.0), XSDDatatype.XSDdouble);
		RDFNode truthValueFalse = ResourceFactory.createTypedLiteral(String.valueOf(-1.0), XSDDatatype.XSDdouble);

		// true facts
		Model model = ModelFactory.createDefaultModel();
		model.read(fileName);
		
		// false facts
		Model falseFacts = ModelFactory.createDefaultModel();
		falseFacts.read(fileName2);
		
		Model reference = ModelFactory.createDefaultModel();
		addFacts(reference, model.listSubjects(), truthValue);
		addFacts(reference, falseFacts.listSubjects(), truthValueFalse);
		
		try (FileWriter out = new FileWriter("/media/ana-silva/Storage/models/freebase_transe/Results/old/reference_dataset_750_1.nt")) {
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
