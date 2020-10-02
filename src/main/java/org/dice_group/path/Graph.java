package org.dice_group.path;

import java.util.Set;

import org.apache.jena.rdf.model.Statement;
import org.dice_group.path.property.Property;

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

	

}
