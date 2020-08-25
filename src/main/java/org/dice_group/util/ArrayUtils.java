package org.dice_group.util;

import java.util.Arrays;
import java.util.function.IntToDoubleFunction;

public class ArrayUtils {

	/**
	 * Computes the element-wise product of 2 vectors
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[] computeHadamardProduct(double[] a, double[] b) {
		return computeArrayOperation(a, b, i -> a[i] * b[i]);
	}

	/**
	 * Computes the element-wise subtraction of 2 vectors
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[] computeVectorSubtraction(double[] a, double[] b) {
		return computeArrayOperation(a, b, i -> a[i] - b[i]);
	}

	/**
	 * Computes the absolute value of a complex vector v with real part a and
	 * imaginary part b
	 * 
	 * @param a the real part of a vector v
	 * @param b the imaginary part of a vector v
	 * @return
	 */
	public static double[] computeAbsoluteValue(double[] a, double[] b) {
		return computeArrayOperation(a, b, i -> Math.sqrt(Math.pow(a[i], 2) + Math.pow(b[i], 2)));
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @param generator
	 * @return
	 */
	public static double[] computeArrayOperation(double[] a, double[] b, IntToDoubleFunction generator) {
		if (a.length > b.length)
			throw new IllegalArgumentException("");

		double[] result = new double[a.length];
		Arrays.parallelSetAll(result, generator);
		return result;
	}

}
