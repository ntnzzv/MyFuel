package controllers.MarketingDepartmentRepresentative.EditCustomer_old;
import elements.CustomButton;
import elements.CustomDialog;
import javafx.event.Event;
import javafx.event.EventHandler;
import client_package.FXClient;
import core.IStorable;
import core.Storage;
import elements.CustomCarList;
import entities.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import server_package.Response;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.control.TextField;

import static core.UIHelper.*;
import static core.UIHelper.toggleClass;

public class CustomerDetailsController implements Initializable, IStorable
{
    @FXML private Pane datailsPane;
    @FXML private TextField customerIDField,firstNameField, lastNameField;
    @FXML ComboBox<String> purchasePlanComboBox,option1Combobox,option2Combobox,option3Combobox;
    @FXML ComboBox<String> servicePlanCombobox;
    public static CustomCarList cl;
    public static CustomDialog addCarDialog;
    String customerID=(String) Storage.get("customerID");
    public static ArrayList<Car> carsToShow = new ArrayList<>();
    public static ArrayList<Car> carsToDelete = new ArrayList<>();
    public static ArrayList<Car> carsToAdd = new ArrayList<>();
    String purchasePlansString=null, op1New=null, op2New=null, op3New=null,servicePlan,op1Old=null,op2Old=null,op3Old=null;

    private final ObservableList<String> servicePlans = FXCollections.observableArrayList(
            "Occasional Refuel",
            "Monthly Single Car Refuel",
            "Monthly Multiple Cars Refuel",
            "Monthly Fixed Single Car",
            "None");

    private final ObservableList<String> purchasePlans = FXCollections.observableArrayList(
            "Exclusive",
            "Non Exclusive","None");

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println(Storage.map);
        servicePlanCombobox.getItems().addAll(servicePlans);
        purchasePlanComboBox.getItems().addAll(purchasePlans);
        Platform.runLater(this::fetchGasStationsFromDatabase);
        Platform.runLater(this::fetchCarsFromDatabase);
        Platform.runLater(this::fillOptions);
        prefill();

        //next button
        CustomButton update = new CustomButton("Update");
        update.setSize("normal");
        update.setPosition(datailsPane.getPrefWidth() -  update.width - 30, datailsPane.getPrefHeight() - update.height - 20);
        update.setOnMouseClicked(updateCustomer());
        addElement(datailsPane, update);


