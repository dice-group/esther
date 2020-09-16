package org.dice_group.graph_search.modes;

import org.apache.jena.ontology.OntModel;
import org.dice_group.embeddings.dictionary.Dictionary;

/**
 * Different scenarios will come under this
 * 1) Domains subsumption 
 * 2) Ranges subsumption
 * 3) Domain subsumes range or vice-versa
 *
 */
public class DRSubsumption extends Matrix{

	public DRSubsumption(OntModel ontModel, Dictionary dictionary) {
		super(ontModel, dictionary);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void compute(String edge) {
		// TODO Auto-generated method stub
		
	}

	

}
