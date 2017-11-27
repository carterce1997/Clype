package application;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

import data.ClypeData;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.ClypeClient;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Main extends Application {
	
	private int numLinesConvo = 10;
	private int numLinesUsers = numLinesConvo;
	private ClypeClient client;
	
	public static final int DEFAULT_PORT = 7000;
	
	public void showLoginWindow(Stage primaryStage) {
		
		try {
			/*
			 * create root
			 */
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 300, 200);
			scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
			
			/*
			 * title
			 */
			
			// label
			Label titleLabel = new Label(" Clype 2.0 Login");
			titleLabel.setId("login-title");

			// add to root
			VBox titleVBox = new VBox();
			titleVBox.getChildren().addAll(titleLabel);
			root.setTop(titleVBox);
			

			/*
			 * credentials input
			 */
			
			// inputs
			TextField usernameInput = new TextField();
			TextField hostnameInput = new TextField();
			hostnameInput.setText(InetAddress.getLocalHost().getHostAddress());
			TextField portInput = new TextField();
			portInput.setText( Integer.toString(DEFAULT_PORT) );
			
			// add to root
			VBox credentialsVBox = new VBox();
			credentialsVBox.getChildren().addAll(usernameInput, hostnameInput, portInput);
			root.setCenter(credentialsVBox);
			
			/*
			 * credentials labels
			 */
			
			// labels
			Label usernameLabel = new Label("Username:");
			Label hostnameLabel = new Label("Hostname:"); // this will default to the client computer's IP in the future
			Label portLabel = new Label("Port:"); 
			
			VBox credentialsLabels = new VBox();
			credentialsLabels.getChildren().addAll(usernameLabel, hostnameLabel, portLabel);
			
			// add to root
			root.setLeft(credentialsLabels);
			
			/*
			 * login controls
			 */
			
			// button
			Button login = new Button("Log in");				
			
			// add to root
			root.setBottom(login);
			
			// login handler
			login.setOnMouseReleased(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					try {
						if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
							String username = usernameInput.getText();
							String hostname = hostnameInput.getText();
							int port = Integer.parseInt( portInput.getText() );
																			
							client = new ClypeClient(username, hostname, port);
							
							if (client.connectionOpen()) { // this is useless
								showMainWindow(primaryStage);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});	
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showMainWindow(Stage primaryStage) {
		try {

			/*
			 * create root
			 */
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 900, 800);
			scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
			
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

			// list of incoming messages
			TextArea convoOutput = new TextArea(); // messages from users box
			convoOutput.setPrefRowCount(this.numLinesConvo);
			convoOutput.clear();
			convoOutput.setWrapText(true);
			convoOutput.setEditable(false);
			convoOutput.setMinHeight(200);
			convoOutput.setText("Messages from other users will appear here!");
			convoOutput.setFont(Font.font("Arial", FontWeight.LIGHT, 20));

			Task<Void> incomingMessageTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					boolean noMessages = true;// used to handle default text
					boolean closedSocket = false;
					while (client.connectionOpen() && !closedSocket) {
						closedSocket = client.recieveData();
						ClypeData messageFromServer = client.getData();
						String username = messageFromServer.getUserName();
						String message = messageFromServer.getData();
						
						if (!closedSocket) {
							if (noMessages) {
								convoOutput.clear();
								noMessages = false;
								convoOutput.setText(username + ": " + message);
							} else {
								convoOutput.setText(convoOutput.getText() + System.getProperty("line.separator")
										+ username + ": " + message);
							}
						}
					}
					return null;
				}
			};
			Thread recieveMessageThread = new Thread(incomingMessageTask);
			recieveMessageThread.start();

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
			messageInput.setFont(Font.font("Arial", FontWeight.LIGHT, 18));
			MessageInputHandler messageHandler = new MessageInputHandler(messageInput);
			messageInput.setOnMouseClicked(messageHandler);

			// button to send message
			Button sendMessageButton = new Button("Send Message");
			sendMessageButton.setMinSize(63, 150);
			sendMessageButton.setWrapText(true);
			sendMessageButton.setTextAlignment(TextAlignment.CENTER);
			sendMessageButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
			SendTextButtonHandler messageButtonHandler = new SendTextButtonHandler(client, messageInput);
			sendMessageButton.setOnMouseReleased(messageButtonHandler);

			// button to send media
			Button sendMediaButton = new Button("Send Media");
			sendMediaButton.setMinSize(55, 150);
			sendMediaButton.setWrapText(true);
			sendMediaButton.setTextAlignment(TextAlignment.CENTER);
			sendMediaButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
			SendMediaButtonHandler mediaButtonHandler = new SendMediaButtonHandler();
			sendMediaButton.setOnMouseReleased(mediaButtonHandler);

			// HBox to hold both buttons with
			HBox sendMessageButtons = new HBox();
			sendMessageButtons.setCenterShape(true);
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

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent arg0) {
					client.setCloseConnection();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		showLoginWindow(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
