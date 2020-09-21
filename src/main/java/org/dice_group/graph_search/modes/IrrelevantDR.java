package org.dice_group.graph_search.modes;

import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
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
	public void compute(Set<? extends OntResource> domainI, Set<? extends OntResource> rangeI,
			Set<? extends OntResource> domainJ, Set<? extends OntResource> rangeJ, int i, int j) {
		for (int w = 0; w < edgeAdjMatrix.length; w++) {
			edgeAdjMatrix[w].set(0, edgeAdjMatrix.length); // square matrix
		}
	}
}
