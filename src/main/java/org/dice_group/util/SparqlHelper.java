package org.dice_group.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class SparqlHelper {

	public static List<Resource> getDomain(QueryExecutioner sparqlExec, String property) {
		String sparqlQuery = "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#domain> ?o  filter isIri(?o) }";
		return selectEndpoint(sparqlExec, sparqlQuery);
	}

	public static List<Resource> getRange(QueryExecutioner sparqlExec, String property) {
		String sparqlQuery = "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#range> ?o  filter isIri(?o) }";
		return selectEndpoint(sparqlExec, sparqlQuery);
	}
	
	public static List<Resource> getDomainFromModel(Model model, String property) {
		String sparqlQuery = "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#domain> ?o  filter isIri(?o) }";
		return selectEndpointFromModel(model, sparqlQuery);
	}

	public static List<Resource> getRangeFromModel(Model model, String property) {
		String sparqlQuery = "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#range> ?o  filter isIri(?o) }";
		return selectEndpointFromModel(model, sparqlQuery);
	}

	public static List<Resource> getSubclasses(QueryExecutioner sparqlExec, String property) {
		String sparqlQuery = "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?o  filter isIri(?o) }";
		return selectEndpoint(sparqlExec, sparqlQuery);
	}

	public static List<Resource> selectEndpoint(QueryExecutioner sparqlExec, String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		List<Resource> objects = new ArrayList<Resource>();
		try (QueryExecution queryExecution = sparqlExec.createExecutioner(query);) {
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				objects.add(resultSet.next().get("?o").asResource());
			}
		}
		return objects;
	}
	
	public static List<Resource> selectEndpointFromModel(Model model, String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		List<Resource> objects = new ArrayList<Resource>();
		try (QueryExecution queryExecution =  QueryExecutionFactory.create(query, model)) {
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				objects.add(resultSet.next().get("?o").asResource());
			}
		}
		return objects;
	}

	/**
	 * 
	 * @param graph
	 * @param sparqlQuery
	 * @return
	 */
	public static boolean askModel(String requestURL, String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution queryExecution = QueryExecutionFactory.createServiceRequest(requestURL, query);
		boolean result = false;
		try {
			result = queryExecution.execAsk();
		} finally {
			queryExecution.close();
		}
		return result;
	}

	public static boolean askGraph(Model model, String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
		boolean result = false;
		try {
			result = queryExecution.execAsk();
		} finally {
			queryExecution.close();
		}
		return result;
	}

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
	 * 
	 * @param graph
	 * @param sparqlQuery
	 * @return
	 */
	public static boolean askModel(Model graph, String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, graph);
		boolean result = false;
		try {
			result = queryExecution.execAsk();
		} finally {
			queryExecution.close();
		}
		return result;
	}

	/**
	 * 
	 * @param p
	 * @param s
	 * @param o
	 * @return
	 */
	public static String getAskQuery(String p, String s, String o) {
		StringBuilder builder = new StringBuilder("ASK  { ");
		builder.append("<").append(s).append("> ").append(p).append(" <").append(o).append("> ").append(". }");
		return builder.toString();

	}

}
