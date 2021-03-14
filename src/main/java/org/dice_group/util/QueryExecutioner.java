package org.dice_group.util;

import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryExecutioner {
	
	private String requestURL;
	
	private static final int MAX_ATTEMPTS_PER_QUERY = 1;
	
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
		
		for(int tries = 0;;tries++) {
			try(QueryExecution queryExec = createExecutioner(query);){
				try {
					return queryExec.execSelect().next().get(desiredVar).asLiteral().getDouble();
				} catch (Exception e) {
					// if tries are reached, throw the exception anyhow
					if(tries > MAX_ATTEMPTS_PER_QUERY) {
						LOGGER.error("Tried the query "+tries+" times and still failed.\n"+queryExec.getQuery());
						return 0;
					}
				}
			}
		}
	}
	
	public List<Resource> selectResources(String query){
		for(int tries = 0;;tries++) {
			try {
				return SparqlHelper.selectEndpoint(requestURL, query);
			} catch (Exception e) {
				// if tries are reached, throw the exception anyhow
				if(tries > MAX_ATTEMPTS_PER_QUERY) {
					LOGGER.error("Tried the query "+tries+" times and still failed.\n"+query);
					throw e;
				}
			}
		}
	}
	
	public boolean ask (String query) {
		for(int tries = 0;;tries++) {
			try {
				return SparqlHelper.ask(requestURL, query);
			} catch (Exception e) {
				// if tries are reached, throw the exception anyhow
				if(tries > MAX_ATTEMPTS_PER_QUERY) {
					LOGGER.error("Tried the query "+tries+" times and still failed.\n"+query);
					return false;
				}
			}
		}
	}
	
	public QueryExecution createExecutioner(Query query) {
		QueryExecution exec = QueryExecutionFactory.createServiceRequest(requestURL, query);
		exec.setTimeout(50000);
		return exec;
	}
}
