package org.dice_group.path.property;

import java.util.List;
import java.util.Map;

public class PropertyHelper {

	public static StringBuilder getPropertyPath(List<Property> pathProperties, String[] propURIs) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < pathProperties.size(); i++) {
			if (pathProperties.get(i).isInverse()) {
				builder.append("^");
			}
			builder.append("<").append(propURIs[i]).append(">");
			if (i + 1 < pathProperties.size())
				builder.append("/");
		}
		return builder;
	}

	/**
	 * Translates the path of ids into the corresponding IRIs TODO to be deleted
	 * 
	 * @param property
	 * @param id2rel
	 * @return
	 */
	public static String translate2IRI(Property property, Map<Integer, String> id2rel) {
		StringBuilder builder = new StringBuilder();
		int offset = id2rel.size();
		List<Integer> path = property.getIDPath();
		for (int i = 0; i < path.size(); i++) {
			// for(int p : path) {
			String t = id2rel.getOrDefault(path.get(i), id2rel.get(path.get(i) - offset));
			if (t == null)
				throw new IllegalArgumentException("Could not find the relation");
			builder.append(t);

			if (i + 1 < path.size())
				builder.append(";");
		}
		return builder.toString();
	}

	/**
	 * Translates the path of ids into the corresponding IRIs
	 * 
	 * @param property
	 * @param id2rel
	 * @return
	 */
	public static String[] translate2IRIArray(Property property, Map<Integer, String> id2rel) {
		String[] iris = new String[property.getPathLength()];
		int offset = id2rel.size();
		List<Integer> path = property.getIDPath();
		for (int i = 0; i < path.size(); i++) {
			String t = id2rel.getOrDefault(path.get(i), id2rel.get(path.get(i) - offset));
			if (t == null)
				throw new IllegalArgumentException("Could not find the relation");
			iris[i] = t;
		}
		return iris;
	}

	public static String translate2DirectedIRI(Property property, Map<Integer, String> id2rel) {
		StringBuilder builder = new StringBuilder();
		int offset = id2rel.size();

		List<Integer> paths = property.getIDDPath();
		for (int i = 0; i < paths.size(); i++) {
			String t = id2rel.getOrDefault(Math.abs(paths.get(i)), id2rel.get(Math.abs(paths.get(i)) - offset));
			if (t == null)
				throw new IllegalArgumentException("Could not find the relation");

			builder.append(t.replace("http://rdf.freebase.com/ns/", "fb:"));
			if (paths.get(i) < 0)
				builder.append("-1");

			if (i + 1 < paths.size())
				builder.append("\t");
		}
		return builder.toString();
	}

	public static String getPropertyPath(Property property, Map<Integer, String> id2rel) {
		StringBuilder builder = new StringBuilder();
		int offset = id2rel.size();
		List<Integer> path = property.getIDPath();
		for (int i = 0; i < path.size(); i++) {
			// for(int p : path) {
			String t = id2rel.get(path.get(i));
			if (t == null) {
				String t1 = id2rel.get(path.get(i) - offset);
				if (t1 == null)
					throw new IllegalArgumentException();
				t = "^<" + t1 + ">";
			} else {
				t = "<" + t + ">";
			}

			builder.append(t);
			if (i + 1 < path.size())
				builder.append("/");
		}
		return builder.toString();
	}

	public static int getInverseID(int edge, int offset) {
		return edge >= offset ? edge - offset : edge + offset;
	}
}
