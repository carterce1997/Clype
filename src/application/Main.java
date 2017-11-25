package application;

import java.util.LinkedList;
import java.util.Queue;

import data.ClypeData;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.ClypeClient;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class Main extends Application {
	
	private int numLinesConvo = 10;
	private int numLinesUsers = numLinesConvo;
	private Queue<ClypeData> convoBuffer = new LinkedList<>();
	private ClypeClient client;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			/*
			 * create root
			 */
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 800, 800);
			scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

			/*
			 * create ClypeClient
			 */
			client = new ClypeClient();
			

			/*
			 * title
			 */

			// label
			Label titleLabel = new Label(" Clype 2.0");
			titleLabel.setId("app-title");

			// add to root
			VBox titleVBox = new VBox();
			titleVBox.getChildren().addAll(titleLabel);
			root.setTop(titleVBox);

			/*
			 * Conversation display
			 */

			// label
			Label convoBoxLabel = new Label("Message Window");
			convoBoxLabel.setId("convo-box-label");

			// list of messages
			TextArea convoOutput = new TextArea(); // input box
			convoOutput.setPrefRowCount(this.numLinesConvo);
			convoOutput.setText("User1: Some text \nUser2: Some more text");
			convoOutput.setWrapText(true);
			convoOutput.setEditable(false);
			convoOutput.setMinHeight(200);

			// add convoBox to root
			VBox convoBox = new VBox();
			convoBox.getChildren().addAll(convoBoxLabel, convoOutput);
			VBox.setVgrow(convoOutput, Priority.ALWAYS);// allows conversation box to grow with window

			root.setCenter(convoBox);

			/*
			 * List users box
			 */

			// label
			Label usersBoxLabel = new Label("Users Window");
			usersBoxLabel.setId("users-box-label");

			// list of users
			TextArea usersList = new TextArea();
			usersList.setPrefRowCount(this.numLinesUsers);
			usersList.setText("User1 \nUser2");
			usersList.setWrapText(true);
			usersList.setMaxWidth(100);
			usersList.setEditable(false);
			usersList.setMinHeight(200);

			// add usersList box to root
			VBox usersBox = new VBox();
			usersBox.setAlignment(Pos.CENTER);
			usersBox.getChildren().addAll(usersBoxLabel, usersList);
			VBox.setVgrow(usersList, Priority.ALWAYS);// Allows the list to expand with the window
			usersBox.setScaleX(.92);
			root.setRight(usersBox);

			/*
			 * Box for sending messages
			 */

			// label
			Label sendMessageBoxLabel = new Label("  Send Message Window");
			sendMessageBoxLabel.setId("send-message-box-label");

			// VBox for spacing on left
			VBox leftSpacing = new VBox();
			leftSpacing.setMaxWidth(10);
			leftSpacing.setMinWidth(10);

			// VBox for spacing on right
			VBox rightSpacing = new VBox();
			rightSpacing.setMaxWidth(10);
			rightSpacing.setMinWidth(10);

			// user input box
			TextArea messageInput = new TextArea(); // input box
			messageInput.setPrefRowCount(4);
			messageInput.setText("Enter your message here...");
			messageInput.setWrapText(true);
			messageInput.setStyle("-fx-background-color: transparent");
			MessageInputHandler messageHandler = new MessageInputHandler(messageInput);
			messageInput.setOnMouseClicked(messageHandler);

			// button to send message
			Button sendMessageButton = new Button("Send Message");
			sendMessageButton.setMinSize(63, 100);
			sendMessageButton.setWrapText(true);
			sendMessageButton.setTextAlignment(TextAlignment.CENTER);
			SendTextButtonHandler messageButtonHandler = new SendTextButtonHandler(messageInput);
			sendMessageButton.setOnMouseReleased(messageButtonHandler);

			// button to send media
			Button sendMediaButton = new Button("Send Media");
			sendMediaButton.setMinSize(55, 100);
			sendMediaButton.setWrapText(true);
			sendMediaButton.setTextAlignment(TextAlignment.CENTER);
			SendMediaButtonHandler mediaButtonHandler = new SendMediaButtonHandler();
			sendMediaButton.setOnMouseReleased(mediaButtonHandler);

			// HBox to hold both buttons with
			HBox sendMessageButtons = new HBox();
			sendMessageButtons.getChildren().addAll(sendMessageButton, sendMediaButton);

			// HBox for all sending message controls
			HBox sendMessageBoxControls = new HBox();
			HBox.setHgrow(messageInput, Priority.ALWAYS);
			HBox.setHgrow(sendMediaButton, Priority.ALWAYS);
			HBox.setHgrow(sendMessageButton, Priority.ALWAYS);
			sendMessageBoxControls.getChildren().addAll(leftSpacing, messageInput, sendMessageButtons, rightSpacing);

			/*
			 * set spacing between message receiving box and message sending box
			 */
			HBox topSpacer = new HBox();
			topSpacer.setMaxHeight(10);
			topSpacer.setMinHeight(10);
			HBox bottomSpacer = new HBox();
			bottomSpacer.setMaxHeight(10);
			bottomSpacer.setMinHeight(10);

			// add message controls to root
			VBox sendMessageBox = new VBox();
			sendMessageBox.getChildren().addAll(topSpacer, sendMessageBoxLabel, sendMessageBoxControls, bottomSpacer);
			root.setBottom(sendMessageBox);

			/*
			 * Spacer for left side of border pane
			 */
			VBox leftSpacer = new VBox();
			leftSpacer.setMaxWidth(10);
			leftSpacer.setMinWidth(10);
			root.setLeft(leftSpacer);

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
