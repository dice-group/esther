package org.dice_group.util;

import java.util.concurrent.TimeUnit;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryExecutioner {
	
	private String requestURL;
	
	private static final int MAX_ATTEMPTS_PER_QUERY = 4;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutioner.class);

	public QueryExecutioner(String requestURL) {
		super();
		this.requestURL = requestURL;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}
	
	public double selectDoubleVar(String queryString, String desiredVar) {
		Query query = QueryFactory.create(queryString);
		try(QueryExecution queryExec = createExecutioner(query);){
			return trySelectQuery(queryExec, desiredVar).asLiteral().getDouble();
		}
	}
	
	public QueryExecution createExecutioner(Query query) {
		return QueryExecutionFactory.createServiceRequest(requestURL, query);
	}
	
	/**
	 * Attempts the same query n times, if it exceeds it will throw 
	 * @param queryExecution
	 * @param desiredVar
	 * @return
	 */
	public RDFNode trySelectQuery(QueryExecution queryExecution, String desiredVar) {
		for(int tries = 0;;tries++) {
			try {
				return queryExecution.execSelect().next().get(desiredVar);
			} catch (Exception e) {
				
				try {
					// sleep for 10 seconds before re-trying
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				// if tries are reached, throw the exception anyhow
				if(tries > MAX_ATTEMPTS_PER_QUERY) {
					LOGGER.error("Tried the query "+tries+" times and still failed.\n"+queryExecution.getQuery());
					throw e;
				}
			} 
		}
	}
}
