package org.dice_group.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class CSVUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVUtils.class);

	/**
	 * Reads a CSV file and returns the corresponding multi-dimensional array
	 * 
	 * @param filePath
	 * @param dim
	 * @return
	 */
	public static double[][] readCSVFile(String filePath) {
		LOGGER.info("Reading CSV from: "+filePath);
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
	
	public static float[][] readFloatCSVFile(String filePath) {
		LOGGER.info("Reading CSV from: "+filePath);
		float[][] embedding = null;
		try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(filePath)))) {
			List<String[]> lines = reader.readAll();
			String [][] s = lines.toArray(new String[lines.size()][]);
			embedding = Arrays.stream(s).map(a -> Arrays.stream(a).map(Float::parseFloat).toArray()).toArray(float[][]::new);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvException e) {
			e.printStackTrace();
		}
		return embedding;
	}

}
