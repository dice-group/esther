package org.dice_group.fact_check.path.scorer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 */
public class OccurrencesCounter {

	/**
	 * triples in a given graph that possess the same subject type (domain) as the
	 * statement we want to check
	 */
	private int subjectTriplesCount;

	/**
	 * triples in a given graph that possess the same object type (range) as the
	 * statement we want to check
	 */
	private int objectTriplesCount;

	/**
	 * triples in a give graph that have the same predicate as the statement we want
	 * to check
	 */
	private int predicateTriplesCount;

	private Set<Node> subjectTypes;

	private Set<Node> objectTypes;

	private Statement stmt;

	private String serviceRequestURL;

	private boolean vTy;

	public OccurrencesCounter(Statement stmt, String serviceRequestURL, boolean vTy) {
		this.stmt = stmt;
		this.serviceRequestURL = serviceRequestURL;
		this.subjectTypes = new HashSet<Node>();
		this.objectTypes = new HashSet<Node>();
		this.vTy = vTy;
		getDomainRangeInfo();
	}

	private void getDomainRangeInfo() {
		subjectTypes = getTypeInformation(stmt.getPredicate(), RDFS.domain);
		objectTypes = getTypeInformation(stmt.getPredicate(), RDFS.range);

		if (subjectTypes.isEmpty()) {
			subjectTypes = getTypeInformation(stmt.getSubject().asResource(), RDF.type);
		}

		if (objectTypes.isEmpty()) {
			objectTypes = getTypeInformation(stmt.getSubject().asResource(), RDF.type);
		}
	}

	public void count() {
		this.predicateTriplesCount = countPredicateOccurrences(NodeFactory.createVariable("s"), stmt.getPredicate(),
				NodeFactory.createVariable("o"));
		if (!vTy) {
			this.subjectTriplesCount = countOccurrences(NodeFactory.createVariable("s"), RDF.type, subjectTypes);
			this.objectTriplesCount = countOccurrences(NodeFactory.createVariable("s"), RDF.type, objectTypes);
		} else {
			this.subjectTriplesCount = countSOOccurrances("count(distinct ?s)", stmt.getPredicate());
			this.objectTriplesCount = countSOOccurrances("count(distinct ?o)", stmt.getPredicate());
		}
	}

	public int countSOOccurrances(String var, Property property) {
		SelectBuilder occurrenceBuilder = new SelectBuilder();
		try {
			occurrenceBuilder.addVar(var, "?c");
			occurrenceBuilder.addWhere(NodeFactory.createVariable("s"), property, NodeFactory.createVariable("o"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return returnCount(occurrenceBuilder);
	}

	public Set<Node> getTypeInformation(Resource subject, Property property) {
		Set<Node> types = new HashSet<Node>();
		SelectBuilder typeBuilder = new SelectBuilder().addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		typeBuilder.addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		typeBuilder.addWhere(subject, property, NodeFactory.createVariable("x"));

		Query typeQuery = typeBuilder.build();
		QueryExecution queryExecution = QueryExecutionFactory.createServiceRequest(serviceRequestURL, typeQuery);

		ResultSet resultSet = queryExecution.execSelect();

		while (resultSet.hasNext())
			types.add(resultSet.next().get("x").asNode());
		queryExecution.close();
		return types;
	}

	public int countOccurrences(Node subject, Property property, Set<Node> objectTypes) {
		SelectBuilder occurrenceBuilder = new SelectBuilder();
		Iterator<Node> typeIterator = objectTypes.iterator();
		try {
			occurrenceBuilder.addVar("count(*)", "?c");
			while (typeIterator.hasNext())
				occurrenceBuilder.addWhere(subject, property, typeIterator.next());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return returnCount(occurrenceBuilder);
	}

	public int countPredicateOccurrences(Node subject, Property property, Node objectType) {
		SelectBuilder occurrenceBuilder = new SelectBuilder();
		try {
			occurrenceBuilder.addVar("count(*)", "?c");
			occurrenceBuilder.addWhere(subject, property, objectType);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return returnCount(occurrenceBuilder);
	}

	public int returnCount(SelectBuilder builder) {
		Query queryOccurrence = builder.build();
		QueryExecution queryExecution = QueryExecutionFactory.createServiceRequest(serviceRequestURL, queryOccurrence);
		int count_Occurrence = 0;
		ResultSet resultSet = queryExecution.execSelect();
		if (resultSet.hasNext())
			count_Occurrence = resultSet.next().get("?c").asLiteral().getInt();
		queryExecution.close();
		return count_Occurrence;
	}

	public boolean isvTy() {
		return vTy;
	}

	public void setvTy(boolean vTy) {
		this.vTy = vTy;
	}

	public Set<Node> getSubjectTypes() {
		return subjectTypes;
	}

	public void setSubjectTypes(Set<Node> subjectTypes) {
		this.subjectTypes = subjectTypes;
	}

	public Set<Node> getObjectTypes() {
		return objectTypes;
	}

	public void setObjectTypes(Set<Node> objectTypes) {
		this.objectTypes = objectTypes;
	}

	public int getSubjectTriplesCount() {
		return subjectTriplesCount;
	}

	public void setSubjectTriplesCount(int subjectTriplesCount) {
		this.subjectTriplesCount = subjectTriplesCount;
	}

	public int getObjectTriplesCount() {
		return objectTriplesCount;
	}

	public void setObjectTriplesCount(int objectTriplesCount) {
		this.objectTriplesCount = objectTriplesCount;
	}

	public int getPredicateTriplesCount() {
		return predicateTriplesCount;
	}

	public void setPredicateTriplesCount(int predicateTriplesCount) {
		this.predicateTriplesCount = predicateTriplesCount;
	}

	public Statement getStmt() {
		return stmt;
	}

	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

	public String getServiceRequestURL() {
		return serviceRequestURL;
	}

	public void setServiceRequestURL(String serviceRequestURL) {
		this.serviceRequestURL = serviceRequestURL;
	}

}
