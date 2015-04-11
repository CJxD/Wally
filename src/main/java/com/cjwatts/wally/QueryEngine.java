package com.cjwatts.wally;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.openimaj.experiment.evaluation.classification.ClassificationResult;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.FeatureCategory;
import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.analysis.SubjectFactory;
import com.cjwatts.wally.analysis.feature.Feature;
import com.cjwatts.wally.detection.processing.SubjectResizer;
import com.cjwatts.wally.util.SubjectMatcher;

public class QueryEngine {
	public static void main(String[] args) {
		if (args.length < 2) {
			usage();
			System.exit(1);
		}
		
		Integer n = null;
		try {
			n = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			// This is now an optional argument, don't exit yet
			//usage();
			//System.exit(1);
			
			// Shift the args array one to the right
			String[] temp = new String[args.length + 1];
			System.arraycopy(args, 0, temp, 1, args.length);
			args = temp;
		}
		
		MBFImage[] subjectImage = new MBFImage[args.length - 1];
		for (int i = 0; i < args.length - 1; i++) {
			try {
				subjectImage[i] = ImageUtilities.readMBF(new File(args[i + 1]));
				subjectImage[i].processInplace(new SubjectResizer());
			} catch (IOException ex) {
				System.err.println("Could not read image from path.");
				usage();
				System.exit(1);
			}
		}
		
		
		Wally wally = new Wally();
		try {
			wally.loadPCE();
		} catch (ClassNotFoundException | IOException ex) {
			System.err.println("Unable to load principal component data.");
			System.exit(1);
		}
		
		DoubleFV[] components = new DoubleFV[args.length - 1];
		for (int i = 0; i < args.length - 1; i++) {
			components[i] = wally.extract(subjectImage[i]);
		}
		
		Subject subject = SubjectFactory.databaseBackedSubjectFromData(components);
		
		System.out.printf("\n            Feature\tCategory\n");
		for (Entry<Feature, Category> f : subject) {
			FeatureCategory c = f.getKey().getFeatureCategory(f.getValue().ordinal());
			System.out.printf("%18s:\t%s\n", f.getKey(), c);
		}
		
		if (n != null && n > 0) {
			SubjectMatcher sm = new SubjectMatcher(wally.getConfig().dbUri);
			ClassificationResult<Subject> r = sm.findSimilar(subject, n);
			
			System.out.println("Closest matches:");
			System.out.println(r);
		}
	}
	
	public static void usage() {
		System.out.println("Usage:");
		System.out.println("QueryEngine [num_matches] <img_path> [img_path ...]");
	}
}
