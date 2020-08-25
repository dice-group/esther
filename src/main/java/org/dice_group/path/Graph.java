package org.dice_group.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.graph_search.ComplexL1;

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import it.unimi.dsi.fastutil.ints.IntSet;

public class Graph {

	private Grph grph;
	private Dictionary dictionary;

	public Graph() {
		grph = new InMemoryGrph();
	}

	public Graph(Grph grph, Dictionary dictionary) {
		this.grph = grph;
		this.dictionary = dictionary;
	}

	public Graph(Model model, Dictionary dictionary) {
		grph = new InMemoryGrph();
		this.dictionary = dictionary;

		Map<String, Integer> entities2ID = dictionary.getEntities2ID();
		Map<String, Integer> rel2ID = dictionary.getRelations2ID();

		StmtIterator iterator = model.listStatements();
		while (iterator.hasNext()) {
			Statement curStmt = iterator.next();

			int subjID = entities2ID.getOrDefault(curStmt.getSubject().toString(), -1);
			int objID = entities2ID.getOrDefault(curStmt.getObject().toString(), -1);
			int predID = rel2ID.getOrDefault(curStmt.getPredicate().toString(), -1);

			if (subjID < 0 || objID < 0 || predID < 0) {
				// give warning and skip it
				continue;
			}
			grph.addDirectedSimpleEdge(subjID, predID, objID);
		}
	}

	public Grph getGrph() {
		return grph;
	}

	public void setGrph(Grph grph) {
		this.grph = grph;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	public List<Node> getUndirectedSuccessors(Node node, ComplexL1 scorer, double[][] relations) {
		List<Node> succNodes = new ArrayList<Node>();
		IntSet edges = grph.getEdgesIncidentTo(node.getNodeID());
		for (int i : edges) {
			IntSet nodes = grph.getVerticesAccessibleThrough(node.getNodeID(), i);
			double score = scorer.computeDistance(node, relations[i]);
			for (int j : nodes) {
				succNodes.add(new Node(new BackPointer(node, i), j, node.getPathLength() + 1, score));
			}
		}
		return succNodes;
	}

}
