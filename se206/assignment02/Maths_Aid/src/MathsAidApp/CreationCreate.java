package MathsAidApp;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.Duration;
import javafx.scene.control.ButtonType;

public class CreationCreate extends CreationProcess {
	
	private String _title;
	private Controller _controller;
	
	// Constructor
	public CreationCreate(String title, Controller controller) {
		super();
		_title = title;
		_controller = controller;
	}
	
	// TODO add threading capability for cancelling process.
	
	/**
	 * Creates a video file based on input title name, includes 3 second recording as
	 * and combining. Ensures title is not null or does not already exist.
	 * @throws MathsAidException
	 */
	@Override
	public void begin() throws MathsAidException {
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
		
		_controller.updateList();
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
			recordingAlert();
		}
	}
	
	private void recordingAlert() {
		Alert recordingPopup = new Alert(AlertType.INFORMATION);
		recordingPopup.setTitle("Recording...");
		recordingPopup.setHeaderText(null);
		recordingPopup.setContentText("Recording!");
		
		ButtonType buttonTypeCancel = new ButtonType("Cancel");
		
		recordingPopup.getButtonTypes().setAll(buttonTypeCancel);
		
		PauseTransition delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(e -> recordingPopup.hide());
		delay.play();
		
		Optional<ButtonType> result = recordingPopup.showAndWait();
		try {
			if (result.get() == buttonTypeCancel) {
				// TODO end process
			}
		} catch (NoSuchElementException e) {
		}
	}
	
	private void createVideo() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String command = // Record audio
						"ffmpeg -f alsa -i hw:0 -t 3 -y \"" + _title + ".wav\" &> /dev/null;" +
								
						// Make Video
						"ffmpeg -f lavfi -i color=c=blue:s=320x240:d=3.0 -vf " + 
						"\"drawtext=fontfile=/path/to/font.ttf:fontsize=30:" + 
						" fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _title + "'\" \\\n" + 
						"\"" + _title + "\".mp4;" +
						
						// Combine files
						"ffmpeg -y -i \"" + _title + ".wav\" -i \"" + _title + 
						".mp4\" -vcodec copy -strict -2 \"" + _title + ".mp4\";" +
						
						// Delete excess
						"rm -f \"" + _title + ".wav\"";
				System.out.println(command);
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
				
				builder.directory(new File(getHostFolder() + 
						System.getProperty("file.separator") + "creations"));
				
				try {
					@SuppressWarnings("unused")
					Process process = builder.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}
}
