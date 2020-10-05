package org.dice_group.path.property;

import java.util.List;
import java.util.Map;

public class PropertyHelper {
	
	/**
	 * Translates the path of ids into the corresponding IRIs
	 * @param property
	 * @param id2rel
	 * @return
	 */
	public static String translate2IRI(Property property, Map<Integer, String> id2rel) {
		StringBuilder builder = new StringBuilder();
		int offset = id2rel.size();
		List<Integer> path = property.getIDPath();
		for(int i = 0; i<path.size();i++) {
		//for(int p : path) {
			String t = id2rel.getOrDefault(path.get(i), id2rel.get(path.get(i)-offset));
			if(t == null)
				throw new IllegalArgumentException("Could not find the relation");
			builder.append(t);
			
			if(i+1 < path.size())
				builder.append(";");
		}
		return builder.toString();
	}
	
	public static String getPropertyPath(Property property, Map<Integer, String> id2rel) {
		StringBuilder builder = new StringBuilder();
		int offset = id2rel.size();
		List<Integer> path = property.getIDPath();
		for(int i = 0; i<path.size();i++) {
		//for(int p : path) {
			String t = id2rel.get(path.get(i));
			if(t == null) {
				String t1 = id2rel.get(path.get(i)-offset);
				if(t1 == null)
					throw new IllegalArgumentException();
				t= "^<"+t1+">";
			} else {
				t = "<" + t + ">";
			}
			
			
			builder.append(t);
			if(i+1 < path.size())
				builder.append("/");
		}
		return builder.toString();
	}

}
