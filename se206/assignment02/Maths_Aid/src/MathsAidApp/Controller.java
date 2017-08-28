package MathsAidApp;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.MediaView;

public class Controller implements Initializable {

    public Button _createButton;
    public TextField _createField;
    public ListView<String> _creationList;
    public MediaView _embeddedVideo;

    // Handles logic for user pressing Create button
    public void handleCreateClick() {
    	CreationCreate createCreation = new CreationCreate(_createField.getText(), this);
    	
    	try {
    		createCreation.start();
    	} catch (MathsAidException e) {
    	}
    	
    	_createField.clear();
    }
    
    // Handles logic for user pressing Delete button
    public void handleDeleteClick() throws InvocationTargetException {
    	CreationDelete deleteCreation = new CreationDelete(_creationList.getSelectionModel().getSelectedItem(), this);
		
		try {
			deleteCreation.start();
		} catch (MathsAidException e) {
		}
    }
    
    public void handlePlayClick() {
    	CreationPlay playCreation = new CreationPlay(_creationList.getSelectionModel().getSelectedItem(), this);
    	
    	try {
    		playCreation.start();
    	} catch (MathsAidException e) {
    	}
    }

    // Initialize the list with creation content found in creations folder, remove non .mp4
    // files and remove extension.
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateList();
	}
	
	public void updateList() {
		File f = new File(System.getProperty("user.dir") + 
				System.getProperty("file.separator") + "creations");
		
		ArrayList<String> creations = new ArrayList<String>(Arrays.asList(f.list()));
		
		for (int i = creations.size() - 1; i >= 0; i--) {
			if (!creations.get(i).contains(".mp4")) {
				creations.remove(i);
			} else {
				creations.set(i, creations.get(i).replaceAll(".mp4", ""));
			}
		}
		
		ObservableList<String> listContent = FXCollections.observableArrayList(creations);
		_creationList.setItems(listContent);
	}
}