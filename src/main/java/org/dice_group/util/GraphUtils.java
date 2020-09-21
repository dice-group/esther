package org.dice_group.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import com.google.common.collect.Streams;

public class GraphUtils {

	/**
	 * 
	 * @param graph
	 * @param sparqlQuery
	 * @return
	 */
	public static List<QuerySolution> selectModel(Model graph, String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, graph);
		List<QuerySolution> querySolutionList = new ArrayList<QuerySolution>();
		try {
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				querySolutionList.add(resultSet.next());
			}
		} finally {
			queryExecution.close();
		}
		return querySolutionList;
	}
	

	/**
	 * Returns a new model without the literal statements and without RDF.type statements
	 * @param model
	 * @return
	 */
	public static Model cleanGraph(Model model) {
		Model cleanModel = ModelFactory.createDefaultModel();
		cleanModel.add(model);
		
		cleanModel.remove(cleanModel.listStatements(null, RDF.type, (RDFNode)null));
		
		List<Statement> literalStmts = cleanModel.listStatements().toList();
		literalStmts.removeIf(k->!k.getObject().isURIResource());
		cleanModel.remove(literalStmts);
		
		return cleanModel;
	}
	
	public static Set<?> getElementsInCommon(Iterator<?> a, Iterator<?> b) {
		return Streams.stream(a).filter(w -> Streams.stream(b).anyMatch(t -> t.equals(w))).collect(Collectors.toSet());
	}

}
