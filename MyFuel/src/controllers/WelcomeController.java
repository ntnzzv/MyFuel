package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import client_package.FXClient;
import core.GenericObserver;
import core.UIHelper;
import elements.CustomAnimation;
import entities.DiscountTemplate;
import entities.Fuel;
import entities.RequestTariff;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server_package.Response;

public class WelcomeController implements Initializable {

    @FXML private Text welcomeTitle;
    @FXML private Pane welcomePane;
    @FXML private HBox welcomePricing;
    @FXML private HBox WelcomeNews;

    
    @FXML private ImageView man, cloud1, cloud2, mountain, bg;
    
    ArrayList<Fuel> fuels;
    ArrayList<DiscountTemplate> Dis;
    ArrayList<RequestTariff> req;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		welcomeTitle.setText("Welcome back, " + FXClient.user.firstName);
		welcomePricing.setSpacing(12);
		WelcomeNews.setSpacing(12);

		
		try { FXClient.client.request("SELECT * FROM fuels", Fuel.class); }
		catch (IOException e) { e.printStackTrace(); }
		
		FXClient.observable.addObserver(new GenericObserver() {

			@Override
			public void update(Observable o, Object arg) {
				fuels = ((Response<Fuel>)arg).result();
				Platform.runLater(() -> {
					for(Fuel f : fuels)
					{
						addPriceItem(f);
					}
				});
			}});
		
		if(FXClient.user.userType.equals("CEO")) {
			try {FXClient.client.request("SELECT * FROM fuelPriceRequest",RequestTariff.class);}
			catch(IOException e) {e.printStackTrace();}
			FXClient.observable.addObserver(new GenericObserver() {
				@Override
				public void update(Observable o, Object arg) {
					req = ((Response<RequestTariff>)arg).result();
					Platform.runLater(() -> {
					for(RequestTariff R : req)
					{
						if(R.getStatus().equals("Pending")) {
							addRequest(R);
						}
					}
				 });}});}
		
		if(FXClient.user.userType.equals("Marketing Manager")) {
			try {FXClient.client.request("SELECT * FROM fuelPriceRequest",RequestTariff.class);}
			catch(IOException e) {e.printStackTrace();}
			FXClient.observable.addObserver(new GenericObserver() {
				@Override
				public void update(Observable o, Object arg) {
					req = ((Response<RequestTariff>)arg).result();
					Platform.runLater(() -> {
					for(RequestTariff R : req)
					{
						if(!(R.getStatus().equals("Pending"))) {
							addRequest(R);
						}
					}
				 });}});
		}
		animate();
	}
	
	
	private void addPriceItem(Fuel fuel)
	{
		HBox h = new HBox();
		VBox v = new VBox();

		Text t = new Text(fuel.name);
		Text p = new Text(fuel.getPrice());
		
		Image i = new Image(getClass().getResource("/files/img/icons/fueltypes/" + fuel.type + ".png").toExternalForm());
		ImageView img = new ImageView(i);
		img.setFitHeight(24);
		img.setFitWidth(24);
		
		h.setPrefWidth(104);
		h.setAlignment(Pos.CENTER);
		v.setAlignment(Pos.CENTER);
		
		h.setId(fuel.type);
		
		v.setSpacing(12);
		
		UIHelper.toggleClass(h, "priceItem");
		UIHelper.toggleClass(t, "priceTitle");
		UIHelper.toggleClass(p, "priceValue");
		
		UIHelper.addElement(v, img);
		UIHelper.addElement(v, t);
		UIHelper.addElement(v, p);
		UIHelper.addElement(h, v);
		UIHelper.addElement(welcomePricing, h);
	}
	
	private void addRequest(RequestTariff Request) {
		HBox h = new HBox();
		VBox v = new VBox();

		Text t = new Text(Request.getFuelType());
		Text p = new Text(Request.getStatus());
		Text q= new Text(Request.getNewPrice());
		
		Image i = new Image(getClass().getResource("/files/img/icons/fueltypes/" + Request.fuelType + ".png").toExternalForm());
		ImageView img = new ImageView(i);
		img.setFitHeight(24);
		img.setFitWidth(24);
		
		h.setPrefWidth(104);
		h.setAlignment(Pos.CENTER);
		v.setAlignment(Pos.CENTER);
		
		h.setId(Request.getID());
		
		v.setSpacing(8);

		UIHelper.toggleClass(h, "priceItem");
		UIHelper.toggleClass(t, "priceTitle");
		UIHelper.toggleClass(q, "priceValue");
		UIHelper.toggleClass(p, "priceTitle");
		
		UIHelper.addElement(v, img);
		UIHelper.addElement(v, t);
		UIHelper.addElement(v, q);
		UIHelper.addElement(v, p);
		UIHelper.addElement(h, v);
		UIHelper.addElement(WelcomeNews, h);
	}

	private void animate()
	{
		new CustomAnimation(man, Duration.seconds(0.75)).fadeIn().translate(-20, 0).play();
		new CustomAnimation(cloud1, Duration.seconds(1)).fadeIn().translate(20, 0).scale(0.9, 1).play();
		new CustomAnimation(cloud2, Duration.seconds(1)).fadeIn().translate(-20, 0).scale(0.9, 1).play();
		new CustomAnimation(mountain, Duration.seconds(0.2)).fadeIn().scale(0.9, 1).play();
	}

}
