package org.dice_group.graph_search.modes;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.dice_group.embeddings.dictionary.Dictionary;

public class ChainedDR extends Matrix {
	private Model ontModel;

	public ChainedDR(OntModel ontology, Dictionary dictionary) {
		super(ontology, dictionary);
		ontModel = ontology.getBaseModel();
	}

	@Override
	public void compute(String edge) {
		// get domain of P
		ExtendedIterator<? extends OntResource> domain = ontology.getOntProperty(edge).listDomain();
		while (domain.hasNext()) {
			OntResource cur = domain.next();
			Set<Resource> closed = new HashSet<Resource>();
			connectChain(closed, cur);
		}
	}

	/**
	 * Follows a one directional chain of properties, find the properties for which
	 * the domain corresponds to the range of the previous iteration (d_1,...,d_n)
	 * ---(p_1,...,p_n)----> (r_1,...,r_n) ---(p²_1,...,p²_n)----> (r²_1,...,r²_n).
	 * Even though they are iterators, most often than not, they contain only one
	 * element. TODO there might be a better way than this recursion to achieve the
	 * intended
	 * 
	 * 1) Get properties (p_1,...,p_n) that have the same domain as P
	 * 2) Get range of properties (p_1,...,p_n)
	 * 3) Get properties (p²_1,...,p²_n) that have the same domain as (p_1,...,p_n)
	 * 4) Repeat to form chains
	 * @param closed set of explored properties
	 * 
	 * @param dPropP domain of given property P
	 */
	private void connectChain(Set<Resource> closed, Resource dPropP) {
		ResIterator nPropIter = ontModel.listSubjectsWithProperty(RDFS.domain, dPropP); // (p_1,...,p_n)
		while (nPropIter.hasNext()) {
			Resource curProp = nPropIter.next();
			if(closed.contains(curProp))
				continue;
			closed.add(curProp);
			int rowID = dictionary.getRelations2ID().getOrDefault(curProp.toString(), -1);
			System.out.print(rowID+"-");
			// get range of p_n
			NodeIterator rangeNProp = ontModel.listObjectsOfProperty(curProp, RDFS.range);
			// find properties with the domain same as the range of p_n (get
			// (p²_1,...,p²_n))
			while (rangeNProp.hasNext()) {
				Resource cur2Prop = rangeNProp.next().asResource();
				connectChain(closed, cur2Prop); // (p²_1,...,p²_n)
				
				// connect (p_1,...,p_n) to (p²_1,...,p²_n)
				//edgeAdjMatrix[rowID].set(colID);
				//edgeAdjMatrix[colID].set(rowID);
			}
		}
		System.out.println("\n");
	}
}
