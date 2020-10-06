package org.dice_group.fact_check.path.scorer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.dice_group.path.Graph;

public class ResultWriter {
	private Model resultsModel;

	private int curID;

	private static final String STMT = "http://swc2019.dice-research.org/task/dataset/s-";
	private static final String TRUTH_VALUE_STR = "http://swc2017.aksw.org/hasTruthValue";

	public ResultWriter() {
		resultsModel = ModelFactory.createDefaultModel();
	}

	public void printToFile(String fileName) {
		try (FileWriter out = new FileWriter(fileName)) {
			resultsModel.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addResult(Graph graph) {
		Statement curStmt = graph.getTriple();
		RDFNode truthValue = ResourceFactory.createTypedLiteral(String.valueOf(graph.getScore()),
				XSDDatatype.XSDdouble);
		Resource subject = ResourceFactory.createResource(STMT + String.format("%05d", ++curID));

		resultsModel.add(ResourceFactory.createStatement(subject, RDF.type, RDF.Statement));
		resultsModel.add(ResourceFactory.createStatement(subject, RDF.subject, curStmt.getSubject()));
		resultsModel.add(ResourceFactory.createStatement(subject, RDF.predicate, curStmt.getPredicate()));
		resultsModel.add(ResourceFactory.createStatement(subject, RDF.object, curStmt.getObject()));
		resultsModel.add(ResourceFactory.createStatement(subject, ResourceFactory.createProperty(TRUTH_VALUE_STR), truthValue));

	}

	public void addResults(Set<Graph> graphs) {
		graphs.forEach(g -> addResult(g));
	}

}
