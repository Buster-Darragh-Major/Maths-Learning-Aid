package MathsAidApp;

import java.util.Map;

import javafx.application.Application;

public abstract class CreationProcess {

	
	protected String _bash = "/bin/bash";
	
	protected String getHostFolder() {
		return System.getProperty("user.dir");
	}
	
}
