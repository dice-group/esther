package org.dice_group.graph_search.modes;

import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.util.QueryExecutioner;

/**
 * The domain and range of other paths have to match exactly the one of the
 * given predicate. Both sets need to contain exactly the same elements
 *
 */
public class StrictDR extends Matrix {
	public StrictDR() {
		// TODO Auto-generated constructor stub
	}
	public StrictDR(QueryExecutioner sparqlExec, Dictionary dictionary) {
		super(sparqlExec, dictionary);
	}

	@Override
	public boolean compareSets(List<Resource> a, List<Resource> b) {
		return a.equals(b);
	}
	
	@Override
	public String toString() {
		return "Strict";
	}
	
}
