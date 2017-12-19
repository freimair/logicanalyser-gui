package org.itdevas.logicanalyser;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Hello World!");

		VBox root = new VBox(3);

		double totalwidth = Math.pow(2, 13);
		double totalheight = 500;
		double width = 1;
		double height = totalheight / 8;

		Canvas sequence[] = { new Canvas(totalwidth, height), new Canvas(totalwidth, height),
				new Canvas(totalwidth, height), new Canvas(totalwidth, height), new Canvas(totalwidth, height),
				new Canvas(totalwidth, height), new Canvas(totalwidth, height), new Canvas(totalwidth, height) };

		List<Character> data = getData();

		long start = System.currentTimeMillis();

		for (int i = 0; i < sequence.length; i++) {
			GraphicsContext gc = sequence[i].getGraphicsContext2D();
			gc.strokeLine(0, height, totalwidth, height);

			Scale scale = new Scale();
			scale.setX(1000 / totalwidth);
			sequence[i].getTransforms().add(scale);

			for (int current = 0; current < data.size() - 1; current++) {

				gc.setLineCap(StrokeLineCap.BUTT);
				gc.setLineWidth(10);

				if (0 == (data.get(current).charValue() & (1 << i)))
					gc.strokeLine(current * width, height, current * width + width, height);
				else
					gc.strokeLine(current * width, 0, current * width + width, 0);

				if ((data.get(current).charValue() & (1 << i)) != (data.get(current + 1).charValue() & (1 << i)))
					gc.strokeLine((current + 1) * width, 0, (current + 1) * width, height);
			}
		}
		root.getChildren().addAll(sequence);

		System.out.println("done setting things up " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		primaryStage.setScene(new Scene(root, 1000, totalheight));
		primaryStage.show();
		System.out.println("done rendering " + (System.currentTimeMillis() - start));

	}

	private List<Character> getData() {
		List<Character> result = new ArrayList<>();

		for (int i = 0; i < 256; i++)
			for (int j = 0; j < 32; j++) {
				result.add(new Character((char) i));
			}

		return result;
	}
}