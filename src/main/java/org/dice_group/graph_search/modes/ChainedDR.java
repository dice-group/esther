package org.dice_group.graph_search.modes;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;

public class ChainedDR extends Matrix{
	

	public ChainedDR(OntModel ontModel) {
		super(ontModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void compute(Map<String, Integer> rel2id, String edge) {
		
		Set<OntProperty> ontProperties = ontModel.listAllOntProperties().toSet();
		
		// get the domain of the given predicate P
		Set<? extends OntResource> domain = ontModel.getOntProperty(edge).listDomain().toSet();
		
		for(OntProperty curProp: ontProperties) {
			if(!Collections.disjoint(domain, curProp.listDomain().toSet())) {
				// search for properties that have as domain the range of this one
			}
		}
		
		
//		Set<? extends OntResource> domain = ontModel.getOntProperty(edge).listDomain().toSet();
//		
//		// get the properties that share the same domain as p_1
//		Set<OntProperty> props = ontProperties.stream().filter(p -> !Collections.disjoint(domain, p.listDomain().toSet())).collect(Collectors.toSet());
//		
	}

}
