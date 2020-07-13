package controllers.Dalkan;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.ResourceBundle;

import client_package.Client;
import client_package.FXClient;
import core.DiscountManager;
import elements.CustomAnimation;
import elements.CustomButton;
import entities.ActivesSale;
import entities.Car;
import entities.Customer;
import entities.Fuel;
import entities.PurchasePlan;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server_package.Response;

import static core.UIHelper.*;

public class DalkanStage1Controller implements Initializable {

	@FXML private Pane dalkanPane;
	@FXML private Text name, ready;
	@FXML private HBox readyBox, nextBox;
	@FXML private ImageView man, woman, plant1, plant2, plant3;
	
	private Customer customer;
	private CustomButton next;

	private ArrayList<Car> cars;
	private ArrayList<Fuel> fuels;
	private ArrayList<PurchasePlan> plan;
	private ArrayList<ActivesSale> sales;

	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		try { setupDalkan(); }
		catch (IOException e) { e.printStackTrace(); }
		
		CustomButton back = new CustomButton("Back to login", 12, 12);
		back.setOnMouseClicked(e -> backToLogin());
		back.setSize("normal");
		back.setCustomStyle("button2");
		addElement(dalkanPane, back);
		
		next = new CustomButton("Fuel");
		next.setSize("wide");
		next.setOnMouseClicked(e -> {
			try { FXClient.lm.load("Dalkan/DalkanStage2"); }
			catch (IOException e1) { e1.printStackTrace(); }
		});
		addElement(nextBox, next);
		
		animation();
	}

	/**
	 * fetches all information needed for purchasing fuel
	 */
	private void setupDalkan() throws IOException
	{
		FXClient.client = new Client("localhost", 5550);
		
		FXClient.client.request("SELECT * FROM customers C, users U WHERE C.customerID = U.username AND username = 'dalkan'", Customer.class);
		FXClient.observable.addObserver((o, arg) -> { customer = ((Response<Customer>)arg).result().get(0); setCustomer(); });
		
		FXClient.client.request("SELECT * FROM cars WHERE customerID = 'dalkan'", Car.class);
		FXClient.observable.addObserver((o, arg) -> { cars = ((Response<Car>)arg).result(); setCars(); });
		
		FXClient.client.request("SELECT * FROM fuels", Fuel.class);
		FXClient.observable.addObserver((o, arg) -> { fuels = ((Response<Fuel>)arg).result(); setFuels(); });
		
		FXClient.client.request("SELECT * FROM purchasePlans WHERE customerID = 'dalkan'", PurchasePlan.class);
		FXClient.observable.addObserver((o, arg) -> { plan = ((Response<PurchasePlan>)arg).result(); setPurchasePlan(); });
		
		FXClient.client.request("SELECT * FROM activeSales A, saleTemplates T WHERE A.templateID = T.templateID", ActivesSale.class);
		FXClient.observable.addObserver((o, arg) -> { sales = ((Response<ActivesSale>)arg).result(); setActiveSales(); });
	}

	/**
	 * go back for main login page
	 */
	private void backToLogin()
	{
		try { FXClient.client.closeConnection(); FXClient.lm.load("Login"); }
		catch (IOException e) { e.printStackTrace(); }
	}

	/**
	 * initialises information about customer
	 */
	private void setCustomer() { name.setText("Hi " + customer.firstName + "!"); DiscountManager.customer = customer; }

	/**
	 * initialises information about fuels
	 */
	private void setFuels() { DiscountManager.fuels = fuels; }

	/**
	 * initialises information about purchase plans
	 */
	private void setPurchasePlan() { DiscountManager.plan = plan; }

	/**
	 * initialises information about active sales
	 */
	private void setActiveSales() { DiscountManager.sales = sales; }

	/**
	 * chooses car to refuel
	 */
	private void setCars()
	{
		Car randomCar = 				cars.get(new Random().nextInt(cars.size()));
		DiscountManager.specificCar =	randomCar;
		DiscountManager.customerCars = 	cars;
		
		ready.setText(randomCar.fuelType + " fuel pump #"+ (new Random().nextInt(5) + 1) +" is now ready for vehicle #" + randomCar.carID);
	}

	/**
	 * method to schedule entrance of image views
	 */
	private void animation()
	{
		new CustomAnimation(name, Duration.seconds(0.2)).fadeIn().delay(Duration.seconds(0.2)).play();
		new CustomAnimation(readyBox, Duration.seconds(0.6)).fadeIn().delay(Duration.seconds(0.6)).play();
		new CustomAnimation(next, Duration.seconds(0.6)).fadeIn().delay(Duration.seconds(1.2)).setClickable().play();

		new CustomAnimation(woman, Duration.seconds(0.5)).fadeIn().translate(-20, 0).play();
		new CustomAnimation(man, Duration.seconds(0.7)).fadeIn().translate(20, 0).play();
		new CustomAnimation(plant1, Duration.seconds(0.5)).fadeIn().delay(Duration.seconds(0.3)).play();
		new CustomAnimation(plant2, Duration.seconds(0.5)).fadeIn().delay(Duration.seconds(0.3)).play();
		new CustomAnimation(plant3, Duration.seconds(0.5)).fadeIn().delay(Duration.seconds(0.5)).play();
		
	}
}
