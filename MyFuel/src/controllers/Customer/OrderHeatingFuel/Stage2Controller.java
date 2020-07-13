package controllers.Customer.OrderHeatingFuel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.ResourceBundle;

import client_package.FXClient;
import core.DiscountManager;
import core.IStorable;
import core.Storage;
import elements.CustomAnimation;
import elements.CustomButton;
import elements.CustomTooltip;
import entities.ActivesSale;
import entities.CreditCard;
import entities.Fuel;
import entities.FuelStock;
import entities.PurchaseItem;
import enums.FuelType;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server_package.Response;

import static core.UIHelper.*;

public class Stage2Controller implements Initializable, IStorable, IHomeHeatingStage2Manager {

    @FXML private Pane orderPane;
    @FXML private HBox orderProgress;
    @FXML private HBox validateError, outOfStockError;
    @FXML private Pane detailsTitle;
    @FXML private BorderPane detailsPane;
    @FXML private VBox orderBox;
    @FXML private Text totalValue;

    @FXML private TextField amountField;
    @FXML private TextField fullnameField;
    @FXML private TextField cardNumberField;
    @FXML private ComboBox<String> monthField;
    @FXML private TextField cvvField;
    @FXML private ComboBox<String> yearField;    
    @FXML private ImageView man;

    private float price, total;
    private final HashSet<PurchaseItem> items = new HashSet<PurchaseItem>();
    private CustomButton monthButton, yearButton;
    
    private int amountNum;
    private int stock, gasStationID;
    private final int minimum = 10;
    private Random rand = new Random();
    private static String _fullName, _cardNumber, _month, _year, _cvv, _amount;

	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		gasStationID = rand.nextInt(6)+1;
		CustomButton placeOrder = new CustomButton("Place Order");
		placeOrder.setSize("normal");
		placeOrder.setPosition(orderPane.getPrefWidth() -  placeOrder.width - 30, orderPane.getPrefHeight() - placeOrder.height - 20);
		placeOrder.setCustomStyle("button3");
		placeOrder.setOnMouseClicked(this::goToStage3);
		
		CustomButton back = new CustomButton("Back");
		back.setSize("small");
		back.setPosition(orderPane.getPrefWidth() -  3 * back.width - 50, orderPane.getPrefHeight() - back.height - 20);
		back.setOnMouseClicked(this::goToStage1);
		
		addElement(orderPane, placeOrder);
		addElement(orderPane, back);
		
		yearButton = new CustomButton("YYYY",339, 340);
		yearButton.setCustomStyle("button2");
		yearButton.setSize(91,30);
		yearButton.setOnMouseClicked(e -> { yearField.show(); });
		
		monthButton = new CustomButton("MM", 238, 340);
		monthButton.setCustomStyle("button2");
		monthButton.setSize(91,30);
		monthButton.setOnMouseClicked(e -> { monthField.show(); });
		
		addElement(orderPane, yearButton);
		addElement(orderPane, monthButton);
				
		setAmountFieldEvents();
		numbersOnly(cardNumberField, 16);
		numbersOnly(cvvField, 3);
		
		CustomTooltip cvvTooltip = new CustomTooltip("?", "CVV is the last three digits on the back of your credit card.", 320, 398);
		toggleClass(cvvTooltip, "tooltip2");
		addElement(orderPane, cvvTooltip);
		
		removeStyleOnFocus("emptyTextField", amountField, fullnameField, cardNumberField, monthField, cvvField, yearField);
		
		monthField.getItems().addAll("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
		monthField.valueProperty().addListener((obs, oldValue, newValue) -> { monthButton.setText(newValue); });
		
		yearField.getItems().addAll("2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027");
		yearField.valueProperty().addListener((obs, oldValue, newValue) -> { yearButton.setText(newValue); }); 
		
		animate();
		
		if(Storage.get("cc") != null)
		{
			CreditCard cc = (CreditCard) Storage.get("cc");
			fullnameField.setText(cc.firstName + " " + cc.lastName);
			cardNumberField.setText(cc.cardNumber);
			cvvField.setText(cc.cvv);
			yearField.setValue(cc.expYear);
			monthField.setValue(cc.expMonth);
		}
		
		Platform.runLater(this::fetchPrice);
		Platform.runLater(this::fetchStock);
	}

	/**
	 * method to set amount of gas values on screen
	 */
	private void setAmountFieldEvents()
	{
		amountField.textProperty().addListener((obs, oldVal, newVal) -> {
			
			amountField.setText(numbersOnly(newVal, 4));
			
			ActivesSale sale = DiscountManager.getSaleDiscountObject();
			
			orderBox.getChildren().clear();
			
			int amount = amountField.getText().isEmpty() ? 0 : Integer.parseInt(amountField.getText());
			
			if(amount != 0) addElement(orderBox, newItem(amount + " liters", price, false));
			
			total = amount * price; // Initial cost
			
			if((boolean) Storage.map.get("urgent"))
			{
				addElement(orderBox, newItem("2% Urgency fee", 0, true));
				total += amount * 0.02;
			}
			
			if(amount >= 600 && amount < 800)
			{
				addElement(orderBox, newItem("3% 600-800 liter Discount", 0, true));
				total -= amount * 0.03;
			}
			else if(amount >= 800)
			{
				addElement(orderBox, newItem("4% 800+ liter discount", 0, true));
				total -= amount * 0.04;
			}
			
			if(sale != null)
			{
				if(sale.discountPercent > 0)
				{
					Storage.add("saleID", DiscountManager.getSaleDiscountObject().saleID);
					addElement(orderBox, newItem(sale.discountPercent + "% " + sale.templateName + " discount", 0 , true));
					total *= 1 - sale.discountPercent;
				}
			}
			else Storage.add("saleID", 0);
			
			totalValue.setText(roundNumber(total, 1) + " ₪");
		});
	}

