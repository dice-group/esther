package org.dice_group.fact_check.path.scorer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyHelper;

/**
 * NPMI Calculation as per:
 * https://github.com/dice-group/COPAAL/blob/45c7f274063f35540fd1dc3163536a1b65179c99/service/src/main/java/org/dice/FactCheck/Corraborative/NPMICalculator.java
 *
 */
public class NPMICalculator {

	private Property path;
	private OccurrencesCounter counter;
	private String builder;
	private String sPath;
	private List<Property> pathProperties;

	public NPMICalculator(Property path, Map<Integer, String> id2rel, OccurrencesCounter counter) {
		this.path = path;
		this.counter = counter;
		builder = "?s ?p1 ?x1; ?x1 ?p2 ?x2; ?x2 ?p3 ?o";
		sPath = PropertyHelper.translate2IRI(path, id2rel);
		pathProperties = path.getPaths();
	}

	public double calculatePMIScore_vTy() {
		String predicateTriple = "?s <" + counter.getStmt().getPredicate() + "> ?o .";
		String pathQueryString, pathPredicateQueryString;
		String[] querySequence = builder.split(";");
		String subjType = " filter(exists {?s <" + counter.getStmt().getPredicate() + ">  []}).";
		String objType = " filter(exists {[] <" + counter.getStmt().getPredicate() + ">  ?o}).";

		String firstPath;
		if (pathProperties.get(0).isInverse()) {
			firstPath = querySequence[0].split(" ")[2].trim() + " <" + sPath.split(";")[0] + "> "
					+ querySequence[0].split(" ")[0].trim();
		} else {
			firstPath = querySequence[0].split(" ")[0].trim() + " <" + sPath.split(";")[0] + "> "
					+ querySequence[0].split(" ")[2].trim();
		}

		if (path.getPathLength() == 3) {
			String secondPath;
			if (pathProperties.get(2).isInverse()) {
				secondPath = querySequence[1].split(" ")[2].trim() + " <" + sPath.split(";")[1] + "> "
						+ querySequence[1].split(" ")[0].trim();
			} else {
				secondPath = querySequence[1].split(" ")[0].trim() + " <" + sPath.split(";")[1] + "> "
						+ querySequence[1].split(" ")[2].trim();
			}

			String thirdPath;
			if (pathProperties.get(3).isInverse()) {
				thirdPath = querySequence[2].split(" ")[2].trim() + " <" + sPath.split(";")[2] + "> "
						+ querySequence[2].split(" ")[0].trim();
			} else {
				thirdPath = querySequence[2].split(" ")[0].trim() + " <" + sPath.split(";")[2] + "> "
						+ querySequence[2].split(" ")[2].trim();
			}

			pathQueryString = "Select (count(*) as ?cnt) where {select distinct ?s ?o { " + firstPath + " . " + subjType
					+ "{select distinct ?o1 ?o{" + secondPath + " . " + thirdPath + " . " + objType + " } }" + "}} ";
			pathPredicateQueryString = "Select (count(*) as ?c) where {select distinct ?s ?o {\n" + firstPath + " .\n"
					+ secondPath + " .\n" + thirdPath + " .\n" + predicateTriple + "\n" + "}}\n";
		} else if (path.getPathLength() == 2) {
			String secondPath;
			if (pathProperties.get(2).isInverse()) {
				secondPath = querySequence[1].split(" ")[2].trim() + " <" + sPath.split(";")[1] + "> "
						+ querySequence[1].split(" ")[0].trim();
			} else {
				secondPath = querySequence[1].split(" ")[0].trim() + " <" + sPath.split(";")[1] + "> "
						+ querySequence[1].split(" ")[2].trim();
			}

			pathQueryString = "Select (count(*) as ?cnt) where {select distinct ?s ?o { " + firstPath + " . " + subjType
					+ secondPath + " . " + objType + "}} ";

			pathPredicateQueryString = "Select (count(*) as ?c) where {select distinct ?s ?o {\n" + firstPath + " .\n"
					+ secondPath + " .\n" + predicateTriple + "\n" + "}}\n";
		} else {
			pathQueryString = "Select (count(*) as ?sum) where {\n" + firstPath + " .\n" + subjType + objType + "}\n";

			pathPredicateQueryString = "Select (count(*) as ?c) where {\n" + firstPath + " .\n" + predicateTriple + "\n"
					+ "}\n";

		}

		Query pathQuery = QueryFactory.create(pathQueryString);
		QueryExecution pathQueryExecution = QueryExecutionFactory.createServiceRequest(counter.getServiceRequestURL(),
				pathQuery);
		double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?cnt").asLiteral().getDouble();
		pathQueryExecution.close();

		Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);
		QueryExecution predicatePathQueryExecution = QueryExecutionFactory
				.createServiceRequest(counter.getServiceRequestURL(), pathPredicateQuery);

		double count_path_Predicate_Occurrence = predicatePathQueryExecution.execSelect().next().get("?c").asLiteral()
				.getDouble();
		predicatePathQueryExecution.close();

