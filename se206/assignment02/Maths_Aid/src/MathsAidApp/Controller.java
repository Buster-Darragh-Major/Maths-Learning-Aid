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

public class Controller implements Initializable {

    public Button _createButton;
    public TextField _createField;
    public ListView<String> _creationList;

    // Handles logic for user pressing Create button
    public void handleCreateClick() {
    	CreationCreate createCreation = new CreationCreate(_createField.getText());
    	
    	try {
    		createCreation.start();
    	} catch (MathsAidException e) {
    	}
    	
    	_createField.clear();
    }
    
    // Handles logic for user pressing Delete button
    public void handleDeleteClick() throws InvocationTargetException {
    	CreationDelete deleteCreation = new CreationDelete(_creationList.getSelectionModel().getSelectedItem());
		
		try {
			deleteCreation.start();
		} catch (MathsAidException e) {
		}
    }
    
    public void handlePlayClick() {
    	System.out.println("Play functionality to go here here");
    }

    // Initialize the list with creation content found in creations folder.
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO addition of updating mechanism for playing/deleting
		// TODO extract name of creations removing file extensions
		
		File f = new File(System.getProperty("user.dir") + 
				System.getProperty("file.separator") + "creations");
		
		ArrayList<String> creations = new ArrayList<String>(Arrays.asList(f.list()));
		
		ObservableList<String> listContent = FXCollections.observableArrayList(creations);
		_creationList.setItems(listContent);
	}
}