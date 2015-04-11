package wally;

import java.io.IOException;

import org.openimaj.image.MBFImage;

import com.cjwatts.wally.Wally;
import com.cjwatts.wally.WallyConfiguration;
import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.training.*;
import com.cjwatts.wally.util.RandomSplitter;
import com.cjwatts.wally.util.SubjectDataset;

public class Benchmark {
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		WallyConfiguration config = new WallyConfiguration(
				"jdbc:derby:db",
				"temp/heuristics/",
				"temp/cache/",
				"trainingdata/stills/aligned/grouped/",
				false
		);
		
		Wally context = new Wally(config);
		
		/*
		 * Load images
		 */
		SubjectDataset<MBFImage> subjects = context.load();
		
		/*
		 * Scale, remove backgrounds and normalise
		 */
		context.prepareProcessor();
		
		/*
		 * Create testing and training datasets
		 */
		RandomSplitter<Subject, MBFImage> splitter = new RandomSplitter<>(subjects, subjects.size() - 20, 0, 20);
		
		/*
		 * Train the principle component extractor
		 */
		context.loadPCE();
		
		/*
		 * Training set is kept constant with a known-good one
		 * We're testing the algorithm's performance here
		 */
		context.loadTrainingSet();
		
        int n = 10;
        float error, recall;
        
        /*
		 * Gradient Descent tests
		 */
        System.out.println();
        System.out.println("## GD TESTS");
        System.out.println();
        System.out.println("Age,Arm Length,Arm Thickness,Chest,Ethnicity,Facial Hair Colour,Facial Hair Length,Figure,Hair Colour,Hair Length,Height,Hips,Leg Length,Leg Shape,Leg Thickness,Muscle Build,Neck Length,Neck Thickness,Proportions,Sex,Shoulder Shape,Skin Colour,Weight");
        error = 0;
        recall = 0;
        for (int j = 0; j < n; j++) {
        	context.algorithm = new GradientDescentTrainer(0.00005, 1000);
        	//splitter.recomputeSubsets();
        	//context.createTrainingSet(splitter.getTrainingDataset());
        	context.clearWeightings();
	        context.train();
	        
			Wally.TestResult r = context.testRecall(splitter.getTestDataset());
			error += r.averageError;
			recall += r.recallRate;
			
			int k = 1;
			for (Float e : r.accuracies.values()) {
				System.out.print(e);
				if (k++ < r.accuracies.size()) System.out.print(',');
			}
			
			System.out.println();
        }
        System.out.printf("Average error: %f\n", error / n);
		System.out.printf("Average recall rate: %.2f%%\n", recall / n);
		
		/*
		 * Linear Regression tests
		 */
        System.out.println();
        System.out.println("## LR TESTS");
        System.out.println();
        System.out.println("Age,Arm Length,Arm Thickness,Chest,Ethnicity,Facial Hair Colour,Facial Hair Length,Figure,Hair Colour,Hair Length,Height,Hips,Leg Length,Leg Shape,Leg Thickness,Muscle Build,Neck Length,Neck Thickness,Proportions,Sex,Shoulder Shape,Skin Colour,Weight");
        error = 0;
        recall = 0;
        for (int j = 0; j < n; j++) {
        	context.algorithm = new LinearRegressionTrainer();
        	//splitter.recomputeSubsets();
        	//context.createTrainingSet(splitter.getTrainingDataset());
        	context.clearWeightings();
	        context.train();
	        
			Wally.TestResult r = context.testRecall(splitter.getTestDataset());
			error += r.averageError;
			recall += r.recallRate;
			
			int k = 1;
			for (Float e : r.accuracies.values()) {
				System.out.print(e);
				if (k++ < r.accuracies.size()) System.out.print(',');
			}
			
			System.out.println();
        }
        System.out.printf("Average error: %f\n", error / n);
		System.out.printf("Average recall rate: %.2f%%\n", recall / n);
        
