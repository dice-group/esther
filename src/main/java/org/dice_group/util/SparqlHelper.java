package org.dice_group.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class SparqlHelper {

	public static String getDomainQuery(String property) {
		return "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#domain> ?o  filter isIri(?o) }";
	}

	public static String getRangeQuery(String property) {
		return "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#range> ?o  filter isIri(?o) }";
	}

	public static String getSubClassesQuery(String property) {
		return "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?o  filter isIri(?o) }";
	}

	public static List<Resource> selectEndpoint(String requestURL, String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		List<Resource> objects = new ArrayList<Resource>();
		try (QueryExecution queryExecution = QueryExecutionFactory.createServiceRequest(requestURL, query);) {
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				objects.add(resultSet.next().get("?o").asResource());
			}
		}
		return objects;
	}

	public static Set<Node> getDomainFromModel(Model model, String property) {
		String sparqlQuery = "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#domain> ?o  filter isIri(?o) }";
		return selectEndpointFromModel(model, sparqlQuery);
	}

	public static Set<Node> getRangeFromModel(Model model, String property) {
		String sparqlQuery = "select ?o where { <" + property
				+ "> <http://www.w3.org/2000/01/rdf-schema#range> ?o  filter isIri(?o) }";
		return selectEndpointFromModel(model, sparqlQuery);
	}

	public static Set<Node> selectEndpointFromModel(Model model, String sparqlQuery) {
		Query query = QueryFactory.create(sparqlQuery);
		Set<Node> objects = new HashSet<Node>();
		try (QueryExecution queryExecution = QueryExecutionFactory.create(query, model)) {
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				objects.add(resultSet.next().get("?o").asNode());
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
	public static boolean ask(String requestURL, String sparqlQuery) {
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

	public static boolean askModel(Model model, String sparqlQuery) {
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
	
	public static Set<Node> getTypeInformation(Resource subject, Property property, QueryExecutioner sparqlExec) {
		Set<Node> types = new HashSet<Node>();
		SelectBuilder typeBuilder = new SelectBuilder().addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		typeBuilder.addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		typeBuilder.addWhere(subject, property, NodeFactory.createVariable("x"));

		Query typeQuery = typeBuilder.build();
		try(QueryExecution queryExecution = sparqlExec.createExecutioner(typeQuery);){
			ResultSet resultSet = queryExecution.execSelect();
			while (resultSet.hasNext()) {
				types.add(resultSet.next().get("x").asNode());
			}
		}
		return types;
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
