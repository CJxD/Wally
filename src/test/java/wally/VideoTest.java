package wally;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;

import com.cjwatts.wally.detection.processing.*;
import com.cjwatts.wally.detection.processing.background.*;
import com.cjwatts.wally.util.XuggleVideo;

public class VideoTest {
	public static void main(String args[]) throws IOException {
		Video<MBFImage> video = new XuggleVideo(new File("trainingdata/video/008a013s04L.dv"));
		
		SubjectVideoProcessor<MBFImage> preprocessor = new SubjectVideoProcessor<>(
				new SubjectNormaliser(
						new InputLimiter()));
		
		preprocessor.process(video);
		video.reset();
		
		MBFImage background = new TemporalMedian().detectBackground(video);
		video.reset();
		
		SubjectVideoProcessor<MBFImage> postprocessor = new SubjectVideoProcessor<>(
				new SubjectResizer(
						new SubjectTrimmer(
								new TsukabaBackgroundRemover(background))));
		
		postprocessor.process(video);
		video.reset();
		
		VideoDisplay.createVideoDisplay(video, new JFrame("Output"));
	}
}
