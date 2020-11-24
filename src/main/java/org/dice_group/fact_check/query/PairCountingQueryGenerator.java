package org.dice_group.fact_check.query;

import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.dice_group.path.property.Property;

public class PairCountingQueryGenerator implements QueryGenerator {

    protected static final String COUNT_VARIABLE_NAME = "?sum";
    protected static final String SUBJECT_VARIABLE_NAME = "?s";
    protected static final String OBJECT_VARIABLE_NAME = "?o";
    protected static final String INTERMEDIATE_NODE_VARIABLE_NAME = "?x";

    @Override
    public String getCountVariableName() {
        return COUNT_VARIABLE_NAME;
    }

    @Override
    public String createCountQuery(List<Property> pathProperties, String[] propURIs, Set<Node> subjectTypes,
            Set<Node> objectTypes) {
        StringBuilder sTypeTriples = generateTypeRestrictions(subjectTypes, SUBJECT_VARIABLE_NAME);
        StringBuilder oTypeTriples = generateTypeRestrictions(objectTypes, OBJECT_VARIABLE_NAME);

        return createPropertyQueryRecursively(pathProperties, propURIs, sTypeTriples, oTypeTriples);
    }

    private StringBuilder generateTypeRestrictions(Set<Node> types, String variableName) {
        StringBuilder builder = new StringBuilder();
        for (Node type : types) {
            builder.append(variableName);
            builder.append(" a <");
            builder.append(type.getURI());
            builder.append("> .\n");
        }
        return builder;
    }

    private String createPropertyQueryRecursively(List<Property> pathProperties, String[] propURIs,
            StringBuilder sTypeTriples, StringBuilder oTypeTriples) {
        StringBuilder queryBuilder = new StringBuilder();
        // This is the first property in the list
        queryBuilder.append("Select count(*) as " + COUNT_VARIABLE_NAME + ") where { \n");
        // Recursion
        createPropertyQuery_Recursion(0, pathProperties, propURIs, sTypeTriples, oTypeTriples, queryBuilder);
        queryBuilder.append("}");
        return queryBuilder.toString();
    }

    private void createPropertyQuery_Recursion(int propId, List<Property> pathProperties, String[] propURIs,
            StringBuilder sTypeTriples, StringBuilder oTypeTriples, StringBuilder queryBuilder) {
        String localSubject = propId == 0 ? SUBJECT_VARIABLE_NAME : INTERMEDIATE_NODE_VARIABLE_NAME + propId;
        queryBuilder.append("Select distinct ");
        queryBuilder.append(localSubject);
        queryBuilder.append(" " + OBJECT_VARIABLE_NAME + " where { \n");

        // In case, we have the first part of the path
        if (propId == 0) {
            // Add subject types
            queryBuilder.append(sTypeTriples);
        }
        // If this is the end of the recursion
        if (propId == pathProperties.size() - 1) {
            // Use object variable name
            addTriplePattern(pathProperties.get(propId), propURIs[propId], localSubject,
                    OBJECT_VARIABLE_NAME, queryBuilder);
            // Add object types
            queryBuilder.append(oTypeTriples);
        } else {
            addTriplePattern(pathProperties.get(propId), propURIs[propId], localSubject,
                    INTERMEDIATE_NODE_VARIABLE_NAME + (propId + 1), queryBuilder);
            // Start the recursion
            queryBuilder.append("{\n");
            createPropertyQuery_Recursion(propId + 1, pathProperties, propURIs, sTypeTriples, oTypeTriples,
                    queryBuilder);
            queryBuilder.append("}\n");
        }
        queryBuilder.append("}\n");
    }

    private void addTriplePattern(Property property, String propUri, String firstVariable, String secondVariable,
            StringBuilder builder) {
        String subject, object;
        if (property.isInverse()) {
            object = firstVariable;
            subject = secondVariable;
        } else {
            object = secondVariable;
            subject = firstVariable;
        }
        builder.append(subject);
        builder.append(" <");
        builder.append(propUri);
        builder.append("> ");
        builder.append(object);
        builder.append(" .\n");
    }
}
