package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import javafx.util.Duration;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import data.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Circle;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import main.*;

public class Main extends Application {

	private static final int WidthLoginScreen = 300;
	private static final int HeightLoginScreen = 200;
	private static final String iconFilePath = "/resources/icon.jpg";
	
	private int numLinesConvo = 10;
	private int numLinesUsers = numLinesConvo;
	private ClypeClient client;
	private ArrayList<HBox> messages;
	
	
	public static final int DEFAULT_PORT = 7000;
	static int dx = 1;
	static int dy = 1;

	private void addBouncyBall(final Scene scene) {
		Circle ball = new Circle(50, 50, 20);
		ball.setFill(Color.TEAL);
		ball.setStroke(Color.NAVY);

		final BorderPane root = (BorderPane) scene.getRoot();
		root.getChildren().add(ball);

		Timeline tl = new Timeline();
		tl.setCycleCount(Animation.INDEFINITE);
		KeyFrame moveBall = new KeyFrame(Duration.seconds(.0100), new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {

				double xMin = ball.getBoundsInParent().getMinX();
				double yMin = ball.getBoundsInParent().getMinY();
				double xMax = ball.getBoundsInParent().getMaxX();
				double yMax = ball.getBoundsInParent().getMaxY();

				if (xMin < 0 || xMax > scene.getWidth()) {
					dx = dx * -1;
				}
				if (yMin < 0 || yMax > scene.getHeight()) {
					dy = dy * -1;
				}

				ball.setTranslateX(ball.getTranslateX() + dx);
				ball.setTranslateY(ball.getTranslateY() + dy);

			}
		});

