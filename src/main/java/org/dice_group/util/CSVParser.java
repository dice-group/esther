package org.dice_group.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;

public class CSVParser {

	/**
	 * Reads a CSV file and returns the corresponding multi-dimensional array
	 * [entities/relations count][dimensionsCount]
	 *TODO change to standard library
	 * @param filePath
	 * @param dim
	 * @param factor
	 * @return
	 */
	public static double[][] readCSVFile(String filePath, int dim, int factor) {
		int noLines = 0;
		// TODO find a better way to count lines in file
		try (LineNumberReader reader = new LineNumberReader(new FileReader(filePath))) {
			reader.skip(Integer.MAX_VALUE);
			noLines =  reader.getLineNumber();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (noLines == 0)
			throw new IllegalArgumentException("Number of dimensions cannot be 0.");

		double[][] embedding = new double[noLines][factor *dim];

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line = reader.readLine();
			for (int i = 0; i < noLines; i++) {
				embedding[i] = Arrays.stream(line.split("\\s*,\\s*")).mapToDouble(Double::parseDouble).toArray();
				line = reader.readLine();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return embedding;
	}

}
