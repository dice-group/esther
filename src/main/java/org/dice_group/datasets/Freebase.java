package org.dice_group.datasets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Fb15k-237
 *
 */
public class Freebase extends Dataset {
	
	private final static String FB_NS = "http://rdf.freebase.com/ns/";

	@Override
	public Map<Integer, String> readMap(String filePath) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			lines.filter(line -> line.contains("\t")) 
					.forEach(line -> map.putIfAbsent(Integer.valueOf(line.split("\t")[0]),
							FB_NS + line.split("\t")[1].replace("/", ".").substring(1)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
