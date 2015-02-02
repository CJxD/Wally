package com.cjwatts.wally;

import java.io.File;
import java.io.IOException;

import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.xuggle.XuggleVideo;

import com.cjwatts.wally.detection.processing.*;
import com.cjwatts.wally.detection.processing.background.*;

public class VideoTest {
	public static void main(String args[]) throws IOException {
		//Video<MBFImage> video = new XuggleVideo(new File("trainingdata/video/008a013s04L.dv"));
		Video<MBFImage> video = new XuggleVideo(new File("X:/HiD gait database/large/a/dv/008a013s04L.dv"));
		
		SubjectVideoProcessor<MBFImage> preprocessor = new SubjectVideoProcessor<>(
				new SubjectNormaliser(
						new InputLimiter()));
		
		preprocessor.process(video);
		
		MBFImage background = new TemporalMedian().detectBackground(video);
		
		VideoDisplay.createVideoDisplay(video);
		SubjectVideoProcessor<MBFImage> postprocessor = new SubjectVideoProcessor<>(
				new SubjectResizer(
						new SubjectTrimmer(
								new HorprasertBackgroundRemover(background))));
		
		postprocessor.process(video);
		
		VideoDisplay.createVideoDisplay(video);
	}
}
