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
		List<Integer> path = property.getIDPath();
		for(int p : path) {
			builder.append(id2rel.get(p)).append(";");
		}
		return builder.toString();
	}

}
