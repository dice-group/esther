package org.dice_group.graph_search.modes;

import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.dice_group.embeddings.dictionary.Dictionary;

/**
 * Instead of set equality like StrictDR, we check for a relation of subsumption
 * instead (d = r) OR (r rdfs:subClassOf d)
 *
 */
public class DRSubsumption extends Matrix {

	public DRSubsumption(OntModel ontModel, Dictionary dictionary) {
		super(ontModel, dictionary);
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
		if (isSubsumedBy(domainI, rangeJ)) {
			edgeAdjMatrix[i].set(j);
		}

		// check domain - domain : d_i(p) = d_j(p) [d_i(p) = r_j(p-)]
		if (isSubsumedBy(domainI, domainJ)) {
			edgeAdjMatrix[i].set(j + offset);
		}

		// check range - range : r_i(p) = r_j(p) [d_i(p-) = r_j(p)]
		if (isSubsumedBy(rangeI, rangeJ)) {
			edgeAdjMatrix[i + offset].set(j);
		}

		// check range - domain : r_i(p) = d_j(p) [d_i(p-) = r_j(p-)]
		if (isSubsumedBy(rangeI, domainJ)) {
			edgeAdjMatrix[i + offset].set(j + offset);
		}
	}

	/**
	 * A set d is subsumed under r if: (d = r) OR (r rdfs:subClassOf d)
	 * 
	 * @param a
	 * @param b
	 * @return true if set a subsumes b
	 */
	private boolean isSubsumedBy(Set<?> a, Set<?> b) {
		if (a.equals(b) || isSubSetOf(a, b)) {
			return true;
		}
		return false;
	}

	/**
	 * A set a is subset of b, if all elements of a are subclasses of elements of b
	 * 
	 * @param a
	 * @param b
	 * @return true if a is subset of b
	 */
	private boolean isSubSetOf(Set<?> a, Set<?> b) {

		return false;
	}

}
