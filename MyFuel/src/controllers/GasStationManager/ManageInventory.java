package controllers.GasStationManager;

import client_package.FXClient;
import core.GenericObserver;
import core.UIHelper;
import elements.CustomButton;
import entities.GasRestockBySupplier;
import entities.GasStation;
import entities.InventoryOrder;
import entities.RequestTariff;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import server_package.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import static core.UIHelper.addElement;

public class ManageInventory implements Initializable {

    @FXML private Pane windowPane;
    @FXML private TableView<GasRestockBySupplier> requestsTable;
    @FXML private TextField id;
    @FXML private TextField date;
    @FXML private TextField stationID;
    @FXML private TextField fuelType;
    @FXML private TextField amount;
    @FXML private TextField status;
    private CustomButton  placeOrder,cancelOrder;

    ArrayList<GasRestockBySupplier> requests;
    GasStation gasStation;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        placeOrder = new CustomButton("Order supplies",850,550);
        placeOrder.setSize("wide");
        addElement(windowPane,placeOrder);

        cancelOrder = new CustomButton("Cancel Order",670,550);
        cancelOrder.setSize("wide");
        addElement(windowPane,cancelOrder);

        addEventToTable();
        addEvents();
        getStationID();
    }

    private void getStationID(){
        try { FXClient.client.request("SELECT * FROM gasStations WHERE manager = '"+FXClient.user.username+"'", GasStation.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                gasStation = ((Response<GasStation>)arg).result().get(0);

                Platform.runLater(()->initializeTable());
            }
        });
    }

    /**
     * loads the clicked table contents to the text fields
     */
    private void addEventToTable(){
        requestsTable.setOnMousePressed((MouseEvent event) -> {
            id.setText(String.valueOf(requestsTable.getSelectionModel().getSelectedItem().orderID));
            fuelType.setText(requestsTable.getSelectionModel().getSelectedItem().getFuelType());
            amount.setText(requestsTable.getSelectionModel().getSelectedItem().getAmount());
            date.setText(String.valueOf(requestsTable.getSelectionModel().getSelectedItem().orderDate));
            stationID.setText(String.valueOf(requestsTable.getSelectionModel().getSelectedItem().stationID));
            status.setText(requestsTable.getSelectionModel().getSelectedItem().status);
            placeOrder.setVisible(status.getText().equals("Pending"));
        });
    }

    /**
     * adds events for the button to update the order's status to Accepted in the DB when clicked
     */
    private void addEvents(){
        placeOrder.setOnMousePressed((MouseEvent event) -> {
            try { FXClient.client.request("UPDATE fuelRestockRequest SET status = 'Accepted' WHERE orderID = '"+id.getText()+"'"); }
            catch (IOException e) { e.printStackTrace(); }

            requestsTable.getSelectionModel().getSelectedItem().setStatus("Accepted");
            requestsTable.refresh();
        });

        cancelOrder.setOnMousePressed((event)->{
            try { FXClient.client.request("UPDATE fuelRestockRequest SET status = 'Canceled' WHERE orderID = '"+id.getText()+"'"); }
            catch (IOException e) { e.printStackTrace(); }


            requestsTable.getSelectionModel().getSelectedItem().setStatus("Canceled");
            requestsTable.refresh();
        });
    }

    /**
     * Creates the table and fetches all data from DB
     */
    void initializeTable(){
        try { FXClient.client.request("SELECT * FROM fuelRestockRequest WHERE stationID = '"+gasStation.stationID+"' AND status = 'Pending'", GasRestockBySupplier.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                requests = ((Response<GasRestockBySupplier>)arg).result();
                Platform.runLater(() -> {
                    UIHelper.FillTable(requestsTable, requests);
                });
            }
        });
    }
}