package controllers.MarketingManager;

import java.io.IOException;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import javax.xml.transform.Templates;

import client_package.FXClient;
import core.GenericObserver;
import core.IStorable;
import core.Storage;
import core.UIHelper;
import elements.CustomButton;
import entities.DiscountTemplate;
import entities.Fuel;
import entities.FuelStock;
import entities.RequestTariff;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import server_package.Response;

import static core.UIHelper.*;

public class MarketManReqController implements Initializable,IStorable {
    
	 @FXML private Pane MarketManReq;
	 @FXML private TableView<RequestTariff> TableReq;
	 @FXML private ComboBox<String> ComboFuelType;
	 @FXML private TextField TextSentBy;
	 @FXML private TextField TextDiscount;
	 @FXML private Text RemoveText;
	 @FXML private Text problemText;
	 @FXML  private DialogPane ErrorMessage;
	 private CustomButton addB,removeB,updateB,combo;
	 private String discount,type;
	 private int remove;
	 private RequestTariff temp;
	 private ArrayList<Fuel> Fuel;
	 private ArrayList<RequestTariff> TariffArray;
	 private ArrayList<RequestTariff> RequestID;
	 private Integer ID;
	 private Float NewDiscount,OldPrice,NewPrice;
	 private final ObservableList<String> FuelArray= FXCollections.observableArrayList(
			"Petrol95",
			"Scooter",
			"HeatingFuel",
			"Diesel"); 
	 
	@Override
	public void store() {
			Storage.add("FuelType", type);
			Storage.add("Discount", discount);
	}

		
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		addB=new CustomButton("Send",380,350);
		addB.setSize("normal");
        addB.setCustomStyle("button3");
		addB.setOnMouseClicked(AddFunc());
		addElement(MarketManReq, addB);
		removeB=new CustomButton("Remove",380,420);
		removeB.setSize("normal");
        removeB.setCustomStyle("button3");
		removeB.setVisible(false);
		removeB.setOnMouseClicked(RemovFunc());
		addElement(MarketManReq, removeB);
		updateB= new CustomButton("Update",380,490);
		updateB.setSize("normal");
        updateB.setCustomStyle("button3");
		updateB.setVisible(false);
		updateB.setOnMouseClicked(UpdateFunc());
		addElement(MarketManReq, updateB);
		combo= new CustomButton("Choose Fuel Type",112,509);
		combo.setSize(148,25);
		combo.setCustomStyle("button2");
		combo.setOnMouseClicked(e -> { ComboFuelType.show();
		toggleClass(combo, "emptyTextField", false);});
		addElement(MarketManReq, combo);
		ComboFuelType.setFocusTraversable(false);

