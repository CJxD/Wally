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

public class Sandbox {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		MBFImage subjectImage = ImageUtilities.readMBF(new File("./trainingdata/stills/queries/q8.1-053.jpg"));
		subjectImage.processInplace(new SubjectResizer());
		
		Wally wally = new Wally();
		wally.loadPCE();
		DoubleFV components = wally.extract(subjectImage);
		
		Subject subject = SubjectFactory.databaseBackedSubjectFromData(new DoubleFV[] {components});
		Subject real = SubjectFactory.databaseBackedSubject("053", "jdbc:derby:db");
		
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
}
