package controllers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client_package.Client;
import client_package.FXClient;
import elements.CustomButton;
import entities.Car;
import entities.Customer;
import entities.Fuel;
import entities.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import server_package.Response;

import static core.UIHelper.*;

public class DalkanController implements Initializable {

	@FXML Pane dalkanPane;
	
	private ArrayList<Car> cars;
	private Customer customer;
	private ArrayList<Fuel> fuels;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		
		Platform.runLater(() -> {
			try { getDalkanUser(); }
			catch (IOException e) { e.printStackTrace(); }
		});
		
		
		CustomButton info = new CustomButton("Info", 30, 30);
		info.setOnMouseClicked(e -> getInfo());
		addElement(dalkanPane, info);
	}
	
	private void getDalkanUser() throws IOException
	{
		FXClient.client = new Client("localhost", 5550);
		
		FXClient.client.request("SELECT * FROM customers C, users U WHERE C.customerID = U.username AND username = 'dalkan'", Customer.class);
		FXClient.observable.addObserver((o, arg) -> customer = ((Response<Customer>)arg).result().get(0));
		
		FXClient.client.request("SELECT * FROM cars WHERE customerID = 'dalkan'", Car.class);
		FXClient.observable.addObserver((o, arg) -> cars = ((Response<Car>)arg).result());
		
		FXClient.client.request("SELECT * FROM fuels", Fuel.class);
		FXClient.observable.addObserver((o, arg) -> fuels = ((Response<Fuel>)arg).result());
	}

	private void getInfo()
	{
		System.out.println(customer.username);
		System.out.println(customer.firstName);
		System.out.println(customer.lastName);
		System.out.println(customer.userType);
		
		System.out.println(cars);
		System.out.println(fuels);
	}
}
