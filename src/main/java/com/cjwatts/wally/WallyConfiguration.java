package com.cjwatts.wally;

import java.io.File;

public final class WallyConfiguration {
	
	public static WallyConfiguration defaultConfiguration = new WallyConfiguration(
			"jdbc:derby:db",
			"heuristics/",
			"cache/",
			"trainingdata/stills/aligned/grouped/",
			true
	);
	
	public final String dbUri, heuristicsPath, cachePath, trainingPath;
	public final boolean verbose;
	
	public WallyConfiguration(String dbUri, String heuristicsPath, String cachePath, String trainingPath, boolean verbose) {
		if (!heuristicsPath.endsWith("/")) heuristicsPath += "/";
		if (!cachePath.endsWith("/")) cachePath += "/";
		if (!trainingPath.endsWith("/")) trainingPath += "/";
		
		this.dbUri = dbUri;
		this.cachePath = cachePath;
		this.heuristicsPath = heuristicsPath;
		this.trainingPath = trainingPath;
		this.verbose = verbose;
	}

	public File getHeuristicsFile() {
		return new File(heuristicsPath);
	}
	
	public File getCacheFile() {
		return new File(cachePath);
	}
	
	public File getTrainingFile() {
		return new File(trainingPath);
	}
	
}
