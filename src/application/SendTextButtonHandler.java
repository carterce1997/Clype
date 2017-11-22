package application;

import main.ClypeClient;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class SendTextButtonHandler implements EventHandler<MouseEvent> {
	private ClypeClient client;
	private TextArea messageInput;

	public SendTextButtonHandler(/*ClypeClient client,*/ TextArea messageInput) {
		//this.client = client;
		this.messageInput = messageInput;

	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				client.readClientData();
				client.sendData();
				messageInput.clear();
				System.out.println("Hello World");

		}
	}

}
