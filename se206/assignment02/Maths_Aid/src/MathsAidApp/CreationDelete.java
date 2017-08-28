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
	
	public CreationDelete(String deletable, Controller controller) {
		_deletable = deletable;
		_controller = controller;
	}
	
	@Override
	public void start() throws MathsAidException {
		if (_deletable.equals("")) {
			throw new MathsAidException();
		}
		
		popup();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		
		_controller.updateList();
	}
	
	private void popup() {
		Alert deletePopup = new Alert(AlertType.CONFIRMATION);
		deletePopup.setTitle("Delete Creation");
		deletePopup.setHeaderText(null);
		deletePopup.setContentText("Are you sure you wish to delete the creation \"" + _deletable + "\"?");
		
		ButtonType buttonTypeDelete = new ButtonType("Delete");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		deletePopup.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);
		
		Optional<ButtonType> result = deletePopup.showAndWait();
		
		if (result.get() == buttonTypeDelete) {
			delete();
		}
	}

	private void delete() {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
				"rm -f \"" + _deletable + ".mp4\" &> debug.txt");
		
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
