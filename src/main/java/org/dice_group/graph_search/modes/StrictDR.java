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
	public boolean compareSets(Set<? extends OntResource> a, Set<? extends OntResource> b) {
		return a.equals(b);
	}
	
	
}
