package org.dice_group.graph_search.modes;

import java.util.BitSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.dice_group.embeddings.dictionary.Dictionary;

public abstract class Matrix implements MatrixInterface {

	protected BitSet[] edgeAdjMatrix;

	protected Dictionary dictionary;
	
	public Matrix (Dictionary dictionary) {
		this.dictionary = dictionary;
		edgeAdjMatrix = new BitSet[dictionary.getId2Relations().size() * 2];
	}

	public Matrix(OntModel ontModel, Dictionary dictionary) {
		this.dictionary = dictionary;
		edgeAdjMatrix = new BitSet[dictionary.getId2Relations().size() * 2];
		populateMatrix(ontModel);
	}

	public void populateMatrix(OntModel ontology) {
		if(this instanceof IrrelevantDR) {
			for (int i = 0; i < edgeAdjMatrix.length; i++) {
				edgeAdjMatrix[i].set(0, edgeAdjMatrix.length);
			}
			return;
		}
		
		Map<Integer, String> id2relmap = dictionary.getId2Relations();

		for (int i = 0; i < dictionary.getRelCount(); i++) {
			String curProperty = id2relmap.get(i);

			Set<? extends OntResource> domainI = ontology.getOntProperty(curProperty).listDomain().toSet();
			Set<? extends OntResource> rangeI = ontology.getOntProperty(curProperty).listRange().toSet();
			
			/**
			 * Since the matrix is extended by a factor of 2, this variable is also the
			 * offset between p_n and p-_n
			 */
			int relCount = dictionary.getRelCount();

			for (int j = 0; j < relCount; j++) {
				String curJ = id2relmap.get(j);

				Set<? extends OntResource> domainJ = ontology.getOntProperty(curJ).listDomain().toSet();
				Set<? extends OntResource> rangeJ = ontology.getOntProperty(curJ).listRange().toSet();

				// check domain - range : d_i(p) = r_j(p)
				if (compareSets(domainI, rangeJ)) {
					edgeAdjMatrix[i].set(j);
				}

				// check domain - domain : d_i(p) = d_j(p) [d_i(p) = r_j(p-)]
				if (compareSets(domainI, domainJ)) {
					edgeAdjMatrix[i].set(j + relCount);
				}

				// check range - range : r_i(p) = r_j(p) [d_i(p-) = r_j(p)]
				if (compareSets(rangeI, rangeJ)) {
					edgeAdjMatrix[i + relCount].set(j);
				}

				// check range - domain : r_i(p) = d_j(p) [d_i(p-) = r_j(p-)]
				if (compareSets(rangeI, domainJ)) {
					edgeAdjMatrix[i + relCount].set(j + relCount);
				}

			}
		}
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
}