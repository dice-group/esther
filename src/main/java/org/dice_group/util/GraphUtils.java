package org.dice_group.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import com.google.common.collect.Streams;

public class GraphUtils {


	/**
	 * Returns a new model without the literal statements and without RDF.type statements
	 * @param model
	 * @return
	 */
	public static Model cleanGraph(Model model) {
		Model cleanModel = ModelFactory.createDefaultModel();
		cleanModel.add(model);
		
		cleanModel.remove(cleanModel.listStatements(null, RDF.type, (RDFNode)null));
		
		List<Statement> literalStmts = cleanModel.listStatements().toList();
		literalStmts.removeIf(k->!k.getObject().isURIResource());
		cleanModel.remove(literalStmts);
		
		return cleanModel;
	}
	
	public static Set<?> getElementsInCommon(Iterator<?> a, Iterator<?> b) {
		return Streams.stream(a).filter(w -> Streams.stream(b).anyMatch(t -> t.equals(w))).collect(Collectors.toSet());
	}

}
