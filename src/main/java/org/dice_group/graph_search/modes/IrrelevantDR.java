package org.dice_group.graph_search.modes;

import org.apache.jena.ontology.OntModel;
import org.dice_group.embeddings.dictionary.Dictionary;

/**
 * Doesn't care at all for domain and range consistency, thus all bits are set.
 *
 */
public class IrrelevantDR extends Matrix {

	public IrrelevantDR(OntModel ontModel, Dictionary dictionary) {
		super(ontModel, dictionary);
	}

	@Override
	public void compute(String edge) {
		for (int i = 0; i < edgeAdjMatrix.length; i++) {
			edgeAdjMatrix[i].set(0, edgeAdjMatrix.length); // square matrix
		}
	}
}
