package org.dice_group.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;

public class PrintToFileUtils {

	public static void printStringToFile(String string, File file) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(string);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printRDFToFile(Model model, String savePath) {
		try (FileWriter out = new FileWriter(savePath)) {
			model.write(out, "NT");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
