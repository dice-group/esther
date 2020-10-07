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
	
	private static String FILENAME ;

	public static void main(String[] args) {
		Model trueFacts = ModelFactory.createDefaultModel();
		trueFacts.read(args[0]);

		Model trainingData = ModelFactory.createDefaultModel();
		trainingData.read(args[1]);
		
		FILENAME = args[3];

		Set<Statement> falsetriples;
		switch (args[2]) {
		case "S":
			falsetriples = corruptSubjects(trueFacts, trainingData, FILENAME);
			break;
		case "O":
			falsetriples = corruptObjects(trueFacts, trainingData, FILENAME);
			break;
		default:
			falsetriples = corruptSubjNObjects(trueFacts, trainingData, FILENAME);
			break;
		}

		Model falseFacts = ModelFactory.createDefaultModel();
		falseFacts.add(new ArrayList<Statement>(falsetriples));

		String fileName = "false_triples_" + args[2] + ".nt";
		try (FileWriter out = new FileWriter(fileName)) {
			falseFacts.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Set<Statement> corruptSubjects(Model trueFacts, Model trainingData, String filename2) {
		List<Resource> nodes = trainingData.listResourcesWithProperty(null).toList();
		Set<Statement> falseTriples = new HashSet<Statement>();
		StmtIterator stmtIterator = trueFacts.listStatements();
		Random random = new Random();
		while (stmtIterator.hasNext()) {
			Statement curStmt = stmtIterator.next();

			// get a random possible subject
			Resource newSubject = null;
			Statement newStmt = null;

			boolean isValidSubject = false;
			while (!isValidSubject) {
				newSubject = nodes.get(random.nextInt(nodes.size()));

				// get a random subject until the domain matches
				Set<RDFNode> domain = trainingData.listObjectsOfProperty(curStmt.getPredicate(), RDFS.domain).toSet();
				while (!Collections.disjoint(domain,
						trainingData.listObjectsOfProperty(newSubject, RDF.type).toSet())) {
					newSubject = nodes.get(random.nextInt(nodes.size()));
				}

				// check if this is present in the dump
				StringBuilder builder = new StringBuilder();
				builder.append(" \"<").append(newSubject).append("> <").append(curStmt.getPredicate()).append("> <")
						.append(curStmt.getObject()).append("> .\" ");
				if (runShell(grep(builder.toString(), FILENAME)) != 0) {
					newStmt = ResourceFactory.createStatement(newSubject, curStmt.getPredicate(), curStmt.getObject());
					if (newStmt.equals(curStmt) || falseTriples.contains(newStmt))
						continue;
					isValidSubject = true;
				}
			}

			// add to false triples
			falseTriples.add(newStmt);

		}
		return falseTriples;
	}

	public static Set<Statement> corruptObjects(Model trueFacts, Model trainingData, String filename2) {
		List<Resource> nodes = trainingData.listResourcesWithProperty(null).toList();
		Set<Statement> falseTriples = new HashSet<Statement>();
		StmtIterator stmtIterator = trueFacts.listStatements();
		Random random = new Random();
		while (stmtIterator.hasNext()) {
			Statement curStmt = stmtIterator.next();

			// get a random possible subject
			Resource newObject = null;
			Statement newStmt = null;

			boolean isValidSubject = false;
			while (!isValidSubject) {
				newObject = nodes.get(random.nextInt(nodes.size()));

				// get a random subject until the range matches
				Set<RDFNode> range = trainingData.listObjectsOfProperty(curStmt.getPredicate(), RDFS.range).toSet();
				while (!Collections.disjoint(range, trainingData.listObjectsOfProperty(newObject, RDF.type).toSet())) {
					newObject = nodes.get(random.nextInt(nodes.size()));
				}

				// check if this is present in the dump
				StringBuilder builder = new StringBuilder();
				builder.append(" \"<").append(curStmt.getSubject()).append("> <").append(curStmt.getPredicate())
						.append("> <").append(newObject).append("> .\" ");
				if (runShell(grep(builder.toString(), FILENAME)) != 0) {
					newStmt = ResourceFactory.createStatement(curStmt.getSubject(), curStmt.getPredicate(), newObject);
					if (newStmt.equals(curStmt) || falseTriples.contains(newStmt))
						continue;
					isValidSubject = true;
				}
			}

			// add to false triples
			if (newObject != null)
				falseTriples.add(newStmt);

		}
		return falseTriples;
	}

	public static Set<Statement> corruptSubjNObjects(Model trueFacts, Model trainingData, String filename2) {
		List<Resource> nodes = trainingData.listResourcesWithProperty(null).toList();
		Set<Statement> falseTriples = new HashSet<Statement>();
		StmtIterator stmtIterator = trueFacts.listStatements();
		Random random = new Random();
		while (stmtIterator.hasNext()) {
			Statement curStmt = stmtIterator.next();

			// get a random possible subject
			Resource newSubject = null;
			Resource newObject = null;
			Statement newStmt = null;

			boolean isValidSubject = false;
			boolean isValidObject = false;
			while (!isValidSubject || !isValidObject) {
				newSubject = nodes.get(random.nextInt(nodes.size()));
				newObject = nodes.get(random.nextInt(nodes.size()));

				// get a random subject until the domain matches
				Set<RDFNode> domain = trainingData.listObjectsOfProperty(curStmt.getPredicate(), RDFS.domain).toSet();
				while (!Collections.disjoint(domain,
						trainingData.listObjectsOfProperty(newSubject, RDF.type).toSet())) {
					newSubject = nodes.get(random.nextInt(nodes.size()));
				}

				// get a random object until the range matches
				Set<RDFNode> range = trainingData.listObjectsOfProperty(curStmt.getPredicate(), RDFS.domain).toSet();
				while (!Collections.disjoint(range, trainingData.listObjectsOfProperty(newSubject, RDF.type).toSet())) {
					newObject = nodes.get(random.nextInt(nodes.size()));
				}

				// check if this is present in the dump
				StringBuilder builder = new StringBuilder();
				builder.append(" \"<").append(newSubject).append("> <").append(curStmt.getPredicate()).append("> <")
						.append(newObject).append("> .\" ");
				if (runShell(grep(builder.toString(), FILENAME)) != 0) {
					newStmt = ResourceFactory.createStatement(newSubject, curStmt.getPredicate(), newObject);
					if (newStmt.equals(curStmt) || falseTriples.contains(newStmt))
						continue;
					isValidSubject = true;
					isValidObject = true;
				}
			}

			// add to false triples
			falseTriples.add(newStmt);

		}
		return falseTriples;
	}

	private static String grep(String a, String fileName) {
		StringBuilder builder = new StringBuilder("grep ");
		builder.append(a).append(fileName);
		return builder.toString();
	}

	private static int runShell(String cmd) {
		try {
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd).inheritIO();
			Process p = pb.start();
			int exitVal = p.waitFor();
			return exitVal;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
