package org.dice_group.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataReductor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataReductor.class);

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		model.read(args[0], "TTL");
		
		OntModel ontModel = ModelFactory.createOntologyModel();
		ontModel.read(args[1]);
		
		Set<OntProperty> properties = ontModel.listAllOntProperties().toSet();
		Set<Resource> allowedProperties = new HashSet<Resource>();
		allowedProperties.add(RDF.type);
		
		for(OntProperty prop : properties) {
			if(prop.getDomain() != null && prop.getRange() != null) {
				allowedProperties.add(prop);
			}
		}
		
		LOGGER.info(model.size()+" previously");
		
		StmtIterator iter = model.listStatements();
		while(iter.hasNext()) {
			Statement curStmt = iter.next();
			if(!allowedProperties.contains(curStmt.getPredicate()))
				iter.remove();
		}
		
		LOGGER.info(model.size()+" after");
		

	}

}
