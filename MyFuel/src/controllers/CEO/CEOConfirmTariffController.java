package controllers.CEO;


import static core.UIHelper.addElement;

import static core.UIHelper.toggleClass;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import client_package.FXClient;
import core.GenericObserver;
import core.IStorable;
import core.Storage;
import core.UIHelper;
import elements.CustomButton;
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
import javafx.scene.text.Text;
import server_package.Response;

public class CEOConfirmTariffController implements Initializable,IStorable{

	@FXML private TableView<RequestTariff> TabelTariff;
	@FXML private Pane CeoConTariff;
	@FXML private ComboBox<String> ComboStatus;
	private int selectedIndex;
	private RequestTariff selectedTariff;
	private ArrayList<RequestTariff> TariffArray;
    private final ObservableList<String> Status = FXCollections.observableArrayList(
    		"Accept",
            "Deny");	
    private String TextStatus;
    private CustomButton combo;
    private CustomButton Update;
    @FXML
    private Text GuidText;
    
	@Override
	public void store() {
		Storage.add("Status", TextStatus);
	}
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {


		Update = new CustomButton("Send",460,430);
		Update.setSize("wide");
        Update.setCustomStyle("button3");

		try {
			Update.setOnMouseClicked(UpdateStatus());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		addElement(CeoConTariff, Update);
		combo = new CustomButton("Choose Status", 166, 441);	
		combo.setSize(150, 25);
		combo.setCustomStyle("button2");
		combo.setOnMouseClicked(e -> { ComboStatus.show();
		combo.setDisable(true);
		toggleClass(combo, "emptyTextField", false);	
		});
		addElement(CeoConTariff, combo);

		ComboStatus.setFocusTraversable(false);
		ComboStatus.getItems().addAll(Status);
		ComboStatus.valueProperty().addListener((obs, oldValue, newValue) -> { combo.setText(ComboStatus.getSelectionModel().getSelectedItem()); });

		try {FXClient.client.request("SELECT * FROM fuelPriceRequest", RequestTariff.class);}
		catch(IOException e) {e.printStackTrace();}
		FXClient.observable.addObserver(new GenericObserver() {
			@SuppressWarnings("unchecked")
			@Override
			public void update(Observable o, Object arg) {
				TariffArray = ((Response<RequestTariff>)arg).result();
				Platform.runLater(() -> {UIHelper.FillTable(TabelTariff, TariffArray); });
			}});
		 prefill();
		}

	/**
	 * handles row selection from the table
	 * @param event mouseEvent that triggered the method
	 */
	@FXML
    void SelectRow(MouseEvent event) {
    	if(!TabelTariff.getSelectionModel().isEmpty()) {
    		selectedTariff=TabelTariff.getSelectionModel().getSelectedItem();
    		selectedIndex=TabelTariff.getSelectionModel().getSelectedIndex();
    		ComboStatus.getSelectionModel().select(selectedTariff.status);
    		combo.setDisable(false);
    	}
    }

	/**
	 * updates the status of the request in the DB and the table
	 * @return EventHandler<Event></Event>
	 * @throws IOException if request to DB fails
	 */
    private EventHandler<Event> UpdateStatus() throws IOException {
    	return e ->
    	{
    		if(validate()) {
    	try {store();
        	selectedTariff.status=ComboStatus.getSelectionModel().getSelectedItem();
        	TabelTariff.getItems().set(selectedIndex, selectedTariff);
			FXClient.client.request("UPDATE fuelPriceRequest SET status = '" + selectedTariff.getStatus() + "' WHERE ID=" + selectedTariff.ID);
			combo.setText("Status");
    		combo.setDisable(true);
    	}
    		catch (IOException ex) { ex.printStackTrace();}
    	}
    	};
    }
    
    /**
     * Validates all the fields, proceeds to update the DB if all is correct
     */

	private boolean validate() {
		boolean StatusSelected = ComboStatus.getSelectionModel().getSelectedIndex() != -1;
		if(StatusSelected) TextStatus = ComboStatus.getSelectionModel().getSelectedItem();
		toggleClass(combo, "emptyTextField", !StatusSelected);
		return StatusSelected;
	}

	/**
	 * pre-fills the data
	 */
	private void prefill() {
		if(Storage.get("Status") != null)	
		ComboStatus.getSelectionModel().select((String) Storage.get("Status"));
		
		Storage.clear();		
	}

}

