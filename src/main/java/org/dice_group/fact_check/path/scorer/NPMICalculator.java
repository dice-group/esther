package org.dice_group.fact_check.path.scorer;

import java.util.List;
import java.util.Map;

import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice_group.fact_check.query.CountApproximatingQueryGenerator;
import org.dice_group.fact_check.query.PairCountingQueryGenerator;
import org.dice_group.fact_check.query.QueryGenerator;
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
	
	public NPMICalculator(Property path, OccurrencesCounter counter) {
		this.path = path;
		this.counter = counter;
	}

	public double calculatePMIScore(Map<Integer, String> id2rel) throws ParseException {
		List<Property> pathProperties= path.getProperties();
		String[] iris = PropertyHelper.translate2IRIArray(path, id2rel);
		
		QueryGenerator generator = new CountApproximatingQueryGenerator(counter.getSparqlExec());
		QueryGenerator pairGenerator = new PairCountingQueryGenerator();
		
		String pathQueryString = generator.createCountQuery(pathProperties, iris, counter.getStmt().getPredicate());
		String pathPredicateQueryString = pairGenerator.createCountQuery(pathProperties, iris, counter.getStmt().getPredicate());
		
		double count_Path_Occurrence = counter.getSparqlExec().selectDoubleVar(pathQueryString, "?sum");
		double count_path_Predicate_Occurrence = counter.getSparqlExec().selectDoubleVar(pathPredicateQueryString, "?sum");

		return npmiValue(count_Path_Occurrence, count_path_Predicate_Occurrence);

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
			return 0;
//			throw new IllegalArgumentException(
//					"The given path does never occur. The NPMI is not defined for this case.");
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
		if(npmi > 0)
			path.setPathNPMI(npmi);
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
		return path.getPathNPMI() + "" + path.toString();
	}
}
