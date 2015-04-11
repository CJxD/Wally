package wally;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.openimaj.experiment.evaluation.classification.ClassificationResult;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import com.cjwatts.wally.Wally;
import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.analysis.SubjectFactory;
import com.cjwatts.wally.analysis.feature.Feature;
import com.cjwatts.wally.detection.processing.SubjectResizer;
import com.cjwatts.wally.util.SubjectMatcher;

public class QueryTest {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length < 2) {
			usage();
			System.exit(1);
		}
		
		String id = args[0];
		
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
		Subject real = SubjectFactory.databaseBackedSubject(id, wally.getConfig().dbUri);
		
		if (!real.isLoaded()) {
			System.err.println("No such subject: " + id);
			System.exit(1);
		}
		
		System.out.println("Matching subject " + id);
		
		SubjectMatcher sm = new SubjectMatcher(wally.getConfig().dbUri);
		ClassificationResult<Subject> r = sm.findSimilar(subject);
		
		int i = 1;
		for (Subject s : r.getPredictedClasses()) {
			if (s.equals(real)) {
				System.out.printf("Matched as #%d of %d (%.4f%% confidence)\n", i, r.getPredictedClasses().size(), r.getConfidence(s) * 100);
			}
			i++;
		}
		
		System.out.printf("\n            Feature\tReal Subject\tGuessed Subject\n");
		for (Entry<Feature, Category> f : subject) {
			System.out.printf("%18s:\t%d\t\t%d\n", f.getKey(), real.getCategory(f.getKey()).ordinal(), f.getValue().ordinal());
		}
		System.out.println("Total distance: " + subject.distanceFrom(real));
	}
	
	public static void usage() {
		System.out.println("Usage:");
		System.out.println("QueryTest <subject_id> <img_path> [img_path ...]");
	}
}
