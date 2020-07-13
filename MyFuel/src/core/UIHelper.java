package core;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;

public class UIHelper {


	private static final ColorAdjust on = new ColorAdjust();
    private static final ColorAdjust off = new ColorAdjust();

	/**
	 * A standardized way to retrieve any text values of a given FXML file based on their fx:ids.
	 *
	 * @param fields A list of existing TextField/PasswordField in the current FXML file.
	 * @return String to String map of field names to their values.
	 */
	public static HashMap<String, String> getFields(TextField... fields) {
		HashMap<String, String> temp = new HashMap<String, String>();
		for (TextField f : fields) {
			temp.put(f.getId(), f.getText());
		}
		return temp;
	}

	/**
	 * Automatically fills an column-less table with an ArrayList of values of type E.
	 * @param <E> The generic type of the ArrayLis
	 * @param table The table which to be filled
	 * @param list The list that will fill the table
	 * @param ignored Column names to ignore (fields of the E type) when filling the table
	 * @return Returns an array of the columns that were effectively filled during the process
	 */
	public static <E> ArrayList<TableColumn<E, String>> FillTable(TableView<E> table, ArrayList<E> list, String... ignored) {
		if (list.isEmpty()) return null;
		Field[] fields = list.get(0).getClass().getFields();

		ObservableList<E> obsList = FXCollections.observableArrayList();
		obsList.addAll(list);

		ArrayList<TableColumn<E, String>> columns = new ArrayList<TableColumn<E, String>>();

		for (Field f : fields) {
			System.out.println("F:" + f.getName());
			TableColumn<E, String> tempColumn = new TableColumn<E, String>();
			tempColumn.setId(f.getName());
			tempColumn.setText(f.getName());
			tempColumn.setCellValueFactory(new PropertyValueFactory<E, String>(f.getName()));

			if (!Arrays.asList(ignored).contains(f.getName())) {
				columns.add(tempColumn);
				table.getColumns().add(tempColumn);
			}
		}

		table.setItems(obsList);
		return columns;
	}

	/**
	 * Toggles on and off a CSS class on an element.
	 * @param element The element to apply and remove the class from
	 * @param style The CSS class name which will be toggled
	 */
	public static void toggleClass(Node element, String style) {
		ObservableList<String> styles = element.getStyleClass();
		if (styles.contains(style)) styles.remove(style);
		else styles.add(style);
	}

	/**
	 * Toggles on and off a CSS class on the element, based on the desired state.
	 * @param element The element to apply or remove the class from
	 * @param style The CSS class name which will be toggled
	 * @param state Decides on the on/off state of the CSS class
	 */
	public static void toggleClass(Node element, String style, boolean state) {
		ObservableList<String> styles = element.getStyleClass();
		if (state) {
			if (styles.contains(style)) return;
			else styles.add(style);
		} else styles.remove(style);
	}

	/**
	 * Toggles off the CSS class of all <b>all</b> children of the element
	 * @param element The root parent element
	 * @param style The CSS class to be removed
	 */
	public static void toggleOff(Pane element, String style) {
		for (Node n : element.lookupAll("." + style)) toggleClass(n, style, false);
	}

	/**
	 * Adds a new child to an element
	 * @param parent The parent element to which the new child will be added
	 * @param child The child element to be added
	 */
	public static void addElement(Pane parent, Node child) {
		parent.getChildren().add(child);
	}

	/**
	 * Removes a child from a parent element
	 * @param parent The parent from which the child will be removed
	 * @param child The child element to be removed
	 */
	public static void removeElement(Pane parent, Node child) {
		parent.getChildren().remove(child);
	}

	/**
	 * Adds mouse-click events to all elements
	 * @param event The event to be added
	 * @param nodes The elements onto which the event will be added
	 */
	public static void addClickListeners(EventHandler<Event> event, Node... nodes) {
		for (Node n : nodes) n.setOnMouseClicked(event);
	}

	/**
	 * Adds mouse-on and mouse-off events to all elements
	 * @param on The mouse-on element
	 * @param off The mouse-off element
	 * @param nodes The elements onto which the events will be added
	 */
	public static void addHoverListeners(EventHandler<Event> on, EventHandler<Event> off, Node... nodes) {
		for (Node n : nodes) n.setOnMouseEntered(on);
		for (Node n : nodes) n.setOnMouseExited(off);
	}

	public static void removeStyleOnFocus(String style, Node... nodes) {
		for (Node n : nodes)
			n.focusedProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue) toggleClass(n, style, false);
			});
	}

	/**
	 * Helps to get rid of unwanted characters, leaving only numbers in the TextField.
	 * @param field The TextField onto which this listener is applied to.
	 * @param max   Limits the length of the TextField, unlimited if max = 0;
	 */
	public static void numbersOnly(TextField field, int max) {
		field.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (max != 0 && field.getText().length() > max) field.setText(field.getText().substring(0, max));
				if (!newValue.matches("\\d*")) field.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});
	}

	/**
	 * A generalized method to get rid of unwanted characters, leaving only numbers in the string as well as setting a maximum string length.
	 * @param text The string on which the regular expression operations apply.
	 * @param max  The maximum length of the string, <b>unlimited if set to 0.</b>
	 * @return Yields a string that contains only numbers.
	 */
	public static String numbersOnly(String text, int max) {
		if (max != 0 && text.length() > max) text = text.substring(0, max);
		if (!text.matches("\\d*")) text = text.replaceAll("[^\\d]", "");
		return text;
	}

	/**
	 * Rounds a floating number to a precision point
	 * @param value The number to be rounded
	 * @param precision The precision point of the rounding function
	 * @return Returns the rounded number
	 */
	public static float roundNumber(float value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (float) Math.round(value * scale) / scale;
	}

	/**
	 * Rounds a double number to a precision point
	 * @param value The number to be rounded
	 * @param precision The precision point of the rounding function
	 * @return Returns the rounded number
	 */
	public static double roundNumber(double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}

	/**
	 * Formats a credit-card number to have spaces every four characters
	 * @param cc The credit-card number to be spaced
	 * @return Returns the formatted credit-card number
	 */
	public static String formatCC(String cc) {
		return cc.replaceAll("(.{4})", "$1 ");
	}

	/**
	 * Disables previous dates on a given DatePicker element
	 * @param dp The DatePicker to disable previous days on
	 */
	public static void disablePastDays(DatePicker dp) {
		dp.setDayCellFactory(d -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();

				setDisable(empty || date.compareTo(today) < 0);
			}
		});
	}

	/**
	 * Deletes any characters other than letters from a TextField.
	 * @param field The TextField to apply the function to.
	 * @param max A length limit of the TextField.
	 */
	public static void lettersOnly(TextField field, int max) {
		field.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (max != 0 && field.getText().length() > max) field.setText(field.getText().substring(0, max));
				if (!newValue.matches("\\sa-zA-Z*")) field.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
			}
		});

	}
}