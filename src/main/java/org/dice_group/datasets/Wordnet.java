package org.dice_group.datasets;

public class Wordnet extends Dataset {
	
	private final static String WN_NS = "http://www.w3.org/2006/03/wn/wn20/schema/";
	
	@Override
	public String parseURI(String line) {
		return WN_NS + line.split("\t")[1];
	}

}