		/*
		 * RBF tests
		 */
        System.out.println();
        System.out.println("## RBF TESTS");
        System.out.println();
        System.out.println("Age,Arm Length,Arm Thickness,Chest,Ethnicity,Facial Hair Colour,Facial Hair Length,Figure,Hair Colour,Hair Length,Height,Hips,Leg Length,Leg Shape,Leg Thickness,Muscle Build,Neck Length,Neck Thickness,Proportions,Sex,Shoulder Shape,Skin Colour,Weight");
        error = 0;
        recall = 0;
        for (int j = 0; j < n; j++) {
        	context.algorithm = RadialBasisFunctionTrainer.createGaussianRBF(20);
        	//splitter.recomputeSubsets();
        	//context.createTrainingSet(splitter.getTrainingDataset());
        	context.clearWeightings();
	        context.train();
	        
			Wally.TestResult r = context.testRecall(splitter.getTestDataset());
			error += r.averageError;
			recall += r.recallRate;
			
			int k = 1;
			for (Float e : r.accuracies.values()) {
				System.out.print(e);
				if (k++ < r.accuracies.size()) System.out.print(',');
			}
			
			System.out.println();
        }
        System.out.printf("Average error: %f\n", error / n);
		System.out.printf("Average recall rate: %.2f%%\n", recall / n);
        System.exit(0);
        /*
		 * Random tests
		 */
        System.out.println();
        System.out.println("## RANDOM TESTS");
        System.out.println();
        System.out.println("Age,Arm Length,Arm Thickness,Chest,Ethnicity,Facial Hair Colour,Facial Hair Length,Figure,Hair Colour,Hair Length,Height,Hips,Leg Length,Leg Shape,Leg Thickness,Muscle Build,Neck Length,Neck Thickness,Proportions,Sex,Shoulder Shape,Skin Colour,Weight");
        error = 0;
        recall = 0;
        for (int j = 0; j < n; j++) {
        	context.algorithm = new RandomTrainer();
        	//splitter.recomputeSubsets();
        	//context.createTrainingSet(splitter.getTrainingDataset());
	        context.train();
	        
			Wally.TestResult r = context.testRecall(splitter.getTestDataset());
			error += r.averageError;
			recall += r.recallRate;
			
			int k = 1;
			for (Float e : r.accuracies.values()) {
				System.out.print(e);
				if (k++ < r.accuracies.size()) System.out.print(',');
			}
			
			System.out.println();
        }
        System.out.printf("Average error: %f\n", error / n);
		System.out.printf("Average recall rate: %.2f%%\n", recall / n);
        
        /*
		 * Mid-value tests
		 */
        System.out.println();
        System.out.println("## MID-VALUE TESTS");
        System.out.println();
        System.out.println("Age,Arm Length,Arm Thickness,Chest,Ethnicity,Facial Hair Colour,Facial Hair Length,Figure,Hair Colour,Hair Length,Height,Hips,Leg Length,Leg Shape,Leg Thickness,Muscle Build,Neck Length,Neck Thickness,Proportions,Sex,Shoulder Shape,Skin Colour,Weight");
        error = 0;
        recall = 0;
		for (int j = 0; j < n; j++) {	
	    	context.algorithm = new MedianTrainer();
	    	//splitter.recomputeSubsets();
        	//context.createTrainingSet(splitter.getTrainingDataset());
	        context.train();
	        
			Wally.TestResult r = context.testRecall(splitter.getTestDataset());
			error += r.averageError;
			recall += r.recallRate;
		
			int k = 1;
			for (Float e : r.accuracies.values()) {
				System.out.print(e);
				if (k++ < r.accuracies.size()) System.out.print(',');
			}
			
			System.out.println();
        }
        System.out.printf("Average error: %f\n", error / n);
		System.out.printf("Average recall rate: %.2f%%\n", recall / n);
	}
}
