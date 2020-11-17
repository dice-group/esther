package org.dice_group.graph_search.modes;

import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.util.QueryExecutioner;
import org.dice_group.util.SparqlHelper;

public class NDSubsumedDR extends Matrix {

	public NDSubsumedDR(QueryExecutioner sparqlExec, Dictionary dictionary) {
		super(sparqlExec, dictionary);
	}

	@Override
	public boolean compareSets(List<Resource> a, List<Resource> b) {
		return isSubsumedBy(a, b);
	}

	/**
	 * if: (d = r) OR (r rdfs:subClassOf d)
	 * 
	 * @param a
	 * @param b
	 * @return true if set a subsumes b
	 */
	private boolean isSubsumedBy(List<Resource> a, List<Resource> b) {
		if (a.isEmpty() || b.isEmpty())
			return false;

		if (a.equals(b) || isSubSetOf(a, b)) {
			return true;
		}
		return false;
	}

	/**
	 * A set a is subset of b, if at least one element of a is a subclass of any element of b
	 * 
	 * @param a
	 * @param b
	 * @return true if a is subset of b
	 */
	private boolean isSubSetOf(List<Resource> a, List<Resource> b) {
		for (Resource cur : b) {
			List<Resource> sub = sparqlExec.selectResources(SparqlHelper.getSubClassesQuery(cur.toString()));

			if (sub.isEmpty())
				continue;

			if (!Collections.disjoint(a, sub)) {
				return true;
			}
		}
		return false;
	}
}
