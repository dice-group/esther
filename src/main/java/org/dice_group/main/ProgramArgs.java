package org.dice_group.main;

import com.beust.jcommander.Parameter;

public class ProgramArgs {
	@Parameter(names = { "--data", "-d" }, description = "Folder path")
	String folderPath;

	@Parameter(names = { "--topk", "-k" }, description = "The number of optimal paths we want to find.")
	int k;

	@Parameter(names = { "--matrix",
			"-m" }, description = "The matrix type: S (Strict), SS (Subsumed), ND (Intersecting), or Irrelevant (Default).")
	String type;

	@Parameter(names = { "--emb-model", "-e" }, description = "The embedding model used: RotatE, TransE or DensE")
	String eModel;

	@Parameter(names = { "--sparql-endpoint", "-se" }, description = "The sparql endpoint ")
	String serviceRequestURL;
}
