package wally;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import com.cjwatts.wally.detection.extraction.FullFrontBodyExtractor;
import com.cjwatts.wally.detection.processing.InputLimiter;
import com.cjwatts.wally.detection.processing.background.HorprasertBackgroundRemover;
import com.cjwatts.wally.detection.processing.background.TsukabaBackgroundRemover;

public class Sandbox {
	public static void main(String[] args) throws IOException {
		MBFImage test = ImageUtilities.readMBF(new File("trainingdata/stills/021z010pf.jpg"));
		MBFImage bg = ImageUtilities.readMBF(new File("trainingdata/background.jpg"));

		ImageUtilities.write(test, new File("../input.jpg"));
		
		test.processInplace(new HorprasertBackgroundRemover(bg));
	
		ImageUtilities.write(test, new File("../output.jpg"));
		DisplayUtilities.display(test);
	}
}
