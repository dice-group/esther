package org.dice_group.graph_search.modes;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.util.iterator.ExtendedIterator;

public abstract class Matrix implements MatrixType {

	protected BitSet[] matrix;

	protected Map<String, Integer> class2id;

	protected Map<Integer, String> id2class;

	protected OntModel ontModel;

	public Matrix(OntModel ontModel) {
		this.ontModel = ontModel;

		class2id = new HashMap<String, Integer>();
		id2class = new HashMap<Integer, String>();

		ExtendedIterator<OntProperty> ontProperties = ontModel.listAllOntProperties();
		for (int i = 0; ontProperties.hasNext(); i++) {
			String curProp = ontProperties.next().toString();
			class2id.putIfAbsent(curProp, i);
			id2class.putIfAbsent(i, curProp);
		}

		matrix = new BitSet[class2id.size()];
	}

	@Override
	public void translate() {
		// TODO Auto-generated method stub
		
	}

	public BitSet[] getMatrix() {
		return matrix;
	}

	public void setMatrix(BitSet[] matrix) {
		this.matrix = matrix;
	}

	public Map<String, Integer> getClass2id() {
		return class2id;
	}

	public void setClass2id(Map<String, Integer> class2id) {
		this.class2id = class2id;
	}

	public Map<Integer, String> getId2class() {
		return id2class;
	}

	public void setId2class(Map<Integer, String> id2class) {
		this.id2class = id2class;
	}
}
