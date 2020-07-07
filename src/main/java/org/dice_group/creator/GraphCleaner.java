package org.dice_group.creator;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class GraphCleaner {

	public GraphCleaner() {
	}
	
	/**
	 * Returns a new model without the literal statements and without RDF.type statements
	 * @param model
	 * @return
	 */
	public Model cleanGraph(Model model) {
		Model cleanModel = ModelFactory.createDefaultModel();
		cleanModel.add(model);
		
		cleanModel.remove(cleanModel.listStatements(null, RDF.type, (RDFNode)null));
		
		List<Statement> literalStmts = cleanModel.listStatements().toList();
		literalStmts.removeIf(k->!k.getObject().isURIResource());
		cleanModel.remove(literalStmts);
		
		return cleanModel;
	}

}
