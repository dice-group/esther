package org.dice_group.path;

import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.embeddings.dictionary.EdgeDictionary;

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import toools.collections.primitive.LucIntSet;

public class Graph {

	private Grph grph;
	private Dictionary dictionary;
	private EdgeDictionary edgeDictionary;
	private int offset;

	public Graph() {
		grph = new InMemoryGrph();
		dictionary = new Dictionary();
		edgeDictionary = new EdgeDictionary();
	}

	public Graph(Grph grph, Dictionary dictionary) {
		this.grph = grph;
		this.dictionary = dictionary;
		this.offset = grph.getNumberOfEdges() / 2;
	}

	public Graph(Model model, Dictionary dictionary) {
		grph = new InMemoryGrph();
		edgeDictionary = new EdgeDictionary();
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
			edgeDictionary.addEdge(grph.addDirectedSimpleEdge(subjID, objID), objID);
		}
		this.offset = grph.getNumberOfEdges() / 2;
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

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public EdgeDictionary getEdgeDictionary() {
		return edgeDictionary;
	}

	public void setEdgeDictionary(EdgeDictionary edgeDictionary) {
		this.edgeDictionary = edgeDictionary;
	}

	public boolean isNodeConnectedToEdge(int edge, int node) {
		LucIntSet vertices = grph.getVerticesIncidentToEdge(edge);
		if (vertices.contains(node)) {
			return true;
		}
		return false;

	}
	
	public int getPropertyFromEdge(int id) {
		return edgeDictionary.getEdges2Prop().getOrDefault(id, -1);
	}

	/**
	 * 
	 * @param id
	 * @return the id of the given edge in the graph 
	 */
	public int getGraphEdgeID(int id) {
		int gEdge = id;
		if (id >= offset) {
			gEdge = id - offset;
		}
		return gEdge;
	}

}
