package org.dice_group.graph_search.modes;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Resource;
import org.dice_group.embeddings.dictionary.Dictionary;
import org.dice_group.util.SparqlHelper;

public abstract class Matrix implements MatrixInterface {

	protected BitSet[] edgeAdjMatrix;

	protected Dictionary dictionary;
	
	protected String requestURL;

	public Matrix(Dictionary dictionary) {
		this.dictionary = dictionary;
		edgeAdjMatrix = new BitSet[dictionary.getId2Relations().size() * 2];
		for (int i = 0; i < edgeAdjMatrix.length; i++)
			edgeAdjMatrix[i] = new BitSet();
	}

	public Matrix(String requestURL, Dictionary dictionary) {
		this.dictionary = dictionary;
		edgeAdjMatrix = new BitSet[dictionary.getId2Relations().size() * 2];
		for (int i = 0; i < edgeAdjMatrix.length; i++)
			edgeAdjMatrix[i] = new BitSet();
		this.requestURL = requestURL;
		populateMatrix();
	}

	public void populateMatrix() {
		if (this instanceof IrrelevantDR) {
			for (int i = 0; i < edgeAdjMatrix.length; i++) {
				edgeAdjMatrix[i].set(0, edgeAdjMatrix.length);
			}
			return;
		}

		Map<Integer, String> id2relmap = dictionary.getId2Relations();

		for(int i = 0; i<dictionary.getRelCount();i++) {
			String curProperty = id2relmap.get(i);

			//OntProperty cProp = ontology.getOntProperty(curProperty);
			
			// TODO substitute with a filter that defaults to none, this is specific to freebase at the moment
			Pattern pattern = Pattern.compile("\\.\\.");
			if(curProperty == null || pattern.matcher(curProperty).find())
				continue;
			
			
			//Set<? extends OntResource> domainI = cProp.listDomain().toSet();
			//Set<? extends OntResource> rangeI = cProp.listRange().toSet();
			
			List<Resource> domainI = SparqlHelper.getDomain(requestURL, curProperty);
			List<Resource> rangeI = SparqlHelper.getRange(requestURL, curProperty);

			/**
			 * Since the matrix is extended by a factor of 2, this variable is also the
			 * offset between p_n and p-_n
			 */
			int relCount = dictionary.getRelCount();

			for (int j = 0; j < relCount; j++) {
				String curJ = id2relmap.get(j);
				
				Pattern pattern2 = Pattern.compile("\\.\\.");
				if(curProperty == null || pattern2.matcher(curJ).find())
					continue;
				
				List<Resource> domainJ = SparqlHelper.getDomain(requestURL, curJ);
				List<Resource> rangeJ = SparqlHelper.getRange(requestURL, curJ);
				
				// check domain - range : d_i(p) = r_j(p)
				if (compareSets(domainI, rangeJ)) {
					edgeAdjMatrix[i].set(j);
				}

				// check domain - domain : d_i(p) = d_j(p) [d_i(p) = r_j(p-)]
				if (compareSets(domainI, domainJ)) {
					edgeAdjMatrix[i].set(j + relCount);
				}

				// check range - range : r_i(p) = r_j(p) [d_i(p-) = r_j(p)]
				if (compareSets(rangeI, rangeJ)) {
					edgeAdjMatrix[i + relCount].set(j);
				}

				// check range - domain : r_i(p) = d_j(p) [d_i(p-) = r_j(p-)]
				if (compareSets(rangeI, domainJ)) {
					edgeAdjMatrix[i + relCount].set(j + relCount);
				}

			}
		}
	}

	/**
	 * 
	 * @param id
	 * @return the inverse id 
	 */
	public int getInverseID(int id) {
		int offset = edgeAdjMatrix.length / 2;
		int temp;
		if (id >= offset) {
			temp = id - offset;
		} else {
			temp = id + offset;
		}
		return temp;
	}

	public BitSet[] getEdgeAdjMatrix() {
		return edgeAdjMatrix;
	}

	public void setEdgeAdjMatrix(BitSet[] edgeAdjMatrix) {
		this.edgeAdjMatrix = edgeAdjMatrix;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public String toString() {
		return Arrays.toString(edgeAdjMatrix);
	}
	
	
}
