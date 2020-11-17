package org.dice_group.datasets.preprocessing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
	private static final int MAX_ITER = 50_000;

	public static void main(String[] args) {
		Model trueFacts = ModelFactory.createDefaultModel();
		trueFacts.read("/home/ana-silva/Downloads/freebase_data/test/true_triples.nt");
		
		List<Statement> trueF = trueFacts.listStatements().toList();
		Model finalTrueFacts = ModelFactory.createDefaultModel();
		addRandomNSamples(trueF, finalTrueFacts, 750);
		
		Model trainingData = ModelFactory.createDefaultModel();
		trainingData.read("/home/ana-silva/Downloads/freebase_data/train/train_valid_dr_n_types.nt");

		List<Statement> sTriples = new ArrayList<Statement>(corruptStmt(finalTrueFacts, trainingData, "S"));
		List<Statement> oTriples = new ArrayList<Statement>(corruptStmt(finalTrueFacts, trainingData, "O"));
		List<Statement> soTriples = new ArrayList<Statement>(corruptStmt(finalTrueFacts, trainingData, "SO"));
		
		Model falseFacts = ModelFactory.createDefaultModel();
		addRandomNSamples(sTriples, falseFacts, 250);
		addRandomNSamples(oTriples, falseFacts, 250);
		addRandomNSamples(soTriples, falseFacts, 250);

		String fileName = "/home/ana-silva/Downloads/freebase_data/test/false_triples_750.nt";
		try (FileWriter out = new FileWriter(fileName)) {
			falseFacts.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fileName = "/home/ana-silva/Downloads/freebase_data/test/true_triples_750.nt";
		try (FileWriter out = new FileWriter(fileName)) {
			finalTrueFacts.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Set<Statement> corruptStmt(Model trueFacts, Model trainingData, String type) {
		Set<Resource> nodesSet = new HashSet<Resource>();
		StmtIterator iter = trainingData.listStatements();
		while (iter.hasNext()) {
			Statement curStmt = iter.next();

			// to ensure we get the same  it was trained on (could also, just read it from the dictionary)
			if (curStmt.getPredicate().equals(RDF.type) 
					|| curStmt.getPredicate().equals(RDFS.range)
					|| curStmt.getPredicate().equals(RDFS.domain))
				continue;

			nodesSet.add(curStmt.getSubject());
			if (curStmt.getObject().isResource())
				nodesSet.add(curStmt.getObject().asResource());
		}

		List<Resource> nodes = new ArrayList<Resource>(nodesSet);
		Set<Statement> falseTriples = new HashSet<Statement>();

		Random random = new Random();
		int count = 0;
		
		StmtIterator stmtIterator = trueFacts.listStatements();
		while (stmtIterator.hasNext()) {
			Statement curStmt  = stmtIterator.next();
			// get a random possible subject
			Resource newSubject = null;
			Resource newObject = null;
			Statement newStmt = null;
			int iterations = 0;
			
			boolean isValidStmt = false;
			in : while (!isValidStmt) {
				if (++iterations >= MAX_ITER)
					break in;

				newSubject = nodes.get(random.nextInt(nodes.size()));
				newObject = nodes.get(random.nextInt(nodes.size()));

				if (type.equals("S") || type.equals("SO")) {
					// get a random subject until the domain matches
					Set<RDFNode> domain = trainingData.listObjectsOfProperty(curStmt.getPredicate(), RDFS.domain)
							.toSet();
					Set<RDFNode> types = trainingData.listObjectsOfProperty(newSubject, RDF.type).toSet();
					if (domain.isEmpty())
						continue;
					while (!types.containsAll(domain)) {
						newSubject = nodes.get(random.nextInt(nodes.size()));
						types = trainingData.listObjectsOfProperty(newSubject, RDF.type).toSet();
						if (++iterations >= MAX_ITER)
							break in;
					}
				}
				if (type.equals("O") || type.equals("SO")) {
					// get a random object until the range matches
					Set<RDFNode> range = trainingData.listObjectsOfProperty(curStmt.getPredicate(), RDFS.range).toSet();
					Set<RDFNode> types = trainingData.listObjectsOfProperty(newObject, RDF.type).toSet();
					if (range.isEmpty())
						continue;
					while (!types.containsAll(range)) {
						newObject = nodes.get(random.nextInt(nodes.size()));
						types = trainingData.listObjectsOfProperty(newObject, RDF.type).toSet();
						if (++iterations >= MAX_ITER) {
							break in;
						}
					}
				}

				if (type.equals("S")) {
					newStmt = ResourceFactory.createStatement(newSubject, curStmt.getPredicate(), newObject);
				} else if (type.equals("O")) {
					newStmt = ResourceFactory.createStatement(curStmt.getSubject(), curStmt.getPredicate(), newObject);
				} else if (type.equals("SO")) {
					newStmt = ResourceFactory.createStatement(newSubject, curStmt.getPredicate(), newObject);
				}

				if (newStmt.equals(curStmt) || falseTriples.contains(newStmt) || trueFacts.contains(newStmt) || trainingData.contains(newStmt)) {
					continue;
				}
				isValidStmt = true;
				iterations = 0;
			}

			// add to false triples
			if(isValidStmt) {
				falseTriples.add(newStmt);
				System.out.println(++count + "/" + trueFacts.size());
			}
			
		}
		return falseTriples;
	}

	private static void addRandomNSamples(List<Statement> stmts, Model finalFalseFacts, int n) {
		Collections.shuffle(stmts);
		int cur = 0;
		for (int i = 0; i < stmts.size() && cur < n; i++) {
			Statement curStmt = stmts.get(i);
			if (finalFalseFacts.contains(curStmt))
				continue;
			finalFalseFacts.add(curStmt);
			cur++;
		}
	}
}
