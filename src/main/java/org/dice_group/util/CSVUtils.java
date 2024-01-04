package org.dice_group.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
		LOGGER.info("Reading CSV from: " + filePath);
		double[][] embedding = null;
		try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(filePath)))) {
			List<String[]> lines = reader.readAll();
			String[][] s = lines.toArray(new String[lines.size()][]);
			embedding = Arrays.stream(s).map(a -> Arrays.stream(a).mapToDouble(Double::parseDouble).toArray())
					.toArray(double[][]::new);
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
		return embedding;
	}

	public static double[][] readTSVFile(String filePath) {
		LOGGER.info("Reading TSV from: " + filePath);
		double[][] embedding = null;
		try (Reader reader = new FileReader(filePath); CSVParser csvParser = new CSVParser(reader, CSVFormat.TDF)) {
			List<double[]> lines = new ArrayList<>();
			for (CSVRecord csvRecord : csvParser) {
				double[] row = new double[csvRecord.size()];
				for (int i = 1; i < csvRecord.size(); i++) {
					row[i] = Double.parseDouble(csvRecord.get(i));
				}
				lines.add(row);
			}

			embedding = lines.toArray(new double[lines.size()][]);

		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}

		return embedding;
	}

}
