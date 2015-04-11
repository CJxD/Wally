package com.cjwatts.wally;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListBackedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.MapBackedDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.PrincipalComponentExtractor;
import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.analysis.SubjectFactory;
import com.cjwatts.wally.analysis.feature.Feature;
import com.cjwatts.wally.detection.processing.SubjectProcessor;
import com.cjwatts.wally.detection.processing.SubjectResizer;
import com.cjwatts.wally.persistence.HeuristicPersistenceHandler;
import com.cjwatts.wally.training.Heuristic;
import com.cjwatts.wally.training.RadialBasisFunctionTrainer;
import com.cjwatts.wally.training.TrainingAlgorithm;
import com.cjwatts.wally.training.TrainingSet;
import com.cjwatts.wally.util.RandomSplitter;
import com.cjwatts.wally.util.SubjectDataset;
import com.cjwatts.wally.util.SubjectMatcher;

public class Wally {

	public class TestResult {
		public Map<Feature, Float> accuracies;
		public float averageError;
		public float recallRate;
	}
	
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
		
		System.out.println("Training Set:");
		int i = 0;
		for (Subject s : training.getGroups()) {
			System.out.print(s.getID() + "  ");
			if (++i % 10 == 0) System.out.print('\n');
		}
		System.out.print('\n');
		
		System.out.println("Testing Set:");
		i = 0;
		for (Subject s : testing.getGroups()) {
			System.out.print(s.getID() + "  ");
			if (++i % 10 == 0) System.out.print('\n');
		}
		System.out.print('\n');
		
		//DisplayUtilities.display("Training Subjects", training.);
		
		/*System.out.println("Extracting subjects from training data...");
		FullFrontBodyExtractor ffbe = new FullFrontBodyExtractor();
		List<MBFImage> trainingSubjects = ffbe.extractAll(training, preprocessedTraining);
		List<MBFImage> testSubjects = ffbe.extractAll(testing, preprocessedTesting);*/

		
		/*
		 * Train the principle component extractor
		 */
		//context.analyse(subjects);
		//context.savePCE();
		context.loadPCE();
		
		/*
		 * Train heuristics
		 */
		//context.createTrainingSet(training);
		//context.saveTrainingSet();
        context.loadTrainingSet();
		
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
	
	private WallyConfiguration config;
	
	public Wally() {
		this(WallyConfiguration.defaultConfiguration);
	}
	
	public Wally(WallyConfiguration config) {
		this.config = config;
		// Cheat
		HeuristicPersistenceHandler.dbPath = config.heuristicsPath;
	}
	
	public SubjectDataset<MBFImage> load() throws FileSystemException {
		if (config.verbose) System.out.println("Loading images...");
		return new SubjectDataset<>(
				config.getTrainingFile().getAbsolutePath(),
				ImageUtilities.MBFIMAGE_READER);
	}
	
	public void prepareProcessor() {
		if (config.verbose) System.out.println("Preparing preprocessors...");
		
		processor = new SubjectResizer();
		//new SubjectNormaliser(new SubjectTrimmer(new TsukabaBackgroundRemover(background, new InputLimiter())));
	}
	
	public void analyse(SubjectDataset<MBFImage> subjects) {
		analyse(subjects, 100);
	}
	
	public void analyse(SubjectDataset<MBFImage> subjects, int numComponents) {
		if (config.verbose) System.out.println("Processing training images...");

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

		if (config.verbose) System.out.println("Analysing principle components...");
		pce = new PrincipalComponentExtractor(numComponents);
		pce.train(allTraining);
	}
	
	public DoubleFV extract(MBFImage subject) {
		return extract(subject.flatten());
	}
	
	public DoubleFV extract(FImage subject) {
		return pce.extractFeature(subject);
	}
	
	public void savePCE() throws IOException {
		savePCE(config.cachePath + "pce.dat");
	}
	
	public void savePCE(String path) throws IOException {
		saveData(pce, path);
	}
	
	public void loadPCE() throws IOException, ClassNotFoundException {
		loadPCE(config.cachePath + "pce.dat");
	}
	
	public void loadPCE(String path) throws IOException, ClassNotFoundException {
		if (config.verbose) System.out.println("Loading PCA data...");
		pce = (PrincipalComponentExtractor) loadData(path);
	}
	
