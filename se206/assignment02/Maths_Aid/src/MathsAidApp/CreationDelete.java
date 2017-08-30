package MathsAidApp;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

public class CreationDelete extends CreationProcess {

	private String _deletable;
	private Controller _controller;
	
	// Constructor
	public CreationDelete(String deletable, Controller controller) {
		super();
		_deletable = deletable;
		_controller = controller;
	}
	
	/**
	 * Deletes a file based on input title name, prompts under if they are sure they want to
	 * delete, then with confirmation proceeds with deletion.
	 */
	@Override
	public void begin() throws MathsAidException {
		// Checks if name passed into function is null or empty, indicating no item is selected
		// in the list view in the GUI. Throws a MathsAidException.
		if ((_deletable == null) || (_deletable.equals(""))) {
			throw new MathsAidException();
		}
		
		// Prompt user with confirmation they are wanting to delete this creation.
		popup();
		
		// Update the GUI in the controller.
		_controller.updateList();
	}
	
	/**
	 * Prompt user with dialogue confirming they want to delete the specified creation file
	 */
	private void popup() {
		Alert deletePopup = new Alert(AlertType.CONFIRMATION);
		deletePopup.setTitle("Delete Creation");
		deletePopup.setHeaderText(null);
		deletePopup.setContentText("Are you sure you wish to delete the creation \"" + _deletable + "\"?");
		
		// Set buttons on dialogue
		ButtonType buttonTypeDelete = new ButtonType("Delete");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		deletePopup.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);
		
		Optional<ButtonType> result = deletePopup.showAndWait();
		
		// If user hits the button confirming they want to delete the creation, begin delete()
		// method.
		if (result.get() == buttonTypeDelete) {
			delete();
		}
	}

	/**
	 * Deletes a specified file in the creations directory corresponding to file name
	 * passed into constructor.
	 */
	private void delete() {
		// Create builder with bash command targeting file with the creation title 
		// to delete.
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
				"rm -f \"" + _deletable + ".mp4\"");
		
		// Switch working directory for cleanup to take place to creations folder in directory
		// application is run in.
		builder.directory(new File(getHostFolder() + 
				System.getProperty("file.separator") + "creations"));
		
		try {
			// Start Process
			@SuppressWarnings("unused")
			Process process = builder.start();
		} catch (IOException e) {
		}
	}
}
