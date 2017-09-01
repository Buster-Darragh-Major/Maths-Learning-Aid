package MathsAidApp;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private Process _process;
	
	// Constructor
	public CreationCreate(String title, Controller controller) {
		super();
		_title = title;
		_controller = controller;
	}
	
	
	/**
	 * Creates a video file based on input title name, includes 3 second recording as
	 * and combining. Ensures title is not null or does not already exist.
	 * @throws MathsAidException
	 */
	@Override
	public void begin() throws MathsAidException {
		// Check to see if file already exists under same name, if so, prompt with invalid name dialogue
		File tmpDir = new File(getHostFolder() + 
				System.getProperty("file.separator") + "creations" + 
				System.getProperty("file.separator") + _title + ".mp4");
		if (tmpDir.exists()) {
			invalidPopupAlreadyExists();
			_controller.updateList();
			throw new MathsAidException();
		}
		
		// Check for invalid characters, only valid characters are letters, numbers and spaces
		Pattern p = Pattern.compile("[^a-zA-Z\\d\\s:]");
		Matcher m = p.matcher(_title);
		if (m.find()) {
			invalidPopupInvalidCharacter();
			throw new MathsAidException();
		}
		
		// Ensure name is not empty string, if so, prompt with invalid name dialogue.
		if (_title.equals("")) {
			throw new MathsAidException();
		}
		
		// Make a creations directory
		makeCreationsDirectory();
		
		// Prompt user to hit record when ready
		popup();
		
		// Update the list view in the GUI
		_controller.updateList();
	}
	
	
	/**
	 * Creates a creations directory in the location of running if there isn't one
	 * already.
	 */
	private void makeCreationsDirectory() {
		File f = new File(getHostFolder() + 
				System.getProperty("file.separator") + "creations");
		
		if (!f.exists()) {
			f.mkdir();
		}
	}
	
	
	/**
	 * Prompt user with information that this creation already exists
	 * @throws MathsAidException 
	 */
	private void invalidPopupAlreadyExists() throws MathsAidException {
		Alert errorPopup = new Alert(AlertType.CONFIRMATION);
		errorPopup.setTitle("Cannot Create Creation");
		errorPopup.setHeaderText(null);
		errorPopup.setContentText("A creation already exists with the name \"" + _title + "\". Do you wish to overwrite?");
		
		ButtonType buttonTypeOverwrite = new ButtonType("Overwrite");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		errorPopup.getButtonTypes().setAll(buttonTypeOverwrite, buttonTypeCancel);
		
		Optional<ButtonType> result = errorPopup.showAndWait();
		
		if (result.get() == buttonTypeOverwrite) {
			popup();
		}
	}
	
	
	/**
	 * prompt user with information that only certain characters are allowed
	 */
	private void invalidPopupInvalidCharacter() {
		Alert errorPopup = new Alert(AlertType.CONFIRMATION);
		errorPopup.setTitle("Cannot Create Creation");
		errorPopup.setHeaderText(null);
		errorPopup.setContentText("Invalid character, only letters, numbers and spaces allowed.");
		
		ButtonType buttonTypeOK = new ButtonType("OK", ButtonData.CANCEL_CLOSE);
		errorPopup.getButtonTypes().setAll(buttonTypeOK);
		
		errorPopup.showAndWait();
	}
	
	
	/**
	 * Prompt user with information dialog on what their options are for creating a creation.
	 * They are able to press 'cancel', which throws a MathsAidException and is caught by the 
	 * controller, ending any creation process, or continue with the creation by pressing 
	 * 'record', where recordVideo() and recordingAlert() are then called.
	 * @throws MathsAidException
	 */
	private void popup() throws MathsAidException {
		Alert audioPopup = new Alert(AlertType.CONFIRMATION);
		audioPopup.setTitle("Record Audio");
		audioPopup.setHeaderText(null);
		audioPopup.setContentText("You are now about to record your audio, \n you will have 3 seconds to record.");
		
		// Set Buttons on dialogue
		ButtonType buttonTypeRecord = new ButtonType("Record");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		audioPopup.getButtonTypes().setAll(buttonTypeRecord, buttonTypeCancel);
		
		Optional<ButtonType> result = audioPopup.showAndWait();
		
		// If user hits record first create the .mp4 file, then prompt with recording popup.
		// Then prompt user with popup allowing to listen and re record audio.
		if (result.get() == buttonTypeRecord) {
			createVideo();
			recordingAlert();
			confirmationPopup();
		}
	}
	
	
	/**
	 * Prompts user with dialogue informing them that recording is taking place. They have
	 * the option to cancel, which will destroy the recording process, delete any files already
	 * made and finally throw a MathsAidException, caught by the controller and terminating
	 * the process.
	 * @throws MathsAidException
	 */
	private void recordingAlert() throws MathsAidException {
		Alert recordingPopup = new Alert(AlertType.INFORMATION);
		recordingPopup.setTitle("Recording...");
		recordingPopup.setHeaderText(null);
		recordingPopup.setContentText("Recording!");
		
		// Set only button to 'cancel'
		ButtonType buttonTypeCancel = new ButtonType("Cancel");
		recordingPopup.getButtonTypes().setAll(buttonTypeCancel);
		
		// Ensure popup is only displayed for 3 seconds before automatically closing
		// when recording is done.
		PauseTransition delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(e -> recordingPopup.hide());
		delay.play();
		
		Optional<ButtonType> result = recordingPopup.showAndWait();
		try {
			if (result.get() == buttonTypeCancel) {
				// If 'cancel' pressed, destroy the process, clean up the files and throw exception
				_process.destroy();
				deleteRemnants();
				throw new MathsAidException();
			}
		} catch (NoSuchElementException e) {
		}
	}
	
	
	/**
	 * Implements a ProcessBuilder that builds a process object, then executing a series 
	 * of bash commands that create a .mp4 file under the specifications of a process. 
	 * Will create the video file in the creations directory in which the application 
	 * is run from.
	 */
	private void createVideo() {
		// Create new thread for creation making to occur in, recording takes time so allow
		// GUI to be responsive and for cancellation to occur by assigning separate thread.
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String command = 
						// Record audio
						"ffmpeg -f alsa -i hw:0 -t 3 -y \"" + _title + ".wav\" &> /dev/null;" +
								
						// Make Video
						"ffmpeg -y -f lavfi -i color=c=blue:s=320x240:d=3.0 -vf " + 
						"\"drawtext=fontfile=/path/to/font.ttf:fontsize=30:" + 
						" fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _title + "'\" \\\n" + 
						"\"" + _title + "\".mp4;" +
						
						// Combine files
						"ffmpeg -y -i \"" + _title + ".wav\" -i \"" + _title + 
						".mp4\" -vcodec copy -strict -2 \"" + _title + ".mp4\";" +
						
						// Delete excess
						"rm -f \"" + _title + ".wav\"";
				// Build a builder with relevant bash command.
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
				
				// Change working directory of builder to creations folder found in directory
				// where application is run.
				builder.directory(new File(getHostFolder() + 
						System.getProperty("file.separator") + "creations"));
				
				try {
					// Start the process.
					_process = builder.start();
				} catch (IOException e) {
				}
				return null;
			}
		};
		
		// Start thread
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}
	
	
	/**
	 * Prompt user with popup allowing them to relisten, rerecord or finish their creation.
	 * @throws MathsAidException
	 */
	private void confirmationPopup() throws MathsAidException {
		Alert alertPopup = new Alert(AlertType.INFORMATION);
		alertPopup.setTitle("Creatio Confirmation");
		alertPopup.setHeaderText(null);
		alertPopup.setContentText("Would you like to listen to your audio before you create?");
		
		// Set buttons on popup
		ButtonType buttonTypeReRecord = new ButtonType("Rerecord");
		ButtonType buttonTypeListen = new ButtonType("Listen");
		ButtonType buttonTypeCreate = new ButtonType("Create!");
		alertPopup.getButtonTypes().setAll(buttonTypeReRecord, buttonTypeListen, buttonTypeCreate);
		
		Optional<ButtonType> result = alertPopup.showAndWait();
		// If user chooses to relisten create new thread for listening
		if (result.get() == buttonTypeListen) {
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					// Use ffplay on .mp4 with no display
					String command = "ffplay -nodisp \"" + _title + ".mp4\"";
					
					// Build a builder with the relevant bash command
					ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
					
					// Change working directory of builder to creations folder found in directory
					// where application is run.
					builder.directory(new File(getHostFolder() + 
							System.getProperty("file.separator") + "creations"));
					
					try {
						@SuppressWarnings("unused")
						Process process = builder.start();
					} catch (IOException e) {
					}
					return null;
				}
			};
			
			// Begin thread
			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();
			
			// After relistening has occurred prompt user with fresh confirmation window
			confirmationPopup();
		} else if (result.get() == buttonTypeReRecord) {
			// If user chooses to re record their creation, create a new creation from scratch
			createVideo();
			recordingAlert();
			confirmationPopup();
		} else if (result.get() == buttonTypeCreate) {
			// Continue
		}
	}
	
	
	/**
	 * Used when called by recordingAlert(), and cancel button has been pressed, i.e.
	 * premature termination of the process has occurred and leftover files from the 
	 * process need to be deleted.
	 */
	private void deleteRemnants() {
		// Create builder with bash command targeting all files with the creation title 
		// to delete.
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
				"rm -f \"" + _title + "\".*;");
		
		// Switch working directory for cleanup to take place to creations folder in directory
		// application is run in.
		builder.directory(new File(getHostFolder() + 
				System.getProperty("file.separator") + "creations"));
		
		try {
			@SuppressWarnings("unused")
			// Start process
			Process process = builder.start();
		} catch (IOException e) {
		}
	}
}
