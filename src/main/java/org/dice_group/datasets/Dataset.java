package org.dice_group.datasets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *  * Reads a dictionary specific to a dataset format. 
 * Can be used directly if the dataset doesn't need extra parsing.
 * 
 * @author Ana Silva
 *
 */
public class Dataset {
	
	/**
	 * Reads an Integer to String map from File
	 * 
	 * @param filePath File path
	 * @return Map<Integer, String> object
	 */
	public Map<Integer, String> readMap(String filePath) {
		// read the map instead
		Map<Integer, String> map = new HashMap<Integer, String>();
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			lines.filter(line -> line.contains("\t")).forEach(line -> map.putIfAbsent(Integer.valueOf(line.split("\t")[0]), parseURI(line)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * Expects the URI to be in the second column of a TSV line
	 * 
	 * @param line TSV line
	 * @return URI
	 */
	public String parseURI(String line) {
		return line.split("\t")[1];
	}

}
