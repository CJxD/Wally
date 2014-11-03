package com.cjwatts.wally;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import com.cjwatts.wally.analysis.Height;
import com.cjwatts.wally.analysis.Measurement;
import com.cjwatts.wally.detection.BackgroundRemover;
import com.cjwatts.wally.detection.FullFrontBodyExtractor;
import com.cjwatts.wally.training.Heuristic;

public class Wally {
	public static void main(String[] args) throws IOException {
		System.out.println("Setting up heuristics...");
		Heuristic h = new Height();
		h.setWeighting("length", 0.0128f);
		
		System.out.println("Testing measurements...");
		HashSet<Measurement> measurements = new HashSet<>();
		measurements.add(new Measurement("length", 312));
		
		System.out.println(h.estimate(measurements).name());
		
		System.out.println("Loading image...");

		MBFImage background = ImageUtilities.readMBF(new File(
				"trainingdata/stills/background.jpg"));
		MBFImage image = ImageUtilities.readMBF(new File(
				"trainingdata/stills/011z026pf.jpg"));

		System.out.println("Applying haar detector...");

		FullFrontBodyExtractor ffbe = new FullFrontBodyExtractor();
		BackgroundRemover br = new BackgroundRemover(background);

		List<MBFImage> bodies = ffbe.extractAll(image, br);

		System.out.println(bodies.size() + " bodies found");

		for (MBFImage subject : bodies) {
			DisplayUtilities.display(subject);
		}

	}
}