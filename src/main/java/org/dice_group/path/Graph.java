package org.dice_group.path;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Statement;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyHelper;

public class Graph {

	private Set<Property> paths;
	private double score;
	private Statement triple;
	
	public Graph(Set<Property> paths, double score, Statement triple) {
		this.paths = paths;
		this.score = score;
		this.triple = triple;
	}

	public Set<Property> getPaths() {
		return paths;
	}

	public void setPaths(Set<Property> paths) {
		this.paths = paths;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Statement getTriple() {
		return triple;
	}

	public void setTriple(Statement triple) {
		this.triple = triple;
	}

	public StringBuilder getPrintableResults(Map<Integer, String> id2rel) {
		StringBuilder builder = new StringBuilder();
		builder.append("\nTriple: ").append(triple);
		builder.append("\nNPMI:" ).append(score);
		builder.append("\nPath NPMI").append("\t").append("A* score").append("\t").append("Length").append("\t").append("Path(s)");
		
		List<Property> result = paths.stream().sorted(Comparator.comparingDouble(Property :: getFinalScore)).collect(Collectors.toList());
		
		for(Property path : result) {
			builder.append("\n").append(path.getFinalScore()).append("\t").append(path.getScore()).append("\t").append(path.getPathLength());
			builder.append("\t").append(PropertyHelper.translate2DirectedIRI(path, id2rel));
		}
		builder.append("\n");
		return builder;
		

	}

}
