package org.dice_group.datasets.preprocessing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * Generates false triples by either: Corrupting the subject, Corrupting the
 * object or Corrupting both While keeping the domain and range information in
 * check.
 *
 */
public class FalseTriplesGenerator {

	public static void main(String[] args) {
		Model trueFacts = ModelFactory.createDefaultModel();
		trueFacts.read(args[0]);

		Model trainingData = ModelFactory.createDefaultModel();
		trainingData.read(args[1]);

		Set<Statement> falsetriples;
		String type = args[2];
		switch (type) {
		case "S":
			falsetriples = corruptStmt(trueFacts, trainingData, type);
			break;
		case "O":
			falsetriples = corruptStmt(trueFacts, trainingData, type);
			break;
		default:
			falsetriples = corruptStmt(trueFacts, trainingData, type);
			break;
		}

		Model falseFacts = ModelFactory.createDefaultModel();
		falseFacts.add(new ArrayList<Statement>(falsetriples));

		String fileName = "false_triples_" + type + ".nt";
		try (FileWriter out = new FileWriter(fileName)) {
			falseFacts.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Set<Statement> corruptStmt(Model trueFacts, Model trainingData, String type) {
		List<Resource> nodes = trainingData.listResourcesWithProperty(null).toList();
		Set<Statement> falseTriples = new HashSet<Statement>();
		StmtIterator stmtIterator = trueFacts.listStatements();
		Random random = new Random();
		int count = 0;
		while (stmtIterator.hasNext()) {
			Statement curStmt = stmtIterator.next();

			// get a random possible subject
			Resource newSubject = null;
			Resource newObject = null;
			Statement newStmt = null;

			boolean isValidStmt = false;
			while (!isValidStmt) {
				newSubject = nodes.get(random.nextInt(nodes.size()));
				newObject = nodes.get(random.nextInt(nodes.size()));

				if (type.equals("S") || type.equals("SO")) {
					// get a random subject until the domain matches
					Set<RDFNode> domain = trainingData.listObjectsOfProperty(curStmt.getPredicate(), RDFS.domain)
							.toSet();
					Set<RDFNode> types = trainingData.listObjectsOfProperty(newSubject, RDF.type).toSet();
					if(domain.isEmpty())
						continue;
					while (!types.containsAll(domain)) {
						newSubject = nodes.get(random.nextInt(nodes.size()));
						types = trainingData.listObjectsOfProperty(newSubject, RDF.type).toSet();
					}
				}
				if (type.equals("O") || type.equals("SO")) {
					// get a random object until the range matches
					Set<RDFNode> range = trainingData.listObjectsOfProperty(curStmt.getPredicate(), RDFS.range).toSet();
					Set<RDFNode> types = trainingData.listObjectsOfProperty(newObject, RDF.type).toSet();
					if(range.isEmpty())
						continue;
					while (!types.containsAll(range)) {
						newObject = nodes.get(random.nextInt(nodes.size()));
						types = trainingData.listObjectsOfProperty(newObject, RDF.type).toSet();
					}
				}

				if (type.equals("S")) {
					newStmt = ResourceFactory.createStatement(newSubject, curStmt.getPredicate(), newObject);
				} else if (type.equals("O")) {
					newStmt = ResourceFactory.createStatement(curStmt.getSubject(), curStmt.getPredicate(), newObject);
				} else if (type.equals("SO")) {
					newStmt = ResourceFactory.createStatement(newSubject, curStmt.getPredicate(), newObject);
				}

				if (newStmt.equals(curStmt) || falseTriples.contains(newStmt) || trueFacts.contains(newStmt)) {
					continue;
				}
				isValidStmt = true;
			}

			// add to false triples
			falseTriples.add(newStmt);
			System.out.println(++count + "/" + trueFacts.size());
		}
		return falseTriples;
	}

}