	/**
	 * method to set amount of gas values on screen
	 * @param name
	 * @param value price
	 * @param discount if there's a discount
	 * @return returns a visual border pane with all the right purchase information
	 */
	private BorderPane newItem(String name, float value, boolean discount)
	{
		BorderPane item = new BorderPane();
		PurchaseItem i = new PurchaseItem(name, value, discount, FuelType.HeatingFuel);
		items.remove(i);
		items.add(i);
		
		Text n = new Text(name);
		toggleClass(n, "orderItemName");
		Text v = new Text("x " + value);
		toggleClass(v, "orderItemValue");
		
		item.setLeft(n);
		if(!discount) item.setRight(v);
		
		return item;
	}

	/**
	 * This method implements the IStorable interface that allows to store in a hashmap information and pass it between classes
	 */
	@Override
	public void store()
	{
		Storage.add("amount", _amount);
		Storage.add("price", price);
		Storage.add("station",gasStationID);
	}

	/**
	 * checks if all fields are filled
	 * @return boolean value if passes validation
	 */
	public boolean isInputValid(String amount, int minimum, String fullname, String cardnumber, String year, String cvv, String month, boolean yearSelected, boolean monthSelected, int amountNum)
	{
		
		_amount = amount;
		_fullName = fullname;
		_cardNumber = cardnumber;
		_year = year;
		_month = month;
		_cvv = cvv;
		
		
		toggleClass(yearButton, "emptyTextField", !yearSelected);
		toggleClass(monthButton, "emptyTextField", !monthSelected);
		toggleClass(fullnameField, "emptyTextField", fullname.isEmpty());
		toggleClass(amountField, "emptyTextField", amount.isEmpty() || Integer.parseInt(amount) < minimum);
		toggleClass(cardNumberField, "emptyTextField", cardnumber.length() < 9);
		toggleClass(cvvField, "emptyTextField", cvv.length() < 3);
		
		return validate(amount,amountNum,minimum,cardnumber,cvv,fullname,yearSelected,monthSelected);
	}

	/**
	 * method to schedule entrance of image views
	 */
	private void animate()
	{
		new CustomAnimation(man, Duration.seconds(1)).fadeIn().translate(20, 0).play();
	}

	/**
	 * go back to previous stage
	 * @param event mouse click
	 */
	private void goToStage1(MouseEvent event)
	{
		try { FXClient.lm.set("Customer/OrderHeatingFuel/Stage1", this); }
		catch (IOException e1) { e1.printStackTrace(); }
	}

	/**
	 * next to future stage after validation
	 * @param event mouse click
	 */
	private void goToStage3(MouseEvent event)
	{
		_amount = amountField.getText();
		amountNum = _amount.isEmpty() ? 0 : Integer.parseInt(_amount);
		String year = yearField.getSelectionModel().getSelectedItem();
		String month = monthField.getSelectionModel().getSelectedItem();
		boolean yearSelected = yearField.getValue() != null;
		boolean monthSelected = monthField.getValue() != null;
		
		if(isInputValid(amountField.getText(),minimum,fullnameField.getText(),cardNumberField.getText(),year,cvvField.getText(),month,yearSelected,monthSelected,amountNum) && amountNum < stock)
		{
			try { store(); FXClient.lm.set("Customer/OrderHeatingFuel/Stage3", this); }
			catch (IOException e1) { e1.printStackTrace(); }
		}
		else
		{
			// Invalid input
            validateError.setVisible(!isInputValid(amountField.getText(),minimum,fullnameField.getText(),cardNumberField.getText(),year,cvvField.getText(),month,yearSelected,monthSelected,amountNum) && amountNum < stock);
			// Out of stock
            System.out.println(amountNum);
            outOfStockError.setVisible(amountNum > stock);
		}
	}

	/**
	 * fetch credit card from DB
	 */
	@Override
	public void fetchPrice()
	{
		try { FXClient.client.request("SELECT price FROM fuels WHERE Type = 'HeatingFuel'", Fuel.class); }
		catch (IOException e) { e.printStackTrace(); }
		
		FXClient.observable.addObserver((o, arg) -> { price = ((Response<Fuel>)arg).result().get(0).price; });
	}

	/**
	 * fetch stock from DB
	 */
	@Override
	public void fetchStock()
	{
		try { FXClient.client.request("SELECT * FROM fuelStock WHERE type = 'HeatingFuel' AND gasStationID = " + gasStationID, FuelStock.class); }
		catch (IOException e) { e.printStackTrace(); }
		
		FXClient.observable.addObserver((o, arg) -> { stock = ((Response<FuelStock>)arg).result().get(0).amount; System.out.println(stock); });
	}
}