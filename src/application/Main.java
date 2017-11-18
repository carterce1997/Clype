package application;

import java.util.LinkedList;
import java.util.Queue;

import data.ClypeData;
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
	
	private int numLinesConvo = 10;
	private int numLinesUsers = numLinesConvo;
	private Queue<ClypeData> convoBuffer = new LinkedList<>();

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			/*
			 * create root
			 */
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

			/*
			 * title 
			 */
			
			// label
			Label titleLabel = new Label("Clype 2.0");
			titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));

			// add to root
			VBox titleVBox = new VBox();
			titleVBox.getChildren().addAll(titleLabel);
			root.setTop(titleVBox);
			
			
			/*
			 * Conversation display
			 */
			
			// label
			Label convoBoxLabel = new Label("Message Window");
			
			// list of messages
			TextArea convoOutput = new TextArea(); // input box
			convoOutput.setPrefRowCount( this.numLinesConvo );
			convoOutput.setText("User1: Some text \nUser2: Some more text");
			convoOutput.setWrapText(true);
			convoOutput.setEditable(false);
			convoOutput.setMaxHeight(300);
			convoOutput.setMinHeight(300);

			// add to root
			VBox convoBox = new VBox();
			convoBox.getChildren().addAll(convoBoxLabel, convoOutput);
			root.setCenter(convoBox);
			
			
			/*
			 * List users box
			 */
			
			// label
			Label usersBoxLabel = new Label("Users Window");
			
			// list of users
			TextArea usersList = new TextArea(); 
			usersList.setPrefRowCount( this.numLinesUsers );
			usersList.setText("User1 \nUser2");
			usersList.setWrapText(true);
			usersList.setMaxWidth(100);
			usersList.setEditable(false);
			usersList.setMaxHeight(300);
			usersList.setMinHeight(300);

			
			// add to root
			VBox usersBox = new VBox();
			usersBox.getChildren().addAll(usersBoxLabel, usersList);
			root.setRight(usersBox);
			
			
			/*
			 * Send message
			 */
			
			// label
			Label sendMessageBoxLabel = new Label("Send Message Window"); 
			
			// input box
			TextArea messageInput = new TextArea(); // input box
			messageInput.setPrefRowCount(2);
			messageInput.setText("Some text");
			messageInput.setWrapText(true);
			messageInput.setMaxWidth(200);

			
			// buttons
			HBox sendMessageButtons = new HBox();
			
			Button sendMessageButton = new Button();
			sendMessageButton.setText("Send Message");
			SendTextButtonHandler messageButtonHandler = new SendTextButtonHandler();
			sendMessageButton.setOnMouseReleased(messageButtonHandler);
			
			Button sendMediaButton = new Button();
			sendMediaButton.setText("Send Media");
			SendTextButtonHandler mediaButtonHandler = new SendTextButtonHandler();
			sendMediaButton.setOnMouseReleased(mediaButtonHandler);
			
			sendMessageButtons.getChildren().addAll(sendMessageButton, sendMediaButton);
			 
			// all send message controls
			HBox sendMessageBoxControls = new HBox();
			sendMessageBoxControls.getChildren().addAll(messageInput, sendMessageButtons);
			
			// add controls to root
			VBox sendMessageBox = new VBox();
			sendMessageBox.getChildren().addAll(sendMessageBoxLabel, sendMessageBoxControls);
			root.setBottom(sendMessageBox);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getBufferString() {
		String outputString = "";
		for (ClypeData cd : this.convoBuffer) {
			outputString += "\n" + cd.getData();
		}
		
		return outputString;
	}

	public static void main(String[] args) {		
		
		launch(args);
	}
	
	
	
}
