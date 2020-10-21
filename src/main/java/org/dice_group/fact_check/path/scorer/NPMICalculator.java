package org.dice_group.fact_check.path.scorer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
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
		builder = "?s ?p1 ?x1;?x1 ?p2 ?x2;?x2 ?p3 ?o";
		sPath = PropertyHelper.translate2IRI(path, id2rel);
		pathProperties = path.getPaths();
	}

	public double calculatePMIScore_vTy() {
		String predicateTriple = "?s <" + counter.getStmt().getPredicate() + "> ?o .";
		String pathQueryString, pathPredicateQueryString;
		String subjType = " filter(exists {?s <" + counter.getStmt().getPredicate() + ">  []}).";
		String objType = " filter(exists {[] <" + counter.getStmt().getPredicate() + ">  ?o}).";

		String firstPath = getPath(pathProperties.get(0).isInverse(), 0, path.getPathLength());

		if (path.getPathLength() == 3) {

			String secondPath = getPath(pathProperties.get(1).isInverse(), 1, path.getPathLength());

			String thirdPath = getPath(pathProperties.get(2).isInverse(), 2, path.getPathLength());

			pathQueryString = "Select (count(*) as ?cnt) where {select distinct ?s ?o { " + firstPath + " . " + subjType
					+ "{select distinct ?x1 ?o{" + secondPath + " . " + thirdPath + " . " + objType + " } }" + "}} ";
			pathPredicateQueryString = "Select (count(*) as ?c) where {select distinct ?s ?o {\n" + firstPath + " .\n"
					+ secondPath + " .\n" + thirdPath + " .\n" + predicateTriple + "\n" + "}}\n";
		} else if (path.getPathLength() == 2) {

			String secondPath = getPath(pathProperties.get(1).isInverse(), 1, path.getPathLength());

			pathQueryString = "Select (count(*) as ?cnt) where {select distinct ?s ?o { " + firstPath + " . " + subjType
					+ secondPath + " . " + objType + "}} ";

			pathPredicateQueryString = "Select (count(*) as ?c) where {select distinct ?s ?o {" + firstPath + " . "
					+ secondPath + " . " + predicateTriple + " }}";
		} else {
			pathQueryString = "Select (count(*) as ?cnt) where {" + firstPath + " .\n" + subjType + objType + "}\n";

			pathPredicateQueryString = "Select (count(*) as ?c) where { Select distinct ?s ?o where {" + firstPath
					+ " . " + predicateTriple + " }}";

		}

		double count_Path_Occurrence = counter.getSparqlExec().selectDoubleVar(pathQueryString, "?cnt");
		double count_path_Predicate_Occurrence = counter.getSparqlExec().selectDoubleVar(pathPredicateQueryString,
				"?c");

		return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);

	}

	public double calculatePMIScore() throws ParseException {

		if (counter.isvTy())
			return calculatePMIScore_vTy();

		// Find all subject and object types, we need them in query

		Iterator<Node> subTypeIterator = counter.getSubjectTypes().iterator();
		String subTypeTriples = "";
		while (subTypeIterator.hasNext()) {
			subTypeTriples = subTypeTriples + "?s a <" + subTypeIterator.next() + "> . ";
		}

		Iterator<Node> objTypeIterator = counter.getObjectTypes().iterator();
		String objTypeTriples = "";
		while (objTypeIterator.hasNext()) {
			objTypeTriples = objTypeTriples + "?o a <" + objTypeIterator.next() + "> . ";
		}

		String pathQueryString, pathPredicateQueryString;
		String predicateTriple = "?s <" + counter.getStmt().getPredicate() + "> ?o .";

		if (path.getPathLength() == 3) {
			String firstPath = getPath(pathProperties.get(0).isInverse(), 0, path.getPathLength());

			String secondPath = getPath(pathProperties.get(1).isInverse(), 1, path.getPathLength());

			String thirdPath = getPath(pathProperties.get(2).isInverse(), 2, path.getPathLength());

			pathQueryString = "select (sum(?b3*?k) as ?sum) where { "
					+ "select (count(*) as ?b3) (?b2*?b1 as ?k) ?x1 where { " + firstPath + " . " + subTypeTriples
					+ "{ \n" + "Select (count(*) as ?b2) ?x1 ?b1 where { " + secondPath + "{ "
					+ "select (count(*) as ?b1) ?x2 where { " + thirdPath + ". " + objTypeTriples + "} group by ?x2 "
					+ "} " + "} group by ?b1 ?x1" + " } } group by ?x1 ?b2 ?b1 }";

			pathPredicateQueryString = "Select (count(*) as ?c) where { Select distinct ?s ?o where { " + firstPath
					+ " . " + subTypeTriples + secondPath + " . " + thirdPath + " ." + objTypeTriples + predicateTriple
					+ " }}";

//			pathPredicateQueryString = "Select (count(*) as ?c) where { "
//					+ firstPath + " . " + subTypeTriples + secondPath + " . " + thirdPath + " ." + objTypeTriples
//					+ predicateTriple + " }";

		} else if (path.getPathLength() == 2) {

			String firstPath = getPath(pathProperties.get(0).isInverse(), 0, path.getPathLength());

			String secondPath = getPath(pathProperties.get(1).isInverse(), 1, path.getPathLength());

			pathQueryString = "Select (sum(?b1*?b2) as ?sum) where {\n" + "select (count(*) as ?b2) ?b1 where { \n"
					+ firstPath + " .\n" + subTypeTriples + "{ \n" + "select (count(*) as ?b1) ?x1 where { \n"
					+ secondPath + " .\n" + objTypeTriples + "} group by ?x1\n" + "}\n" + "} group by ?b1\n" + "}\n";

			pathPredicateQueryString = "Select (count(*) as ?c) where { Select distinct ?s ?o where { " + firstPath
					+ " . " + subTypeTriples + secondPath + " . " + objTypeTriples + predicateTriple + " }}";

//			pathPredicateQueryString = "Select (count(*) as ?c) where { "
//					+ firstPath + " . " + subTypeTriples + secondPath + " . " + objTypeTriples + predicateTriple
//					+ " }";

		} else {

			String firstPath = getPath(pathProperties.get(0).isInverse(), 0, path.getPathLength());

			pathQueryString = "Select (count(*) as ?sum) where { " + firstPath + " . " + subTypeTriples + objTypeTriples
					+ "}";

			pathPredicateQueryString = "Select (count(*) as ?c) where { Select distinct ?s ?o where { " + firstPath
					+ " . " + subTypeTriples + objTypeTriples + predicateTriple + " }}";

//			pathPredicateQueryString = "Select (count(*) as ?c) where { "
//					+ firstPath + " . " + subTypeTriples + objTypeTriples + predicateTriple + " }";

		}

		double count_Path_Occurrence = counter.getSparqlExec().selectDoubleVar(pathQueryString, "?sum");
		double count_path_Predicate_Occurrence = counter.getSparqlExec().selectDoubleVar(pathPredicateQueryString,
				"?c");

		return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);

	}

	private String getPath(boolean isInverse, int i, int pathLength) {
		String path;
		String[] vars = builder.split(";")[i].split(" ");
		String object = vars[2].trim();
		if (pathLength == i + 1) {
			object = "?o";
		}
		if (isInverse) {
			path = object + " <" + sPath.split(";")[i] + "> " + vars[0].trim();
		} else {
			path = vars[0].trim() + " <" + sPath.split(";")[i] + "> " + object;
		}
		return path;
	}

	public double pmiValue(double count_Path_Occurrence, double count_path_Predicate_Occurrence) {
		double score = 0.0;
		try {

			BigDecimal NO_OF_SUBJECT_TRIPLES = new BigDecimal(Integer.toString(counter.getSubjectTriplesCount()));
			BigDecimal NO_OF_OBJECT_TRIPLES = new BigDecimal(Integer.toString(counter.getObjectTriplesCount()));
			BigDecimal NO_PATH_PREDICATE_TRIPLES = new BigDecimal(Double.toString(count_path_Predicate_Occurrence));
			BigDecimal SUBJECT_OBJECT_TRIPLES = NO_OF_SUBJECT_TRIPLES.multiply(NO_OF_OBJECT_TRIPLES);

			// add a small epsilon = 10 power -18 to avoid zero in logarithm
			double PROBABILITY_PATH_PREDICATE = NO_PATH_PREDICATE_TRIPLES
					.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN).doubleValue() + 0.000000000000000001;
			BigDecimal NO_PATH_TRIPLES = new BigDecimal(Double.toString(count_Path_Occurrence));
			BigDecimal NO_OF_PREDICATE_TRIPLES = new BigDecimal(Integer.toString(counter.getPredicateTriplesCount()));
			double PROBABILITY_PATH = NO_PATH_TRIPLES.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN)
					.doubleValue();
			double PROBABILITY_PREDICATE = NO_OF_PREDICATE_TRIPLES
					.divide(SUBJECT_OBJECT_TRIPLES, 20, RoundingMode.HALF_EVEN).doubleValue();

			score = Math.log(PROBABILITY_PATH_PREDICATE / (PROBABILITY_PATH * PROBABILITY_PREDICATE))
					/ -Math.log(PROBABILITY_PATH_PREDICATE);
			return score;
		}

		catch (Exception ex) {
			ex.printStackTrace();
			return score;
		} finally {
			path.setFinalScore(score);
		}
	}

	public double npmiValue(double count_Path_Occurrence, double count_path_Predicate_Occurrence)
			throws IllegalArgumentException {
		double npmi;
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

		// Path and predicate never occur together
		if (count_path_Predicate_Occurrence == 0) {
			// Since we know that A and B exist, there is a chance that they should occur
			// together. Since it never happens, we have to return -1
			// npmi = -1;
			npmi = 0;
		} else {

			double logSubObjTriples = Math.log(counter.getSubjectTriplesCount())
					+ Math.log(counter.getObjectTriplesCount());
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
		return path.getFinalScore() + "" + path.toString();
	}
}
