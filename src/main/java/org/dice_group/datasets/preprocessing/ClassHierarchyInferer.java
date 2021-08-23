package org.dice_group.datasets.preprocessing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.thrift.wire.RDF_StreamRow;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class ClassHierarchyInferer {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		model.read(args[0]);
		
		Model subclassModel = ModelFactory.createDefaultModel();
		
		Map<RDFNode, Set<Resource>> map = new HashMap<RDFNode, Set<Resource>>();

		// get all types
		Set<RDFNode> types = model.listObjectsOfProperty(RDF.type).toSet();
		for (RDFNode curNode : types) {

			// get all instances of that type
			Set<Resource> classInstances = model.listSubjectsWithProperty(RDF.type, curNode).toSet();
			map.put(curNode, classInstances);
		}

		// if we have two types A and B and all instances of A are instances of B, A is a subclass of B.
		for (RDFNode keyA : map.keySet()) {

			Set<Resource> curA = map.get(keyA);
			for (RDFNode keyB : map.keySet()) {
				Set<Resource> curB = map.get(keyB);

				if (curA.size() != curB.size() && curA.containsAll(curB)) {
					subclassModel.add(keyB.asResource(), RDFS.subClassOf, keyA);
				}

				if (curA.size() != curB.size() && curB.containsAll(curA)) {
					subclassModel.add(keyA.asResource(), RDFS.subClassOf, keyB);
				}
			}
		}
		
		System.out.println(subclassModel.size());
		try (FileWriter out = new FileWriter(args[1])) {
			subclassModel.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
