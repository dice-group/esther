package org.dice_group.fact_check.query;

import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.dice_group.path.property.Property;

public class CountApproximatingQueryGenerator implements QueryGenerator {

    protected static final String COUNT_VARIABLE_NAME = "?sum";
    protected static final String SUBJECT_VARIABLE_NAME = "?s";
    protected static final String OBJECT_VARIABLE_NAME = "?o";
    protected static final String INTERMEDIATE_COUNT_VARIABLE_NAME = "?b";
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
        
        if (pathProperties.size() == 1) {
            return createSinglePropertyQuery(pathProperties.get(0), propURIs[0], sTypeTriples, oTypeTriples);
        } else {
            return createPropertyQueryRecursively(pathProperties, propURIs, sTypeTriples, oTypeTriples);
        }
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

    private String createSinglePropertyQuery(Property property, String propUri, StringBuilder sTypeTriples,
            StringBuilder oTypeTriples) {
        StringBuilder builder = new StringBuilder();
        builder.append("Select (count(*) as " + COUNT_VARIABLE_NAME + ") where { \n");
        addTriplePattern(property, propUri, SUBJECT_VARIABLE_NAME, OBJECT_VARIABLE_NAME, builder);
        builder.append(sTypeTriples);
        builder.append(oTypeTriples);
        builder.append("}");
        return builder.toString();
    }

    private String createPropertyQueryRecursively(List<Property> pathProperties, String[] propURIs,
            StringBuilder sTypeTriples, StringBuilder oTypeTriples) {
        StringBuilder queryBuilder = new StringBuilder();
        // This is the first property in the list
        queryBuilder.append("Select (coalesce(sum(" + INTERMEDIATE_COUNT_VARIABLE_NAME);
        queryBuilder.append("0*" + INTERMEDIATE_COUNT_VARIABLE_NAME);
        queryBuilder.append("1), 0) as " + COUNT_VARIABLE_NAME + ") where { \n");
        // Recursion
        createPropertyQuery_Recursion(0, pathProperties, propURIs, sTypeTriples, oTypeTriples, queryBuilder);
        queryBuilder.append("}");
        return queryBuilder.toString();
    }

    private void createPropertyQuery_Recursion(int propId, List<Property> pathProperties, String[] propURIs,
            StringBuilder sTypeTriples, StringBuilder oTypeTriples, StringBuilder queryBuilder) {
        if (propId == pathProperties.size() - 1) {
            // This is the last property in the list --> recursion ends
            queryBuilder.append("Select (count(*) as ?b1) ?x1 where { \n");
            // Use the subject variable
            addTriplePattern(pathProperties.get(propId), propURIs[propId], INTERMEDIATE_NODE_VARIABLE_NAME + propId,
                    OBJECT_VARIABLE_NAME, queryBuilder);
            // Add subject types
            queryBuilder.append(oTypeTriples);
            queryBuilder.append("} group by " + INTERMEDIATE_NODE_VARIABLE_NAME);
            queryBuilder.append(propId);
            queryBuilder.append('\n');
        } else {
            // Create first sub select which selects the subject and it's types
            queryBuilder.append("Select (count(*) as " + INTERMEDIATE_COUNT_VARIABLE_NAME);
            queryBuilder.append(propId + 1);
            queryBuilder.append(") " + INTERMEDIATE_COUNT_VARIABLE_NAME);
            queryBuilder.append(propId);
            queryBuilder.append(" where { \n");
            // If this is the first sub select
            if (propId == 0) {
                // Use the subject variable
                addTriplePattern(pathProperties.get(propId), propURIs[propId], SUBJECT_VARIABLE_NAME,
                        INTERMEDIATE_NODE_VARIABLE_NAME + (propId + 1), queryBuilder);
                // Add subject types
                queryBuilder.append(sTypeTriples);
            } else {
                addTriplePattern(pathProperties.get(propId), propURIs[propId], INTERMEDIATE_NODE_VARIABLE_NAME + propId,
                        INTERMEDIATE_NODE_VARIABLE_NAME + (propId + 1), queryBuilder);
            }
            // Start the recursion
            queryBuilder.append("{\n");
            createPropertyQuery_Recursion(propId + 1, pathProperties, propURIs, sTypeTriples, oTypeTriples,
                    queryBuilder);
            queryBuilder.append("}\n");
            // Finalize sub select of this recursion step
            queryBuilder.append("} group by ?b");
            queryBuilder.append(propId + 1);
            queryBuilder.append('\n');
        }
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
