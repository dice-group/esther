package org.dice_group.graph_search.modes;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.dice_group.embeddings.dictionary.Dictionary;

/**
 * The domain and range of other paths have to match exactly the one of the given predicate.
 *
 */
public class StrictDR extends Matrix {

	public StrictDR(OntModel ontology, Dictionary dictionary) {
		super(ontology, dictionary);
	}

	@Override
	public void compute(String edge) {
		Set<? extends OntResource> domain = ontology.getOntProperty(edge).listDomain().toSet();
		Set<? extends OntResource> range = ontology.getOntProperty(edge).listRange().toSet();
		
		Set<Resource> allowedProperties = new HashSet<Resource>();
		
		for(OntResource curRes : domain) {
			ResIterator dProp = ontology.listSubjectsWithProperty(RDFS.domain, curRes); // find properties with same domain
			while(dProp.hasNext()) {
				Resource c = dProp.next();
				if(domain.contains(c))
					allowedProperties.add(c);
			}
		}
		
		Set<Resource> rProp = new HashSet<Resource>();
		for(OntResource curRes : range) {
			rProp.addAll(ontology.listSubjectsWithProperty(RDFS.range, curRes).toSet());			
		}
		allowedProperties.retainAll(rProp);
		
		// TODO  we do get a set of possible edges (open set in A* search)
		
		
	}
	
	
	//int destID = dictionary.getRelations2ID().get(curDest.toString());
	// TODO confirm whether we want it to be a symmetric matrix or not m(i,j) = m(j,i)
	//edgeAdjMatrix[startID].set(destID);
	//edgeAdjMatrix[destID].set(startID);
	
}
