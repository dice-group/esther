package org.dice_group.graph_search;

import org.dice_group.graph_search.distance.Distance;
import org.dice_group.graph_search.distance.TransEL1;
import org.dice_group.path.property.Property;
import org.dice_group.path.property.PropertyBackPointer;
import org.dice_group.util.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

public class DistanceTest {

	@Test
	public void testTransEDistanceFunction() {
		double[] targetEdge = { 1, 2, 3 };

		Distance scorer = new TransEL1(targetEdge);
		double distance = scorer.computeDistance(new Property(0), targetEdge, false);
		Assert.assertEquals(0, distance);

		double[] newEdge = { 1, 3, 5 };
		Property first = new Property(0);
		first.updateCost(scorer.computeDistance(first, newEdge, false));
		// || r_1 - r_n ||
		//Assert.assertEquals(4, first.getPathCost());

		// || r_1 - r_1 - r_n || = || r_n ||
		Property second = new Property(2, new PropertyBackPointer(first), scorer.computeDistance(first, newEdge, true),true);
		//Assert.assertEquals(8, second.getPathCost());

		// ||r_1 - r_1 +r_n -r_n|| = 0
		Property third = new Property(2, new PropertyBackPointer(second), scorer.computeDistance(second, targetEdge, false), false);
		//Assert.assertEquals(3, third.getPathCost());

	}

}
