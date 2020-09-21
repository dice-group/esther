package org.dice_group.graph_search.modes;

import java.util.Collections;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.dice_group.embeddings.dictionary.Dictionary;

/**
 * If it has at least one element in common 
 *
 */
public class NotDisjointDR extends Matrix {
	
	public NotDisjointDR(OntModel ontology, Dictionary dictionary) {
		super(ontology, dictionary);
	}
	
	@Override
	public boolean compareSets(Set<? extends OntResource> a, Set<? extends OntResource> b) {
		return !Collections.disjoint(a, b);
	}
}
