package org.dice_group.graph_search.modes;

import java.util.BitSet;

import org.apache.jena.ontology.OntModel;

import org.dice_group.embeddings.dictionary.Dictionary;

public abstract class Matrix implements MatrixType {

	protected BitSet[] edgeAdjMatrix;

	protected Dictionary dictionary;

	/**
	 * hierarchy of types TODO remove the useless one
	 */
	protected OntModel ontology;

	public Matrix(OntModel ontModel, Dictionary dictionary) {
		this.ontology = ontModel;
		this.dictionary = dictionary;
		edgeAdjMatrix = new BitSet[dictionary.getId2Relations().size()];
	}

	public BitSet[] getEdgeAdjMatrix() {
		return edgeAdjMatrix;
	}

	public void setEdgeAdjMatrix(BitSet[] edgeAdjMatrix) {
		this.edgeAdjMatrix = edgeAdjMatrix;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public OntModel getOntology() {
		return ontology;
	}

	public void setOntology(OntModel ontology) {
		this.ontology = ontology;
	}
}
