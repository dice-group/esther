package org.dice_group.path;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice_group.embeddings.dictionary.Dictionary;

public class Graph {

	private Model model;
	private Dictionary dictionary;

	public Graph() {
		model = ModelFactory.createDefaultModel();
		dictionary = new Dictionary();
	}

	public Graph(Model model, Dictionary dictionary) {
		this.model = model;
		this.dictionary = dictionary;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

}
