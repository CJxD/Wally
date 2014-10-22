package com.cjwatts.wally.detection;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.background.BasicBackgroundSubtract;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector.BuiltInCascade;

public class FullFrontBodyExtractor implements FeatureExtractor {
	public static void main(String[] args) throws IOException {
		// Create a window
    	JFrame window = DisplayUtilities.createNamedWindow("Feature Detection");
    	
    	System.out.println("Loading image...");
    	
		// Create an image
    	FImage background = ImageUtilities.readF(new File("trainingdata/stills/background.jpg"));
    	FImage image = ImageUtilities.readF(new File("trainingdata/stills/012z033pf.jpg"));
    	image.processInplace(new BasicBackgroundSubtract<FImage>(background));
    	
    	System.out.println("Applying haar detector...");
    	
    	HaarCascadeDetector detector = BuiltInCascade.fullbody.load();
    	List<DetectedFace> bodies = detector.detectFaces(image);
    	
    	System.out.println(bodies.size() + " bodies found");
    	
    	for (DetectedFace b : bodies) {
    		FImage subject = b.getFacePatch().process(new CannyEdgeDetector());
    		DisplayUtilities.display(subject);
    	}
    	
    	// Draw
    	//DisplayUtilities.display(image, window);
	}
}
