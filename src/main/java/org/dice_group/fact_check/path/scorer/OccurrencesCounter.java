package org.dice_group.fact_check.path.scorer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_group.util.QueryExecutioner;
import org.dice_group.util.SparqlHelper;

/**
 *
 */
public class OccurrencesCounter {

	/**
	 * triples in a given graph that possess as the subject type, the domain of the
	 * statement's predicate we want to check
	 */
	private int subjectTriplesCount;

	/**
	 * triples in a given graph that possess as the object type, the range of the
	 * statement's predicate we want to check
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

	private QueryExecutioner sparqlExec;

	public OccurrencesCounter(Statement stmt, QueryExecutioner sparqlExec, boolean vTy) {
		this.stmt = stmt;
		this.sparqlExec = sparqlExec;
		this.subjectTypes = new HashSet<Node>();
		this.objectTypes = new HashSet<Node>();
		getDomainRangeInfo();
		this.predicateTriplesCount = countPredicateOccurrences(NodeFactory.createVariable("s"), stmt.getPredicate(),
				NodeFactory.createVariable("o"));
		this.subjectTriplesCount = countOccurrences(NodeFactory.createVariable("s"), RDF.type, subjectTypes);
		this.objectTriplesCount = countOccurrences(NodeFactory.createVariable("s"), RDF.type, objectTypes);
	}

	private void getDomainRangeInfo() {
		subjectTypes = SparqlHelper.getTypeInformation(stmt.getPredicate(), RDFS.domain, sparqlExec);
		objectTypes =  SparqlHelper.getTypeInformation(stmt.getPredicate(), RDFS.range, sparqlExec);

		if (subjectTypes.isEmpty()) {
			subjectTypes =  SparqlHelper.getTypeInformation(stmt.getSubject().asResource(), RDF.type, sparqlExec);
		}

		if (objectTypes.isEmpty()) {
			objectTypes =  SparqlHelper.getTypeInformation(stmt.getSubject().asResource(), RDF.type, sparqlExec);
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
		try (QueryExecution queryExecution = sparqlExec.createExecutioner(queryOccurrence);){
			int count_Occurrence = 0;
			ResultSet resultSet = queryExecution.execSelect();
			if (resultSet.hasNext())
				count_Occurrence = resultSet.next().get("?c").asLiteral().getInt();
			return count_Occurrence;
		}
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

	public QueryExecutioner getSparqlExec() {
		return sparqlExec;
	}

	public void setSparqlExec(QueryExecutioner sparqlExec) {
		this.sparqlExec = sparqlExec;
	}


}
