package controllers.Customer.PurchaseFuel;

import client_package.FXClient;
import core.DiscountManager;
import core.GenericObserver;
import core.IStorable;
import core.Storage;
import core.TransactionManager;
import elements.CustomAnimation;
import elements.CustomButton;
import entities.*;
import enums.FuelType;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server_package.Response;

import static core.UIHelper.*;

public class Stage2Controller implements Initializable {

    @FXML private Pane orderPanePurchase, loadingBar;
    @FXML private HBox fuelProgress;
    @FXML private Pane detailsTitle;
    @FXML private BorderPane detailsPane, progressTextBar;
    @FXML private VBox orderBox;
    @FXML private Text totalValue, text1, text2, text3, customerName;
    @FXML private HBox startBox, checkmark, barBox;
    @FXML private ImageView man, plant1, plant2;
    
    private CustomButton startFueling, receipt;
    private final HashSet<PurchaseItem> items = new HashSet<>();
    private ArrayList<Fuel> fuel;
    private Integer amount, saleID;
    private ArrayList<FuelStock> fuelStock;
    private float fuelPrice, total = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	detailsPane.setOpacity(0);
    	text1.setOpacity(0); text2.setOpacity(0); text3.setOpacity(0);
    	checkmark.setOpacity(0);
    	barBox.setOpacity(0);
    	
        setupButtons();
        animate();
        Platform.runLater(this::fetchGasPrices);
    }

    /**
     * Gets the gas prices from the DB, needed for fuelStock data
     */
	private void fetchGasPrices() {

            try { FXClient.client.request("SELECT * FROM fuels WHERE type = '"+Storage.get("fuelType")+"'", Fuel.class); }
            catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                fuel = ((Response<Fuel>) arg).result();
                fetchFuelStock();
                Platform.runLater(() -> {
                    setBillSummary();
                });
            }
        });
    }

    /**
     * fetches the current fuelStock for the selected by user fuel type
     */
    private void fetchFuelStock() {
        try {
            FXClient.client.request("SELECT * FROM fuelStock WHERE type = '" + fuel.get(0).type + "' AND gasStationID = "+ Storage.get("stationID"), FuelStock.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                fuelStock = ((Response<FuelStock>) arg).result();
                finishTransactionEvents();
            }
        });

    }

    /**
     * creates an update the the corresponding tables in the DB and starts the refueling animation
     */
    private void finishTransactionEvents() {
        startFueling.setOnMousePressed(e -> {
            if(saleID == null){saleID = 0; }
            TransactionManager.refuel(FXClient.user.username, fuelPrice, amount, fuel.get(0), fuelStock.get(0), saleID, (Integer) Storage.get("stationID"));
            startFueling.setVisible(false);
            fuelAnimation();
        });
    }

