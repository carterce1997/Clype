package GUI;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);

			HBox titleHBox = new HBox();
			Label titleLabel = new Label("Clype 2.0");
			titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));
			titleHBox.getChildren().addAll(titleLabel);
			root.setTop(titleHBox);

			HBox convoBox = new HBox();
			root.setCenter(convoBox);
			Label messageWindowLabel = new Label("Message Window");
			convoBox.getChildren().add(messageWindowLabel);
			
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
