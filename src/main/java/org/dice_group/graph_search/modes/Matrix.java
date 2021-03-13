package org.dice_group.graph_search.modes;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.util.QueryExecutioner;
import org.dice_group.util.SparqlHelper;

public abstract class Matrix implements MatrixInterface {

	protected BitSet[] edgeAdjMatrix;

	protected Dictionary dictionary;

	protected QueryExecutioner sparqlExec;

	public Matrix() {
	}

	public Matrix(Dictionary dictionary) {
		this.dictionary = dictionary;
		edgeAdjMatrix = new BitSet[dictionary.getId2Relations().size() * 2];
		for (int i = 0; i < edgeAdjMatrix.length; i++)
			edgeAdjMatrix[i] = new BitSet();
	}

	public Matrix(QueryExecutioner sparqlExec, Dictionary dictionary) {
		this.dictionary = dictionary;
		edgeAdjMatrix = new BitSet[dictionary.getId2Relations().size() * 2];
		for (int i = 0; i < edgeAdjMatrix.length; i++)
			edgeAdjMatrix[i] = new BitSet();
		this.sparqlExec = sparqlExec;
	}

	/**
	 * Populates the d/r edge adjacency matrix
	 */
	public void populateMatrix() {		
		if (this instanceof IrrelevantDR) {
			for (int i = 0; i < edgeAdjMatrix.length; i++) {
				edgeAdjMatrix[i].set(0, edgeAdjMatrix.length);
			}
			return;
		}

		Map<Integer, String> id2relmap = dictionary.getId2Relations();

		for (int i = 0; i < dictionary.getRelCount(); i++) {
			String curProperty = id2relmap.get(i);

			List<Resource> domainI = sparqlExec.selectResources(SparqlHelper.getDomainQuery(curProperty));
			List<Resource> rangeI = sparqlExec.selectResources(SparqlHelper.getRangeQuery(curProperty));

			int offset = dictionary.getRelCount();

			for (int j = 0; j < offset; j++) {
				String curJ = id2relmap.get(j);

				List<Resource> domainJ = sparqlExec.selectResources(SparqlHelper.getDomainQuery(curJ));
				List<Resource> rangeJ = sparqlExec.selectResources(SparqlHelper.getRangeQuery(curJ));

				// check domain - range : d_i(p) = r_j(p)
				if (compareSets(domainI, rangeJ)) {
					edgeAdjMatrix[i].set(j);
				}

				// check domain - domain : d_i(p) = d_j(p) 
				if (compareSets(domainI, domainJ)) {
					edgeAdjMatrix[i].set(j + offset);
				}

				// check range - range : r_i(p) = r_j(p) 
				if (compareSets(rangeI, rangeJ)) {
					edgeAdjMatrix[i + offset].set(j);
				}

				// check range - domain : r_i(p) = d_j(p) 
				if (compareSets(rangeI, domainJ)) {
					edgeAdjMatrix[i + offset].set(j + offset);
				}

			}
		}
	}

	/**
	 * 
	 * @param id
	 * @return the inverse id of a relation in the matrix
	 */
	public int getInverseID(int id) {
		int offset = edgeAdjMatrix.length / 2;
		return id >= offset ? id - offset : id + offset;
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

	@Override
	public String toString() {
		return Arrays.toString(edgeAdjMatrix);
	}

}
