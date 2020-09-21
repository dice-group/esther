package org.dice_group.graph_search.modes;

import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.dice_group.embeddings.dictionary.Dictionary;

/**
 * The domain and range of other paths have to match exactly the one of the
 * given predicate. Both sets need to contain exactly the same elements
 *
 */
public class StrictDR extends Matrix {

	public StrictDR(OntModel ontology, Dictionary dictionary) {
		super(ontology, dictionary);
	}

	@Override
	public void compute(Set<? extends OntResource> domainI, Set<? extends OntResource> rangeI,
			Set<? extends OntResource> domainJ, Set<? extends OntResource> rangeJ, int i, int j) {
		/**
		 * Since the matrix is extended by a factor of 2, this variable is also the
		 * offset between p_n and p-_n
		 */
		int offset = dictionary.getRelCount();

		// check domain - range : d_i(p) = r_j(p)
		if (domainI.equals(rangeJ)) {
			edgeAdjMatrix[i].set(j);
		}

		// check domain - domain : d_i(p) = d_j(p) [d_i(p) = r_j(p-)]
		if (domainI.equals(domainJ)) {
			edgeAdjMatrix[i].set(j + offset);
		}

		// check range - range : r_i(p) = r_j(p) [d_i(p-) = r_j(p)]
		if (rangeI.equals(rangeJ)) {
			edgeAdjMatrix[i + offset].set(j);
		}

		// check range - domain : r_i(p) = d_j(p) [d_i(p-) = r_j(p-)]
		if (rangeI.equals(domainJ)) {
			edgeAdjMatrix[i + offset].set(j + offset);
		}
	}
}
