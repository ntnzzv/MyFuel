package controllers.MarketingDepartmentRepresentative;

import client_package.FXClient;
import core.IStorable;
import core.Storage;
import elements.CustomAnimation;
import elements.CustomButton;
import elements.CustomTooltip;
import entities.GasStation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import server_package.Response;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import static core.UIHelper.addElement;
import static core.UIHelper.toggleClass;


public class AddNewCustomer_PlanInfo_Controller implements Initializable, IStorable
{
    @FXML
    private Pane planInfo;
    @FXML private ComboBox<String> servicePlanComboBox;
    @FXML private ComboBox<String> dalkanComboBox; // yes/no
    @FXML private ComboBox<String> purchasePlanComboBox;
    @FXML private ComboBox<String> option1ComboBox;
    @FXML private ComboBox<String> option2ComboBox;
    @FXML private ComboBox<String> option3ComboBox;
    private CustomButton serviceCustom, dalkanCustom, purchasePlanCustom,op1Custom,op2Custom,op3Custom;
    String gasServicePlanString,hasDalkanString,purchasePlansString,op1String,op2String,op3String;
    @FXML private ImageView circle, table, woman, books, lamp, plant;
    @FXML private BorderPane servicePlanError,purchasePlanError,dalkanError;

    private final ObservableList<String> servicePlans = FXCollections.observableArrayList(
            "Occasional Refuel",
            "Monthly Single Car Refuel",
            "Monthly Multiple Cars Refuel",
            "Monthly Fixed Single Car");

    private final ObservableList<String> dalkans = FXCollections.observableArrayList(
            "yes",
            "no");

    private final ObservableList<String> purchasePlans = FXCollections.observableArrayList(
            "Exclusive",
            "Non Exclusive","None");


