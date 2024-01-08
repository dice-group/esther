package org.dice_group.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CSV-related classes
 * 
 * @author Ana Silva
 *
 */
public class CSVUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVUtils.class);

	/**
	 * Reads a CSV file, can provide delimiter
	 * and returns the corresponding multi-dimensional array
	 * 
	 * @param filePath File path
	 * @param format   File format. Use CSVFormat.DEFAULT for CSV and CSVFormat.TDF for TSV data.
	 * @return
	 */
	public static double[][] readCSVFile(String filePath, CSVFormat format) {
		LOGGER.info("Reading CSV from: " + filePath);
		double[][] embedding = null;
		try (Reader reader = new FileReader(filePath); CSVParser csvParser = new CSVParser(reader, format)) {
			List<double[]> lines = new ArrayList<>();
			for (CSVRecord csvRecord : csvParser) {
				double[] row = new double[csvRecord.size()];
				// ignore first column
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
