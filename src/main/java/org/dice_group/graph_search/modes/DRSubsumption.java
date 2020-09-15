package org.dice_group.graph_search.modes;

import java.util.Map;

import org.apache.jena.ontology.OntModel;

/**
 * Different scenarios will come under this
 * 1) Domains subsumption 
 * 2) Ranges subsumption
 * 3) Domain subsumes range or vice-versa
 *
 */
public class DRSubsumption extends Matrix{

	public DRSubsumption(OntModel ontModel) {
		super(ontModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void compute(Map<String, Integer> rel2id, String edge) {
		// TODO Auto-generated method stub
		
	}

}
