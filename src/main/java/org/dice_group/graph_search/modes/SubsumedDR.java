package org.dice_group.graph_search.modes;

import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.dice_group.embeddings.dictionary.Dictionary;

/**
 * Check for subsumption relation (d = r) OR (r rdfs:subClassOf d)
 *
 */
public class SubsumedDR extends Matrix {

	public SubsumedDR(OntModel ontModel, Dictionary dictionary) {
		super(ontModel, dictionary);
	}

	@Override
	public boolean compareSets(Set<? extends OntResource> a, Set<? extends OntResource> b) {
		return isSubsumedBy(a, b);
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
