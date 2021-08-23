package org.dice_group.graph_search.modes;

import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.util.QueryExecutioner;

/**
 * If it has at least one element in common 
 *
 */
public class NotDisjointDR extends Matrix {
	
	public NotDisjointDR() {
	}
	
	public NotDisjointDR(QueryExecutioner sparqlExec, Dictionary dictionary) {
		super(sparqlExec, dictionary);
	}
	
	@Override
	public boolean compareSets(List<Resource> a, List<Resource> b) {
		return !Collections.disjoint(a, b);
	}
	
	@Override
	public String toString() {
		return "Non-disjoint";
	}
}
