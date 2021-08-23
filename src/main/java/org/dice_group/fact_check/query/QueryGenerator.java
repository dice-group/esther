package org.dice_group.fact_check.query;

import java.util.List;

import org.dice_group.path.property.Property;

public interface QueryGenerator {

    String getCountVariableName();

	String createCountQuery(List<Property> properties, String[] translate2iriArray, org.apache.jena.rdf.model.Property predicate);
}
