package org.dice_group.fact_check;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.RDF;
import org.dice_group.fact_check.path.scorer.CubicMeanSummarist;
import org.dice_group.fact_check.path.scorer.ScoreSummarist;
import org.dice_group.path.Graph;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyHelper;
import org.dice_group.util.PrintToFileUtils;
import org.dice_group.util.QueryExecutioner;
import org.dice_group.util.SparqlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(FactChecker.class);

	private Model reifiedStmts;

	private QueryExecutioner sparqlExec;

	private static final org.apache.jena.rdf.model.Property TRUTH_VALUE = ResourceFactory
			.createProperty("http://swc2017.aksw.org/hasTruthValue");

	public FactChecker(String fileName, QueryExecutioner sparqlExec) {
		reifiedStmts = ModelFactory.createDefaultModel();
		reifiedStmts.read(fileName);
		this.sparqlExec = sparqlExec;
	}

	/**
	 * Computes the veracity of the facts and saves both the applicable meta-paths
	 * and the score to file
	 * 
	 * @param metaPaths Map of pre-computed meta-paths for all possible predicates
	 * @param id2rel    Map of ID to Predicate URI
	 * @param savePath  Desired file name for the results file, the meta-paths will
	 *                  be saved under the same name with a suffix
	 */
	public void checkFacts(Map<String, Set<Property>> metaPaths, Map<Integer, String> id2rel, String savePath) {
		Model resultsModel = ModelFactory.createDefaultModel();
		StringBuilder effectiveMetaPaths = new StringBuilder();
		StmtIterator stmtIter = reifiedStmts.listStatements(null, RDF.type, RDF.Statement);
		for (int i = 1; stmtIter.hasNext(); i++) {
			Resource subject = stmtIter.next().getSubject();
			Statement statement = new StatementImpl(subject.getPropertyResourceValue(RDF.subject),
					subject.getPropertyResourceValue(RDF.predicate).as(org.apache.jena.rdf.model.Property.class),
					subject.getPropertyResourceValue(RDF.object));

			Graph singleGraph = checkSingleFact(statement, metaPaths.get(statement.getPredicate().toString()), id2rel);

			resultsModel.add(ResourceFactory.createStatement(subject, TRUTH_VALUE, ResourceFactory
					.createTypedLiteral(Double.toString(singleGraph.getScore()), XSDDatatype.XSDdouble)));

			effectiveMetaPaths.append(singleGraph.getPrintableResults(id2rel));

			LOGGER.info(i +" : " + singleGraph.getScore() + " - " + singleGraph.getTriple());
		}

		PrintToFileUtils.printStringToFile(effectiveMetaPaths.toString(), new File(savePath + "_paths.txt"));
		PrintToFileUtils.printRDFToFile(resultsModel, savePath + ".nt");
	}

	/**
	 * Similar to checkFacts, but does so in parallel
	 * 
	 * @param metaPaths Map of pre-computed meta-paths for all possible predicates
	 * @param id2rel    Map of ID to Predicate URI
	 * @param savePath  Desired file name for the results file, the meta-paths will
	 *                  be saved under the same name with a suffix
	 */
	public void checkFactsParallel(Map<String, Set<Property>> metaPaths, Map<Integer, String> id2rel, String savePath) {
		Model resultsModel = ModelFactory.createDefaultModel();
		List<Statement> stmts = reifiedStmts.listStatements(null, RDF.type, RDF.Statement).toList();
		StringBuffer effectiveMetaPaths = new StringBuffer();
		stmts.parallelStream().map(s -> s.getSubject()).forEach(r -> {
			Graph singleGraph = checkSingle(r, metaPaths, id2rel);
			LOGGER.info(r + " - " + singleGraph.getScore() + " - " + singleGraph.getTriple());
			effectiveMetaPaths.append(singleGraph.getPrintableResults(id2rel));
			synchronized (resultsModel) {
				resultsModel.add(ResourceFactory.createStatement(r, TRUTH_VALUE, ResourceFactory
						.createTypedLiteral(Double.toString(singleGraph.getScore()), XSDDatatype.XSDdouble)));
			}
		});
		PrintToFileUtils.printStringToFile(effectiveMetaPaths.toString(), new File(savePath + "_paths.txt"));
		PrintToFileUtils.printRDFToFile(resultsModel, savePath + ".nt");
	}

	/**
	 * Computes the veracity score for one fact
	 * 
	 * @param subject   The statement number (since we're using reified statements)
	 * @param metaPaths Map of pre-computed meta-paths for all possible predicates
	 * @param id2rel    Map of ID to Predicate URI
	 * @return The graph object containing the triple, its equivalent paths and
	 *         their individual scores and the aggregated score.
	 */
	public Graph checkSingle(Resource subject, Map<String, Set<Property>> metaPaths, Map<Integer, String> id2rel) {
		Statement statement = new StatementImpl(subject.getPropertyResourceValue(RDF.subject),
				subject.getPropertyResourceValue(RDF.predicate).as(org.apache.jena.rdf.model.Property.class),
				subject.getPropertyResourceValue(RDF.object));
		return checkSingleFact(statement, metaPaths.get(statement.getPredicate().toString()), id2rel);

	}

	/**
	 * Computes the veracity score for one fact
	 * 
	 * @param fact      The fact we want to check
	 * @param metaPaths Map of pre-computed meta-paths for all possible predicates
	 * @param id2rel    Map of ID to Predicate URI
	 * @return The graph object containing the triple, its equivalent paths and
	 *         their individual scores and the aggregated score.
	 */
	public Graph checkSingleFact(Statement fact, Set<Property> metaPaths, Map<Integer, String> id2rel) {
		Set<Property> newMetaPaths = new HashSet<Property>(metaPaths);
		int pSize = newMetaPaths.size();

		// remove if meta-path not present in graph
		newMetaPaths.removeIf(
				curProp -> !sparqlExec.ask(SparqlHelper.getAskQuery(PropertyHelper.getPropertyPath(curProp, id2rel),
						fact.getSubject().toString(), fact.getObject().toString())));

		// if there are metapaths but none apply, assume -0.1
		// if no metapaths are found, 0.0
		if (newMetaPaths.isEmpty()) {
			double npmi = pSize != 0 ? -0.1 : 0;
			return new Graph(newMetaPaths, npmi, fact);
		}

		// aggregate scores
		double[] scores = new double[newMetaPaths.size()];
		scores = newMetaPaths.stream().mapToDouble(s -> s.getPathNPMI()).toArray();

		ScoreSummarist summarist = new CubicMeanSummarist();
		double score = summarist.summarize(scores);

		return new Graph(newMetaPaths, score, fact);
	}

}
