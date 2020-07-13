package controllers.Supplier;
import client_package.FXClient;
import core.Storage;
import elements.CustomButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

import static core.UIHelper.addElement;
import static core.UIHelper.toggleClass;

public class reasonDialog implements Initializable
{
    @FXML private Pane reasonDialog;
    @FXML private ComboBox<String> reasonComboBox;
    private CustomButton update;
    private final ObservableList<String> reasons = FXCollections.observableArrayList(
            "Inventory Missing",
            "Payment issues",
            "Transport problems");

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        update = new CustomButton("Update");
        update.setSize("normal");
        update.setPosition(125, 170);
        update.setOnMouseClicked(updateCancelingReason());
        addElement(reasonDialog, update);

        reasonComboBox.getItems().addAll(reasons);
        CustomButton combo = new CustomButton("Choose here", 110, 120);
        combo.setSize(160, 30);
        combo.setCustomStyle("button2");
        addElement(reasonDialog, combo);
        reasonComboBox.setFocusTraversable(false);
        combo.setOnMouseClicked(e -> { reasonComboBox.show(); toggleClass(combo, "emptyTextField", false); });
        reasonComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            combo.setText(reasonComboBox.getSelectionModel().getSelectedItem());
        });
    }

    /**
     * updates the cancellation reason
     * @return EventHandler<MouseEvent>
     */
    private EventHandler<MouseEvent> updateCancelingReason()
    {
        return e ->
        {
            try {
                String reason = reasonComboBox.getSelectionModel().getSelectedItem();
                UpdateOrderStatusBySupplierController.selectedOrder.setComment(reason);
                FXClient.client.request("UPDATE fuelRestockRequest SET fuelRestockRequest.status = 'Canceled' WHERE orderID=" + Storage.get("id"));
                FXClient.client.request("UPDATE fuelRestockRequest SET fuelRestockRequest.comment = '" + reason + "' WHERE orderID=" + UpdateOrderStatusBySupplierController.selectedOrder.orderID);
                UpdateOrderStatusBySupplierController.reasonCanceledDialog.hide();
            }
            catch (Exception ex) { ex.printStackTrace(); }
        };
    }
}
