package org.dice_group.graph_search.modes;

import java.util.BitSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.dice_group.embeddings.dictionary.Dictionary;

public abstract class Matrix implements MatrixType {

	protected BitSet[] edgeAdjMatrix;

	protected Dictionary dictionary;

	protected OntModel ontology;

	public Matrix(OntModel ontModel, Dictionary dictionary) {
		this.ontology = ontModel;
		this.dictionary = dictionary;
		edgeAdjMatrix = new BitSet[dictionary.getId2Relations().size()*2];
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
	
	public void populateMatrix() {
		Map<Integer, String> id2relmap = dictionary.getId2Relations();

		for (int i = 0; i < dictionary.getRelCount(); i++) {
			String curProperty = id2relmap.get(i);

			Set<? extends OntResource> domainI = ontology.getOntProperty(curProperty).listDomain().toSet();
			Set<? extends OntResource> rangeI = ontology.getOntProperty(curProperty).listRange().toSet();

			for (int j = 0; j < dictionary.getRelCount(); j++) {
				String curJ = id2relmap.get(j);

				Set<? extends OntResource> domainJ = ontology.getOntProperty(curJ).listDomain().toSet();
				Set<? extends OntResource> rangeJ = ontology.getOntProperty(curJ).listRange().toSet();

				compute(domainI, rangeI, domainJ, rangeJ, i, j);

			}
		}
	}
}