/**
 * Responsible for calculating the final price based on fuel prices from the DB, and the user's preferred refueling method.
 */
    private void setBillSummary() {
        orderBox.getChildren().clear();

        customerName.setText("Thank you " + DiscountManager.customer.firstName + ", the fuel pump is ready.");
        
        amount = 		((String)Storage.get("limit")).isEmpty() ? 0 : Integer.parseInt((String) Storage.get("limit"));
        fuelPrice = 	fuel.get(0).price;
        
		float 		purchasePlanDiscount =	DiscountManager.getPurchasePlanDiscount();
		float 		servicePlanDiscount =	DiscountManager.getServicePlanDiscount();
		ActivesSale saleObject =			DiscountManager.getSaleDiscountObject();
        
        String limitType = (String) Storage.get("limitType");
        
        if(limitType.equals("sum"))
        {
        	if(amount != 0) addElement(orderBox, newItem(amount + " NIS", fuelPrice, false));
        	
        	float amountF = amount * 1f;
        	float totalF = 0;
        	
        	if(purchasePlanDiscount > 0) totalF += purchasePlanDiscount;
        	if(servicePlanDiscount > 0) totalF += servicePlanDiscount;
        	if(saleObject != null &&saleObject.discountPercent > 0) totalF += saleObject.discountPercent;
        	
        	total = roundNumber((amountF *  (1 + totalF)) / fuelPrice, 1);
        	totalValue.setText(roundNumber(total * 1.0f, 1) + " liters");
        }
        
        if(limitType.equals("liters"))
        {
        	if(amount != 0) addElement(orderBox, newItem(amount + " liters", fuelPrice, false));   	
        	total = roundNumber(amount * fuelPrice, 1);
        	
        	float totalDiscount = 0;
        	
        	if(purchasePlanDiscount > 0) totalDiscount += purchasePlanDiscount;
        	if(servicePlanDiscount > 0) totalDiscount += servicePlanDiscount;
        	if(saleObject != null && saleObject.discountPercent > 0) totalDiscount += saleObject.discountPercent;
        	
        	total *= 1 - totalDiscount;
        	
        	totalValue.setText(roundNumber(total, 1) + " NIS");
        }
        
        if(limitType.equals("fullTank"))
        {
        	amount = new Random().nextInt(50) + 1;
        	if(amount != 0) addElement(orderBox, newItem(amount + " liters", fuelPrice, false));
        	total = roundNumber(amount * fuelPrice, 1);
        	
        	float totalDiscount = 0;
        	
        	if(purchasePlanDiscount > 0) totalDiscount += purchasePlanDiscount;
        	if(servicePlanDiscount > 0) totalDiscount += servicePlanDiscount;
        	if(saleObject != null && saleObject.discountPercent > 0) totalDiscount += saleObject.discountPercent;
        	
        	total *= 1 - totalDiscount;
        	
        	totalValue.setText(roundNumber(total, 1) + " NIS");
        }
        
        if(purchasePlanDiscount > 0)
        	addElement(orderBox, newItem("Purchase Plan Discount", 1 - purchasePlanDiscount, false));
        
        if(servicePlanDiscount > 0)
        	addElement(orderBox, newItem(DiscountManager.customer.gasServicePlan + " Discount", 1 - servicePlanDiscount, false));
        
        if(saleObject != null && saleObject.discountPercent > 0)
        {
        	addElement(orderBox, newItem(saleObject.templateName, 1 - saleObject.discountPercent, false));
        	saleID = saleObject.saleID;
        }
    }

    private void setupButtons() {
        startFueling = new CustomButton("Start Refueling");
        startFueling.setSize("wide");
//        startFueling.setCustomStyle("button3");


        receipt = new CustomButton("Print Receipt");
        receipt.setSize("normal");
        receipt.setPosition(28, 562);
        receipt.setOnMouseClicked(e -> {
        	// TODO Send email to the customer
        	try { FXClient.lm.set("Welcome"); }
        	catch (IOException e1) { e1.printStackTrace(); }
        });
        receipt.setOpacity(0);
        addElement(startBox, startFueling);
        addElement(orderPanePurchase, receipt);
    }

    /**
     * creates an Item to be inserted into the final billing box, containing all the transaction's information
     * @param name the text to be shown
     * @param value the value of the text
     * @param discount discount if should be added
     * @return returns an Item object
     */
    private BorderPane newItem(String name, float value, boolean discount) {

        BorderPane item = new BorderPane();
        PurchaseItem i = new PurchaseItem(name, value, discount, FuelType.valueOf((String) Storage.get("fuelType")));
        items.remove(i);
        items.add(i);

        Text n = new Text(name);
        toggleClass(n, "orderItemName");
        Text v = Storage.get("limitType").equals("sum") ? new Text("/ " + value) : new Text("x " + value);
        toggleClass(v, "orderItemValue");

        item.setLeft(n);
        if (!discount) item.setRight(v);

        return item;
    }



    private void fuelAnimation()
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
			new CustomAnimation(detailsPane, Duration.seconds(0.6)).fadeIn().translate(0, -150).play();
			new CustomAnimation(receipt, Duration.seconds(0.3)).fadeIn().setClickable().play();
		});
		
		new CustomAnimation(barBox, Duration.seconds(0.3)).fadeIn().play();
		new CustomAnimation(customerName, Duration.seconds(0.3)).translate(0, -50).fadeOut().play();
		new CustomAnimation(text1, Duration.seconds(0.3)).fadeIn().play();
		new CustomAnimation(text2, Duration.seconds(0.3)).fadeIn().delay(Duration.seconds(3.5)).play();
		new CustomAnimation(text3, Duration.seconds(0.3)).fadeIn().delay(Duration.seconds(7)).play();
    }
    
    private void animate()
    {
		new CustomAnimation(man, Duration.seconds(0.3)).fadeIn().translate(-10, 0).play();
		new CustomAnimation(plant1, Duration.seconds(0.6)).fadeIn().translate(0, -20).scale(0.75, 1).play();
		new CustomAnimation(plant2, Duration.seconds(0.6)).fadeIn().translate(0,  -20).scale(0.75, 1).play();
    }
    
}

