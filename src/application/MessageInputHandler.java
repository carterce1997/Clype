package application;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class MessageInputHandler implements EventHandler<MouseEvent> {
	TextArea userInputBox;

	public MessageInputHandler(TextArea userInputBox) {
		this.userInputBox = userInputBox;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			if (userInputBox.getText().equals("Enter your message here...")) {
				userInputBox.clear();
			}
		}
	}

}
