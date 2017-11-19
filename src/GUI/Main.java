package GUI;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);

			// Title
			VBox titleVBox = new VBox();
			root.setTop(titleVBox);
			
			Label titleLabel = new Label("Clype 2.0");
			titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
			titleVBox.getChildren().addAll(titleLabel);

			// Conversation
			VBox convoBox = new VBox();
			root.setCenter(convoBox);
			
			Label convoBoxLabel = new Label("Message Window");
			
			TextArea convoOutput = new TextArea(); // input box
			convoOutput.setPrefRowCount(10);
			convoOutput.setText("User1: Some text \nUser2: Some more text");
			convoOutput.setWrapText(true);
			
			convoBox.getChildren().addAll(convoBoxLabel, convoOutput);
			
			// Users list
			VBox usersBox = new VBox();
			root.setRight(usersBox);
			
			Label usersBoxLabel = new Label("Users Window"); // VBox label
			
			TextArea usersList = new TextArea(); // input box
			usersList.setPrefRowCount(10);
			usersList.setText("User1 \nUser2");
			usersList.setWrapText(true);
			usersList.setMaxWidth(100);
			
			usersBox.getChildren().addAll(usersBoxLabel, usersList);
			
			// Send message
			VBox sendMessageBox = new VBox();
			root.setBottom(sendMessageBox);
			
			Label sendMessageBoxLabel = new Label("Send Message Window"); // VBox label
			
			HBox sendMessageBoxControls = new HBox();
			
			TextArea messageInput = new TextArea(); // input box
			messageInput.setPrefRowCount(2);
			messageInput.setText("Some text");
			messageInput.setWrapText(true);
			messageInput.setMaxWidth(200);
			
			
			Button sendMessageButton = new Button();// send message button
			sendMessageButton.setText("Send Message");
			
			Button sendMedia = new Button();// send media button
			sendMedia.setText("Send Media");
			
			sendMessageBoxControls.getChildren().addAll(messageInput, sendMessageButton, sendMedia);
			sendMessageBox.getChildren().addAll(sendMessageBoxLabel, sendMessageBoxControls);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
