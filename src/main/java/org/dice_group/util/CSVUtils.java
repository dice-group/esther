package org.dice_group.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class CSVUtils {

	/**
	 * Reads a CSV file and returns the corresponding multi-dimensional array
	 * 
	 * @param filePath
	 * @param dim
	 * @return
	 */
	public static double[][] readCSVFile(String filePath) {
		double[][] embedding = null;
		try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(filePath)))) {
			List<String[]> lines = reader.readAll();
			String [][] s = lines.toArray(new String[lines.size()][]);
			embedding = Arrays.stream(s).map(a -> Arrays.stream(a).mapToDouble(Double::parseDouble).toArray()).toArray(double[][]::new);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvException e) {
			e.printStackTrace();
		}
		return embedding;
	}

}
