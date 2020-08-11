package org.dice_group.main;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;

public class Launcher {

	public static void main(String[] args) {
		
		// data to be checked on
		Resource subject = ResourceFactory.createResource("http://dbpedia.org/resource/Anarchism");
		Property predicate = RDFS.seeAlso;
		RDFNode object = ResourceFactory.createResource("http://dbpedia.org/resource/France");
		
		// 
		
		
	}

}
