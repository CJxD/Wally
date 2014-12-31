package com.cjwatts.wally;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map.Entry;

import org.apache.commons.lang.mutable.MutableFloat;
import org.apache.commons.lang.mutable.MutableInt;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.MapBackedDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.experiment.dataset.sampling.GroupedUniformRandomisedSampler;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.util.function.Operation;
import org.openimaj.util.parallel.Parallel;

import com.cjwatts.wally.analysis.PrincipleComponentExtractor;
import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.detection.extraction.FullFrontBodyExtractor;
import com.cjwatts.wally.detection.processing.InputLimiter;
import com.cjwatts.wally.detection.processing.SubjectNormaliser;
import com.cjwatts.wally.detection.processing.SubjectProcessor;
import com.cjwatts.wally.detection.processing.SubjectTrimmer;
import com.cjwatts.wally.detection.processing.background.BasicBackgroundRemover;
import com.cjwatts.wally.detection.processing.background.HorprasertBackgroundRemover;
import com.cjwatts.wally.detection.processing.background.TsukabaBackgroundRemover;
import com.cjwatts.wally.training.Heuristic;
import com.cjwatts.wally.training.TrainingData;
import com.cjwatts.wally.training.TrainingSet;
import com.cjwatts.wally.util.RandomSplitter;
import com.cjwatts.wally.util.SubjectDataset;

public class Wally {
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		/*
		 * Load images
		 */
		System.out.println("Loading images...");
		File cwd = new File(".");
		SubjectDataset<MBFImage> subjects = new SubjectDataset<>(
				cwd.getAbsolutePath() + "/trainingdata/stills/aligned/grouped",
				ImageUtilities.MBFIMAGE_READER);

		//MBFImage background = ImageUtilities.readMBF(new File(
		//		"trainingdata/aligned/background.jpg"));
		
		
		/*
		 * Scale, remove backgrounds and normalise
		 */
		System.out.println("Preparing preprocessors...");
		
		final SubjectProcessor<MBFImage> processor = new SubjectNormaliser();
		//new SubjectNormaliser(new SubjectTrimmer(new TsukabaBackgroundRemover(background, new InputLimiter())));
		
		/*
		 * Create testing and training datasets
		 */
		System.out.println("Splitting subjects into training and testing sets...");
		RandomSplitter<Subject, MBFImage> splitter = new RandomSplitter<>(subjects, subjects.size() / 2, 0, subjects.size() / 2);
		GroupedDataset<Subject, ListDataset<MBFImage>, MBFImage> training = splitter.getTrainingDataset();
		GroupedDataset<Subject, ListDataset<MBFImage>, MBFImage> testing = splitter.getTestDataset();
		
		//DisplayUtilities.display("Training Subjects", training.);
		
		/*System.out.println("Extracting subjects from training data...");
		FullFrontBodyExtractor ffbe = new FullFrontBodyExtractor();
		List<MBFImage> trainingSubjects = ffbe.extractAll(training, preprocessedTraining);
		List<MBFImage> testSubjects = ffbe.extractAll(testing, preprocessedTesting);*/

		
		/*
		 * Train the principle component extractor
		 */
		/**System.out.println("Processing training images...");

		GroupedDataset<Subject, ListDataset<FImage>, FImage> flattened = new MapBackedDataset<>();
		ListDataset<FImage> allTraining = new ListBackedDataset<>();
		
		// Process and flatten all images
		for (Entry<Subject, VFSListDataset<MBFImage>> e : subjects.entrySet()) {
			
			ListDataset<FImage> group = new ListBackedDataset<>();
			for (MBFImage image : e.getValue()) {
				group.add(image.process(processor).flatten());
			}
			
			allTraining.addAll(group);
			flattened.put(e.getKey(), group);
		}

		System.out.println("Analysing principle components...");
		final PrincipleComponentExtractor pce = new PrincipleComponentExtractor(100);
		pce.train(allTraining);
		
		/*
		 * Save for later
		 */
		/*FileOutputStream pceFile = new FileOutputStream("temp/pce.dat");
		ObjectOutputStream pceOut = new ObjectOutputStream(pceFile);
		pceOut.writeObject(pce);
		pceOut.close();
		pceFile.close();*/
		
		FileInputStream pceFile = new FileInputStream("temp/pce.dat");
        ObjectInputStream pceIn = new ObjectInputStream(pceFile);
        final PrincipleComponentExtractor pce = (PrincipleComponentExtractor) pceIn.readObject();
        pceIn.close();
        pceFile.close();
		
		/*
		 * Train heuristics
		 */
		System.out.println("Analysing principle components...");
		/*Parallel.forEach(training.entrySet(), new Operation<Entry<Subject, ListDataset<MBFImage>>>(){

			@Override
			public void perform(Entry<Subject, ListDataset<MBFImage>> e) {
				Subject s = e.getKey();
				
				// Run training for each image of the subject
				for (MBFImage image : e.getValue()) {
					DoubleFV components = pce.extractFeature(image.process(processor).flatten());
					s.trainFeatures(components);
				}
			}
			
		});*//**
		
		TrainingSet set = new TrainingSet();
			
		for (Entry<Subject, ListDataset<MBFImage>> e : training.entrySet()) {
			Subject s = e.getKey();
			
			// Add training data for each image of the subject
			for (MBFImage image : e.getValue()) {
				DoubleFV components = pce.extractFeature(image.process(processor).flatten());
				set.addAll(s.getTrainingSet(components));
			}
		}
		
		/*
		 * Save for later
		 */
		/*FileOutputStream setFile = new FileOutputStream("temp/cache.dat");
		ObjectOutputStream setOut = new ObjectOutputStream(setFile);
		setOut.writeObject(set);
		setOut.close();
		setFile.close();*/
		
        FileInputStream setFile = new FileInputStream("temp/cache.dat");
        ObjectInputStream setIn = new ObjectInputStream(setFile);
        TrainingSet set = (TrainingSet) setIn.readObject();
        setIn.close();
        setFile.close();
		
		// Run training
        System.out.println("Adjusting feature weights...");
		set.trainAll();
		
		/*
		 * Test results
		 */
		System.out.println("Testing recall...");
		final MutableInt n = new MutableInt(0);
		final MutableFloat error = new MutableFloat(0);

		/*Parallel.forEach(testing.entrySet(), new Operation<Entry<Subject, ListDataset<MBFImage>>>(){

			@Override
			public void perform(Entry<Subject, ListDataset<MBFImage>> e) {
				Subject s = e.getKey();
				
				// Get the error distance for each calculated image
				float err = 0f;
				for (MBFImage image : e.getValue()) {
					DoubleFV components = pce.extractFeature(image.process(processor).flatten());
					err += s.distanceFrom(Subject.fromData(components));
				}
				
				synchronized (error) {
					error.add(err);
				}
				
				synchronized (n) {
					n.increment();
				}
			}
			
		});*/
		
		for (Entry<Subject, ListDataset<MBFImage>> e : testing.entrySet()) {
			Subject s = e.getKey();
			
			// Get the error distance for each calculated image
			float err = 0f;
			for (MBFImage image : e.getValue()) {
				DoubleFV components = pce.extractFeature(image.process(processor).flatten());
				err += s.distanceFrom(Subject.fromData(components));
			}
			
			error.add(err);
			
			n.increment();
		}
		
		System.out.printf("Average error: %f", error.toFloat() / n.toInteger());
		
	}
}