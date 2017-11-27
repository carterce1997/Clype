package application;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import main.ClypeClient;

public class LoginButtonHandler implements EventHandler<MouseEvent> {

	private ClypeClient client;
	private TextField usernameInput;
	private TextField ipInput;
	private TextField portInput;
	
	public LoginButtonHandler(ClypeClient client, TextField usernameInput2, TextField ipInput2, TextField portInput2) {
		this.client = client;
		this.usernameInput = usernameInput2;
		this.ipInput = ipInput2;
		this.portInput = portInput2;
	}
	
	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
			String username = usernameInput.getText();
			String ip = ipInput.getText();
			int port = Integer.parseInt( portInput.getText() );
			
			client = new ClypeClient(username, ip, port);
		}	
	}

}
