package org.dice_group.graph_search.modes;

import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.dice_group.embeddings.dictionary.Dictionary;

/**
 * Doesn't care at all for domain and range consistency, thus all bits are set.
 *
 */
public class IrrelevantDR extends Matrix {
	
	public IrrelevantDR() {
	}

	public IrrelevantDR(String requestURL, Dictionary dictionary) {
		super(requestURL, dictionary);
	}

	@Override
	public boolean compareSets(List<Resource> a, List<Resource> b) {
		return true;
	}
	
	@Override
	public String toString() {
		return "Irrelevant";
	}
}
