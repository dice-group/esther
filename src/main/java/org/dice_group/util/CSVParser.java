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
	 * 
	 * @param filePath
	 * @param elementCount
	 * @param dim
	 * @return
	 */
	public static double[][] readCSVFile(String filePath, int elementCount, int factor) {
		int dim = 0;
		// TODO find a better way to count lines in file
		try (LineNumberReader reader = new LineNumberReader(new FileReader(filePath))) {
			reader.skip(Integer.MAX_VALUE);
			dim = factor * (reader.getLineNumber() + 1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dim == 0)
			throw new IllegalArgumentException("Number of dimensions cannot be 0.");

		double[][] embedding = new double[elementCount][dim];

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line = reader.readLine();
			for (int i = 0; i < elementCount; i++) {
				

				embedding[i] = Arrays.stream(line.split("\\s*,\\s*")).mapToDouble(Double::parseDouble).toArray();
				line = reader.readLine();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return embedding;
	}

}