		tl.getKeyFrames().add(moveBall);
		tl.play();
	}

	public void showLoginWindow(Stage primaryStage) {

		try {			
			
			primaryStage.getIcons().add(new Image(iconFilePath));
			primaryStage.setTitle("Login - Clype");

			/*
			 * create root
			 */
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, WidthLoginScreen, HeightLoginScreen);

			scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

			addBouncyBall(scene);

			/*
			 * title
			 */

			// title text
			Label titleLabel = new Label(" Clype 2.0 Login");
			titleLabel.setId("login-title");

			// add title to root
			VBox titleVBox = new VBox();
			titleVBox.getChildren().addAll(titleLabel);
			root.setTop(titleVBox);

			/*
			 * credentials input
			 */

			// credential input fields
			TextField usernameInput = new TextField();
			TextField hostnameInput = new TextField();
			hostnameInput.setText(InetAddress.getLocalHost().getHostAddress());
			TextField portInput = new TextField();
			portInput.setText(Integer.toString(DEFAULT_PORT));

			// add fields to root
			VBox credentialsVBox = new VBox();
			credentialsVBox.getChildren().addAll(usernameInput, hostnameInput, portInput);
			root.setCenter(credentialsVBox);

			/*
			 * credentials labels
			 */

			// credential labels
			Label usernameLabel = new Label(" Username:");
			Label hostnameLabel = new Label(" Host-name:"); // this will default to the client computer's IP in the
															// future
			Label portLabel = new Label(" Port:");

			VBox credentialsLabels = new VBox();
			credentialsLabels.getChildren().addAll(usernameLabel, hostnameLabel, portLabel);
			credentialsLabels.setSpacing(10);

			// add labels to root
			root.setLeft(credentialsLabels);

			// Error box
			HBox errorBox = new HBox();
			Label errorField = new Label("");
			errorBox.setMinHeight(30);
			errorField.setId("error-field");
			errorBox.getChildren().add(errorField);
			errorBox.setAlignment(Pos.CENTER);

			// login button
			Button login = new Button("Log in");
			login.setId("login-button");
			login.setMinWidth(300);

			HBox buttonBox = new HBox();
			buttonBox.getChildren().add(login);
			buttonBox.setAlignment(Pos.CENTER);
			HBox.setHgrow(buttonBox, Priority.ALWAYS);

			VBox bottomBox = new VBox();
			bottomBox.getChildren().addAll(errorBox, buttonBox);

			// add button to root
			root.setBottom(bottomBox);

			// login button handler: creates new client and shows main window
			login.setOnMouseReleased(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					try {
						if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
							String username = usernameInput.getText();
							String hostname = hostnameInput.getText();
							int port = Integer.parseInt(portInput.getText());

							if (!username.isEmpty() && !hostname.isEmpty()) {
								System.out.println("Attempting to connect to server.");
								client = new ClypeClient(username, hostname, port);

								if (client.connectionOpen()) { // allows us to check if connection was made
									showMainWindow(primaryStage);
								} else {
									errorField.setText("Could not connect to server.");
								}
							} else {
								errorField.setText("Error: Invalid username or host-name given");
							}
						}
					} catch (NumberFormatException nfe) {
						errorField.setText("Invalid port number given.");
					} catch (IllegalArgumentException iae) {
						errorField.setText(iae.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

			// show
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showMainWindow(Stage primaryStage) {
		try {

			primaryStage.getIcons().add(new Image("icon.jpg"));
			primaryStage.setTitle("Messenger - Clype");

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
			VBox convoOutput = new VBox();
			ScrollPane convoContainer = new ScrollPane(convoOutput);
			convoContainer.setVvalue(1.0); // 1.0 means 100% at the bottom

			// add convoBox to root
			VBox convoBox = new VBox();
			convoBox.getChildren().addAll(convoBoxLabel, convoContainer);
			VBox.setVgrow(convoContainer, Priority.ALWAYS);// allows conversation box to grow with window

			root.setCenter(convoBox);

			/*
			 * List users box
			 */

			// label
			Label usersBoxLabel = new Label("Users Window");
			usersBoxLabel.setId("users-box-label");

			// list of users
			TextArea usersList = new TextArea();
			usersList.setId("users-box-list");
			usersList.setPrefRowCount(this.numLinesUsers);
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
			 * Handles incoming messages
			 */
			Task<Void> incomingMessageTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					boolean closedSocket = false;
					while (client.connectionOpen() && !closedSocket) {
						closedSocket = client.recieveData();
						ClypeData messageFromServer = client.getData();

						if (messageFromServer.getType() == ClypeData.SEND_MESSAGE) {
							MessageClypeData messageDataFromServer = (MessageClypeData) messageFromServer;
							String username = messageDataFromServer.getUserName();
							String message = messageDataFromServer.getData();

							Label messageOutput = new Label(message);
							messageOutput.setFont(Font.font(18));

							if (username.equals(client.getUserName())) {
								Label usernameOutput = new Label("me:");
								usernameOutput.setFont(Font.font("Serif", FontPosture.ITALIC, 24));

								VBox messageOutputVBox = new VBox(usernameOutput, messageOutput);
								messageOutputVBox.setMinWidth(.9 * convoContainer.getWidth());

								messageOutputVBox.setAlignment(Pos.CENTER_RIGHT);

								Platform.runLater(() -> {
									convoOutput.getChildren().add(messageOutputVBox);
								});
							} else {
								Label usernameOutput = new Label(username + ":");
								usernameOutput.setFont(Font.font("Serif", FontPosture.ITALIC, 24));

								HBox spacer = new HBox();
								spacer.setMinWidth(.1 * convoContainer.getWidth());
								HBox messageOutputVBox = new HBox(spacer, new VBox(usernameOutput, messageOutput));
								messageOutputVBox.setMinWidth(.9 * convoContainer.getWidth());

								Platform.runLater(() -> {
									convoOutput.getChildren().add(messageOutputVBox);
								});
							}

						} else if (messageFromServer.getType() == ClypeData.LIST_USERS) {
							MessageClypeData users = (MessageClypeData) messageFromServer;
							usersList.setText(users.getData());
						} else if (messageFromServer.getType() == ClypeData.SEND_FILE) {
							FileClypeData fileMessageFromServer = (FileClypeData) messageFromServer;
							String username = fileMessageFromServer.getUserName();
							String message = fileMessageFromServer.getData();

							Label messageOutput = new Label(message);
							messageOutput.setFont(Font.font(18));

							if (username.equals(client.getUserName())) {
								Label usernameOutput = new Label("me:");
								usernameOutput.setFont(Font.font("Serif", FontPosture.ITALIC, 24));

								VBox messageOutputVBox = new VBox(usernameOutput, messageOutput);
								messageOutputVBox.setMinWidth(.9 * convoContainer.getWidth());

								messageOutputVBox.setAlignment(Pos.CENTER_RIGHT);

								Platform.runLater(() -> {
									convoOutput.getChildren().add(messageOutputVBox);
								});
							} else {
								Label usernameOutput = new Label(username + ":");
								usernameOutput.setFont(Font.font("Serif", FontPosture.ITALIC, 24));

								HBox spacer = new HBox();
								spacer.setMinWidth(.1 * convoContainer.getWidth());
								HBox messageOutputVBox = new HBox(spacer, new VBox(usernameOutput, messageOutput));
								messageOutputVBox.setMinWidth(.9 * convoContainer.getWidth());

								Platform.runLater(() -> {
									convoOutput.getChildren().add(messageOutputVBox);
								});
							}

						} else if (messageFromServer.getType() == ClypeData.SEND_PHOTO) {
							PhotoClypeData photoMessageFromServer = (PhotoClypeData) messageFromServer;
							String username = photoMessageFromServer.getUserName();
							BufferedImage message = photoMessageFromServer.getData();

							ImageView imageView = new ImageView();
							Image image = SwingFXUtils.toFXImage(message, null);
							imageView.setImage(image);
							imageView.setFitHeight(300);
							imageView.maxWidth(200.0);
							imageView.setPreserveRatio(true);

							if (username.equals(client.getUserName())) {
								Label usernameOutput = new Label("me:");
								usernameOutput.setFont(Font.font("Serif", FontPosture.ITALIC, 24));

								VBox messageOutputVBox = new VBox(usernameOutput, imageView);
								messageOutputVBox.setMinWidth(.9 * convoContainer.getWidth());

								messageOutputVBox.setAlignment(Pos.CENTER_RIGHT);

								Platform.runLater(() -> {
									convoOutput.getChildren().add(messageOutputVBox);
								});
							} else {
								Label usernameOutput = new Label(username + ":");
								usernameOutput.setFont(Font.font("Serif", FontPosture.ITALIC, 24));

								HBox spacer = new HBox();
								spacer.setMinWidth(.1 * convoContainer.getWidth());
								HBox messageOutputVBox = new HBox(spacer, new VBox(usernameOutput, imageView));
								messageOutputVBox.setMinWidth(.9 * convoContainer.getWidth());

								Platform.runLater(() -> {
									convoOutput.getChildren().add(messageOutputVBox);
								});
							}
						}
						convoContainer.vvalueProperty().bind(convoOutput.heightProperty()); // scroll down
					}
					return null;
				}
			};
			Thread recieveMessageThread = new Thread(incomingMessageTask);
			recieveMessageThread.start();

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
			messageInput.setWrapText(true);
			messageInput.setStyle("-fx-background-color: transparent");
			messageInput.setFont(Font.font("Arial", FontWeight.LIGHT, 18));
			messageInput.setEditable(true);
			messageInput.setPromptText("Type a message here!");

			// button to send message
			Button sendButton = new Button("Send");
			sendButton.setMinSize(63, 150);
			sendButton.setWrapText(true);
			sendButton.setTextAlignment(TextAlignment.CENTER);
			sendButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
			sendButton.setOnMouseReleased(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
						if (client.getDataToSendToServer() == null) {
							ClypeData textMessageData = new MessageClypeData(client.getUserName(),
									messageInput.getText(), ClypeData.SEND_MESSAGE);
							client.setDataToSendToServer(textMessageData);
						}
						client.sendData();

						messageInput.clear();
					}
				}

			});

			// button to send media
			Button addFileButton = new Button("Add File");
			addFileButton.setMinSize(55, 150);
			addFileButton.setWrapText(true);
			addFileButton.setTextAlignment(TextAlignment.CENTER);
			addFileButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));

			addFileButton.setOnMouseReleased(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
						FileChooser fileChooser = new FileChooser();
						fileChooser.setTitle("Select File");

						File file = fileChooser.showOpenDialog(primaryStage);
						FileClypeData fileData = new FileClypeData(client.getUserName(), file.getAbsolutePath(),
								ClypeData.SEND_FILE);

						try {
							fileData.readFileContents();
							client.setDataToSendToServer(fileData);
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
				}

			});

			Button addPhotoButton = new Button("Add photo");
			addPhotoButton.setMinSize(55, 150);
			addPhotoButton.setWrapText(true);
			addPhotoButton.setTextAlignment(TextAlignment.CENTER);
			addPhotoButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
			addPhotoButton.setOnMouseReleased(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Select File");

					File file = fileChooser.showOpenDialog(primaryStage);
					if (file != null) {
						PhotoClypeData photoData = new PhotoClypeData(client.getUserName(), file.getAbsolutePath(),
								ClypeData.SEND_PHOTO);

						try {
							client.setDataToSendToServer(photoData);
						} catch (Exception ioe) {
							ioe.printStackTrace();
						}
					}
				}
			});

			// HBox to hold both buttons with
			HBox sendMessageButtons = new HBox();
			sendMessageButtons.setCenterShape(true);
			sendMessageButtons.getChildren().addAll(sendButton, addFileButton, addPhotoButton);

			// HBox for all sending message controls
			HBox sendMessageBoxControls = new HBox();
			HBox.setHgrow(messageInput, Priority.ALWAYS);
			HBox.setHgrow(addFileButton, Priority.ALWAYS);
			HBox.setHgrow(addPhotoButton, Priority.ALWAYS);
			HBox.setHgrow(sendButton, Priority.ALWAYS);
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