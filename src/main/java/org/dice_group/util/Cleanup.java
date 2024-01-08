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

public class Cleanup {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		
		try (BufferedReader reader = new BufferedReader(new FileReader("/home/aams/Desktop/esther/ESTHER_Files/FAVEL/output-train_factbench.nt"))) {
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
		
		try (OutputStream outputStream = new FileOutputStream("/home/aams/Desktop/esther/ESTHER_Files/FAVEL/train_facts.nt")) {
            model.write(outputStream, "NT");
            System.out.println("Model written to file successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
