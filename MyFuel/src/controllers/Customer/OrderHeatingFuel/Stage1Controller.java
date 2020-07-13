package controllers.Customer.OrderHeatingFuel;

import static core.UIHelper.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client_package.FXClient;
import core.DiscountManager;
import core.IStorable;
import core.Storage;
import elements.CustomAnimation;
import elements.CustomButton;
import elements.CustomCheckbox;
import entities.ActivesSale;
import entities.CreditCard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import server_package.Response;

public class Stage1Controller implements Initializable, IStorable, IHomeHeatingStage1Manager {

	@FXML private Pane orderPane;
    @FXML private HBox orderProgress;
    @FXML private DatePicker dates;
    @FXML private ComboBox<String> timesCombobox;
    @FXML private HBox validateError;
    @FXML private TextField streetField, houseField, cityField, zipcodeField;
	
    @FXML private ImageView man, table, laptop, plant, notif, documents, msg;
    
    private String _street, _house, _city, _zipcode, timeText;
    private CustomButton date, combo;
    private CustomCheckbox check;

	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		CustomButton next = new CustomButton("Next");
		next.setSize("normal");
		next.setPosition(orderPane.getPrefWidth() -  next.width - 30, orderPane.getPrefHeight() - next.height - 20);
		
		next.setOnMouseClicked(this::goToStage2);
		
		addElement(orderPane, next);
		date = new CustomButton("dd/mm/yyyy", 28, 340);
		date.setSize(170, 34);
		date.setCustomStyle("button2");
		addElement(orderPane, date);
		dates.setFocusTraversable(false);
		
		date.setOnMouseClicked(e -> { dates.show(); toggleClass(date, "emptyTextField", false); });
		dates.valueProperty().addListener((obs, oldValue, newValue) -> { date.setText(newValue.toString()); });
		
		combo = new CustomButton("Choose a time range", 207, 340);	
		combo.setSize(170, 34);
		combo.setCustomStyle("button2");
		combo.setOnMouseClicked(e -> { timesCombobox.show(); toggleClass(combo, "emptyTextField", false); });
		addElement(orderPane, combo);
		
		timesCombobox.setFocusTraversable(false);
		timesCombobox.getItems().addAll("08:00 - 12:00", "12:00 - 16:00", "16:00 - 20:00");
		timesCombobox.valueProperty().addListener((obs, oldValue, newValue) -> { combo.setText(newValue); });
		
		check = new CustomCheckbox("Urgent", "Your order will be delivered within the next 6 hours" , 30, 390);
		check.setEvents(e -> {
			if(!check.checked()) { dates.setValue(LocalDate.now()); combo.setText(" - - - "); }
			else combo.setText("Choose a time range");
			date.setDisable(!check.checked());
			dates.setDisable(!check.checked());
			combo.setDisable(!check.checked());
			timesCombobox.setDisable(!check.checked());
		});
		addElement(orderPane, check);
		
		disablePastDays(dates);
	
		removeStyleOnFocus("emptyTextField", streetField, houseField, cityField, zipcodeField);
		