	public void createTrainingSet(GroupedDataset<Subject, ListDataset<MBFImage>, MBFImage> training) {
		if (config.verbose) System.out.println("Creating training set...");
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
	
	public void saveTrainingSet() throws IOException {
		saveTrainingSet(config.cachePath + "trainingset.dat");
	}
	
	public void saveTrainingSet(String path) throws IOException {
		saveData(trainingSet, path);
	}
	
	public void loadTrainingSet() throws IOException, ClassNotFoundException {
		loadTrainingSet(config.cachePath + "trainingset.dat");
	}
	
	public void loadTrainingSet(String path) throws IOException, ClassNotFoundException {
		if (config.verbose) System.out.println("Loading training set...");
        trainingSet = (TrainingSet) loadData(path);
	}
	
	private void saveData(Object data, String path) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		try (
				FileOutputStream file = new FileOutputStream(f);
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
		if (config.verbose) System.out.println("Adjusting feature weights...");
		trainingSet.trainAll(algorithm);
	}
	
	/**
	 * @return Accuracies for each feature, average error, and recall rate
	 */
	public TestResult testRecall(GroupedDataset<Subject, ListDataset<MBFImage>, MBFImage> testing) {
		if (config.verbose) System.out.println("Testing recall...");
		int n = 0;
		float error = 0;
		SubjectMatcher matcher = new SubjectMatcher(config.dbUri);
		int recallRate = 0;

		// Specific accuracies
		Map<Feature, Integer> accuracies = new TreeMap<>();
		for (Feature f : SubjectFactory.xmlBackedSubject().getFeatures().keySet()) {
			accuracies.put(f, 0);
		}
		
		for (Entry<Subject, ListDataset<MBFImage>> e : testing.entrySet()) {
			Subject s = e.getKey();
			
			for (MBFImage image : e.getValue()) {
				DoubleFV components = pce.extractFeature(image.process(processor).flatten());
				Subject guess = SubjectFactory.databaseBackedSubjectFromData(new DoubleFV[] {components});
				
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
		
		TestResult r = new TestResult();
		r.accuracies = new TreeMap<>();
		for (Entry<Feature, Integer> e : accuracies.entrySet()) {
			// Convert frequencies to percentages
			r.accuracies.put(e.getKey(), e.getValue() * 100f / n);
		}
		
		r.averageError = error / n;
		r.recallRate = recallRate * 100f / n;
		
		return r;
	}
	
	/**
	 * Destroys all heuristic weightings
	 */
	public void clearWeightings() {
		for (Heuristic h : SubjectFactory.xmlBackedSubject().getFeatures().keySet()) {
			h.clearAll();
			h.save();
		}
	}
	
	/**
	 * Manually reloads all heuristic weightings
	 */
	public void reloadHeuristics() {
		for (Heuristic h : SubjectFactory.xmlBackedSubject().getFeatures().keySet()) {
			if (!h.load()) {
				throw new RuntimeException("Unable to load the " + h + " heuristic.");
			}
		}
	}
	
	/**
	 * Manually saves all heuristic weightings
	 */
	public void saveHeuristics() {
		for (Heuristic h : SubjectFactory.xmlBackedSubject().getFeatures().keySet()) {
			if (!h.save()) {
				throw new RuntimeException("Unable to save the " + h + " heuristic.");
			}
		}
	}
	
	/**
	 * @return Average error as a percentage
	 */
	public float test(GroupedDataset<Subject, ListDataset<MBFImage>, MBFImage> testing) {
		TestResult r = testRecall(testing);
		
		System.out.println("\nAccuracy levels");
		System.out.println("--------------------|---------");
		for (Entry<Feature, Float> e : r.accuracies.entrySet()) {
			System.out.printf("%19s | %.2f%%\n", e.getKey().toString(), e.getValue());
		}
		System.out.println("--------------------|---------\n");
		
		System.out.printf("Average error: %f\n", r.averageError);
		
		System.out.printf("Recall rate (within top 5): %.2f%%\n", r.recallRate);
		
		return r.averageError;
	}
	
	public WallyConfiguration getConfig() {
		return config;
	}
}