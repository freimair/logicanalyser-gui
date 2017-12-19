package org.itdevas.logicanalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Hello World!");

		HBox root = new HBox();

		List<Character> data = getData();

		for (Character current : data) {
			Label tmp = new Label();
			tmp.setText(0 == (current.charValue() & 1) ? "0" : "1");

			root.getChildren().add(tmp);
		}

		primaryStage.setScene(new Scene(root, 1000, 500));
		primaryStage.show();
	}

	private List<Character> getData() {
		List<Character> result = new ArrayList<>();

		for (int i = 0; i < 100; i++)
			for (int j = 0; j < new Random().nextInt(100); j++)
				result.add(new Character((char) i));

		return result;
	}
}