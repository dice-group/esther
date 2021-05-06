package org.dice_group.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to convert a N-triple file into a TSV file (PBG's input format)
 */
public class PBGraphInputFormatter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PBGraphInputFormatter.class);

	public static void main(String[] args) throws IOException {
		LOGGER.info("Starting...");
		try (BufferedReader br = new BufferedReader(new FileReader(args[0]));
				BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));) {
			int writeCount = 0;
			String line;
			while ((line = br.readLine()) != null) {
				// remove last .
				line = line.substring(0, line.lastIndexOf("."));
				String[] lineItems = line.split(" ");
				for (String curItem : lineItems) {
					// remove surrounding <>
					curItem = curItem.substring(1, curItem.length() - 1);
				}
				String outputLine = String.join("\t", lineItems);
				bw.write(outputLine);
				if (writeCount % 10000 == 0) {
					bw.flush();
					LOGGER.info("Processed "+writeCount);
				}
			}
		}
		LOGGER.info("Done!");
	}

}
