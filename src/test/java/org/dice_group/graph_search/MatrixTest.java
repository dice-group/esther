package org.dice_group.graph_search;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.dice_group.graph_search.modes.IrrelevantDR;
import org.dice_group.graph_search.modes.Matrix;
import org.dice_group.graph_search.modes.NotDisjointDR;
import org.dice_group.graph_search.modes.StrictDR;
import org.junit.Assert;
import org.junit.Test;

public class MatrixTest {
	private final static String PREFIX_NS = "www.example.com:";

	@Test
	public void testStrict() {
		
		Matrix strictMatrix = new StrictDR();

		List<Resource> a = new ArrayList<Resource>();
		a.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		a.add(ResourceFactory.createResource(PREFIX_NS + "c3"));

		List<Resource> b = new ArrayList<Resource>();
		b.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		b.add(ResourceFactory.createResource(PREFIX_NS + "c3"));

		List<Resource> c = new ArrayList<Resource>();
		c.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		c.add(ResourceFactory.createResource(PREFIX_NS + "c4"));

		List<Resource> d = new ArrayList<Resource>();
		d.add(ResourceFactory.createResource(PREFIX_NS + "c5"));
		d.add(ResourceFactory.createResource(PREFIX_NS + "c4"));

		Assert.assertTrue(strictMatrix.compareSets(a, a));
		Assert.assertTrue(strictMatrix.compareSets(a, b));
		Assert.assertFalse(strictMatrix.compareSets(a, c));
		Assert.assertFalse(strictMatrix.compareSets(a, d));
	}

	@Test
	public void testNonDisjoint() {

		Matrix notDisjoint = new NotDisjointDR();

		List<Resource> a = new ArrayList<Resource>();
		a.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		a.add(ResourceFactory.createResource(PREFIX_NS + "c3"));

		List<Resource> b = new ArrayList<Resource>();
		b.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		b.add(ResourceFactory.createResource(PREFIX_NS + "c3"));

		List<Resource> c = new ArrayList<Resource>();
		c.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		c.add(ResourceFactory.createResource(PREFIX_NS + "c4"));

		List<Resource> d = new ArrayList<Resource>();
		d.add(ResourceFactory.createResource(PREFIX_NS + "c5"));
		d.add(ResourceFactory.createResource(PREFIX_NS + "c4"));

		Assert.assertTrue(notDisjoint.compareSets(a, a));
		Assert.assertTrue(notDisjoint.compareSets(a, b));
		Assert.assertTrue(notDisjoint.compareSets(a, c));
		Assert.assertFalse(notDisjoint.compareSets(a, d));
		Assert.assertTrue(notDisjoint.compareSets(c, d));

	}
	
	@Test
	public void testIrrelevant() {
		Matrix irrelevant = new IrrelevantDR();
		
		List<Resource> a = new ArrayList<Resource>();
		a.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		a.add(ResourceFactory.createResource(PREFIX_NS + "c3"));

		List<Resource> b = new ArrayList<Resource>();
		b.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		b.add(ResourceFactory.createResource(PREFIX_NS + "c3"));

		List<Resource> c = new ArrayList<Resource>();
		c.add(ResourceFactory.createResource(PREFIX_NS + "c1"));
		c.add(ResourceFactory.createResource(PREFIX_NS + "c4"));

		List<Resource> d = new ArrayList<Resource>();
		d.add(ResourceFactory.createResource(PREFIX_NS + "c5"));
		d.add(ResourceFactory.createResource(PREFIX_NS + "c4"));
		
		Assert.assertTrue(irrelevant.compareSets(a, a));
		Assert.assertTrue(irrelevant.compareSets(a, b));
		Assert.assertTrue(irrelevant.compareSets(a, c));
		Assert.assertTrue(irrelevant.compareSets(a, d));
		Assert.assertTrue(irrelevant.compareSets(c, d));
	}

}
