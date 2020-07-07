package org.dice_group.creator;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_group.edges.UndirectedEdge;
import org.jgrapht.Graph;
import org.junit.Assert;
import org.junit.Test;

public class LineGraphTest {
	
	private final String LABEL1 = "LABEL1";
	private final String LABEL2 = "LABEL2";
	private final String DUMMY_TARGET = "dummyTarget";
	private final String DUMMY_SOURCE = "dummySource";
	
	@Test
	public void testUndirectedEdges() {
		UndirectedEdge edge = new UndirectedEdge(LABEL1);
		edge.setSource(ResourceFactory.createProperty(DUMMY_SOURCE));
		edge.setTarget(ResourceFactory.createProperty(DUMMY_TARGET));
		
		UndirectedEdge otherEdge = new UndirectedEdge(LABEL1);
		otherEdge.setSource(ResourceFactory.createProperty(DUMMY_TARGET));
		otherEdge.setTarget(ResourceFactory.createProperty(DUMMY_SOURCE));
		
		UndirectedEdge otherEdge2 = new UndirectedEdge(LABEL2);
		otherEdge2.setSource(ResourceFactory.createProperty(DUMMY_SOURCE));
		otherEdge2.setTarget(ResourceFactory.createProperty(DUMMY_TARGET));
		
		UndirectedEdge otherEdge3 = new UndirectedEdge(LABEL2);
		otherEdge3.setSource(ResourceFactory.createProperty(DUMMY_TARGET));
		otherEdge3.setTarget(ResourceFactory.createProperty(DUMMY_SOURCE));
		
		Assert.assertTrue(edge.equals(otherEdge));
		Assert.assertFalse(edge.equals(otherEdge2));
		Assert.assertTrue(otherEdge2.equals(otherEdge3));
		Assert.assertFalse(otherEdge.equals(otherEdge3));
	}
	
	@Test
	public void testGraphCreation() {
		Model model = ModelFactory.createDefaultModel();
		model.read("graph1.n3");
		
		LineGraph graph = new LineGraph(model);
		
		Graph<Property, UndirectedEdge> lineGraph = graph.getGraph();
		for(UndirectedEdge edge : lineGraph.edgeSet())
			System.out.println(edge + " \t " + lineGraph.getEdgeWeight(edge));
		System.out.println();
	}

}
