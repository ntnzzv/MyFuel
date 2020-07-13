package controllers.MarketingManager;

import client_package.FXClient;
import core.GenericObserver;
import elements.CustomButton;
import entities.ActivesSale;
import entities.Purchase;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import server_package.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;


import static core.UIHelper.*;


public class marketingReportsController implements Initializable {

    @FXML private TableView<Purchase> purchasesTable;
    @FXML private PieChart pieChart;
    @FXML private TextField clientsField;
    @FXML private TextField sumField;
    @FXML private Pane pane;
    @FXML private ComboBox<String> reportsCombo;
    @FXML private TableView<Purchase> reportsTable;
    private ArrayList<ActivesSale> sales;
    private CustomButton update;
    private ArrayList<Purchase> purchasesBySale,sum,customers,stations,purchases;
    private ObservableList<PieChart.Data> pd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::getSalesID);
        update = new CustomButton("Retrieve Reports",230,100);
        update.setSize("wide");

        Platform.runLater(this::initializePurchaseTable);
        Platform.runLater(this::getGasStations);

        update.setOnMouseClicked(e->{
            initializeSaleTemplatesClients();
            initializeSaleTemplatesSum();
            initializeSaleTable();
        });

        addElement(pane,update);
    }

    /**
     * returns the currently active sales ID, and puts it into the combo - box as options
     */
    private void getSalesID(){

        try {FXClient.client.request("SELECT * FROM activeSales", ActivesSale.class);}
        catch(IOException e) {e.printStackTrace();}

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                sales = ((Response<ActivesSale>)arg).result();
                if(sales.size() == 0){reportsCombo.getItems().add("No Sales"); return;}
                for(ActivesSale s : sales){
                    reportsCombo.getItems().add(s.saleID.toString());
                }
            }
        });
    }

    /**
     * after the user selects a saleID, this method saves the amount of purchases over that specific sale in a textfield.
     */
    private void initializeSaleTemplatesClients() {
        String selectedSaleID = reportsCombo.getSelectionModel().getSelectedItem();
        try {FXClient.client.request("SELECT * FROM purchase WHERE saleID = '"+selectedSaleID+"' group by customerID", Purchase.class);}
        catch(IOException e) {e.printStackTrace();}

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                purchasesBySale = ((Response<Purchase>)arg).result();

                if(purchasesBySale.size() == 0) {
                    clientsField.setText("N/A");
                }
                else {clientsField.setText(String.valueOf(purchasesBySale.size()));}
            }
        });
    }

    /**
     * initializes the sumField text field to hold the sum of the purchases done for a saleID
     */
    private void initializeSaleTemplatesSum() {

        String selectedSaleID = reportsCombo.getSelectionModel().getSelectedItem();
        try {FXClient.client.request("SELECT SUM(P.price*P.quantity) AS sum FROM  purchase P WHERE P.saleID = '"+selectedSaleID+"'",Purchase.class);}
        catch(IOException e) {e.printStackTrace();}

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                sum = ((Response<Purchase>)arg).result();

                if(sum.get(0).sum == null) {
                    sumField.setText("N/A");
                }
                else {sumField.setText(String.valueOf(roundNumber(Float.parseFloat(sum.get(0).sum.toString()), 2))); }
            }
        });
    }

    /**
     * fetches the data from the DB to create a table which holds the summary of purchases for each customer
     */
    private void initializeSaleTable(){
        if(reportsTable.getColumns().size() == 2) {
            reportsTable.getColumns().remove(0,2);
        }

        String selectedSaleID = reportsCombo.getSelectionModel().getSelectedItem();
        try {FXClient.client.request("SELECT P.customerID, SUM(P.price*P.quantity) AS sum FROM activeSales A, purchase P WHERE A.saleID = P.saleID AND A.saleID = '"+selectedSaleID+"'"+
                "GROUP BY P.customerID;",Purchase.class);}
        catch(IOException e) {e.printStackTrace();}

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                customers = ((Response<Purchase>)arg).result();

                for(Purchase p : customers){
                    p.sum = Double.parseDouble(String.valueOf(Math.round(p.sum)));
                }

                Platform.runLater(()->FillTable(reportsTable,customers,"purchaseID","price","gasStation"
                ,"quantity","fuelType","saleID","purchaseSum","sum2","QuarterlyIncome"));
            }
        });
    }

    /**
     * fetches the list of gas stations and the amount of customers that bought from each one for a pie chart diagram
     */
    private void getGasStations(){
        ObservableList<PieChart.Data> obsList = FXCollections.observableArrayList();

        try {FXClient.client.request("SELECT gasStation,count(distinct customerID) AS sum2 FROM purchase group by gasStation", Purchase.class);}
        catch(IOException e) {e.printStackTrace();}

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                stations = ((Response<Purchase>)arg).result();

                for(Purchase gas : stations){
                    obsList.add(new PieChart.Data(gas.gasStation,gas.sum2));
                }
                Platform.runLater(()->{pieChart.setData(obsList); });
            }
        });
    }

    /**
     * fetches the amount of purchases for each customer
     */
    private void initializePurchaseTable(){
        if(purchasesTable.getColumns().size() == 2) {
            purchasesTable.getColumns().remove(0,2);
        }
        try {FXClient.client.request("select customerID,count(purchaseID) AS purchaseSum from purchase group by  customerID ORDER BY purchaseSum DESC",Purchase.class);}
        catch(IOException e) {e.printStackTrace();}

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                purchases = ((Response<Purchase>)arg).result();

                Platform.runLater(()->FillTable(purchasesTable,purchases,"purchaseID","price","gasStation"
                        ,"quantity","fuelType","saleID","sum","sum2","QuarterlyIncome"));
            }
        });
    }
}