        //CustomDialog
        Platform.runLater(()->
        {
            try {
                addCarDialog = new CustomDialog(FXClient.lm.frame, "Add A vehicle", "MarketingDepartmentRepresentative/ClientCard/addCarDialog");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

    }

    private EventHandler<Event> updateCustomer()
    {
        return e ->
        {
            if(validate())
                try
                {
                    Platform.runLater(()-> {
                        store();
                        try {
                            FXClient.lm.set("MarketingDepartmentRepresentative/ClientCard/updateCustomer", this);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    });
                }
                catch (Exception ex) { ex.printStackTrace(); }
            else System.out.println("didnt pass validation");
        };
    }

    /**
     * fetches all of the cars the client has registered under his name
     */
    private void fetchCarsFromDatabase()
    {
        try { FXClient.client.request("SELECT * FROM cars WHERE customerID = '" + customerID + "'", Car.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg) -> {

            carsToShow = ((Response<Car>)arg).result();

            Platform.runLater(() -> {
                cl = new CustomCarList(carsToShow, 65, 270, deleteCar());
                cl.setPrefSize(400,200);
                addElement(datailsPane, cl);
            });
        });
    }


    @FXML
    void openAddCarDialog(MouseEvent event)  {addCarDialog.show();}

    private EventHandler<Event> deleteCar()
    {
        return e ->
        {
            try {
                Platform.runLater(() -> {
                String selected = cl.getSelected().carID;
                for(CustomCarList.VCar c : cl.vcars)
                {
                    int index = cl.vcars.indexOf(c);
                    if(selected.compareTo(c.car.carID) == 0)
                    {
                        cl.removeCar(selected);
                        carsToShow.remove(index);
                        carsToDelete.add(cl.getSelected());
                    }
                }
                });
            }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }

    /**
     * fetches all of the gas stations into combo boxes
     */
    private void fetchGasStationsFromDatabase()
    {
        try { FXClient.client.request("SELECT name FROM gasStations", GasStation.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg) -> {
            System.out.println(arg);
            for(GasStation gs : ((Response<GasStation>)arg).result())
            {
                option1Combobox.getItems().add(gs.name);
                option2Combobox.getItems().add(gs.name);
                option3Combobox.getItems().add(gs.name);
            }

        });

    }


    private void prefill()
    {
        if(Storage.get("firstName") != null)	firstNameField.setText((String) Storage.get("firstName"));
        if(Storage.get("lastName") != null)	lastNameField.setText((String) Storage.get("lastName"));
        if(Storage.get("customerID") != null)		customerIDField.setText((String) Storage.get("customerID"));
        if(Storage.get("servicePlan") != null) servicePlanCombobox.getSelectionModel().select((String) Storage.get("servicePlan"));
        if(Storage.get("purchasePlan") != null) purchasePlanComboBox.getSelectionModel().select((String) Storage.get("purchasePlan"));
    }


    public void fillOptions()
    {
        if(SearchCustomerController.options.size() == 1)
        {
            option1Combobox.getSelectionModel().select(SearchCustomerController.options.get(0));
            op1Old = SearchCustomerController.options.get(0);
        }

        if(SearchCustomerController.options.size() == 2)
        {
            option1Combobox.getSelectionModel().select(SearchCustomerController.options.get(0));
            op1Old = SearchCustomerController.options.get(0);
            option2Combobox.getSelectionModel().select(SearchCustomerController.options.get(1));
            op2Old = SearchCustomerController.options.get(1);
        }

        if(SearchCustomerController.options.size() == 1)
        {
            option1Combobox.getSelectionModel().select(SearchCustomerController.options.get(0));
            op1Old = SearchCustomerController.options.get(0);
            option2Combobox.getSelectionModel().select(SearchCustomerController.options.get(1));
            op2Old = SearchCustomerController.options.get(1);
            option3Combobox.getSelectionModel().select(SearchCustomerController.options.get(2));
            op3Old = SearchCustomerController.options.get(2);
        }

    }

    @Override
    public void store()
    {
        Storage.add("ServicePlan", servicePlan);
        Storage.add("purchasePlan", purchasePlansString);
        Storage.add("op1Old", op1Old);
        Storage.add("op2Old", op2Old);
        Storage.add("op3Old", op3Old);
        Storage.add("option1", op1New);
        Storage.add("option2", op2New);
        Storage.add("option3", op3New);
    }

    private boolean validate()
    {
        servicePlan = servicePlanCombobox.getSelectionModel().getSelectedItem();
        purchasePlansString = purchasePlanComboBox.getSelectionModel().getSelectedItem();
        op1New = option1Combobox.getSelectionModel().getSelectedItem();
        op2New = option2Combobox.getSelectionModel().getSelectedItem();
        op3New = option3Combobox.getSelectionModel().getSelectedItem();
        boolean fittedServicePlan = servicePlanCheck();
        boolean enoughPurchasePlan = purchasePlanCheck();

        return fittedServicePlan && enoughPurchasePlan;
    }

    /**
     * checks if service plan matches client
     */

    public boolean servicePlanCheck()
    {
        boolean serviceValid = true;
        if(servicePlanCombobox.getSelectionModel().getSelectedItem().equals("Monthly Single Car Refuel") &&
                carsToShow.size()>1) serviceValid = false;

        if(servicePlanCombobox.getSelectionModel().getSelectedItem().equals("Monthly Fixed Single Car") &&
                carsToShow.size()>1) serviceValid = false;

        if(servicePlanCombobox.getSelectionModel().getSelectedItem().equals("Monthly Multiple Cars Refuel") &&
                carsToShow.size()==1) serviceValid = false;

        if (!serviceValid)System.out.println("service plan not valid");

        return serviceValid;
    }


    /**
     * checks if purchase plan options are valid
     */
    public boolean purchasePlanCheck()
    {
        String o1 = null,o2=null,o3=null;
        boolean purchaseValid = false;
        if(purchasePlanComboBox.getSelectionModel().getSelectedItem() == "Non Exclusive")
        {
            boolean option1selected = option1Combobox.getSelectionModel().getSelectedIndex() != -1;
            if(option1selected) o1 = option1Combobox.getSelectionModel().getSelectedItem();
            //toggleClass(op1Custom, "emptyTextField", !option1selected);

            boolean option2selected = option2Combobox.getSelectionModel().getSelectedIndex() != -1;
            if(option2selected) o2 = option2Combobox.getSelectionModel().getSelectedItem();
            //toggleClass(op2Custom, "emptyTextField", !option2selected);

            boolean option3selected = option3Combobox.getSelectionModel().getSelectedIndex() != -1;
            if(option3selected) o3 = option3Combobox.getSelectionModel().getSelectedItem();

            if(option1selected && option2selected)
            {
                if (option1selected && option2selected && option3selected)
                {
                    if (!(o1.equals(o2) || o1.equals(o3) || o2.equals(o3)))
                        purchaseValid = true;
                    else System.out.println("purchase plan not valid");
                }
            }
        }
        return purchaseValid;
    }
}
