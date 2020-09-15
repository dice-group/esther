package org.dice_group.graph_search.modes;

import java.util.Map;

import org.apache.jena.ontology.OntModel;

/**
 * Doesn't care at all for domain and range consistency, thus all bits are set.
 *
 */
public class IrrelevantDR extends Matrix {

	public IrrelevantDR(OntModel ontModel) {
		super(ontModel);
	}

	@Override
	public void compute(Map<String, Integer> rel2id, String edge) {
		for(int i = 0; i< matrix.length; i++) {
			matrix[i].set(0, matrix.length); // square matrix
		}
	}
}
