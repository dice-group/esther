package org.dice_group.fact_check.query;

import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.dice_group.path.property.Property;

public interface QueryGenerator {

    String getCountVariableName();
    
    String createCountQuery(List<Property> pathProperties, String[] propURIs, Set<Node> subjectTypes, Set<Node> objectTypes);
}
