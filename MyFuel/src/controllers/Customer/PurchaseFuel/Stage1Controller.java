package controllers.Customer.PurchaseFuel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import java.util.ResourceBundle;

import client_package.FXClient;
import core.DiscountManager;
import core.GenericObserver;
import core.IStorable;
import core.Storage;
import core.UIHelper;
import elements.CustomAnimation;
import elements.CustomButton;
import elements.CustomCarList;
import elements.CustomCheckbox;
import elements.CustomDialog;
import entities.ActivesSale;
import entities.Car;
import entities.CreditCard;
import entities.Customer;
import entities.Fuel;
import entities.FuelStock;
import entities.PurchasePlan;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server_package.Response;

import static core.UIHelper.*;

public class Stage1Controller implements Initializable, IStorable {

    @FXML private Pane orderPane;
    @FXML private TextField amountField;
    @FXML private Text limitType;
    @FXML private HBox fuelProgress;
    @FXML private HBox validateError, validateErrorCar,noStockError;
    @FXML private ImageView woman, machine, notif, select;
    
    private CustomCheckbox fullTank,liters,sum,creditCardCheckBox,cashCheckBox;
    private CustomCarList cl;
    private CustomButton proceedButton;
    static CustomDialog creditCardDialog;
    static ArrayList<CreditCard> userCredit;
    private ArrayList<Car> cars;
    private ArrayList<FuelStock> stocks;
    private Random rand = new Random();
    private Integer gasStationID;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        gasStationID = rand.nextInt(6)+1;
        setupScreenButtons();
        setupEventHandlers();

        Platform.runLater(this::fetchCarsFromDatabase);
        Platform.runLater(this::fetchCreditCardFromDatabase);
        Platform.runLater(this::fetchFuelStocks);

         try { setupDiscountManager(); }
         catch (IOException e) { e.printStackTrace(); }

