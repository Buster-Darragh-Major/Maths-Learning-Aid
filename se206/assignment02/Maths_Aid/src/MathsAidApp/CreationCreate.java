package MathsAidApp;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

public class CreationCreate extends CreationProcess {
	
	private String _title;
	
	// Constructor
	public CreationCreate(String title) {
		super();
		_title = title;
	}
	
	/**
	 * Creates a video file based on input title name, includes 3 second recording as
	 * and combining. Ensures title is not null or does not already exist.
	 * @throws MathsAidException
	 */
	@Override
	public void start() throws MathsAidException {
		File tmpDir = new File(getHostFolder() + 
				System.getProperty("file.separator") + "creations" + 
				System.getProperty("file.separator") + _title + ".mp4");
		if (tmpDir.exists()) {
			invalidPopup();
			throw new MathsAidException();
		}
		
		if (_title.equals("")) {
			throw new MathsAidException();
		}
		
		makeCreationsDirectory();
		
		popup();
	}
	
	// Creates a creations directory in the location of running if there isn't one
	// already.
	private void makeCreationsDirectory() {
		File f = new File(getHostFolder() + 
				System.getProperty("file.separator") + "creations");
		
		if (!f.exists()) {
			f.mkdir();
		}
	}
	
	private void invalidPopup() {
		Alert errorPopup = new Alert(AlertType.INFORMATION);
		errorPopup.setTitle("Cannot Create Creation");
		errorPopup.setHeaderText(null);
		errorPopup.setContentText("Error: A creation already exists with the name \"" + _title + "\"");
		
		errorPopup.showAndWait();
	}
	
	private void popup() {
		Alert audioPopup = new Alert(AlertType.CONFIRMATION);
		audioPopup.setTitle("Record Audio");
		audioPopup.setHeaderText(null);
		audioPopup.setContentText("You are now about to record your audio, \n you will have 3 seconds to record.");
		
		ButtonType buttonTypeRecord = new ButtonType("Record");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		audioPopup.getButtonTypes().setAll(buttonTypeRecord, buttonTypeCancel);
		
		Optional<ButtonType> result = audioPopup.showAndWait();
		
		if (result.get() == buttonTypeRecord) {
			createVideo();
		}
	}
	
	private void createVideo() {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
				// Record audio
				"ffmpeg -f alsa -i hw:0 -t 3 -y \"" + _title + ".wav\" &> /dev/null;" +
						
				// Make Video
				"ffmpeg -f lavfi -i color=c=blue:s=320x240:d=3.0 -vf " + 
				"\"drawtext=fontfile=/path/to/font.ttf:fontsize=30:" + 
				" fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _title + "'\" \\\n" + 
				"\"" + _title + "\".mp4;" +
				
				// Combine files
				"ffmpeg -y -i \"" + _title + ".wav\" -i \"" + _title + 
				".mp4\" -vcodec copy \"" + _title + ".mp4\";" +
				
				// Delete excess
				"rm -f \"" + _title + ".wav\"");
		
		builder.directory(new File(getHostFolder() + 
				System.getProperty("file.separator") + "creations"));
		
		try {
			@SuppressWarnings("unused")
			Process process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