    /**
     * initialises all needed information and elements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println(Storage.map);
        Platform.runLater(this::fetchGasStationsFromDatabase);

        servicePlanComboBox.getItems().addAll(servicePlans);
        dalkanComboBox.getItems().addAll(dalkans);
        purchasePlanComboBox.getItems().addAll(purchasePlans);

        option1ComboBox.setVisible(false);
        option2ComboBox.setVisible(false);
        option3ComboBox.setVisible(false);

        //back to personal info
        CustomButton back = new CustomButton("Back");
        back.setSize("normal");
        back.setPosition(30, planInfo.getPrefHeight() - back.height - 20);
        back.setOnMouseClicked(goToVehicle());
        addElement(planInfo, back);

        //save
        CustomButton save = new CustomButton("Save");
        save.setSize("normal");
        save.setPosition(planInfo.getPrefWidth() -  back.width - 30,planInfo.getPrefHeight() - back.height - 20);
        save.setOnMouseClicked(CreateNewCustomer());
        addElement(planInfo, save);

        //service Plan combo
        serviceCustom = new CustomButton("Service Plan", 62, 165);
        serviceCustom.setSize(190, 30);
        serviceCustom.setCustomStyle("button2");
        addElement(planInfo, serviceCustom);
        servicePlanComboBox.setFocusTraversable(false);
        serviceCustom.setOnMouseClicked(e -> { servicePlanComboBox.show(); toggleClass(serviceCustom, "emptyTextField", false); });
        servicePlanComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            serviceCustom.setText(servicePlanComboBox.getSelectionModel().getSelectedItem());
        });

        //dalkan combo box
        dalkanCustom = new CustomButton("Dalkan", 62, 230);
        dalkanCustom.setSize(190, 30);
        dalkanCustom.setCustomStyle("button2");
        addElement(planInfo, dalkanCustom);
        dalkanComboBox.setFocusTraversable(false);
        dalkanCustom.setOnMouseClicked(e -> { dalkanComboBox.show(); toggleClass(dalkanCustom, "emptyTextField", false); });
        dalkanComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            dalkanCustom.setText(dalkanComboBox.getSelectionModel().getSelectedItem());
        });


        //purchase Plan combo box
        purchasePlanCustom = new CustomButton("Purchase Plan", 62, 300);
        purchasePlanCustom.setSize(190, 30);
        purchasePlanCustom.setCustomStyle("button2");
        addElement(planInfo, purchasePlanCustom);
        purchasePlanComboBox.setFocusTraversable(false);
        purchasePlanCustom.setOnMouseClicked(e -> { purchasePlanComboBox.show(); toggleClass(purchasePlanCustom, "emptyTextField", false); });
        purchasePlanComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            purchasePlanCustom.setText(purchasePlanComboBox.getSelectionModel().getSelectedItem());
        });

        //op1 combo box
        op1Custom = new CustomButton("op1", 62, 345);
        op1Custom.setSize(70, 30);
        op1Custom.setCustomStyle("button2");
        op1Custom.setVisible(false);
        addElement(planInfo, op1Custom);
        option1ComboBox.setFocusTraversable(false);
        op1Custom.setOnMouseClicked(e -> { option1ComboBox.show(); toggleClass(op1Custom, "emptyTextField", false); });
        option1ComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            op1Custom.setText(option1ComboBox.getSelectionModel().getSelectedItem());
        });

        //op2 combo box
        op2Custom = new CustomButton("op2", 145, 345);
        op2Custom.setSize(70, 30);
        op2Custom.setCustomStyle("button2");
        op2Custom.setVisible(false);
        addElement(planInfo, op2Custom);
        option2ComboBox.setFocusTraversable(false);
        op2Custom.setOnMouseClicked(e -> { option2ComboBox.show(); toggleClass(op2Custom, "emptyTextField", false); });
        option2ComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            op2Custom.setText(option2ComboBox.getSelectionModel().getSelectedItem());
        });

        //op3 combo box
        op3Custom = new CustomButton("op3", 227, 345);
        op3Custom.setSize(70, 30);
        op3Custom.setCustomStyle("button2");
        op3Custom.setVisible(false);
        addElement(planInfo, op3Custom);
        option3ComboBox.setFocusTraversable(false);
        op3Custom.setOnMouseClicked(e -> { option3ComboBox.show(); toggleClass(op3Custom, "emptyTextField", false); });
        option3ComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            op3Custom.setText(option3ComboBox.getSelectionModel().getSelectedItem());
        });

        servicePlanError.setVisible(false);
        dalkanError.setVisible(false);
        purchasePlanError.setVisible(false);

        //dalkan tooltip
        CustomTooltip dalkantt = new CustomTooltip("?", "Allows customer to fast fuel", 256, 234);
        addElement(planInfo, dalkantt);

        //purchase plan tooltip
        CustomTooltip purchasePlanTT = new CustomTooltip("?", "Purchase plan will allow customer choose where he'd like to purchase fuel", 256, 304);
        addElement(planInfo, purchasePlanTT);

        prefill();
        animate();
    }


    /**
     * An event to store information from current page and load previous stage
     *
     * @return event that preforms this method
     */
    private EventHandler<Event> goToVehicle()
    {
        return e ->
        {
            try { store(); FXClient.lm.set("MarketingDepartmentRepresentative/NewCustomer/VehicleInfo", this); }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }

    /**
     * An event to store information from current page and load next stage to create new customer
     *
     * @return event that preforms this method
     */
    private EventHandler<MouseEvent> CreateNewCustomer()
    {
        return e ->
        {
           if(validate())
               Platform.runLater(()-> {
                   try {
                       store();
                       FXClient.lm.set("MarketingDepartmentRepresentative/NewCustomer/CustomerCreatedPage", this);
                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }
               });
           else System.out.println("didnt pass validation");
        };
    }

    /**
     * This method makes sure all the data is correct logically
     * @return boolean value which indicates if all tests passes
     */
    private boolean validate()
    {
        boolean dalkanCheck = false, fittedServicePlan=false,enoughPurchasePlan=false;

        purchasePlansString = purchasePlanComboBox.getSelectionModel().getSelectedItem();
        op1String = option1ComboBox.getSelectionModel().getSelectedItem();
        op2String = option2ComboBox.getSelectionModel().getSelectedItem();
        op3String = option3ComboBox.getSelectionModel().getSelectedItem();

        boolean servicePlanSelected = servicePlanComboBox.getSelectionModel().getSelectedIndex() != -1;
        if(servicePlanSelected) gasServicePlanString = servicePlanComboBox.getSelectionModel().getSelectedItem();
        toggleClass(serviceCustom, "emptyTextField", !servicePlanSelected);

        boolean dalkanSelected = dalkanComboBox.getSelectionModel().getSelectedIndex() != -1;
        if(dalkanSelected) hasDalkanString = dalkanComboBox.getSelectionModel().getSelectedItem();
        toggleClass(dalkanCustom, "emptyTextField", !dalkanSelected);

        boolean purchasePlanSelected = purchasePlanComboBox.getSelectionModel().getSelectedIndex() != -1;
        if(purchasePlanSelected) purchasePlansString = purchasePlanComboBox.getSelectionModel().getSelectedItem();
        toggleClass(purchasePlanCustom, "emptyTextField", !purchasePlanSelected);

        if(dalkanSelected && servicePlanSelected)
        {
            dalkanCheck = dalkanCheck();
            fittedServicePlan = servicePlanCheck();
            enoughPurchasePlan = purchasePlanCheck();
        }

        return servicePlanSelected && dalkanSelected && servicePlanSelected && dalkanCheck && fittedServicePlan && enoughPurchasePlan;
    }

    /**
     * This method dynamically decides which combo boxes user will see depending on his choice
     */
    @FXML
    public void showPurchasePlanOptions() throws IOException
    {
        if(purchasePlanComboBox.getSelectionModel().getSelectedItem() == "Exclusive")
        {
            option1ComboBox.setVisible(true);
            op1Custom.setVisible(true);
            option2ComboBox.setVisible(false);
            op2Custom.setVisible(false);
            option3ComboBox.setVisible(false);
            op3Custom.setVisible(false);
        }

        if(purchasePlanComboBox.getSelectionModel().getSelectedItem() == "Non Exclusive")
        {
            option1ComboBox.setVisible(true);
            op1Custom.setVisible(true);
            option2ComboBox.setVisible(true);
            op2Custom.setVisible(true);
            option3ComboBox.setVisible(true);
            op3Custom.setVisible(true);
        }

        if(purchasePlanComboBox.getSelectionModel().getSelectedItem() == "None")
        {
            option1ComboBox.setVisible(false);
            op1Custom.setVisible(false);
            option2ComboBox.setVisible(false);
            op2Custom.setVisible(false);
            option3ComboBox.setVisible(false);
            op3Custom.setVisible(false);
            op1String=null;
            op2String=null;
            op3String=null;
        }
    }

    /**
     * This method implements the IStorable interface that allows to store in a hashmap information and pass it between classes
     */
    @Override
    public void store()
    {
        Storage.add("ServicePlan", gasServicePlanString);
        Storage.add("hasDalkan", hasDalkanString);
        Storage.add("purchasePlan", purchasePlansString);
        Storage.add("option1", op1String);
        Storage.add("option2", op2String);
        Storage.add("option3", op3String);
    }

    /**
     * method to schedule entrance of image views
     */
    private void animate()
    {
        new CustomAnimation(table, Duration.seconds(0.5)).fadeIn().translate(0, 10).delay(Duration.seconds(0.2)).play();
        new CustomAnimation(woman, Duration.seconds(0.5)).fadeIn().translate(10, 10).delay(Duration.seconds(0.3)).play();
        new CustomAnimation(books, Duration.seconds(0.5)).fadeIn().translate(0, -20).delay(Duration.seconds(0.4)).play();
        new CustomAnimation(lamp, Duration.seconds(0.5)).fadeIn().translate(0, 0).delay(Duration.seconds(0.4)).play();
        new CustomAnimation(plant, Duration.seconds(0.5)).fadeIn().translate(0, -5).delay(Duration.seconds(0.4)).play();
    }

    /**
     * This method gets existing information in storage and places it in the correct field
     */
    private void prefill() {
        if (Storage.get("ServicePlan") != null)
            servicePlanComboBox.getSelectionModel().select((String) Storage.get("ServicePlan"));
        if (Storage.get("hasDalkan") != null)
            dalkanComboBox.getSelectionModel().select((String) Storage.get("hasDalkan"));
        if (Storage.get("purchasePlan") != null)
            purchasePlanComboBox.getSelectionModel().select((String) Storage.get("purchasePlan"));
        if (Storage.get("option1") != null)
        {
            option1ComboBox.getSelectionModel().select((String) Storage.get("option1"));
            option1ComboBox.setVisible(true);
            op1Custom.setVisible(true);
        }
        if(Storage.get("option2") != null) {
            option2ComboBox.getSelectionModel().select((String) Storage.get("option2"));
            option2ComboBox.setVisible(true);
            op2Custom.setVisible(true);
        }

        if(Storage.get("option3") != null) {
            option3ComboBox.getSelectionModel().select((String) Storage.get("option3"));
            option3ComboBox.setVisible(true);
            op3Custom.setVisible(true);

        }

    }


    /**
     * fetches all of the gas stations
     */
    private void fetchGasStationsFromDatabase()
    {
        try { FXClient.client.request("SELECT name FROM gasStations", GasStation.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg) -> {
                for(GasStation gs : ((Response<GasStation>)arg).result())
                {
                    System.out.println(gs.name);
                    option1ComboBox.getItems().add(gs.name);
                    option2ComboBox.getItems().add(gs.name);
                    option3ComboBox.getItems().add(gs.name);
                }
            });
    }

    /**
     * checks if service plan matches number of cars
     */

    public boolean servicePlanCheck()
    {
      boolean serviceValid = true;
      if(servicePlanComboBox.getSelectionModel().getSelectedItem().equals("Monthly Single Car Refuel") &&
      AddNewCustomer_vehicle_Controller.cars.size()>1) serviceValid = false;

      if(servicePlanComboBox.getSelectionModel().getSelectedItem().equals("Monthly Fixed Single Car") &&
                AddNewCustomer_vehicle_Controller.cars.size()>1) serviceValid = false;

      if(servicePlanComboBox.getSelectionModel().getSelectedItem().equals("Monthly Multiple Cars Refuel") &&
                AddNewCustomer_vehicle_Controller.cars.size()==1) serviceValid = false;

      if (!serviceValid)servicePlanError.setVisible(true);
      if(serviceValid)servicePlanError.setVisible(false);

      return serviceValid;
    }


    /**
     * checks if purchase plan options are valid
     */
    public boolean purchasePlanCheck()
    {
        String o1 = null,o2=null,o3=null;
        boolean purchaseValid = true;

        if(purchasePlanComboBox.getSelectionModel().getSelectedItem() == "Non Exclusive")
        {
            boolean option1selected = option1ComboBox.getSelectionModel().getSelectedIndex() != -1;
            if(option1selected) o1 = option1ComboBox.getSelectionModel().getSelectedItem();
            toggleClass(op1Custom, "emptyTextField", !option1selected);

            boolean option2selected = option2ComboBox.getSelectionModel().getSelectedIndex() != -1;
            if(option2selected) o2 = option2ComboBox.getSelectionModel().getSelectedItem();
            toggleClass(op2Custom, "emptyTextField", !option2selected);

            boolean option3selected = option3ComboBox.getSelectionModel().getSelectedIndex() != -1;
            if(option3selected) o3 = option3ComboBox.getSelectionModel().getSelectedItem();

            if(option1selected && option2selected)
            {
                if (!(o1.equals(o2))) {
                    purchaseValid = true;
                    purchasePlanError.setVisible(false);
                }
                else purchasePlanError.setVisible(true);
            }

            if (option1selected && option2selected && option3selected)
            {
                if (!(o1.equals(o2) || o1.equals(o3) || o2.equals(o3))) {
                    purchaseValid = true;
                    purchasePlanError.setVisible(false);
                }
                else purchasePlanError.setVisible(true);
            }
        }

        if(purchasePlanComboBox.getSelectionModel().getSelectedItem() == "Exclusive")
        {
            boolean option1selected = option1ComboBox.getSelectionModel().getSelectedIndex() != -1;
            if(option1selected) o1 = option1ComboBox.getSelectionModel().getSelectedItem();
            toggleClass(op1Custom, "emptyTextField", !option1selected);
            if(!option1selected)purchaseValid=false;
        }

        return purchaseValid;
    }

    /**
     * checks if dalkan option is valid
     */
    public boolean dalkanCheck()
    {
        hasDalkanString = dalkanComboBox.getSelectionModel().getSelectedItem();
        boolean dalkanValid = true;
        if(hasDalkanString.equals("yes") && !AddNewCustomer_PersonalInfo_Controller.hasCreditCard)
        {
            dalkanError.setVisible(true);
            dalkanValid=false;
        }
        else dalkanError.setVisible(false);
        return dalkanValid;
    }


}
