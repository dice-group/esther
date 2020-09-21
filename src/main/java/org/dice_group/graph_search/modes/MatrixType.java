package org.dice_group.graph_search.modes;

import java.util.Set;

import org.apache.jena.ontology.OntResource;

/**
 * Refers to the different possible ways of building the matrix for the A* search
 *
 */
public interface MatrixType {
	
	/**
	 * Computes a 2D binary matrix with relation to the allowed predicates dictated by the edge's domain and range.
	 * 
	 *  
	 * domain| range		0		...		N		|	N+1 	... 	N*2
	 * 	0					0/1		...									0/1
	 *	.			
	 * 	.			
	 * 	.
	 * 	N
	 * __
	 * N+1
	 * .
	 * .
	 * .
	 * N*2
	 * @param j 
	 * @param i 
	 * @param rangeJ 
	 * @param domainJ 
	 * @param rangeI 
	 * @param domainI 
	 * 
	 * @param rel2id
	 * @param edge
	 */
	void compute(Set<? extends OntResource> domainI, Set<? extends OntResource> rangeI, Set<? extends OntResource> domainJ, Set<? extends OntResource> rangeJ, int i, int j);
	

}
