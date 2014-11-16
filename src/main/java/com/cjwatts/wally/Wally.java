package com.cjwatts.wally;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import com.cjwatts.wally.analysis.PrincipleComponentExtractor;
import com.cjwatts.wally.detection.extraction.FullFrontBodyExtractor;
import com.cjwatts.wally.detection.processing.BasicBackgroundRemover;
import com.cjwatts.wally.detection.processing.SubjectNormaliser;

public class Wally {
	public static void main(String[] args) throws IOException {
		
		System.out.println("Loading images...");
		
		File trainingFile = new File("trainingdata/stills/training");
		File testingFile = new File("trainingdata/stills/testing");

		VFSListDataset<MBFImage> training = 
			    new VFSListDataset<>(trainingFile.getAbsolutePath(),
			    		ImageUtilities.MBFIMAGE_READER);
			    
	    VFSListDataset<MBFImage> testing = 
			    new VFSListDataset<>(testingFile.getAbsolutePath(),
			    		ImageUtilities.MBFIMAGE_READER);
		
		System.out.println("Preprocessing images...");
		MBFImage background = ImageUtilities.readMBF(new File(
				"trainingdata/background.jpg"));
		
		BasicBackgroundRemover<MBFImage> r = new BasicBackgroundRemover<>(background);
		List<MBFImage> preprocessedTraining = r.processAll(training);
		List<MBFImage> preprocessedTesting = r.processAll(testing);
		
		System.out.println("Extracting subjects from training data...");
		FullFrontBodyExtractor ffbe = new FullFrontBodyExtractor();
		List<MBFImage> trainingSubjects = ffbe.extractAll(training, preprocessedTraining);
		List<MBFImage> testSubjects = ffbe.extractAll(testing, preprocessedTesting);
		
		SubjectNormaliser s = new SubjectNormaliser();
		trainingSubjects = s.processAll(trainingSubjects);
		testSubjects = s.processAll(testSubjects);
		
		
		
		System.out.println("Analysing training images...");
		PrincipleComponentExtractor<FImage> pce = new PrincipleComponentExtractor<>(100);
		List<FImage> flattened = new ArrayList<>();
		for (MBFImage subject : trainingSubjects) {
			flattened.add(subject.flatten());
			DisplayUtilities.display(subject);
		}
		
		pce.train(flattened);

		List<DoubleFV> features = new ArrayList<>();
		for (FImage subject : flattened) {
			features.add(pce.extractFeature(subject));
		}
		
		System.out.println("Adjusting feature weights...");
		
		
		
		
		System.out.println("Testing recall...");

		
		
		
	}
}