        animate();
    }

	/**
     * fetches the user's CC from the DB, and stores it in Storage component.\
     * Also loads the page's dialogs
     */
    private void fetchCreditCardFromDatabase()
    {

        try { FXClient.client.request("SELECT * FROM creditCards WHERE customerID = '" + FXClient.user.username + "'", CreditCard.class); }
        catch (IOException e1) { e1.printStackTrace(); }

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                userCredit = ((Response<CreditCard>)arg).result();

                if(userCredit.size()>0) {
                    Storage.add("creditCardNumber",userCredit.get(0).cardNumber);
                    Storage.add("month",userCredit.get(0).expMonth);
                    Storage.add("year",userCredit.get(0).expYear);
                    Storage.add("cvv",userCredit.get(0).cvv);
                }

                Platform.runLater(()->{
                    try {
                        creditCardDialog = new CustomDialog(FXClient.lm.frame, "", "Customer/PurchaseFuel/CreditCardDialog");
                        creditCardDialog.setOnExit(e->{
                            if(Storage.isEmpty()){ creditCardCheckBox.setChecked(false); } // only checks credit-card box if isn't empty
                            creditCardDialog.hide();
                            });
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });
            }});
    }

    /**
     * fetches all of the cars the user has registered under his name
     */
    private void fetchCarsFromDatabase()
    {
        try { FXClient.client.request("SELECT * FROM cars WHERE customerID = '" + FXClient.user.username + "'", Car.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg) -> {
        	
        	cars = ((Response<Car>)arg).result();
        	
        	Platform.runLater(() -> {
            	cl = new CustomCarList(cars, 28, 180);
            	addElement(orderPane, cl);
        	});
        });
    }

    private void fetchFuelStocks(){
        try { FXClient.client.request("SELECT FS.type, amount,threshold, price FROM fuelStock FS, fuels F WHERE FS.type = F.type AND gasStationID = "+gasStationID, FuelStock.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg)->{
            stocks = ((Response<FuelStock>)arg).result();
        });
    }

    private void setupEventHandlers(){
        fullTank.setOnMouseClicked((MouseEvent event) -> {
            setCheckboxMultipleFields(true,false,false,true);
            limitType.setText("F");
            toggleOff(orderPane, "emptyCheckbox");
        });

        sum.setOnMouseClicked((MouseEvent event) ->{
           setCheckboxMultipleFields(false,true,false,false);
            limitType.setText("$");
            toggleOff(orderPane, "emptyCheckbox");
        });

        liters.setOnMouseClicked((MouseEvent event) ->{
            setCheckboxMultipleFields(false,false,true,false);
            limitType.setText("L");
            toggleOff(orderPane, "emptyCheckbox");
        });

        cashCheckBox.setOnMouseClicked((MouseEvent event) ->{
            cashCheckBox.setChecked(true);
            creditCardCheckBox.setChecked(false);
            toggleOff(orderPane, "emptyCheckbox");
        });
        creditCardCheckBox.setOnMouseClicked(e->{
            cashCheckBox.setChecked(false);
            creditCardDialog.show();
            toggleOff(orderPane, "emptyCheckbox");
        });
        /*
          proceedButton validates all fields before changing the page.
         */
        proceedButton.setOnMouseClicked((MouseEvent event) -> {
       	
        	if(validate())
        		try { store(); FXClient.lm.set("Customer/PurchaseFuel/Stage2", this); }
        		catch(Exception ex) { ex.printStackTrace(); }
        	else validateError.setVisible(true);
        });
        UIHelper.numbersOnly(amountField,3);
    }

    void setCheckboxMultipleFields(boolean fullTankField,boolean sumField, boolean litersField,boolean disableAmount){
        amountField.setDisable(disableAmount);
        fullTank.setChecked(fullTankField);
        sum.setChecked(sumField);
        liters.setChecked(litersField);
    }
    /**
     * stores the refueling information needed for further calculations in the next controller.
     */
    @Override
    public void store() {
        Storage.add("limit",amountField.getText());
        Storage.add("stationID",gasStationID);
        if(sum.checked()) { Storage.add("limitType","sum");}
        else if(liters.checked()) { Storage.add("limitType","liters");}
        else { Storage.add("limitType","fullTank");}

        if(creditCardCheckBox.checked()) { Storage.add("payment","credit"); }
        else { Storage.add("payment","cash"); }
        
        if(cl.getSelected() != null) Storage.add("fuelType", cl.getSelected().getFuelType());
        DiscountManager.customerCars = cars;
    }

    /**
     * Setuping all the custom screen buttons, placing in position and styling.
     */
    private void setupScreenButtons(){
        proceedButton = new CustomButton("Proceed");
        proceedButton.setSize("normal");
        proceedButton.setPosition(orderPane.getPrefWidth() -  proceedButton.width - 30, orderPane.getPrefHeight() - proceedButton.height - 20);
        proceedButton.setCustomStyle("button3");
        addElement(orderPane, proceedButton);

        fullTank = new CustomCheckbox("Full Tank","Click here if you wish to refill a full tank for your vehicle",28,430 );
        liters = new CustomCheckbox("Liters",28,395 );
        sum = new CustomCheckbox("Cash",28,360 );
        addElement(orderPane,fullTank);
        addElement(orderPane, liters);
        addElement(orderPane, sum);

        creditCardCheckBox = new CustomCheckbox("Credit-Card",450, 360);
        cashCheckBox = new CustomCheckbox("Cash",450, 395);
        addElement(orderPane, creditCardCheckBox);
        addElement(orderPane, cashCheckBox);
    }

    /**
     * validates all the fields in the stage were correctly inserted to proceed to the next stage
     * @return boolean
     */
    private boolean validate()
    {

        String amount = amountField.getText();
        boolean method = fullTank.checked() || liters.checked() || sum.checked();
        boolean payment = creditCardCheckBox.checked() || cashCheckBox.checked();
        boolean vehicle = cl.getSelected() != null;

    	toggleClass(amountField, "emptyTextField", amount.isEmpty() && !fullTank.checked());
    	fullTank.toggleCheckboxStyle("emptyCheckbox", !method);
    	liters.toggleCheckboxStyle("emptyCheckbox", !method);
    	sum.toggleCheckboxStyle("emptyCheckbox", !method);
    	creditCardCheckBox.toggleCheckboxStyle("emptyCheckbox", !payment);
    	cashCheckBox.toggleCheckboxStyle("emptyCheckbox", !payment);
    	
    	validateErrorCar.setVisible(!vehicle);
        if(!amount.isEmpty() && cl.getSelected() != null && !fullTank.checked()){noStockError.setVisible(validateStock());}

        return (!amount.isEmpty() || fullTank.checked()) && method && payment && vehicle && !noStockError.isVisible();
    }

    /**
     * validates that the refuel is possible by checking the stock currently
     * @return boolean
     */
    boolean validateStock(){

        FuelStock currentStock = null;
        Random rand = null;
        float amountInLiters;

        for(FuelStock fs : stocks){
            if(fs.type.equals(cl.getSelected().fuelType))currentStock = fs;
        }

        amountInLiters = liters.checked() ? Integer.valueOf(amountField.getText()) : sum.checked() ? (Integer.parseInt(amountField.getText()) / currentStock.price) : currentStock.amount-2;

        return amountInLiters > currentStock.amount;
    }

    /**
     * animations for the page
     */
    private void animate()
    {
    	new CustomAnimation(machine, Duration.seconds(0.3)).fadeIn().play();
    	new CustomAnimation(woman, Duration.seconds(0.3)).fadeIn().translate(10, 0).delay(Duration.seconds(0.1)).play();
    	new CustomAnimation(notif, Duration.seconds(0.5)).fadeIn().translate(-20, 0).delay(Duration.seconds(0.2)).play();
    	new CustomAnimation(select, Duration.seconds(0.5)).fadeIn().translate(-20, 0).delay(Duration.seconds(0.3)).play();
    }

    /**
     * fetches all the necessary discounts and information for the bill proccess
     * @throws IOException in case DB query fails
     */
    private void setupDiscountManager() throws IOException
    {
		FXClient.client.request("SELECT * FROM customers C, users U WHERE C.customerID = U.username AND username = '" + FXClient.user.username + "'", Customer.class);
		FXClient.observable.addObserver((o, arg) -> DiscountManager.customer = ((Response<Customer>)arg).result().get(0));
		
		FXClient.client.request("SELECT * FROM activeSales A, saleTemplates T WHERE A.templateID = T.templateID", ActivesSale.class);
		FXClient.observable.addObserver((o, arg) -> DiscountManager.sales = ((Response<ActivesSale>)arg).result());

		FXClient.client.request("SELECT * FROM purchasePlans WHERE customerID = '" + FXClient.user.username + "'", PurchasePlan.class);
		FXClient.observable.addObserver((o, arg) -> DiscountManager.plan = ((Response<PurchasePlan>)arg).result());
		
		FXClient.client.request("SELECT * FROM fuels", Fuel.class);
		FXClient.observable.addObserver((o, arg) -> DiscountManager.fuels = ((Response<Fuel>)arg).result());
	}
}
