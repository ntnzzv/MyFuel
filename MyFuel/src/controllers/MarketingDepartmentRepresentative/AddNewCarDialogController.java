package controllers.MarketingDepartmentRepresentative;

import elements.CustomButton;
import entities.Car;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;
import static core.UIHelper.*;

public class AddNewCarDialogController implements Initializable
{
    @FXML private Pane addCarPane;
    @FXML  TextField carIdTxt;
    @FXML private ComboBox<String> gasTypeComboBox;
    private CustomButton combo;

    /**
     * initialises all needed information and elements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        numbersOnly(carIdTxt, 15);

        CustomButton save = new CustomButton("save");
        save.setPosition(160, 210);
        save.setSize(60,20);
        save.setOnMouseClicked(saveNewCar());
        addElement(addCarPane, save);

        toggleClass(carIdTxt,"textfield1");

       //type combo
        combo = new CustomButton("Type", 140, 165);
        combo.setSize(100, 30);
        combo.setCustomStyle("button2");
        addElement(addCarPane, combo);
        gasTypeComboBox.setFocusTraversable(false);
        combo.setOnMouseClicked(e -> { gasTypeComboBox.show(); toggleClass(combo, "emptyTextField", false); });
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
    public EventHandler<MouseEvent> saveNewCar()
    {
        return e ->
        {
            if(validate())
            try {
                String serialNumber = carIdTxt.getText();
                String gasType = gasTypeComboBox.getSelectionModel().getSelectedItem();
                Car newCar = new Car(serialNumber,gasType);
                AddNewCustomer_vehicle_Controller.carList.addCar(newCar);
                AddNewCustomer_vehicle_Controller.addCarDialog.hide();
                AddNewCustomer_vehicle_Controller.cars.add(newCar);
                AddNewCustomer_vehicle_Controller.noVehiclesCheck.setDisable(true);
            }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }


    /**
     * This method makes sure all the data is correct logically
     * @return boolean value which indicates if all tests passes
     */
    public boolean validate()
    {
        boolean fuelTypeSelected = gasTypeComboBox.getSelectionModel().getSelectedIndex() != -1;
        toggleClass(combo, "emptyTextField", !fuelTypeSelected);
        return fuelTypeSelected;
    }
}