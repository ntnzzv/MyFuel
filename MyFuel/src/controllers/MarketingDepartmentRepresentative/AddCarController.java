package controllers.MarketingDepartmentRepresentative;

import elements.CustomButton;
import entities.Car;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

import controllers.MarketingDepartmentRepresentative.EditCustomerController;

import static core.UIHelper.*;

public class AddCarController implements Initializable {
	@FXML Pane addCarPane;
	@FXML TextField carIdTxt;
	@FXML ComboBox<String> gasTypeComboBox;
	
	CustomButton combo;

	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		numbersOnly(carIdTxt, 9);

		CustomButton save = new CustomButton("save");
		save.setPosition(160, 210);
		save.setSize(60, 20);
		save.setOnMouseClicked(saveNewCar());
		addElement(addCarPane, save);

		toggleClass(carIdTxt, "textfield1");

		// type combo
		combo = new CustomButton("Type", 140, 165);
		combo.setSize(100, 30);
		combo.setCustomStyle("button2");
		addElement(addCarPane, combo);
		gasTypeComboBox.setFocusTraversable(false);
		combo.setOnMouseClicked(e -> {
			gasTypeComboBox.show();
			toggleClass(combo, "emptyTextField", false);
		});
		gasTypeComboBox.getItems().addAll("Petrol95", "Diesel", "Scooter");
		gasTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
			combo.setText(gasTypeComboBox.getSelectionModel().getSelectedItem());
		});
	}

	/**
	 * A method to save a new car in car list
	 *
	 * @return Invokes a new car saving and putting in a list
	 */
	private EventHandler<MouseEvent> saveNewCar() {
		return e -> {
			try
			{
				if(carIdTxt.getText().isEmpty() || gasTypeComboBox.getSelectionModel().isEmpty())
				{
					toggleClass(carIdTxt, "emptyTextField", true);
					toggleClass(combo, "emptyTextField", true);
				}
				else
				{
					Car newCar = new Car();
					newCar.carID = carIdTxt.getText();
					newCar.fuelType = gasTypeComboBox.getSelectionModel().getSelectedItem();
					
					EditCustomerController.cars.add(newCar);
					EditCustomerController.carList.addCar(newCar);
					EditCustomerController.addDialog.hide();
				}
			}
			catch (Exception ex) { ex.printStackTrace(); }
		};
	}
}
