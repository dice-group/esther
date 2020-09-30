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

public class SparqlHelper {
	
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
			result = queryExecution.execAsk() ;
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
		builder.append(s).append(p).append(o).append(". }");
		return builder.toString();
		
	}

}
