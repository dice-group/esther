package org.dice_group.graph_search.modes;

/**
 * Refers to the different possible ways of building the matrix for the A* search
 *
 */
public interface MatrixType {
	
	/**
	 * Computes a 2D binary matrix with relation to the allowed predicates dictated by the edge's domain and range.
	 * 
	 *  
	 * from| to		0		...		N
	 * 	0			0/1		...		0/1
	 *	.			
	 * 	.			
	 * 	.
	 * 	N
	 * 
	 * @param rel2id
	 * @param edge
	 */
	void compute(String edge);
	

}
