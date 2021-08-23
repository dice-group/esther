package org.dice_group.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogUtils {

	/**
	 * Appends text to a log file, creates the file if it doesn't exist yet
	 * 
	 * @param text
	 * @param folderPath
	 */
	public static void printTextToLog(String text) {
		try (FileWriter fw = new FileWriter("log.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