		return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);

	}

	public double calculatePMIScore() throws ParseException {

		if (counter.isvTy())
			return calculatePMIScore_vTy();

		String sPath = path.toString();

		// Find all subject and object types, we need them in query

		Iterator<Node> subTypeIterator = counter.getSubjectTypes().iterator();
		String subTypeTriples = "";
		while (subTypeIterator.hasNext()) {
			subTypeTriples = subTypeTriples + "?s a <" + subTypeIterator.next() + "> . \n";
		}

		Iterator<Node> objTypeIterator = counter.getObjectTypes().iterator();
		String objTypeTriples = "";
		while (objTypeIterator.hasNext()) {
			objTypeTriples = objTypeTriples + "?o a <" + objTypeIterator.next() + "> . \n";
		}

		String predicateTriple = "?s <" + counter.getStmt().getPredicate() + "> ?o .";

		if (path.getPathLength() == 3) {
			String[] querySequence = builder.split(";");

			String firstPath;
			if (pathProperties.get(0).isInverse()) {
				firstPath = querySequence[0].split(" ")[0].trim() + " <" + sPath.split(";")[0] + "> "
						+ querySequence[0].split(" ")[2].trim();
			} else {
				firstPath = querySequence[0].split(" ")[2].trim() + " <" + sPath.split(";")[0] + "> "
						+ querySequence[0].split(" ")[0].trim();
			}

			String secondPath;
			if (pathProperties.get(1).isInverse()) {
				secondPath = querySequence[1].split(" ")[2].trim() + " <" + sPath.split(";")[1] + "> "
						+ querySequence[1].split(" ")[0].trim();
			} else {
				secondPath = querySequence[1].split(" ")[0].trim() + " <" + sPath.split(";")[1] + "> "
						+ querySequence[1].split(" ")[2].trim();
			}

			String thirdPath;
			if (pathProperties.get(1).isInverse()) {
				thirdPath = querySequence[2].split(" ")[2].trim() + " <" + sPath.split(";")[2] + "> "
						+ querySequence[2].split(" ")[0].trim();
			} else {
				thirdPath = querySequence[2].split(" ")[0].trim() + " <" + sPath.split(";")[2] + "> "
						+ querySequence[2].split(" ")[2].trim();
			}

			String pathQueryString = "select (sum(?b3*?k) as ?sum) where { \n"
					+ "select (count(*) as ?b3) (?b2*?b1 as ?k) ?x1 where { \n" + firstPath + " .\n" + subTypeTriples
					+ "{ \n" + "Select (count(*) as ?b2) ?x1 ?b1 where { \n" + secondPath + "{ \n"
					+ "select (count(*) as ?b1) ?x2 where { \n" + thirdPath + ". \n" + objTypeTriples
					+ "} group by ?x2\n" + "}\n" + "} group by ?b1 ?x1\n" + "}\n" + "} group by ?x1 ?b2 ?b1\n" + "}\n";

			Query pathQuery = QueryFactory.create(pathQueryString);
			QueryExecution pathQueryExecution = QueryExecutionFactory
					.createServiceRequest(counter.getServiceRequestURL(), pathQuery);
			double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral().getDouble();
			pathQueryExecution.close();

			String pathPredicateQueryString = "Select (count(*) as ?c) where {\n" + firstPath + " .\n" + subTypeTriples
					+ secondPath + " .\n" + thirdPath + " .\n" + objTypeTriples + predicateTriple + "\n" + "}\n";

			Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);
			QueryExecution predicatePathQueryExecution = QueryExecutionFactory
					.createServiceRequest(counter.getServiceRequestURL(), pathPredicateQuery);

			double count_path_Predicate_Occurrence = predicatePathQueryExecution.execSelect().next().get("?c")
					.asLiteral().getDouble();
			predicatePathQueryExecution.close();

			return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
		} else if (path.getPathLength() == 2) {
			String[] querySequence = builder.split(";");

			String firstPath;
			if (pathProperties.get(0).isInverse()) {
				firstPath = querySequence[0].split(" ")[2].trim() + " <" + sPath.split(";")[0] + "> "
						+ querySequence[0].split(" ")[0].trim();
			} else {
				firstPath = querySequence[0].split(" ")[0].trim() + " <" + sPath.split(";")[0] + "> "
						+ querySequence[0].split(" ")[2].trim();
			}

			String secondPath;
			if (pathProperties.get(1).isInverse()) {
				secondPath = querySequence[1].split(" ")[2].trim() + " <" + sPath.split(";")[1] + "> "
						+ querySequence[1].split(" ")[0].trim();
			} else {
				secondPath = querySequence[1].split(" ")[0].trim() + " <" + sPath.split(";")[1] + "> "
						+ querySequence[1].split(" ")[2].trim();
			}

			String pathQueryString = "Select (sum(?b1*?b2) as ?sum) where {\n"
					+ "select (count(*) as ?b2) ?b1 where { \n" + firstPath + " .\n" + subTypeTriples + "{ \n"
					+ "select (count(*) as ?b1) ?x1 where { \n" + secondPath + " .\n" + objTypeTriples
					+ "} group by ?x1\n" + "}\n" + "} group by ?b1\n" + "}\n";

			Query pathQuery = QueryFactory.create(pathQueryString);
			QueryExecution pathQueryExecution = QueryExecutionFactory
					.createServiceRequest(counter.getServiceRequestURL(), pathQuery);
			double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral().getDouble();
			pathQueryExecution.close();

			String pathPredicateQueryString = "Select (count(*) as ?c) where {\n" + firstPath + " .\n" + subTypeTriples
					+ secondPath + " .\n" + objTypeTriples + predicateTriple + "\n" + "}\n";

			Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);

			QueryExecution pathPredicateQueryExecution = QueryExecutionFactory
					.createServiceRequest(counter.getServiceRequestURL(), pathPredicateQuery);
			double count_path_Predicate_Occurrence = pathPredicateQueryExecution.execSelect().next().get("?c")
					.asLiteral().getDouble();
			pathPredicateQueryExecution.close();

			return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);

		} else {
			String firstPath;
			if (pathProperties.get(0).isInverse()) {
				firstPath = builder.split(" ")[2].trim() + " <" + sPath.split(";")[0] + "> "
						+ builder.split(" ")[0].trim();
			} else {
				firstPath = builder.split(" ")[0].trim() + " <" + sPath.split(";")[0] + "> "
						+ builder.split(" ")[2].trim();
			}

			String pathQueryString = "Select (count(*) as ?sum) where {\n" + firstPath + " .\n" + subTypeTriples
					+ objTypeTriples + "}\n";

			Query pathQuery = QueryFactory.create(pathQueryString);
			QueryExecution pathQueryExecution = QueryExecutionFactory
					.createServiceRequest(counter.getServiceRequestURL(), pathQuery);
			double count_Path_Occurrence = pathQueryExecution.execSelect().next().get("?sum").asLiteral().getDouble();
			pathQueryExecution.close();

			String pathPredicateQueryString = "Select (count(*) as ?c) where {\n" + firstPath + " .\n" + subTypeTriples
					+ objTypeTriples + predicateTriple + "\n" + "}\n";

			Query pathPredicateQuery = QueryFactory.create(pathPredicateQueryString);
			QueryExecution pathPredicateQueryExecution = QueryExecutionFactory
					.createServiceRequest(counter.getServiceRequestURL(), pathPredicateQuery);

			double count_path_Predicate_Occurrence = pathPredicateQueryExecution.execSelect().next().get("?c")
					.asLiteral().getDouble();
			pathPredicateQueryExecution.close();

			return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);
		}

	}

	public double npmiValue(double count_Path_Occurrence, double count_path_Predicate_Occurrence)
			throws IllegalArgumentException {
		// If the predicate never occurs
		if (counter.getPredicateTriplesCount() == 0) {
			throw new IllegalArgumentException(
					"The given predicate does never occur. The NPMI is not defined for this case.");
		}
		// If the path never occurs
		if (count_Path_Occurrence == 0) {
			throw new IllegalArgumentException(
					"The given path does never occur. The NPMI is not defined for this case.");
		}
		// If subject or object types never occur
		if ((counter.getSubjectTriplesCount() == 0) || (counter.getObjectTriplesCount() == 0)) {
			throw new IllegalArgumentException(
					"The given number of triples for the subject or object type is 0. The NPMI is not defined for this case. Given occurrences is subject="
							+ counter.getSubjectTriplesCount() + " and object=" + counter.getObjectTriplesCount());
		}
		double logSubObjTriples = Math.log(counter.getSubjectTriplesCount())
				+ Math.log(counter.getObjectTriplesCount());
		double npmi;

		// Path and predicate never occur together
		if (count_path_Predicate_Occurrence == 0) {
			// Since we know that A and B exist, there is a chance that they should occur
			// together. Since it never happens, we have to return -1
			// npmi = -1;
			npmi = 0;
		} else {
			npmi = calculateNPMI(Math.log(count_path_Predicate_Occurrence), logSubObjTriples,
					Math.log(count_Path_Occurrence), logSubObjTriples, Math.log(counter.getPredicateTriplesCount()),
					logSubObjTriples);
		}
//        if ((filter != null) && (!filter.npmiIsOk(npmi, pathLength, count_path_Predicate_Occurrence,
//                count_Path_Occurrence, count_predicate_Occurrence))) {
//            throw new NPMIFilterException("The NPMI filter rejected the calculated NPMI.");
//        }
		path.setFinalScore(npmi);
		return npmi;
	}

	public static double calculateNPMI(double logCountAB, double logNormAB, double logCountA, double logNormA,
			double logCountB, double logNormB) {
		// Calculate probabilities
		double logProbA = logCountA - logNormA;
		double logProbB = logCountB - logNormB;
		double logProbAB = logCountAB - logNormAB;

		// If the probability of AB is 1.0 (i.e., its log is 0.0)
		if (logProbAB == 0) {
			return 1.0;
		} else {
			return (logProbAB - logProbA - logProbB) / -logProbAB;
		}
	}
	
	@Override
	public String toString() {
		return path.getFinalScore()+""+path.toString();
	}
}
