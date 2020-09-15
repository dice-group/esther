package org.dice_group.graph_search.modes;

import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 * The domain and range of other paths have to match exactly the one of the given predicate.
 *
 */
public class StrictDR extends Matrix {

	public StrictDR(OntModel ontModel) {
		super(ontModel);
	}

	@Override
	public void compute(Map<String, Integer> rel2id, String edge) {
		ExtendedIterator<? extends OntResource> domain = ontModel.getOntProperty(edge).listDomain();
		Set<? extends OntResource> range = ontModel.getOntProperty(edge).listRange().toSet();
		while (domain.hasNext()) {
			OntResource curRes = domain.next();
			int startID = class2id.get(curRes.toString());
			for (OntResource curDest : range) {
				int destID = class2id.get(curDest.toString());
				// TODO confirm whether we want it to be a symmetric matrix or not m(i,j) = m(j,i)
				matrix[startID].set(destID);
				matrix[destID].set(startID);
			}
		}
	}
}
