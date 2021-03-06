package org.itdevas.logicanalyser;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
	private MenuButton add;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Hello World!");

		double width = 1;
		double height = (totalheight - 8 * 3 - 30) / 9;

		root = new ScrollPane();

		VBox vbox = new VBox(3);

		Slider triggerPosition = new Slider(0, totalwidth, totalwidth / 2);

		triggerPosition.setMaxWidth(Control.USE_PREF_SIZE);
		triggerPosition.setMinWidth(Control.USE_PREF_SIZE);
		triggerPosition.setPrefHeight(height);
		vbox.getChildren().add(triggerPosition);

		vbox.setMaxWidth(Control.USE_PREF_SIZE);
		vbox.setMinWidth(Control.USE_PREF_SIZE);

		vbox.prefWidthProperty().bind(triggerPosition.widthProperty());

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
				double x = event.getX();
				double z = x - root.getHvalue() * (width - viewportwidth); // workaround. minX does is mostly not
				double newx = event.getX() * newscale / scale.getX();
				// correct
				scale.setX(newscale);
				root.setHvalue(root.getHmax() * (newx - z)
						/ (root.getContent().getBoundsInLocal().getWidth() - root.getViewportBounds().getWidth()));

				triggerPosition.setPrefWidth(totalwidth * scale.getX());
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
		// root.heightProperty().addListener(new ChangeListener<Number>() {
		// @Override
		// public void changed(ObservableValue<? extends Number> observableValue, Number
		// oldHeight, Number newHeight) {
		// scale.setY(newHeight.doubleValue() / totalheight);
		// }
		// });

		Canvas sequence[] = { new Canvas(totalwidth, height), new Canvas(totalwidth, height),
				new Canvas(totalwidth, height), new Canvas(totalwidth, height), new Canvas(totalwidth, height),
				new Canvas(totalwidth, height), new Canvas(totalwidth, height), new Canvas(totalwidth, height) };

		List<Character> data = getData();

		long start = System.currentTimeMillis();

		// setup scale object
		scale = new Scale(1000 / totalwidth, 1);
		triggerPosition.setPrefWidth(totalwidth * scale.getX());

		for (int i = 0; i < sequence.length; i++) {
			GraphicsContext gc = sequence[i].getGraphicsContext2D();
			gc.strokeLine(0, height, totalwidth, height);

			for (int current = 0; current < data.size() - 1; current++) {

				gc.setLineCap(StrokeLineCap.BUTT);
				gc.setLineWidth(5);

				if (0 == (data.get(current).charValue() & (1 << i)))
					gc.strokeLine(current * width, height, current * width + width, height);
				else
					gc.strokeLine(current * width, 0, current * width + width, 0);

				if ((data.get(current).charValue() & (1 << i)) != (data.get(current + 1).charValue() & (1 << i)))
					gc.strokeLine((current + 1) * width, 0, (current + 1) * width, height);
			}
			sequence[i].getTransforms().add(scale);
		}
		vbox.getChildren().addAll(sequence);

		System.out.println("done setting things up " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();

		root.setContent(new Group(vbox));
		root.setFitToHeight(true);
		root.setPannable(true);
		root.setVbarPolicy(ScrollBarPolicy.NEVER);
		root.setHbarPolicy(ScrollBarPolicy.ALWAYS);

		VBox traceControl = new VBox(3);
		traceControl.setMaxWidth(Control.USE_PREF_SIZE);
		traceControl.setMinWidth(Control.USE_PREF_SIZE);
		HBox controls[] = { new HBox(3), new HBox(3), new HBox(3), new HBox(3), new HBox(3), new HBox(3), new HBox(3),
				new HBox(3), new HBox(3) };

		VBox.setVgrow(controls[0], Priority.ALWAYS);
		controls[0].setAlignment(Pos.CENTER_RIGHT);
		Label triggerlabel = new Label("Trigger condition:");
		TextField triggercondition = new TextField("0bx");
		ToggleGroup triggerMode = new ToggleGroup();
		ToggleButton triggerModeSingleShot = new ToggleButton("single");
		triggerModeSingleShot.setToggleGroup(triggerMode);
		ToggleButton triggerModeNormal = new ToggleButton("normal");
		triggerModeNormal.setToggleGroup(triggerMode);

		controls[0].getChildren().add(triggerlabel);
		controls[0].getChildren().add(triggercondition);
		controls[0].getChildren().add(triggerModeSingleShot);
		controls[0].getChildren().add(triggerModeNormal);

		for (int i = 1; i < sequence.length + 1; i++) {
			VBox.setVgrow(controls[i], Priority.ALWAYS);
			controls[i].setAlignment(Pos.CENTER_RIGHT);
			Button moveUp = new Button("up");
			moveUp.setOnAction(new EventHandlerWithI<ActionEvent>(i) {

				@Override
				public void handle(ActionEvent event) {
					int position = traceControl.getChildren().indexOf(controls[i]);
					if (2 > position)
						return;
					traceControl.getChildren().remove(position);
					traceControl.getChildren().add(position - 1, controls[i]);

					Node trace = vbox.getChildren().remove(position);
					vbox.getChildren().add(position - 1, trace);
				}
			});
			Button moveDown = new Button("down");
			moveDown.setOnAction(new EventHandlerWithI<ActionEvent>(i) {

				@Override
				public void handle(ActionEvent event) {
					int position = traceControl.getChildren().indexOf(controls[i]);
					if (traceControl.getChildren().size() - 1 <= position)
						return;
					traceControl.getChildren().remove(position);
					traceControl.getChildren().add(position + 1, controls[i]);

					Node trace = vbox.getChildren().remove(position);
					vbox.getChildren().add(position + 1, trace);
				}
			});
			Button hide = new Button("hide");
			hide.setOnAction(new EventHandlerWithI<ActionEvent>(i) {

				@Override
				public void handle(ActionEvent event) {
					int position = traceControl.getChildren().indexOf(controls[i]);
					traceControl.getChildren().remove(position);
					Node trace = vbox.getChildren().remove(position);

					MenuItem justHidden = new MenuItem(String.format("Channel %2d", i - 1));
					justHidden.setUserData(new Object[] { position, controls[i], trace });
					justHidden.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							Object[] userdata = (Object[]) ((MenuItem) event.getSource()).getUserData();
							traceControl.getChildren().add((int) userdata[0], (Node) userdata[1]);
							vbox.getChildren().add((int) userdata[0], (Node) userdata[2]);
							add.getItems().remove(event.getSource());
						}
					});
					add.getItems().add(justHidden);
				}
			});
			TextField label = new TextField("Ch" + i);
			Label channel = new Label(String.format("Channel %2d:", i - 1));
			controls[i].getChildren().add(channel);
			controls[i].getChildren().add(label);
			controls[i].getChildren().add(moveUp);
			controls[i].getChildren().add(moveDown);
			controls[i].getChildren().add(hide);

		}
		traceControl.getChildren().addAll(controls);
		Region spacer = new Region();
		spacer.setPrefHeight(28);
		traceControl.getChildren().add(spacer);

		add = new MenuButton("add");
		add.setMinWidth(Control.USE_PREF_SIZE);
		MenuItem hiddenTraces = new MenuItem("hidden traces:");
		hiddenTraces.setDisable(true);
		add.getItems().add(hiddenTraces);

		HBox columns = new HBox(3);
		columns.getChildren().add(traceControl); // add trace controls
		columns.getChildren().add(root); // add traces
		columns.getChildren().add(add);

		primaryStage.setScene(new Scene(columns, 1400, totalheight));
		primaryStage.show();
		System.out.println("done rendering " + (System.currentTimeMillis() - start));

	}

	abstract class EventHandlerWithI<T extends Event> implements EventHandler<T> {
		protected int i;

		public EventHandlerWithI(int i) {
			this.i = i;
		}
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