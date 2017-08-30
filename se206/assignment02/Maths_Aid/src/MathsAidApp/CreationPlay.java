package MathsAidApp;

import java.io.File;
import java.net.MalformedURLException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class CreationPlay extends CreationProcess {
	
	private String _title;
	private MediaView _videoWindow;
	
	// Constructor
	public CreationPlay(String title, MediaView videoWindow) {
		_title = title;
		_videoWindow = videoWindow;
	}

	/**
	 * Plays a .mp4 creation video based on input name.
	 */
	@Override
	public void begin() throws MathsAidException {
		String uri;
		try {
			// Create URI string of location to video file readable by the Media object
			uri = new File(getHostFolder() + 
					System.getProperty("file.separator") + "creations" + 
					System.getProperty("file.separator") + _title + ".mp4").toURI().toURL().toExternalForm();
			
			// Create new Media object with path to video as argument
			Media video = new Media(uri);
			
			// Create new MediaPlayer object with Media object as argument
			MediaPlayer player = new MediaPlayer(video);
			
			// Set the videoWindow object to play the selected MediaPlayer 
			_videoWindow.setMediaPlayer(player);
			_videoWindow.getMediaPlayer().seek(Duration.ZERO);
		} catch (MalformedURLException e) {
		}
	}
	
	public void play() {
		_videoWindow.getMediaPlayer().play();
	}
}