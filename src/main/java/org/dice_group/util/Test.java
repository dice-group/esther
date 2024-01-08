package org.dice_group.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

public class Test {

	public static void main(String[] args) {
		
		// Read filtered_kg.ttl and keep only the unique properties
		Set<String> predicates = getPredicatesFromModel("/home/aams/Desktop/esther/ESTHER_Files/FAVEL/dbpedia_final.nt");
		System.out.println("Number of predicates found: "+predicates.size());
		
		// Read the embedding file, keep only the properties in the set above and LHS (guess it doesn't make a difference if lhs or rhs?)
		StringBuilder predicateEmbeddings = new StringBuilder();
		int count = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader("/home/aams/Downloads/relation_types_parameters_dbp21-03_transe_dot.tsv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	System.out.println("Processed line "+ ++count);
                String[] elements = line.split("\t");
                String URI = elements[0]; // ResourceFactory.createProperty(elements[0]).getLocalName();
                String hs = elements[1];
                
                // skip if rhs or predicate not interesting 
                if(hs.equals("rhs") || !predicates.contains(URI)) {
                	continue;
                }
                
                String emb = String.join("\t", Arrays.copyOfRange(elements, 5, elements.length));
                predicateEmbeddings.append(URI).append("\t").append(emb).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		// write to file 
		System.out.println("Number of predicates found: "+predicates.size());
		System.out.println("Writing to file");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("/home/aams/Desktop/esther/ESTHER_Files/FAVEL/TransE/relation_embedding.txt"))) {
            writer.write(predicateEmbeddings.toString());
            System.out.println("Content written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		

	}
	
	public static Set<String> getPredicatesFromModel(String filePath){
		System.out.println("Reading: "+filePath);
		Model model = ModelFactory.createDefaultModel();
		model.read(filePath);
		Set<Statement> stmts = model.listStatements().toSet();
		return stmts.stream() .map(stmt -> stmt.getPredicate().getURI())
				.collect(Collectors.toSet());
	}

}
