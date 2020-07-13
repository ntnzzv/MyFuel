package controllers.Supplier;
import client_package.FXClient;
import core.EmailHandler;
import core.GenericObserver;
import static core.UIHelper.*;

import core.Storage;
import elements.CustomButton;
import elements.CustomDialog;
import entities.GasRestockBySupplier;
import entities.InventoryOrder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import server_package.Response;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static core.UIHelper.addElement;

//TODO : UPDATE INVENTORY, DISABLE ROW
public class UpdateOrderStatusBySupplierController implements Initializable
{
    @FXML private Pane updateOrderSupplier;
    @FXML private TableView<GasRestockBySupplier> gasOrderTableView;
    public static GasRestockBySupplier selectedOrder;
    public static int selectedIndex;
    public static CustomDialog reasonCanceledDialog;
    private ArrayList<GasRestockBySupplier> gasOrdersFromDatabase;
    CustomButton suppliedBtn,canceledBtn;
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Platform.runLater(this::initializeTable);

        suppliedBtn = new CustomButton("Supplied");
        suppliedBtn.setSize("normal");
        suppliedBtn.setPosition(350, 550);
        suppliedBtn.setOnMouseClicked(e -> {
            supply();
        });
        suppliedBtn.setDisable(true);
        addElement(updateOrderSupplier, suppliedBtn);

        canceledBtn = new CustomButton("Canceled");
        canceledBtn.setSize("normal");
        canceledBtn.setPosition(550, 550);
        canceledBtn.setOnMouseClicked(e->{
            if(gasOrderTableView.getSelectionModel().getSelectedItem() != null) showDialogReason();
        });
        canceledBtn.setDisable(true);
        addElement(updateOrderSupplier, canceledBtn);

        //CustomDialog reason
        Platform.runLater(()->
        {
            try {
                reasonCanceledDialog = new CustomDialog(FXClient.lm.frame, "Fill reason for canceling", "Supplier/reasonNotSuppliedDialog");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }
/**
 * laucnhes the dialog incase cancel order is clicked, for cancellation reason.
 */
    private void showDialogReason()
    {
            try {
                Storage.add("id",selectedOrder.orderID);
                reasonCanceledDialog.show();}
            catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * on clicking the supply button the fields in the table are updated
     * followed by an update in the DB settings the status to supplied
     */
    private void supply()
    {
            try {
                selectedOrder.setStatus("Supplied");
                selectedOrder.setComment("Supplied");
                gasOrderTableView.getItems().set(selectedIndex, selectedOrder);
                FXClient.client.request("UPDATE fuelRestockRequest SET status = 'Supplied' WHERE orderID= " + selectedOrder.orderID);
                FXClient.client.request("UPDATE fuelRestockRequest SET comment = 'Supplied' WHERE orderID= " + selectedOrder.orderID);
                FXClient.client.request("UPDATE fuelStock SET amount = amount + '" + selectedOrder.amount + "' WHERE type='" + selectedOrder.getFuelType() + "' AND gasStationID = '"+ selectedOrder.getStationID()+"'");
               EmailHandler mail = new EmailHandler();

                Thread thread = new Thread(){
                    public void run(){
                        mail.sendMessage("orianne27@gmail.com","Order was sent","order number" + " " + selectedOrder.getOrderID() + " " + "was supplied to you !");
                    }
                };
                thread.start();
            }
            catch (Exception ex) { ex.printStackTrace(); }
    }

    void initializeTable()
    {
        try { FXClient.client.request("SELECT * FROM fuelRestockRequest WHERE status = 'Accepted'", GasRestockBySupplier.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver(new GenericObserver() {

            @Override
            public void update(Observable o, Object arg) {
                gasOrdersFromDatabase = ((Response<GasRestockBySupplier>)arg).result();
                Platform.runLater(()->{FillTable(gasOrderTableView, gasOrdersFromDatabase);});

            }
        });
    }
/**
 * listening to the selection of a row for fetching data and disabling buttons
 */
    @FXML
    void selectRow(MouseEvent event)
    {
        selectedIndex = gasOrderTableView.getSelectionModel().getSelectedIndex();
        selectedOrder = gasOrderTableView.getSelectionModel().getSelectedItem();
        if (!gasOrderTableView.getSelectionModel().isEmpty())
        {
            selectedOrder = gasOrderTableView.getSelectionModel().getSelectedItem();
            selectedIndex = gasOrderTableView.getSelectionModel().getSelectedIndex();
            suppliedBtn.setDisable(false);
            canceledBtn.setDisable(false);
        }
    }
}