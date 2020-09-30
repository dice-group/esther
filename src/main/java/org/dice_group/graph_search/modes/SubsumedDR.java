package org.dice_group.graph_search.modes;

import java.util.Collections;
import java.util.Set;

import org.apache.jena.ontology.OntClass;
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
	private boolean isSubsumedBy(Set<? extends OntResource> a, Set<? extends OntResource> b) {
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
	private boolean isSubSetOf(Set<? extends OntResource> a, Set<? extends OntResource> b) {
		for(OntResource cur: b) {
			OntClass curClass = ontology.getOntClass(cur.toString());
			if(curClass == null) {
				return false;
			}
			Set<OntClass> sub = curClass.listSubClasses(false).toSet();
			
			if(Collections.disjoint(a, sub)) {
				return false;
			} 
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "Subsumed";
	}

}
