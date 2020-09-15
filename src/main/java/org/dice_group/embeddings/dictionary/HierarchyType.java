package org.dice_group.embeddings.dictionary;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 * TODO to be removed
 *
 */
public class HierarchyType {
	private OntResource type;
	private List<HierarchyType> parents;
	private List<HierarchyType> children;

	public HierarchyType() {
		this(null, new ArrayList<HierarchyType>(), new ArrayList<HierarchyType>());
	}

	public HierarchyType(OntProperty property) {
		super();
		this.parents = new ArrayList<HierarchyType>();
		this.children = new ArrayList<HierarchyType>();
		ExtendedIterator<? extends OntResource> domain = property.listDomain();
		if (domain.hasNext()) {
			this.addParents(domain);
		}
		ExtendedIterator<? extends OntResource> range = property.listRange();
		if (range.hasNext()) {
			this.addChildren(range);
		}
	}

	public HierarchyType(OntResource type) {
		//TODO 
	}

	public HierarchyType(OntResource type, List<HierarchyType> parent, List<HierarchyType> children) {
		super();
		this.type = type;
		this.parents = parent;
		this.children = children;
	}

	public void addChild(HierarchyType child) {
		this.children.add(child);
	}

	public void addParent(HierarchyType child) {
		this.parents.add(child);
	}

	public void addChildren(ExtendedIterator<? extends OntResource> range) {
		while (range.hasNext()) {
			OntResource res = range.next();
			addChild(new HierarchyType(res));
		}
	}

	public void addParents(ExtendedIterator<? extends OntResource> domain) {
		while (domain.hasNext()) {
			OntResource res = domain.next();
			addParent(new HierarchyType(res));
		}
	}

	public OntResource getType() {
		return type;
	}

	public void setType(OntResource type) {
		this.type = type;
	}

	public List<HierarchyType> getParents() {
		return parents;
	}

	public void setParents(List<HierarchyType> parents) {
		this.parents = parents;
	}

	public List<HierarchyType> getChildren() {
		return children;
	}

	public void setChildren(List<HierarchyType> children) {
		this.children = children;
	}

}
