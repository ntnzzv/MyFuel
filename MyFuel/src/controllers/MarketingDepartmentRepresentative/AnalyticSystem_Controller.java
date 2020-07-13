package controllers.MarketingDepartmentRepresentative;

import static core.UIHelper.addElement;
import static core.UIHelper.toggleClass;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import client_package.FXClient;
import core.GenericObserver;
import core.UIHelper;
import elements.CustomButton;
import entities.AnalyticSystem;
import entities.Customer;
import entities.Purchase;
import entities.RequestTariff;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import server_package.Response;

public class AnalyticSystem_Controller implements Initializable {


	@FXML private Pane AnalyticPage;
	@FXML private TableView<AnalyticSystem> TableAnalytic;
	@FXML private ComboBox<String> ComnboOrder;
	private CustomButton Show,combo;
	private ArrayList<Customer> Customers;
	private ArrayList<AnalyticSystem> scoreAnlytic;
	private String getChoose,fuelName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {FXClient.client.request("SELECT C.customerID, C.hasDalkan, C.exclusivePurchasePlan, C.gasServicePlan, CARS.carCount , C.type as customerType, COUNT(DISTINCT P.fuelType) AS fuelTypes, ROUND(SUM(P.quantity * P.price)*0.1) AS profit FROM purchase P, customers C,\n" +
				"(SELECT cc.customerID, COUNT(DISTINCT cars.carID) AS carCount FROM customers AS cc INNER JOIN cars ON cc.customerID = cars.customerID\n" +
				"GROUP BY cc.customerID) AS CARS\n" +
				"WHERE C.customerID = P.customerID OR C.customerID = CARS.customerID\n" +
				"GROUP BY customerID", AnalyticSystem.class);}
		catch(IOException e) {e.printStackTrace();}
		FXClient.observable.addObserver(new GenericObserver() {
			@SuppressWarnings("unchecked")
			@Override
			public void update(Observable o, Object arg) {
				scoreAnlytic = ((Response<AnalyticSystem>)arg).result();
				Platform.runLater(() -> {
					if(scoreAnlytic.size()>0) {
						for(AnalyticSystem A : scoreAnlytic) {
							A.setScore(A.carCount > 4 ? 1 : 0);
							A.setScore(A.type.equals("Private") ? 1 : 2);
							A.setScore(A.gasServicePlan != null ? 1 : 0);
							A.setScore(A.profit < 800 ? 0 : A.profit < 2000 ? 1 : 2);
							A.setScore(A.hasDalkan == 1 ? 1 : 0);
							A.setScore(Math.toIntExact(A.fuelTypes));
						}
					}
					UIHelper.FillTable(TableAnalytic, scoreAnlytic,"gasServicePlan","exclusivePurchasePlan"); });
			}});
	}

	private EventHandler<Event> AnalyticReports() {
		return e->
		{
			getChoose=ComnboOrder.getSelectionModel().getSelectedItem();

		};
	}

}
