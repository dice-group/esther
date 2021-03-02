package org.dice_group.util;

import java.util.Arrays;
import java.util.function.IntToDoubleFunction;

import org.apache.commons.math3.complex.Quaternion;

public class ArrayUtils {

	public static Quaternion[] getQuaternion(double[] relW, double[] relX, double[] relY, double[] relZ) {
		Quaternion[] quatArray = new Quaternion[relW.length];
		Arrays.parallelSetAll(quatArray, i -> new Quaternion(relW[i], relX[i], relY[i], relZ[i]));
		return quatArray;
	}

	public static Quaternion[] getInverseQuat(Quaternion[] q) {
		Quaternion[] result = new Quaternion[q.length];
		Arrays.parallelSetAll(result, i -> q[i].getInverse());
		return result;
	}

	public static Quaternion[] computeHamiltonProduct(Quaternion[] a, Quaternion[] b) {
		Quaternion[] result = new Quaternion[a.length];
		Arrays.parallelSetAll(result, i -> a[i].multiply(b[i]));
		return result;
	}

	public static Quaternion[] computeQuatSubtraction(Quaternion[] a, Quaternion[] b) {
		Quaternion[] result = new Quaternion[a.length];
		Arrays.parallelSetAll(result, i -> a[i].subtract(b[i]));
		return result;
	}
	
	public static double[] getQuatNorm(Quaternion[] q) {
		double[] result = new double[q.length];
		Arrays.parallelSetAll(result, i -> q[i].getNorm());
		return result;
	}
	
	public static double[] cos(double[] a) {
		double flippedArray[] = new double[a.length];
		Arrays.parallelSetAll(flippedArray, i -> Math.cos(a[i]));
		return flippedArray;
	}
	
	public static double[] sin(double[] a) {
		double flippedArray[] = new double[a.length];
		Arrays.parallelSetAll(flippedArray, i -> Math.sin(a[i]));
		return flippedArray;
	}
	
	public static double[] computeAbsoluteValue(double[] realPart, double[] imPart) {
		return computeArrayOperation(realPart, imPart,
				i -> realPart[i] +imPart[i]);
	}

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
	 * Computes the element-wise summation of 2 vectors
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[] computeVectorSummation(double[] a, double[] b) {
		return computeArrayOperation(a, b, i -> a[i] + b[i]);
	}

	/**
	 * Computes the absolute value of a complex vector |a+bi |=√a²+b², where the
	 * first half corresponds to the real part and the other corresponds to the
	 * corresponding imaginary part
	 * 
	 * @param a the real part of a vector v
	 * @param b the imaginary part of a vector v
	 * @return
	 */
	public static double[] computeComplexAbsoluteValue(double[] vector) {
		int offset = (int) Math.floor(vector.length / 2);
		double[] realPart = Arrays.copyOfRange(vector, 0, offset);
		double[] imPart = Arrays.copyOfRange(vector, offset, vector.length);
		return computeArrayOperation(realPart, imPart,
				i -> Math.sqrt(Math.pow(realPart[i], 2) + Math.pow(imPart[i], 2)));
	}
	
	public static double[] computeComplexAbsoluteValue(double[] real, double [] im) {
		return computeArrayOperation(real, im,
				i -> Math.sqrt(Math.pow(real[i], 2) + Math.pow(im[i], 2)));
	}

	/**
	 * |v| = sqrt (sum_i(x²_i))
	 * 
	 * @param a
	 * @return
	 */
	public static double computeVectorsL2(double[] a) {
		double re = 0;
		for (int i = 0; i < a.length; i++) {
			re += Math.pow(a[i], 2);
		}
		return Math.sqrt(re);
	}

	/**
	 * |v| = sum(x+y+...)
	 * 
	 * @param a
	 * @return
	 */
	public static double sumArrayElements(double[] a) {
		double re = 0;
		for (int i = 0; i < a.length; i++) {
			re += Math.abs(a[i]);
		}
		return re;
	}

	public static double[] flipSignArray(double[] a) {
		double flippedArray[] = new double[a.length];
		Arrays.parallelSetAll(flippedArray, i -> -a[i]);
		return flippedArray;
	}

	/**
	 * Helper function to compute element-wise operations between 2 arrays
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
