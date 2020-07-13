package controllers.GasStationManager;

import client_package.FXClient;
import core.GenericObserver;
import core.UIHelper;
import elements.CustomButton;
import entities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import server_package.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

public class GasStationManagerReports implements Initializable {
	@FXML
	private Pane orderPane;

	@FXML
	private TableView<Purchase> quarterlyTable;

	@FXML
	private TableView<Purchase> purchaseTable;

	@FXML
	private TableView<FuelStock> stockTable;

	private ArrayList<GasStation> gasStation;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		CustomButton print = new CustomButton("Print Reports");
		print.setSize("normal");
		print.setPosition(orderPane.getPrefWidth() -  print.width - 30, orderPane.getPrefHeight() - print.height - 20);
		print.setCustomStyle("button3");
		UIHelper.addElement(orderPane,print);

		Platform.runLater(this::getStationID);

	}

	private void getStationID(){
		try {
			FXClient.client.request("SELECT * FROM gasStations WHERE manager = '"+FXClient.user.username+"'", GasStation.class);}
		catch(IOException e) {e.printStackTrace();}

		FXClient.observable.addObserver(new GenericObserver() {
			@Override
			public void update(Observable o, Object arg) {
				gasStation = ((Response<GasStation>)arg).result();

				if(gasStation.size() > 0 ) {
					Platform.runLater(()->fetchQuarterlyReport());
					Platform.runLater(()->fetchPurchasesReport());
					Platform.runLater(()->fetchStockReport());
				}
			}
		});
	}
	private void fetchStockReport() {
		try {
			FXClient.client.request("SELECT * FROM fuelStock WHERE gasStationID = '"+gasStation.get(0).stationID+"'", FuelStock.class);}
		catch(IOException e) {e.printStackTrace();}

		FXClient.observable.addObserver(new GenericObserver() {
			@Override
			public void update(Observable o, Object arg) {
				ArrayList<FuelStock> fuels = ((Response<FuelStock>)arg).result();

				Platform.runLater(()->{
					UIHelper.FillTable(stockTable,fuels,"threshold","price","gasStationID");
				});

			}
		});
	}

	private void fetchPurchasesReport() {
		try {
			FXClient.client.request("SELECT fuelType,SUM(quantity * price) AS sum FROM purchase WHERE gasStation = '"+gasStation.get(0).name+"' GROUP BY fuelType", Purchase.class);}
		catch(IOException e) {e.printStackTrace();}

		FXClient.observable.addObserver(new GenericObserver() {
			@Override
			public void update(Observable o, Object arg) {
				ArrayList<Purchase> purchases = ((Response<Purchase>)arg).result();
				for(Purchase p : purchases){ p.sum = UIHelper.roundNumber(p.sum,2); }
				Platform.runLater(()->{
					UIHelper.FillTable(purchaseTable,purchases,"purchaseID","customerID","price","quantity","gasStation","saleID","purchaseDateTime","sum2","purchaseSum","QuarterlyIncome");
				});
			}
		});
	}

	private void fetchQuarterlyReport() {

		try {
			FXClient.client.request("SELECT SUM(price * quantity) AS QuarterlyIncome from purchase WHERE quarter(purchaseDateTime) = quarter(curdate()) AND gasStation = '"+gasStation.get(0).name+"'", Purchase.class);}
		catch(IOException e) {e.printStackTrace();}

		FXClient.observable.addObserver(new GenericObserver() {
			@Override
			public void update(Observable o, Object arg) {
				ArrayList<Purchase> income = ((Response<Purchase>)arg).result();
				if(income != null) {income.get(0).QuarterlyIncome = UIHelper.roundNumber(income.get(0).QuarterlyIncome,2);}
				Platform.runLater(()->{
					UIHelper.FillTable(quarterlyTable,income,"purchaseID","customerID","price","quantity","gasStation","saleID","purchaseDateTime","sum2","purchaseSum","fuelType","sum");
				});
			}
		});
	}
}
