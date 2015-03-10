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
		MBFImage subjectImage = ImageUtilities.readMBF(new File("./trainingdata/stills/queries/q1-010.jpg"));
		subjectImage.processInplace(new SubjectResizer());
		
		Wally wally = new Wally();
		wally.loadPCE("temp/pce.dat");
		DoubleFV components = wally.extract(subjectImage);
		
		Subject subject = SubjectFactory.databaseBackedSubjectFromData(components);
		Subject real = SubjectFactory.databaseBackedSubject("010", "jdbc:derby:db");
		
		SubjectMatcher sm = new SubjectMatcher("jdbc:derby:db");
		ClassificationResult<Subject> r = sm.findSimilar(subject);
		System.out.println(r);
		
		System.out.printf("            Feature\tReal Subject\tGuessed Subject\n");
		for (Entry<Feature, Category> f : subject) {
			System.out.printf("%18s:\t%d\t\t%d\n", f.getKey(), real.getCategory(f.getKey()).ordinal(), f.getValue().ordinal());
		}
		System.out.println("Total distance: " + subject.distanceFrom(real));
	}
}
