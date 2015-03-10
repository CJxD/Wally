package com.cjwatts.wally;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.MapBackedDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.experiment.evaluation.classification.ClassificationResult;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import com.cjwatts.wally.analysis.*;
import com.cjwatts.wally.analysis.feature.*;
import com.cjwatts.wally.detection.processing.*;
import com.cjwatts.wally.training.*;
import com.cjwatts.wally.util.RandomSplitter;
import com.cjwatts.wally.util.SubjectDataset;
import com.cjwatts.wally.util.SubjectMatcher;

public class Wally {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		Wally context = new Wally();
		
		/*
		 * Load images
		 */
		SubjectDataset<MBFImage> subjects = context.load();

		//MBFImage background = ImageUtilities.readMBF(new File(
		//		"trainingdata/aligned/background.jpg"));
		
		
		/*
		 * Scale, remove backgrounds and normalise
		 */
		context.prepareProcessor();
		
		/*
		 * Create testing and training datasets
		 */
		System.out.println("Splitting subjects into training and testing sets...");
		RandomSplitter<Subject, MBFImage> splitter = new RandomSplitter<>(subjects, subjects.size() - 20, 0, 20);
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
		//context.analyse(subjects);
		//context.savePCE("temp/pce.dat");
		context.loadPCE("temp/pce.dat");
		
		/*
		 * Train heuristics
		 */
		//context.createTrainingSet(training);
		//context.saveTrainingSet("temp/cache.dat");
        context.loadTrainingSet("temp/cache.dat");
		
		/*
		 * Run training
		 */
        context.train();

		/*
		 * Test results
		 */
		context.test(testing);
	}
	
	
	public PrincipalComponentExtractor pce;
	public SubjectProcessor<MBFImage> processor;
	public TrainingSet trainingSet;
	public TrainingAlgorithm algorithm = RadialBasisFunctionTrainer.createGaussianRBF(20);
	
	public SubjectDataset<MBFImage> load() throws FileSystemException {
		System.out.println("Loading images...");
		File cwd = new File(".");
		return new SubjectDataset<>(
				cwd.getAbsolutePath() + "/trainingdata/stills/aligned/grouped",
				ImageUtilities.MBFIMAGE_READER);
	}
	
	public void prepareProcessor() {
		System.out.println("Preparing preprocessors...");
		
		processor = new SubjectResizer();
		//new SubjectNormaliser(new SubjectTrimmer(new TsukabaBackgroundRemover(background, new InputLimiter())));
	}
	
	public void analyse(SubjectDataset<MBFImage> subjects) {
		System.out.println("Processing training images...");

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
		pce = new PrincipalComponentExtractor(100);
		pce.train(allTraining);
	}
	
	public DoubleFV extract(MBFImage subject) {
		return extract(subject.flatten());
	}
	
	public DoubleFV extract(FImage subject) {
		return pce.extractFeature(subject);
	}
	
	public void savePCE(String path) throws IOException {
		saveData(pce, path);
	}
	
	public void loadPCE(String path) throws IOException, ClassNotFoundException {
		System.out.println("Loading PCA data...");
		pce = (PrincipalComponentExtractor) loadData(path);
	}
	
	public void createTrainingSet(GroupedDataset<Subject, ListDataset<MBFImage>, MBFImage> training) {
		System.out.println("Creating training set...");
		trainingSet = new TrainingSet();
		
		for (Entry<Subject, ListDataset<MBFImage>> e : training.entrySet()) {
			Subject s = e.getKey();
			
			// Add training data for each image of the subject
			for (MBFImage image : e.getValue()) {
				DoubleFV components = pce.extractFeature(image.process(processor).flatten());
				trainingSet.addAll(s.createTrainingSet(components));
			}
		}
	}
	
	public void saveTrainingSet(String path) throws IOException {
		saveData(trainingSet, path);
	}
	
	public void loadTrainingSet(String path) throws IOException, ClassNotFoundException {
		System.out.println("Loading training set...");
        trainingSet = (TrainingSet) loadData(path);
	}
	
	private void saveData(Object data, String path) throws IOException {
		try (
				FileOutputStream file = new FileOutputStream(path);
				ObjectOutputStream out = new ObjectOutputStream(file);
			) {
			out.writeObject(data);
		} catch (IOException ex) {
			throw ex;
		}
	}
	
	private Object loadData(String path) throws IOException, ClassNotFoundException {
		try (
				FileInputStream file = new FileInputStream(path);
				ObjectInputStream in = new ObjectInputStream(file);
			) {
			return in.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			throw ex;
		}
	}
	
	public void train() {
		System.out.println("Adjusting feature weights...");
		trainingSet.trainAll(algorithm);
	}
	
	/**
	 * @return Average error as a percentage
	 */
	public float test(GroupedDataset<Subject, ListDataset<MBFImage>, MBFImage> testing) {
		System.out.println("Testing recall...");
		int n = 0;
		float error = 0;
		SubjectMatcher matcher = new SubjectMatcher("jdbc:derby:db");
		int recallRate = 0;

		// Specific accuracies
		HashMap<Feature, Integer> accuracies = new HashMap<>();
		for (Feature f : SubjectFactory.xmlBackedSubject().getFeatures().keySet()) {
			accuracies.put(f, 0);
		}
		
		for (Entry<Subject, ListDataset<MBFImage>> e : testing.entrySet()) {
			Subject s = e.getKey();
			
			for (MBFImage image : e.getValue()) {
				DoubleFV components = pce.extractFeature(image.process(processor).flatten());
				Subject guess = SubjectFactory.databaseBackedSubjectFromData(components);
				
				// Get the error distance for each calculated image
				error += s.distanceFrom(guess);
				n++;
				
				// Recall the subject from the database, see if they are in top 5
				for (Subject r : matcher.findSimilar(guess, 5).getPredictedClasses()) {
					if (r.equals(s))
						recallRate++;
				}
				
				// Specific accuracies
				for (Entry<Feature, Category> f : s.getFeatures().entrySet()) {
					// If category is equal to guess's category, increment accuracy value
					if (f.getValue().equals(
							guess.getCategory(f.getKey())))
						accuracies.put(f.getKey(), accuracies.get(f.getKey()) + 1);
				}
			}
		}
		
		System.out.println("\nAccuracy levels");
		System.out.println("--------------------|---------");
		for (Entry<Feature, Integer> e : accuracies.entrySet()) {
			System.out.printf("%19s | %.2f%%\n", e.getKey().toString(), e.getValue() * 100f / n);
		}
		System.out.println("--------------------|---------\n");
		
		System.out.printf("Average error: %f\n", error / n);
		
		System.out.printf("Recall rate (within top 5): %.2f%%\n", recallRate * 100f / n);
		
		return error / n;
	}
}