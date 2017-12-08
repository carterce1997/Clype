package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import data.ClypeData;
import javafx.fxml.Initializable;

public class Messages {

	private ArrayList<ClypeData> messages;

	public void addMessage( ClypeData message ) {
		this.messages.add( message );
	}
	
	public ArrayList<ClypeData> getMessages() {
		return this.messages;
	}
}
