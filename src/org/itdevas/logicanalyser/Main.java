package org.itdevas.logicanalyser;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	private ScrollPane root;
	double totalwidth = Math.pow(2, 13);
	double totalheight = 500;
	private Scale scale;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Hello World!");

		root = new ScrollPane();

		VBox vbox = new VBox(3);
		vbox.setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				if (0 == event.getDeltaY())
					return;

				double newscale = scale.getX() * (1 + Math.signum(event.getDeltaY()) / 50);
				if (root.getWidth() >= newscale * totalwidth)
					newscale = root.getWidth() / totalwidth; // display all we have

				double width = root.getContent().getBoundsInLocal().getWidth();
				double viewportwidth = root.getViewportBounds().getWidth();
				double x = event.getX() * scale.getX();
				double z = x - root.getHvalue() * (width - viewportwidth); // workaround. minX does is mostly not
				double newx = event.getX() * newscale;
				// correct
				scale.setX(newscale);
				root.setHvalue(root.getHmax() * (newx - z)
						/ (root.getContent().getBoundsInLocal().getWidth() - root.getViewportBounds().getWidth()));
				event.consume();
			}
		});
		root.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
				if (0 != oldWidth.doubleValue())
					scale.setX(scale.getX() * newWidth.doubleValue() / oldWidth.doubleValue());
			}
		});
		root.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldHeight, Number newHeight) {
				scale.setY(newHeight.doubleValue() / totalheight);
			}
		});

		double width = 1;
		double height = (totalheight - 7 * 3 - 30) / 8;

		Canvas sequence[] = { new Canvas(totalwidth, height), new Canvas(totalwidth, height),
				new Canvas(totalwidth, height), new Canvas(totalwidth, height), new Canvas(totalwidth, height),
				new Canvas(totalwidth, height), new Canvas(totalwidth, height), new Canvas(totalwidth, height) };

		List<Character> data = getData();

		long start = System.currentTimeMillis();

		for (int i = 0; i < sequence.length; i++) {
			GraphicsContext gc = sequence[i].getGraphicsContext2D();
			gc.strokeLine(0, height, totalwidth, height);

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
		vbox.getChildren().addAll(sequence);

		// setup scale object
		scale = new Scale(1000 / totalwidth, 1);
		vbox.getTransforms().add(scale);

		System.out.println("done setting things up " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();

		root.setContent(new Group(vbox));
		root.setFitToHeight(true);
		root.setPannable(true);
		root.setVbarPolicy(ScrollBarPolicy.NEVER);
		root.setHbarPolicy(ScrollBarPolicy.ALWAYS);

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