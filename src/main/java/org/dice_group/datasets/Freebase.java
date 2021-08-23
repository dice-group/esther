package org.dice_group.datasets;

/**
 * Fb15k-237
 *
 */
public class Freebase extends Dataset {
	
	private final static String FB_NS = "http://rdf.freebase.com/ns/";
	
	@Override
	public String parseURI(String line) {
		return FB_NS + line.split("\t")[1].replace("/", ".").substring(1);
	}

}
