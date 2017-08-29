package MathsAidApp;

import java.io.File;
import java.net.MalformedURLException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class CreationPlay extends CreationProcess {
	
	private String _title;
	private Controller _controller;
	private MediaView _videoWindow;
	
	public CreationPlay(String title, Controller controller, MediaView videoWindow) {
		_title = title;
		_controller = controller;
		_videoWindow = videoWindow;
	}

	@Override
	public void begin() throws MathsAidException {
		String uri;
		try {
			uri = new File(getHostFolder() + 
					System.getProperty("file.separator") + "creations" + 
					System.getProperty("file.separator") + _title + ".mp4").toURI().toURL().toExternalForm();
			
			Media video = new Media(uri);
			
			MediaPlayer player = new MediaPlayer(video);
			
			_videoWindow.setMediaPlayer(player);
			_videoWindow.getMediaPlayer().seek(Duration.ZERO);
			_videoWindow.getMediaPlayer().play();
		} catch (MalformedURLException e) {
		}
	}
}