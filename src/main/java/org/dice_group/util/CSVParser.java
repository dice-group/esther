package org.dice_group.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class CSVParser {
	
	public CSVParser() {
	}

	/**
	 * Reads a CSV file and returns the corresponding multi-dimensional array [entities/relations count][dimensionsCount]
	 * @param filePath
	 * @param elementCount
	 * @param dim
	 * @return
	 */
	public double[][] readCSVFile(String filePath, int elementCount, int dim) {
		double[][] embedding = new double[elementCount][dim];

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line = reader.readLine();

			for (int i = 0; i < elementCount && line != null; i++) {
				embedding[i] = Arrays.stream(line.split("\\s*,\\s*")).mapToDouble(Double::parseDouble).toArray();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return embedding;
	}


}
