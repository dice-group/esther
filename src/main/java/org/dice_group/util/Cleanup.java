package org.dice_group.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

public class Cleanup {
	private static String STMT = "http://swc2017.aksw.org/task2/dataset/award_";
	private static Property TRUTH_VALUE = ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue");
	
	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();

		try (BufferedReader reader = new BufferedReader(new FileReader("/home/aams/Desktop/BPDP/output_train.nt"))) {
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				String[] elements = line.split("\t");
				Resource subject = ResourceFactory.createResource(elements[0].substring(1, elements[0].length() - 1));
				Property property = ResourceFactory.createProperty(elements[1].substring(1, elements[1].length() - 1));
				RDFNode object = ResourceFactory.createResource(elements[2].substring(1, elements[2].length() - 1));
				Resource stmt = ResourceFactory.createResource(STMT+count);
				model.add(stmt, RDF.type, RDF.Statement);
				model.add(stmt, RDF.subject, subject);
				model.add(stmt, RDF.predicate, property.asResource());
				model.add(stmt, RDF.object, object);
				model.addLiteral(stmt, TRUTH_VALUE, Double.valueOf(elements[3]).doubleValue()); 
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (OutputStream outputStream = new FileOutputStream(
				"/home/aams/Desktop/esther/ESTHER_Files/FAVEL/bpdp_train_reified.nt")) {
			model.write(outputStream, "NT");
			System.out.println("Model written to file successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}

//		Model model = ModelFactory.createDefaultModel();
//		
//		try (BufferedReader reader = new BufferedReader(new FileReader("/home/aams/Desktop/BPDP/output_test.nt"))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] elements = line.split("\t");
//                Resource subject = ResourceFactory.createResource(elements[0].substring(1, elements[0].length()-1));
//                Property property = ResourceFactory.createProperty(elements[1].substring(1, elements[1].length()-1));
//                RDFNode object = ResourceFactory.createResource(elements[2].substring(1, elements[2].length()-1));
//                model.add(subject, property, object);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//		
//		try (OutputStream outputStream = new FileOutputStream("/home/aams/Desktop/esther/ESTHER_Files/FAVEL/bpdp_test.nt")) {
//            model.write(outputStream, "NT");
//            System.out.println("Model written to file successfully.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

	}

}
