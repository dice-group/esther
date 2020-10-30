package org.dice_group.fact_check.path.scorer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_group.path.Graph;

public class ResultWriter {
	// gerbil format result model
	private Model resultsModel;
	
	// starting statement id
	private int curID;
	
	// each graph corresponds to one triple and the paths found
	private Set<Graph> graphs;

	private static final String STMT = "http://swc2019.dice-research.org/task/dataset/s-";
	private static final String TRUTH_VALUE_STR = "http://swc2017.aksw.org/hasTruthValue";

	public ResultWriter(int id, Set<Graph> graphs) {
		resultsModel = ModelFactory.createDefaultModel();
		curID = id;
		this.graphs = graphs;
		addResults();
	}

	/**
	 * Prints the npmi for each triple in gerbil format
	 * 
	 * @param fileName
	 */
	public void printToFile(String fileName) {
		try (FileWriter out = new FileWriter(fileName)) {
			resultsModel.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public StringBuilder getPaths(Map<Integer, String> id2rel) {
		StringBuilder builder = new StringBuilder();
		for (Graph g : graphs) {
			builder.append(g.getPrintableResults(id2rel));
		}
		return builder;
	}

	/**
	 * Prints all the scores, paths for each triple
	 * 
	 * @param fileName
	 * @param id2rel
	 */
	public void printPathsToFile(String fileName, StringBuilder stringbuilder) {
		File file = new File(fileName);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(stringbuilder.toString());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addResult(Graph graph) {
		// Statement curStmt = graph.getTriple();
		RDFNode truthValue = ResourceFactory.createTypedLiteral(String.valueOf(graph.getScore()),
				XSDDatatype.XSDdouble);
		Resource subject = ResourceFactory.createResource(STMT + String.format("%05d", ++curID));
//		resultsModel.add(ResourceFactory.createStatement(subject, RDF.type, RDF.Statement));
//		resultsModel.add(ResourceFactory.createStatement(subject, RDF.subject, curStmt.getSubject()));
//		resultsModel.add(ResourceFactory.createStatement(subject, RDF.predicate, curStmt.getPredicate()));
//		resultsModel.add(ResourceFactory.createStatement(subject, RDF.object, curStmt.getObject()));
		resultsModel.add(ResourceFactory.createStatement(subject, ResourceFactory.createProperty(TRUTH_VALUE_STR), truthValue));

	}

	public void addResults() {
		graphs.forEach(g -> addResult(g));
	}

	public int getCurID() {
		return curID;
	}

	public void setCurID(int curID) {
		this.curID = curID;
	}

	public Model getResultsModel() {
		return resultsModel;
	}

	public void setResultsModel(Model resultsModel) {
		this.resultsModel = resultsModel;
	}
	
	public void addResultsModel(Model anotherModel) {
		this.resultsModel.add(anotherModel);
	}
	
	
}
