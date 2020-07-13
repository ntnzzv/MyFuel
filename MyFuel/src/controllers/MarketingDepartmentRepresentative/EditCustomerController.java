package controllers.MarketingDepartmentRepresentative;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client_package.FXClient;
import elements.CustomButton;
import elements.CustomCarList;
import elements.CustomDialog;
import entities.Car;
import entities.Customer;
import entities.GasStation;
import entities.PurchasePlan;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import server_package.Response;

import static core.UIHelper.*;

public class EditCustomerController implements Initializable {

    @FXML private Pane customerPane;
    @FXML private TextField customerIDTextField, firstNameTextField, lastNameTextField;
    @FXML HBox validateError, customerInfo, carListTitle;
    @FXML VBox carsBox;
    @FXML ComboBox<String> servicePlanCombo, purchasePlanCombo, op1, op2, op3;
    
    private String customerID;
    private CustomButton search, update, add;
    public static CustomCarList carList;
    public static ArrayList<Car> cars;
    public static CustomDialog addDialog;


	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{

		op1.setDisable(true);
		op2.setDisable(true);
		op3.setDisable(true);
		
		customerInfo.setVisible(false);
		
		search = new CustomButton("Search", 220, 126);
		search.setSize("normal");
		search.setOnMouseClicked(e -> {
			try { getCustomer(); }
			catch (IOException e1) { e1.printStackTrace(); }
		});
		
		update = new CustomButton("Update");
		update.setSize("normal");
		update.setCustomStyle("button3");
		update.setPosition(customerPane.getPrefWidth() -  update.width - 30, customerPane.getPrefHeight() - update.height - 20);
		update.setOnMouseClicked(e -> updateInformation(customerID));
		
		update.setDisable(true);
		update.setOpacity(0.5);
		
		add = new CustomButton("Add car");
		add.setSize("small");
		add.setCustomStyle("addCarButton1");
		add.setOnMouseClicked(e -> addDialog.show());
		
		addElement(customerPane, search);
		addElement(customerPane, update);
		addElement(carListTitle, add);
		
		Platform.runLater(() -> {
			try { addDialog = new CustomDialog(FXClient.lm.frame, "Edit Car", "MarketingDepartmentRepresentative/AddCar"); }
			catch (IOException e1) { e1.printStackTrace(); }
		}); 
		
		purchasePlanCombo.getItems().addAll("Exclusive", "Non Exclusive", "None");
		servicePlanCombo.getItems().addAll("Occasional Refuel", "Monthly Single Car Refuel", "Monthly Multiple Cars Refuel", "Monthly Fixed Single Car", "None");
		
		getStations();
	}

	/**
	 * This method fetches all relevant information about a certain customer in DB and places it in the right field
	 */
	private void getCustomer() throws IOException
	{
		customerID = customerIDTextField.getText();
		
		if(customerID.isEmpty())
		{
			validateError.setVisible(true);
		}
		else
		{
			FXClient.client .request("SELECT * FROM customers C, users U WHERE C.customerID = U.username AND C.customerID = '" + customerID + "'", Customer.class);

			FXClient.observable.addObserver((o, arg) ->
			{
				ArrayList<Customer> res = ((Response<Customer>) arg).result();

				if (res.size() > 0)
				{
					validateError.setVisible(false);
					
					customerInfo.setVisible(true);
					Customer customer = res.get(0);
					
					firstNameTextField.setText(customer.firstName);
					lastNameTextField.setText(customer.lastName);
					
					getCars(customerID);
					getPurchasePlan(customer.exclusivePurchasePlan);
					getServicePlan(customer.gasServicePlan);
					
					update.setDisable(false);
					update.setOpacity(1);
				}
				else  validateError.setVisible(true);
			});
		}
	}

	/**
	 * This method fetches all vehicles the customer owns from DB
	 */
	private void getCars(String customerID)
	{	
		try { FXClient.client.request("SELECT * FROM cars WHERE customerID = '" + customerID + "'", Car.class); }
		catch (IOException e) { e.printStackTrace(); }
		
		FXClient.observable.addObserver((o ,arg) ->
		{
			cars = ((Response<Car>)arg).result();
			
			Platform.runLater(() -> {
				
				carsBox.getChildren().remove(carList);
				
				carList = new CustomCarList(cars, 500, 280, e ->  {
					
					Platform.runLater(() -> { 
						carList.removeCar(carList.getSelected().carID);
						cars.remove(carList.getSelected());
						System.out.println(cars.size());
					});

					});
				
				carList.tp.setMaxWidth(400);
				carList.setMinHeight(200);
				
				addElement(carsBox, carList);
			});
		});
	}

	/**
	 * This method translates information about purchase plan, from Boolean to String
	 */
	private void getPurchasePlan(Integer plan)
	{
		Platform.runLater(() -> {
			if(plan == null) purchasePlanCombo.getSelectionModel().select("None");
			else if(plan == 0) purchasePlanCombo.getSelectionModel().select("Non Exclusive");
			else if(plan == 1) purchasePlanCombo.getSelectionModel().select("Exclusive");
		});
		
	}

	/**
	 * This method checks whether customer's service plan is relevant
	 */
	private void getServicePlan(String plan)
	{
		Platform.runLater(() -> {
			if(plan == null) servicePlanCombo.getSelectionModel().select("None");
			servicePlanCombo.getSelectionModel().select(plan);
		});
	}

	/**
	 * fetches all of the gas stations and place it in combo box
	 */
	private void getStations()
	{
		try { FXClient.client.request("SELECT name FROM gasStations", GasStation.class); }
		catch (IOException e) { e.printStackTrace(); }
		
		FXClient.observable.addObserver((o, arg) -> {
			ArrayList<GasStation> stations = ((Response<GasStation>)arg).result();
			
			stations.forEach(station -> {
				op1.getItems().addAll(station.name);
				op2.getItems().addAll(station.name);
				op3.getItems().addAll(station.name);
			});
			
		});
	}

	/**
	 * updates gathered information to tables
	 * @param customerID this is the primary key in table
	 */
	private void updateInformation(String customerID)
	{
		String firstName = firstNameTextField.getText();
		String lastName = lastNameTextField.getText();
		String purchasePlan = purchasePlanCombo.getSelectionModel().getSelectedItem();
		String servicePlan = servicePlanCombo.getSelectionModel().getSelectedItem();
		
		Integer p = purchasePlan == "None" ? null : purchasePlan == "Exclusive" ? 1 : 0;

		try
		{
			FXClient.client.request("UPDATE users SET firstName = '" + firstName + "', lastName = '" + lastName + "' WHERE username = '" + customerID + "'");
			FXClient.client.request("UPDATE customers SET exclusivePurchasePlan = " + p + ", gasServicePlan = '" + servicePlan + "' WHERE customerID = '" + customerID + "'");
			
			FXClient.client.request("DELETE FROM cars WHERE customerID = '" + customerID + "'");
			
			cars.forEach(car -> {
				try { FXClient.client.request("INSERT INTO cars (customerID, carID, fuelType) VALUES ('" + customerID + "', '" + car.carID + "', '" + car.fuelType + "')"); }
				catch (IOException e) { e.printStackTrace(); }
			});
		}
		catch (IOException e) { e.printStackTrace(); }
		
	}
}