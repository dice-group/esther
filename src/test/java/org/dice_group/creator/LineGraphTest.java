package org.dice_group.creator;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_group.edges.UndirectedEdge;
import org.jgrapht.Graph;
import org.jgrapht.graph.WeightedPseudograph;
import org.junit.Assert;
import org.junit.Test;

public class LineGraphTest {
	
	private final String LABEL1 = "LABEL1";
	private final String LABEL2 = "LABEL2";
	private final String DUMMY_TARGET = "dummyTarget";
	private final String DUMMY_SOURCE = "dummySource";
	private final Property REL1 = ResourceFactory.createProperty("http://example.org/relation1");
	private final Property REL2 = ResourceFactory.createProperty("http://example.org/relation2");
	
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
		Assert.assertTrue(otherEdge2.equals(otherEdge3));
		
		Assert.assertFalse(edge.equals(otherEdge2));
		Assert.assertFalse(otherEdge.equals(otherEdge3));
	}
	
	@Test
	public void testGraphCreation() {
		Model model = ModelFactory.createDefaultModel();
		model.read("graph1.n3");
		
		LineGraph graph = new LineGraph(model);
		Graph<Property, UndirectedEdge> lineGraph = graph.getGraph();
		
		UndirectedEdge objEdge = new UndirectedEdge("OBJ");
		objEdge.setSource(REL1);
		objEdge.setTarget(REL1);
		
		UndirectedEdge mixEdge = new UndirectedEdge("MIXED");
		mixEdge.setSource(REL1);
		mixEdge.setTarget(REL2);
		
		UndirectedEdge subEdge = new UndirectedEdge("SUBJ");
		subEdge.setSource(REL2);
		subEdge.setTarget(REL1);
		
		UndirectedEdge selfSubEdge = new UndirectedEdge("SUBJ");
		selfSubEdge.setSource(REL1);
		selfSubEdge.setTarget(REL1);
		
		Graph<Property, UndirectedEdge> expected = new WeightedPseudograph<Property, UndirectedEdge>(UndirectedEdge.class);
		expected.addVertex(REL1);
		expected.addVertex(REL2);
		
		expected.addEdge(REL1, REL2, mixEdge);
		expected.setEdgeWeight(mixEdge, 1);
		expected.addEdge(REL1, REL1, objEdge);
		expected.setEdgeWeight(objEdge, 1);
		expected.addEdge(REL2, REL1, subEdge);
		expected.setEdgeWeight(subEdge, 2);
		expected.addEdge(REL1, REL1, selfSubEdge);
		expected.setEdgeWeight(selfSubEdge, 1);
		
		Assert.assertEquals(expected.vertexSet(), lineGraph.vertexSet());
		Assert.assertEquals(expected.edgeSet(), lineGraph.edgeSet());
		
	}

}