		fetchCC(FXClient.user.username);
		prefill();
		setupDiscountManager();
		animate();
	}

	/**
	 * This method reacts to a mouse event, after validation, stores info and moves to next stage
	 * @param event mouse click
	 */
	private void goToStage2(MouseEvent event)
	{
			if(isInputValid(streetField.getText(), houseField.getText(), cityField.getText(), zipcodeField.getText(), dates.getValue() != null, timesCombobox.getSelectionModel().getSelectedIndex() != -1, check.checked()))
				try { store(); FXClient.lm.set("Customer/OrderHeatingFuel/Stage2", this); }
				catch (Exception ex) { ex.printStackTrace(); }
			else validateError.setVisible(true);
	}

	/**
	 * checks if all fields are filled
	 * @return boolean value if passes validation
	 */
	@Override
	public boolean isInputValid(String street, String house, String city, String zipcode, boolean dateSelected, boolean timeSelected, boolean checkbox)
	{
		toggleClass(date, "emptyTextField", !dateSelected);
		
		_street = street;
		_house = house;
		_city = city;
		_zipcode = zipcode;
		
		if(timeSelected) timeText = timesCombobox.getSelectionModel().getSelectedItem();
		if(check.checked()) timeText = "Urgent";
		toggleClass(combo, "emptyTextField", !timeSelected);
		
		toggleClass(streetField, "emptyTextField", street.isEmpty());
		toggleClass(houseField, "emptyTextField", house.isEmpty());
		toggleClass(cityField, "emptyTextField", city.isEmpty());
		toggleClass(zipcodeField, "emptyTextField", zipcode.isEmpty());
		
		return validate(street, house, city, zipcode, dateSelected, timeSelected, checkbox);
	}

	/**
	 * This method implements the IStorable interface that allows to store in a hashmap information and pass it between classes
	 */
	@Override
	public void store() {
		Storage.add("street", _street);
		Storage.add("house", _house);
		Storage.add("city", _city);
		Storage.add("zipcode", _zipcode);
		Storage.add("time", timeText);
		Storage.add("date", dates.getValue());
		Storage.add("urgent", check.checked());
	}

	/**
	 * This method gets existing information in storage and places it in the correct field
	 */
	private void prefill()
	{
		if(Storage.get("street") != null)	streetField.setText((String) Storage.get("street"));
		if(Storage.get("house") != null)	houseField.setText((String) Storage.get("house"));
		if(Storage.get("city") != null)		cityField.setText((String) Storage.get("city"));
		if(Storage.get("zipcode") != null)	zipcodeField.setText((String) Storage.get("zipcode"));
		if(Storage.get("urgent") != null)	check.setChecked((boolean) Storage.get("urgent"));
		if(Storage.get("date") != null)
		{
			dates.setValue((LocalDate) Storage.get("date"));
			date.setText(dates.getValue().toString());
		}
		if(Storage.get("time") != null)		timesCombobox.getSelectionModel().select((String) Storage.get("time"));
		
		Storage.clear();
	}

	/**
	 * method to schedule entrance of image views
	 */
	private void animate()
	{
		new CustomAnimation(man, Duration.seconds(0.5)).fadeIn().translate(10, 0).delay(Duration.seconds(0.2)).play();
		new CustomAnimation(laptop, Duration.seconds(0.5)).fadeIn().translate(-10, 0).delay(Duration.seconds(0.2)).play();
		new CustomAnimation(msg, Duration.seconds(0.5)).fadeIn().translate(0, -20).delay(Duration.seconds(0.4)).play();
		new CustomAnimation(notif, Duration.seconds(0.5)).fadeIn().translate(0, -20).delay(Duration.seconds(0.5)).play();
		new CustomAnimation(documents, Duration.seconds(0.5)).fadeIn().translate(0, -20).delay(Duration.seconds(0.6)).play();
	}

	/**
	 * method to fetch credit card from DB
	 */
	@Override
	public void fetchCC(String customerID)
	{
		try { FXClient.client.request("SELECT customerID, cardNumber, cvv, expYear, expMonth, firstName, lastName FROM creditCards CC, users U WHERE CC.customerID = U.username AND CC.customerID = '" + customerID +"'", CreditCard.class); }
		catch (IOException e) { e.printStackTrace(); }
		
		
		FXClient.observable.addObserver((o, arg) -> {
			ArrayList<CreditCard> temp = ((Response<CreditCard>)arg).result();
			if(temp.size() > 0) Storage.add("cc", temp.get(0));
		});
	}

	/**
	 * checks if there are any relevant active sales at the time
	 */
	@Override
	public void setupDiscountManager()
	{
		try { FXClient.client.request("SELECT * FROM activeSales A, saleTemplates T WHERE A.templateID = T.templateID", ActivesSale.class); }
		catch (IOException e) { e.printStackTrace(); }
		
		FXClient.observable.addObserver((o, arg) -> { DiscountManager.sales = ((Response<ActivesSale>)arg).result(); });
	}
}
