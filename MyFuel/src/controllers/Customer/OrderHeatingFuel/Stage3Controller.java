package controllers.Customer.OrderHeatingFuel;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import client_package.FXClient;
import core.Storage;
import core.TransactionManager;
import elements.CustomAnimation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Stage3Controller implements Initializable {

    @FXML private ImageView plant1, plant2;

	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		TransactionManager.order(	FXClient.user.username,
									(LocalDate) Storage.get("date"),
									(String) Storage.get("time"),
									(boolean) Storage.get("urgent"),
									(String) Storage.get("street"),
									(String) Storage.get("house"),
									(String) Storage.get("city"),
									(String) Storage.get("zipcode"),
									(float) Storage.get("price"),
									(String) Storage.get("amount"),
									(int) Storage.get("saleID"),
									(int) Storage.get("station"));
		
		new CustomAnimation(plant1, Duration.seconds(0.3)).fadeIn().scale(0.8, 1).play();
		new CustomAnimation(plant2, Duration.seconds(0.3)).fadeIn().scale(0.8, 1).delay(Duration.seconds(0.2)).play();
	}
}