package MathsAidApp;

public abstract class CreationProcess {
	
	protected String getHostFolder() {
		return System.getProperty("user.dir");
	}
	
	public abstract void begin() throws MathsAidException;
	
}
