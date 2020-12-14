package org.dice_group.datasets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Wordnet extends Dataset {
	
	private final static String WN_NS = "http://www.w3.org/2006/03/wn/wn20/schema/";

	@Override
	public Map<Integer, String> readMap(String filePath) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			lines.filter(line -> line.contains("\t")).forEach(line -> map.putIfAbsent(Integer.valueOf(line.split("\t")[0]),WN_NS + line.split("\t")[1]));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

}
