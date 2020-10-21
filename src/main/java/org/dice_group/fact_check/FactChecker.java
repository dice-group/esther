package org.dice_group.fact_check;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.dice_group.fact_check.path.scorer.CubicMeanSummarist;
import org.dice_group.fact_check.path.scorer.NPMICalculator;
import org.dice_group.fact_check.path.scorer.OccurrencesCounter;
import org.dice_group.fact_check.path.scorer.ScoreSummarist;
import org.dice_group.path.Graph;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyHelper;
import org.dice_group.util.QueryExecutioner;
import org.dice_group.util.SparqlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(FactChecker.class);

	private Model testData;

	private QueryExecutioner sparqlExec;

	public FactChecker(String fileName, QueryExecutioner sparqlExec) {
		testData = ModelFactory.createDefaultModel();
		testData.read(fileName);
		this.sparqlExec = sparqlExec;
	}

	public Set<Graph> checkFacts(Map<String, Set<Property>> metaPaths, Map<Integer, String> id2rel) {
		Set<Graph> graphs = new HashSet<Graph>();
		StmtIterator checkStmts = testData.listStatements();
		for (int i = 1; checkStmts.hasNext(); i++) {
			Statement curStmt = checkStmts.next();

			// get precalculated meta-path
			Set<Property> p = new HashSet<Property>(metaPaths.get(curStmt.getPredicate().toString()));
			
			// remove if meta-path not present in graph
			p.removeIf(curProp -> !SparqlHelper.askModel(sparqlExec.getRequestURL(),
					SparqlHelper.getAskQuery(PropertyHelper.getPropertyPath(curProp, id2rel),
							curStmt.getSubject().toString(), curStmt.getObject().toString())));

			// no paths found
			if (p.isEmpty()) {
				graphs.add(new Graph(p, 0, curStmt));
				LOGGER.warn("Skipping edge "+i+" due to no paths found: "+curStmt);
				continue;
			}

			// count occurrences
			OccurrencesCounter c = new OccurrencesCounter(curStmt, sparqlExec, false);
			c.count();

			// calculate npmi for each path found
			for (Property path : p) {
				if (c.getSubjectTypes().isEmpty() || c.getObjectTypes().isEmpty()) {
					graphs.add(new Graph(p, 0, curStmt));
					LOGGER.warn("Skipping edge "+i+" due to no subj/obj types: "+curStmt);
					continue;
				}
				NPMICalculator cal = new NPMICalculator(path, id2rel, c);
				try {
					cal.calculatePMIScore();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			// aggregate scores
			double[] scores = new double[p.size()];
			scores = p.stream().mapToDouble(s -> s.getFinalScore()).toArray();

			ScoreSummarist summarist = new CubicMeanSummarist();
			double score = summarist.summarize(scores);
			
			LOGGER.info(i + "/" + testData.size() + " : " + score + " - " + curStmt.toString());
			graphs.add(new Graph(p, score, curStmt));
		}
		return graphs;
	}

}