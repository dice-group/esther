package org.dice_group.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;

public class NTToTSV {
	public static void main(String[] args) throws IOException {
		Model model = ModelFactory.createDefaultModel();
		model.read("/home/aams/Desktop/esther/ESTHER_Files/FAVEL/dbpedia_final.nt");
		
		// read all the smaller train datasets, fix them, they are not actual NT-files
		model.add(fixNTFile("/home/aams/Desktop/BPDP/output_train.nt"));
		model.add(fixNTFile("/home/aams/Desktop/FactBench/output-train_factbench.nt"));
		
		// read the test datasets separately
		Model test = ModelFactory.createDefaultModel();
		test.add(fixNTFile("/home/aams/Desktop/BPDP/output_test.nt"));
		test.add(fixNTFile("/home/aams/Desktop/FactBench/output-test_factbench.nt"));
		
		
		// remove all test triples from the overall model
		System.out.println("Initial size: "+model.size());
		model.remove(test);
		System.out.println("Final size: "+model.size());
		
		// save
		try (FileWriter out = new FileWriter("/home/aams/Desktop/esther/ESTHER_Files/FAVEL/dbpedia_final_with_train.nt")) {
			model.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Model fixNTFile(String file) {
		Model model = ModelFactory.createDefaultModel();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(" ");
                Resource subject = ResourceFactory.createResource(elements[0].substring(1, elements[0].length()-1));
                Property property = ResourceFactory.createProperty(elements[1].substring(1, elements[1].length()-1));
                RDFNode object = ResourceFactory.createResource(elements[2].substring(1, elements[2].length()-1));
                model.add(subject, property, object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return model;
	}

	public static StmtIterator getStmts() {
		Model model = ModelFactory.createDefaultModel();
		model.read("/home/aams/Desktop/esther/ESTHER_Files/FAVEL/dbpedia_final.nt");
		return model.listStatements();
	}
}
