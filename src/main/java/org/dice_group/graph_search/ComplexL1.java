package org.dice_group.graph_search;

/**
 * Computes the L1-norm for Complex vectors 
 *
 */
public class ComplexL1 implements Distance{

	@Override
	public double computeDistance(double[] head, double[] tail, double[] relation) {
		// h =  || h ° (r_1 ° r_2 ° ... ° r_n) - t|| -> if h is the curNode and tail the destination node
		// h = -||(r_1 ° r_2 ° ... ° r_n) - r_p||
		// L¹ norm of complex numbers is the sum of the absolute values of the vectors for all dimensions
		// r is unitary complex vector, but h and t aren't
		
		
		
		return 0;
	}

}
