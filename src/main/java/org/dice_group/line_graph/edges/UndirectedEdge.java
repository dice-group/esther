package org.dice_group.line_graph.edges;

import org.apache.jena.rdf.model.Property;

/**
 * Target and source nodes do not matter, as the hashcode/equals have been overridden.
 * 
 */
public class UndirectedEdge {

	private String type;

	private Property source;

	private Property target;

	public UndirectedEdge() {
	}

	public UndirectedEdge(String e) {
		this.type = e;
	}

	public UndirectedEdge(String type, Property source, Property target) {
		this.type = type;
		this.source = source;
		this.target = target;
	}
	
	public Property getSource() {
		return source;
	}

	public void setSource(Property source) {
		this.source = source;
	}

	public Property getTarget() {
		return target;
	}

	public void setTarget(Property target) {
		this.target = target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UndirectedEdge other = (UndirectedEdge) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		}
		if (target == null) {
			if (other.target != null)
				return false;
		}
		boolean cond = target.equals(other.source) && source.equals(other.target);
		boolean cond2 = target.equals(other.target) && source.equals(other.source);
		if (!(cond || cond2)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return type + "\t" + this.getSource() + " - " + this.getTarget();
	}

}
