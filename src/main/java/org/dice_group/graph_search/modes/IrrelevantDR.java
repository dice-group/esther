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
	public boolean compareSets(Set<? extends OntResource> a, Set<? extends OntResource> b) {
		return true;
	}
}
