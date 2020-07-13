package controllers.MarketingDepartmentRepresentative;

import client_package.FXClient;
import core.IStorable;
import core.Storage;
import elements.*;
import entities.Car;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static core.UIHelper.*;

public class AddNewCustomer_vehicle_Controller implements Initializable, IStorable
{
    @FXML private Pane vehicleInfo,noCarsPane,validationPane;
    public static CustomCheckbox noVehiclesCheck;
    public static CustomDialog addCarDialog;
    public static Alert deleteCarAlert;
    public static CustomCarList carList;
    CustomButton addNewCar,next;
    public static boolean hasVehicles=true;
    public static ArrayList<Car> cars = new ArrayList<>();


    /**
     * initialises all needed information and elements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println(Storage.map);

        //back to personal info
        CustomButton back = new CustomButton("Back");
        back.setSize("normal");
        back.setPosition(30, vehicleInfo.getPrefHeight() - back.height - 20);
        back.setOnMouseClicked(goToPersonal());
        addElement(vehicleInfo, back);

        //next stage
        next = new CustomButton("Next");
        next.setSize("normal");
        next.setPosition(vehicleInfo.getPrefWidth() -  back.width - 30,vehicleInfo.getPrefHeight() - back.height - 20);
        next.setOnMouseClicked(nextStage());
        addElement(vehicleInfo, next);


        //add car info - addNewCar
        addNewCar = new CustomButton("Add Car");
        addNewCar.setSize("normal");
        addNewCar.setPosition(110,200);
        addNewCar.setOnMouseClicked(addNewCarDialog());
        addElement(vehicleInfo, addNewCar);

        //checkbox
        noVehiclesCheck = new CustomCheckbox("No cars", "The customer does not own any cars at the moment" , 680, 205);
        noVehiclesCheck.setOnMouseClicked(disableAddCars());
        addElement(vehicleInfo, noVehiclesCheck);

        //CustomDialog
        Platform.runLater(()->
        {
            try {
                addCarDialog = new CustomDialog(FXClient.lm.frame, "Add A vehicle", "MarketingDepartmentRepresentative/NewCustomer/CarInfoDialog");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        Platform.runLater(()->
        {
            deleteCarAlert = new Alert(vehicleInfo, "Are you sure you want to delete ?", "Yes", 300, 100, deleteThisCar());
        });

        //CustomCarList
        carList = new CustomCarList(cars, 110,250, openDeleteAlert());
        carList.setPrefSize(380,200);
        addElement(vehicleInfo,carList);

        if(validationPane.isVisible()) validationPane.setVisible(false);

        prefill();
    }

    /**
     * An event to store information from current page and load next stage
     *
     * @return event that preforms this method
     */
    private EventHandler<MouseEvent> nextStage()
    {
            return e ->
            {
                if(validate()) {
                    try {
                        if (!hasVehicles) {
                            CreateCustomerNoCars();
                        }
                        if (hasVehicles && !(cars.isEmpty())) {
                            store();
                            FXClient.lm.set("MarketingDepartmentRepresentative/NewCustomer/PlanInfo", this);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

    }

    /**
     * An event to check page's checkbox and disable/enable adding cars
     *
     * @return event that preforms this method
     */
    private EventHandler<Event> disableAddCars()
    {
        return e ->
        {
            try {
                if(noVehiclesCheck.checked()) {
                    addNewCar.setDisable(true);
                    hasVehicles = false;
                }
                if(!noVehiclesCheck.checked()){
                    addNewCar.setDisable(false);
                    hasVehicles = true;
                }
            }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }

    /**
     * A method to open custom dialog when pressing on custom button element
     */
    private EventHandler<MouseEvent> addNewCarDialog()
    {
        return e ->
        {
            try { addCarDialog.show(); }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }


    /**
     * A method to open custom Alert - delete car method is a "yes" event
     */
    private EventHandler<Event> openDeleteAlert()
    {
        return e ->
        {
            try { deleteCarAlert.show(); }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }



    /**
     * This method gets existing information in storage and places it in the correct field
     */
    private void prefill()
    {
        if(Storage.get("cars") != null)
        {
            carList.removeAllCars(cars);
            carList.addAllCars(cars);
            noVehiclesCheck.setDisable(true);
        }
        else noVehiclesCheck.setDisable(false);
    }

    /**
     * An event to load next stage and create new customer
     *
     */
   private void CreateCustomerNoCars()
    {
        try {
            FXClient.lm.set("MarketingDepartmentRepresentative/NewCustomer/CreateCustomerNoVehicles",this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method implements the IStorable interface that allows to store in a hashmap information and pass it between classes
     */
    @Override
    public void store()
    {
        if(!cars.isEmpty())
        {
            Storage.add("cars",cars);
        }
    }

    /**
     * An event to store information from current page and load previous stage
     *
     * @return event that preforms this method
     */
    private EventHandler<MouseEvent> goToPersonal()
    {
        return e ->
        {
            try { store(); FXClient.lm.set("MarketingDepartmentRepresentative/NewCustomer/PersonalInfo", this); }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }


    /**
     * This method makes sure all the data is correct logically
     * @return boolean value which indicates if all tests passes
     */
    public boolean validate()
    {
        boolean state = true;
        if(!noVehiclesCheck.checked() && cars.isEmpty())
        {
            validationPane.setVisible(true);
            state=false;
        }
        return state;
    }

    /**
     * A method to delete car from car list
     *
     */
    private EventHandler<Event> deleteThisCar()
    {
        return e ->
        {
            try {
                String selected = carList.getSelected().carID;
                for(CustomCarList.VCar c : carList.vcars)
                {
                    int index = carList.vcars.indexOf(c);
                    if(selected.compareTo(c.car.carID) == 0)
                    {
                        carList.removeCar(selected);
                        cars.remove(index);
                        System.out.println(AddNewCustomer_vehicle_Controller.cars.size());
                    }
                    if(cars.isEmpty())
                    {
                        noVehiclesCheck.setDisable(false);
                    }
                }
                deleteCarAlert.hide();
            }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }

}

