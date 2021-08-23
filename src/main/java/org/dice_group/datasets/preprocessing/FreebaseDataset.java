package org.dice_group.datasets.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;

public class FreebaseDataset {
	private final static String FB_NS = "http://rdf.freebase.com/ns/";

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		
		String filename = args[0];
		try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
			for (String line; (line = br.readLine()) != null;) {
				line = line.replace("/", ".");
				String[] temp = line.split("\\s+");
				if (temp.length < 3) {
					System.out.println(temp.length);
					continue;
				}
				Statement stmt = ResourceFactory.createStatement(
						ResourceFactory.createResource(FB_NS + temp[0].substring(1)),
						ResourceFactory.createProperty(FB_NS + temp[1].substring(1)),
						ResourceFactory.createResource(FB_NS + temp[2].substring(1)));
				
				model.add(stmt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		OntModel ont = ModelFactory.createOntologyModel();
		ont.read(args[1]);
		ExtendedIterator<OntProperty> props = ont.listOntProperties();
		List<Statement> stmts = new ArrayList<Statement>();
		while(props.hasNext()) {
			OntProperty curProp = props.next();
			
			Set<Statement> i = model.listStatements(null, curProp, (RDFNode)null).toSet();
			ExtendedIterator<? extends OntResource> domain = curProp.listDomain();
			for(Statement curStmt: i) {
				while(domain.hasNext()) {
					OntResource curDomain = domain.next();
					stmts.add(ResourceFactory.createStatement(curStmt.getSubject(), RDF.type, curDomain));
				}
			}
			
			ExtendedIterator<? extends OntResource> range = curProp.listRange();
			for(Statement curStmt: i) {
				while(range.hasNext()) {
					OntResource curRange = range.next();
					stmts.add(ResourceFactory.createStatement((Resource) curStmt.getObject(), RDF.type, curRange));
				}
			}
		}
		model.add(stmts);
		
		String fileName = "model_typed.nt";
		try(FileWriter out = new FileWriter( fileName )){
			model.write( out, "NT" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