		ComboFuelType.getItems().addAll(FuelArray);
		ComboFuelType.valueProperty().addListener((obs, oldValue, newValue) -> { combo.setText(ComboFuelType.getSelectionModel().getSelectedItem()); });
		TextSentBy.setText(FXClient.user.firstName);
		try {FXClient.client.request("SELECT * FROM fuelPriceRequest",RequestTariff.class);}
		catch(IOException e) {e.printStackTrace();}
		FXClient.observable.addObserver(new GenericObserver() {
			@Override
			public void update(Observable o, Object arg) {
				TariffArray = ((Response<RequestTariff>)arg).result();
				Platform.runLater(() -> {UIHelper.FillTable(TableReq, TariffArray); });
			}
		});
		UIHelper.numbersOnly(TextDiscount, 3);
		removeStyleOnFocus("emptyTextField",TextSentBy,TextDiscount);
		prefill();
	}
	/**
	 * handles row selection from the table
	 * @param event mouseEvent that triggered the method
	 */
	@FXML
    void SelectRow(MouseEvent event) throws IOException {
    	if(!TableReq.getSelectionModel().isEmpty())
    	{
    	temp=TableReq.getSelectionModel().getSelectedItem();
    	remove=TableReq.getSelectionModel().getSelectedIndex();
    	if(!(temp.getStatus().equals("Pending"))) {
    		updateB.setVisible(true);
    		removeB.setVisible(false);
    	}
    	else {
    		updateB.setVisible(false);
    		removeB.setVisible(true);

    	}
    	}
    }
	/**
	 * Send the request to CEO and insert details to DB
	 * @return EventHandler<Event></Event>
	 * @throws IOException if request to DB fails
	 */
    private EventHandler<Event> AddFunc() {
		return e->
		{
			if(validate())
		{ErrorMessage.setVisible(false);
				try {
					store();
					temp=new RequestTariff(TextSentBy.getText(),ComboFuelType.getSelectionModel().getSelectedItem(), Integer.parseInt(TextDiscount.getText()));
					float tempdiscount = Float.parseFloat(TextDiscount.getText())/100;
					NewDiscount= (1- tempdiscount);
					FXClient.client.request("SELECT * FROM fuels WHERE type='" + ComboFuelType.getSelectionModel().getSelectedItem() + "'", Fuel.class);
			    	FXClient.observable.addObserver(new GenericObserver() {
						@Override
						public void update(Observable o, Object arg) {
							Fuel = ((Response<Fuel>)arg).result();
							OldPrice=Fuel.get(0).maxPrice;
								NewPrice= roundNumber(NewDiscount*OldPrice,2);
								if(NewPrice<0) {
									ErrorMessage.setVisible(true);
									combo.setText("Choose Type");
									TextDiscount.clear();
									return;
								}
								temp.newPrice=NewPrice;
								temp.status="Pending";
							try {
								FXClient.client.request("INSERT INTO fuelPriceRequest(sentBy,fuelType,discount,newPrice,status)" + "VALUES('"+temp.getSentBy()+"','"+temp.getFuelType()+"','"+temp.getDiscount()+"','"+temp.getNewPrice()+"','"+temp.getStatus()+"');");
								getID();
							} catch (IOException ioException) {
								ioException.printStackTrace();
							}

						}
			        });
				}
				catch(Exception ex) {ex.printStackTrace();}
		}
	        UIHelper.numbersOnly(TextDiscount,3);
	        UIHelper.lettersOnly(TextSentBy,10);
		};
	}
    /**
     * Creat and save new SaleID
     */
    private void getID(){
		try {
			FXClient.client.request("SELECT * FROM fuelPriceRequest WHERE ID = (SELECT MAX(ID) FROM fuelPriceRequest)", RequestTariff.class);
			FXClient.observable.addObserver(new GenericObserver() {
				@Override
				public void update(Observable o, Object arg) {
					RequestID = ((Response<RequestTariff>) arg).result();
					ID = RequestID.get(0).ID;

					Platform.runLater(() -> {
						temp.ID = ID;
						TableReq.getItems().add(temp);
						combo.setText("Choose Type");
						TextDiscount.clear();
					});
				}
			});
		}catch (IOException e) {e.printStackTrace();}
    }
	/**
	 * Remove the request from DB in case:
	 * 1.if MarketingManager decided to remove it
	 * @return EventHandler<Event></Event>
	 * @throws IOException if request to DB fails
	 */
	private EventHandler<Event> RemovFunc() {
		return e->
		{
    		updateB.setVisible(false);
    		removeB.setVisible(false);
				try {
					store();
			    	TableReq.getItems().remove(remove);
			    	FXClient.client.request("DELETE FROM fuelPriceRequest WHERE ID=" + temp.ID);
					}
				catch(Exception ex) {ex.printStackTrace();}

		};
	}
	/**
	 * Remove from table fuelPriceRequest Or Update table fuelPrice in DB in case:
	 * 1.if CEO return 
	 * @return EventHandler<Event></Event>
	 * @throws IOException if request to DB fails
	 */
	private EventHandler<Event> UpdateFunc() {
		return e->
		{
    		updateB.setVisible(false);
    		removeB.setVisible(false);
				try {
					store();
			    	if(temp.getStatus().equals("Accept")) {
			        	TableReq.getItems().remove(remove);
			        	FXClient.client.request("UPDATE fuels SET fuels.price = '" + temp.getNewPrice() + "' WHERE type= '" + temp.fuelType + "'");
			        	FXClient.client.request("DELETE FROM fuelPriceRequest WHERE ID=" + temp.ID);
			        	}
			        	else {
			        		TableReq.getItems().remove(remove);
			            	FXClient.client.request("DELETE FROM fuelPriceRequest WHERE ID=" + temp.ID);

			        	}
			}
				catch(Exception ex) {ex.printStackTrace();}
			};
	}
    /**
     * Validates all the fields, proceeds to update the DB if all is correct
     */
    private boolean validate() {
    	discount=TextDiscount.getText();	
		toggleClass(TextDiscount, "emptyTextField", discount.isEmpty());
		
		boolean typeSelected = ComboFuelType.getSelectionModel().getSelectedIndex() != -1;
		if(typeSelected) type=ComboFuelType.getSelectionModel().getSelectedItem();
		toggleClass(combo, "emptyTextField", !typeSelected);
		return !discount.isEmpty() && typeSelected;
    }
	/**
	 * pre-fills the data
	 */
    private void prefill() {
		if(Storage.get("FuelType") != null)	ComboFuelType.getSelectionModel().select((String) Storage.get("FuelType"));
		if(Storage.get("Discount") != null)	TextDiscount.setText((String) Storage.get("Discount"));
		
		Storage.clear();
	}
    


}
