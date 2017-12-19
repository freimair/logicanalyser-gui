package org.itdevas.logicanalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Hello World!");

		VBox root = new VBox();

		HBox sequence[] = { new HBox(), new HBox(), new HBox(), new HBox(), new HBox(), new HBox(), new HBox(),
				new HBox() };

		List<Character> data = getData();

		long start = System.currentTimeMillis();

		for (Character current : data) {
			for (int i = 0; i < sequence.length; i++) {
				Label tmp = new Label();
				tmp.setText(0 == (current.charValue() & (1 << i)) ? "0" : "1");
				sequence[i].getChildren().add(tmp);
			}
		}

		root.getChildren().addAll(sequence);
		System.out.println("done setting things up " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		primaryStage.setScene(new Scene(root, 1000, 500));
		primaryStage.show();
		System.out.println("done rendering " + (System.currentTimeMillis() - start));

	}

	private List<Character> getData() {
		List<Character> result = new ArrayList<>();

		for (int i = 0; i < 100; i++)
			for (int j = 0; j < new Random().nextInt(100); j++)
				result.add(new Character((char) i));

		return result;
	}
}