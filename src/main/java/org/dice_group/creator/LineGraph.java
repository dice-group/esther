package org.dice_group.creator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_group.edges.UndirectedEdge;
import org.dice_group.util.GraphUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.WeightedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineGraph {
	private static final Logger LOGGER = LoggerFactory.getLogger(LineGraph.class);

	private Graph<Property, UndirectedEdge> graph;

	private final String[] types = { "SUBJ", "OBJ", "MIXED" };

	public LineGraph() {
		graph = new WeightedPseudograph<Property, UndirectedEdge>(UndirectedEdge.class);
	}

	public LineGraph(Model model) {
		graph = new WeightedPseudograph<Property, UndirectedEdge>(UndirectedEdge.class);
		createLineGraph(model);
	}

	public void createLineGraph(Model model) {
		Map<String, String> edgeTypes = new HashMap<String, String>();
		edgeTypes.put(types[0], "select * where { ?n ?p1 ?q . ?n ?p2 ?n2 . filter (?p1!=?p2 || ?q!=?n2). } ");
		edgeTypes.put(types[1], "select * where { ?q ?p1 ?n . ?n2 ?p2 ?n . filter (?p1!=?p2 || ?q!=?n2). } ");
		edgeTypes.put(types[2], "select * where { ?n ?p1 ?q . ?n2 ?p2 ?n . filter (?p1!=?p2 || ?q!=?n2). } ");

		edgeTypes.forEach((id, query) -> {
			List<QuerySolution> solutions = GraphUtils.selectModel(model, query);
			addResultsToGraph(solutions, id);
		});

	}

	private void addResultsToGraph(List<QuerySolution> solutions, String type) {
		// removing the inverted statements (duplicates), there might be a better
		// solution to this problem 
		Iterator<QuerySolution> iter = solutions.iterator();
		while (iter.hasNext()) {
			QuerySolution curSol = iter.next();
			if (solutions.stream()
					.anyMatch(b -> b.get("n2").equals(curSol.get("q")) && b.get("q").equals(curSol.get("n2")))) {
				iter.remove();
			}
		}

		for (QuerySolution curSol : solutions) {
			RDFNode p1 = curSol.get("p1");
			RDFNode p2 = curSol.get("p2");

			Property p1P = ResourceFactory.createProperty(p1.toString());
			Property p2P = ResourceFactory.createProperty(p2.toString());

			graph.addVertex(p1P);
			graph.addVertex(p2P);

			// edge type doesn't affect the way the connection is built
			UndirectedEdge edge = new UndirectedEdge(type, p1P, p2P);

			// add the edge, otherwise increment the weight
			if (!graph.addEdge(p1P, p2P, edge)) {
				graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) + 1);
			}
		}
	}

	public Graph<Property, UndirectedEdge> getGraph() {
		return graph;
	}

	public void setGraph(Graph<Property, UndirectedEdge> graph) {
		this.graph = graph;
	}

}
