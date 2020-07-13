package controllers.Dalkan;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import client_package.FXClient;
import core.DiscountManager;
import elements.CustomAnimation;
import elements.CustomButton;
import entities.ActivesSale;
import entities.Car;
import entities.Customer;
import entities.Fuel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static core.UIHelper.*;

public class DalkanStage2Controller implements Initializable {

    @FXML private Pane dalkanPane2, loadingBar;
    @FXML private HBox barBox, receipt, checkmark;
    @FXML private Text text1, text2, text3;
    @FXML private BorderPane progressTextBar;
    @FXML private ImageView man, plant1, plant2;
    @FXML private VBox receiptText, receiptValue;
	
    private final Customer customer = DiscountManager.customer;
    private final Car car = DiscountManager.specificCar;

	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		CustomButton back = new CustomButton("Back to login", 12, 12);
		back.setOnMouseClicked(e -> backToLogin());
		back.setSize("normal");
		back.setCustomStyle("button2");
		addElement(dalkanPane2, back);
		
		checkmark.setOpacity(0);
		receipt.setOpacity(0);
		
		buildReceipt();
		animate();
	}

	/**
	 * method to schedule entrance of image views
	 */
	private void animate()
	{
		KeyValue widthValue = 	new KeyValue(loadingBar.prefWidthProperty(), 600, Interpolator.EASE_BOTH);
		KeyFrame frame = 		new KeyFrame(Duration.seconds(7), widthValue);
		Timeline timeline = 	new Timeline(frame);
		timeline.setDelay(Duration.seconds(1));
		timeline.play();
		
		timeline.setOnFinished(e -> {
			new CustomAnimation(barBox, Duration.seconds(0.5)).translate(0, -150).play();
			new CustomAnimation(progressTextBar, Duration.seconds(0.5)).translate(0, -150).play();
			new CustomAnimation(checkmark, Duration.seconds(0.6)).fadeIn().delay(Duration.seconds(0.5)).play();
			new CustomAnimation(receipt, Duration.seconds(0.5)).fadeIn().translate(0, -80).play();
		});
		
		new CustomAnimation(text1, Duration.seconds(0.3)).fadeIn().play();
		new CustomAnimation(text2, Duration.seconds(0.3)).fadeIn().delay(Duration.seconds(3.5)).play();
		new CustomAnimation(text3, Duration.seconds(0.3)).fadeIn().delay(Duration.seconds(7)).play();
		
		new CustomAnimation(man, Duration.seconds(0.3)).fadeIn().translate(-10, 0).play();
		new CustomAnimation(plant1, Duration.seconds(0.6)).fadeIn().translate(0, -20).scale(0.75, 1).play();
		new CustomAnimation(plant2, Duration.seconds(0.6)).fadeIn().translate(0,  -20).scale(0.75, 1).play();
	}

	/**
	 * method to take all information, calculate check and show it
	 */
	private void buildReceipt()
	{
		
		float purchasePlanDiscount = DiscountManager.getPurchasePlanDiscount();
		float serivcePlanDiscount = DiscountManager.getServicePlanDiscount();
		float saleDiscount = DiscountManager.getSaleDiscount();
		ActivesSale saleObject = DiscountManager.getSaleDiscountObject();
		
		float fuelAmount = new Random().nextFloat() * 50;
		float fuelPrice = 0, total = 0;
		
		for(Fuel f : DiscountManager.fuels) if(car.fuelType.compareTo(f.type) == 0) fuelPrice = f.price;
		
		total += fuelAmount * fuelPrice;
		
		addElement(receiptText, new Text(car.fuelType));
		addElement(receiptValue, new Text(roundNumber(fuelAmount, 1) + " x " + fuelPrice + " NIS"));
		
		if(purchasePlanDiscount > 0)
		{
			addElement(receiptText, new Text("Purchase Plan Discount"));
			addElement(receiptValue, new Text("-" + purchasePlanDiscount * 100 + "%"));
			
			total *= 1 - purchasePlanDiscount;
		}
		
		if(serivcePlanDiscount > 0)
		{
			addElement(receiptText, new Text(customer.gasServicePlan + " Discount"));
			addElement(receiptValue, new Text("-" + serivcePlanDiscount * 100 + "%"));
			
			total *= 1 - serivcePlanDiscount;
		}
		
		if(saleDiscount > 0)
		{
			addElement(receiptText, new Text(saleObject.templateName));
			addElement(receiptValue, new Text("-" + saleDiscount * 100 + "%"));
			
			total *= 1 - saleDiscount;
		}
		
		addElement(receiptText, new Text());
		addElement(receiptValue, new Text());
		
		Text totalText = new Text("Total");
		toggleClass(totalText, "totalText");
		
		Text totalValue = new Text(roundNumber(total, 1) + " NIS");
		toggleClass(totalValue, "totalValue");
				
		addElement(receiptText, totalText);
		addElement(receiptValue, totalValue);
	}

	/**
	 * Go back to log in page
	 */
	private void backToLogin()
	{
		try { FXClient.client.closeConnection(); FXClient.lm.load("Login"); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
}
