package org.dice_group.creator;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.dice_group.edges.UndirectedEdge;
import org.jgrapht.Graph;
import org.junit.Test;

public class LineGraphTest {
	
	@Test
	public void test() {
		Model model = ModelFactory.createDefaultModel();
		model.read("graph1.n3");
		
		LineGraph graph = new LineGraph(model);
		
		Graph<Property, UndirectedEdge> lineGraph = graph.getGraph();
		for(UndirectedEdge edge : lineGraph.edgeSet())
			System.out.println(edge + " - " + lineGraph.getEdgeWeight(edge));
		System.out.println();
	}

}
