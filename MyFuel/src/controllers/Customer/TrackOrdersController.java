package controllers.Customer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;


import client_package.FXClient;
import elements.CustomAnimation;
import entities.Order;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server_package.Response;

import static core.UIHelper.*;

public class TrackOrdersController implements Initializable {

    @FXML private Pane trackOrdersPane;
    @FXML private FlowPane flowPane;
    @FXML private ImageView books1, books2, books3, woman;
	
    private ArrayList<Order> orders;

	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		Platform.runLater(this::fetchOrders);
		animate();
	}

	/**
	 * fetch data from orders in DB
	 */
	private void fetchOrders()
	{
		try { FXClient.client.request("SELECT * FROM orders WHERE customerID = '"+ FXClient.user.username + "' ORDER BY orderID DESC", Order.class); }
		catch (IOException e) { e.printStackTrace(); }
		
		FXClient.observable.addObserver((obs, arg) -> {
			orders = ((Response<Order>)arg).result();
			
			if(orders.size() > 0) Platform.runLater(this::buildOrders);
			
			
		});
	}

	/**
	 * Build a visual element to display orders
	 */
	private void buildOrders()
	{
		for(Order order : orders)
		{
			VBox v = new VBox();
			v.setSpacing(6);
			
			HBox titleBox = new HBox();
			titleBox.setAlignment(Pos.CENTER_LEFT);
			titleBox.setPrefWidth(150);
			titleBox.setSpacing(12);

			LocalDate deliveryDate = ((java.sql.Date) order.deliveryDate).toLocalDate();
			boolean received = deliveryDate.compareTo(LocalDate.now()) < 0;
			Image i = new Image(getClass().getResource("/files/img/icons/trackorders/" + (received ? "received" : "shipped") + ".png").toExternalForm());
			ImageView img = new ImageView(i);
			
			HBox statusBox = new HBox();
			statusBox.setAlignment(Pos.CENTER);
			HBox statusPane = new HBox(new Text(received ? "Received" : "Shipped"));
			
			HBox deliveryDateBox = new HBox();
			deliveryDateBox.setAlignment(Pos.CENTER_LEFT);
			deliveryDateBox.setSpacing(12);
			Image di = new Image(getClass().getResource("/files/img/icons/trackorders/date.png").toExternalForm());
			ImageView dateImage = new ImageView(di);
			addElement(deliveryDateBox, dateImage);
			addElement(deliveryDateBox, new Text(order.deliveryDate.toString()));
				
			HBox deliveryTimeBox = new HBox();
			deliveryTimeBox.setAlignment(Pos.CENTER_LEFT);
			deliveryTimeBox.setSpacing(12);
			Image ti = new Image(getClass().getResource("/files/img/icons/trackorders/time.png").toExternalForm());
			ImageView timeImage = new ImageView(ti);
			addElement(deliveryTimeBox, timeImage);
			addElement(deliveryTimeBox, new Text(order.deliveryTime));
			
			HBox amountBox = new HBox();
			amountBox.setAlignment(Pos.CENTER_LEFT);
			amountBox.setSpacing(12);
			Image ai = new Image(getClass().getResource("/files/img/icons/trackorders/amount.png").toExternalForm());
			ImageView amountImage = new ImageView(ai);
			addElement(amountBox, amountImage);
			addElement(amountBox, new Text(order.amount + " liters"));
			
			toggleClass(statusPane, received ? "received" : "shipped");
			toggleClass(statusBox,"status");
			toggleClass(titleBox, "title");
			toggleClass(v, "order");
			
			VBox.setMargin(titleBox, new Insets(0, 0, 12, 0));
			VBox.setMargin(statusBox, new Insets(12, 0, 0, 0));
			
			addElement(titleBox, img);
			addElement(titleBox, new Text("Order ID #" + order.orderID));
			addElement(statusBox, statusPane);
			
			addElement(v, titleBox);
			addElement(v, deliveryDateBox);
			addElement(v, deliveryTimeBox);
			addElement(v, amountBox);
			addElement(v, statusBox);

			addElement(flowPane, v);
			
			FlowPane.setMargin(v, new Insets(0, 12, 0, 0));
		}
	}

	/**
	 * method to schedule entrance of image views
	 */
	private void animate() 
	{
		new CustomAnimation(books1, Duration.seconds(0.3)).fadeIn().translate(10, 0).play();
		new CustomAnimation(books2, Duration.seconds(0.3)).delay(Duration.seconds(0.1)).fadeIn().translate(-10, 0).play();
		new CustomAnimation(books3, Duration.seconds(0.3)).delay(Duration.seconds(0.2)).fadeIn().translate(10, 0).play();
		new CustomAnimation(woman, Duration.seconds(0.5)).delay(Duration.seconds(0.3)).fadeIn().play();
    }
}
