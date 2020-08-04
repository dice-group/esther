package org.dice_group.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class Dictionary {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		String dataFolderPath = args[0];

		File folder = new File(dataFolderPath);
		if (folder != null && folder.isDirectory() && folder.listFiles().length > 0) {
			for (String fileName : folder.list()) {
				model.read(dataFolderPath + "/" + fileName);
			}
		}

		Set<Resource> entities = new HashSet<Resource>();
		Set<Resource> relations = new HashSet<Resource>();

		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			Statement curStmt = stmtIterator.next();
			entities.add(curStmt.getSubject());
			if (curStmt.getObject().isResource())
				entities.add(curStmt.getObject().asResource());
			relations.add(curStmt.getPredicate());
		}
		
		printDictionary(entities, dataFolderPath + "/entities.dict");
		printDictionary(relations, dataFolderPath + "/relations.dict");

	}

	public static void printDictionary(Set<Resource> set, String savedFile) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (Resource curRes : set) {
			builder.append(i++);
			builder.append(" " + curRes.toString() + "\n");
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(savedFile))) {
			bw.append(builder);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
