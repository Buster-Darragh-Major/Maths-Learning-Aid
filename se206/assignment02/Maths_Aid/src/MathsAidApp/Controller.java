package MathsAidApp;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.MediaView;

public class Controller implements Initializable {

	@FXML
    public Button _createButton;
	@FXML
    public TextField _createField;
	@FXML
    public ListView<String> _creationList;
	@FXML
    public MediaView _embeddedVideo;
	
	//TODO: add a refresh button?

    /**
     * Creates a new CreationCreate object with input from the text field. If input is blank,
     * MathsIdException is thrown and caught, nothing occurs.
     */
    public void handleCreateClick() {
    	// Create a CreationCreate object with input from TextField.
    	CreationCreate createCreation = new CreationCreate(_createField.getText(), this);
    	
    	try {
    		// Call begin() in object, start creation process.
    		createCreation.begin();
    	} catch (MathsAidException e) {
    	}
    	
    	_createField.clear();
    }
    
    /**
     * Creates a CreationDelete object with input from the selected item in the list view
     * from the GUI. If nothing is selected, a MathsAidException is thrown, and nothing
     * occurs.
     * @throws InvocationTargetException
     */
    public void handleDeleteClick() throws InvocationTargetException {
    	// Create CreationDelete object with input from the list selection
    	CreationDelete deleteCreation = new CreationDelete(_creationList.getSelectionModel().getSelectedItem(), this);
		
		try {
			// Call begin() in object, start deletion process.
			deleteCreation.begin();
		} catch (MathsAidException e) {
		}
    }
    
    /**
     * Creates CreationPlay object with input for the selected item in the list view
     * from the GUI. If nothing is selected, a MathsAidException is thrown, and nothing
     * occurs.
     */
    public void handlePlayClick() {
    	// TODO add switching thumbnail function for clicking on different creations
    	// Create CreationPLay object with input from the list selection.
    	CreationPlay playCreation = new CreationPlay(_creationList.getSelectionModel().getSelectedItem(), _embeddedVideo);
    	
    	try {
    		playCreation.begin();
    		// Call begin() on object, start playing process.
    	} catch (MathsAidException e) {
    	}
    }

    /**
     * Initialize the ObservableList<String> of creations found in the creations directory.
     * This list is read and displayed by the list view in the GUI.
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateList();
	}
	
	public void updateList() {
		// TODO: figure out how to rest thread without freezing
		/*Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				return null;
			}
		};*/
		
		// Wait one second for creations directory contents to properly update, ensures 
		// list is not updated before directory changes have been made.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		/*Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();*/
		try {
			// Create file object modelling the creations directory
			File f = new File(System.getProperty("user.dir") + 
					System.getProperty("file.separator") + "creations");
			
			// Create ArrayList<String> of all file names found in folder.
			ArrayList<String> creations = new ArrayList<String>(Arrays.asList(f.list()));
			
			// Loop backwards through list, remove non- .mp4's and then remove .mp4 extension
			for (int i = creations.size() - 1; i >= 0; i--) {
				if (!creations.get(i).contains(".mp4")) {
					// If creation name does not have .mp4 extension, remove it from list
					creations.remove(i);
				} else {
					// If creation name does have .mp4 extension, remove it from string
					creations.set(i, creations.get(i).replaceAll(".mp4", ""));
				}
			}
			
			// Create ObservableList<String> from creations ArrayList<String> and set the 
			// GUI list to read from it.
			ObservableList<String> listContent = FXCollections.observableArrayList(creations);
			_creationList.setItems(listContent);
		} catch (Exception e) {
		}
	}
}