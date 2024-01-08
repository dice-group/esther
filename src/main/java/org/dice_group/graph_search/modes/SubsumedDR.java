package org.dice_group.graph_search.modes;

import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.util.QueryExecutioner;
import org.dice_group.util.SparqlHelper;

/**
 * Check for subsumption relation (d = r) OR (r rdfs:subClassOf d)
 *
 */
public class SubsumedDR extends Matrix {
	
	public SubsumedDR() {
		// TODO Auto-generated constructor stub
	}
	
	public SubsumedDR(QueryExecutioner sparqlExec, Dictionary dictionary) {
		super(sparqlExec, dictionary);
	}

	@Override
	public boolean compareSets(List<Resource> a, List<Resource> b) {
		return isSubsumedBy(a, b);
	}

	/**
	 * A set d is subsumed under r if: (d = r) OR (r rdfs:subClassOf d)
	 * 
	 * @param a
	 * @param b
	 * @return true if set a subsumes b
	 */
	private boolean isSubsumedBy(List<Resource> a, List<Resource> b) {
		if(a.isEmpty() || b.isEmpty())
			return false;
		
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
	private boolean isSubSetOf(List<Resource> a, List<Resource> b) {
		for(Resource cur: b) {
			List<Resource> sub = sparqlExec.selectResources(SparqlHelper.getSubClassesQuery(cur.toString()));
			
			if(sub.isEmpty() || Collections.disjoint(a, sub)) {
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
