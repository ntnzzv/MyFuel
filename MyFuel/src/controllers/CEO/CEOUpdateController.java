package controllers.CEO;

import static core.UIHelper.addElement;
import static core.UIHelper.removeStyleOnFocus;
import static core.UIHelper.roundNumber;
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
import entities.Fuel;
import enums.FuelType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import server_package.Response;


public class CEOUpdateController implements Initializable,IStorable{

	@FXML private TableView<Fuel> TableFuel;
	@FXML private TextField TextPrice;
	@FXML private Pane CeoUpMaxPrice;
	private Fuel selectedFuel;
	private int selectedIndex;
	private ArrayList<Fuel> FuelArray;
	private String textprice;
	CustomButton Update;
	@FXML private DialogPane MassageError;


	@Override
	public void store() {
		Storage.add("Price",textprice);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Update=new CustomButton("Update",370,376);
		Update.setSize("wide");
        Update.setCustomStyle("button3");
		Update.setOnMouseClicked(UpdatePrice());
		addElement(CeoUpMaxPrice,Update);
		try {FXClient.client.request("SELECT * FROM fuels", Fuel.class);}
		catch(IOException e) {e.printStackTrace();}
		FXClient.observable.addObserver(new GenericObserver() {
			@Override
			public void update(Observable o, Object arg) {
				FuelArray = ((Response<Fuel>)arg).result();
				Platform.runLater(()-> {UIHelper.FillTable(TableFuel, FuelArray, "name","maxPrice"); 
				TableFuel.getColumns().get(0).setPrefWidth(162);
				TableFuel.getColumns().get(1).setPrefWidth(140);

				});
			}
		});
		removeStyleOnFocus("emptyTextField",TextPrice);
		prefill();
	}
	
	/**
	 * handles row selection from the table
	 * @param event mouseEvent that triggered the method
	 */
    @FXML
    void selectRow(MouseEvent event) {
    	if(!TableFuel.getSelectionModel().isEmpty()) {
    		selectedFuel = TableFuel.getSelectionModel().getSelectedItem();
    		selectedIndex = TableFuel.getSelectionModel().getSelectedIndex();
    		TextPrice.setText(selectedFuel.getPrice());
    		TextPrice.setDisable(false);
    	}
    }
    
	/**
	 * updates the Fuel Price in the DB and the table
	 * @return EventHandler<Event></Event>
	 * @throws IOException if request to DB fails
	 */
    
    private EventHandler<Event> UpdatePrice(){
    	return e->
    	{
    	if(validate())
    	{
    		MassageError.setVisible(false);
    	try {
        	store();
        	if(FuelArray.get(selectedIndex).maxPrice<Float.parseFloat(TextPrice.getText())){
        		MassageError.setVisible(true);
            	TextPrice.clear();
        		return;
        	}
        	TableFuel.getItems().set(selectedIndex, selectedFuel);
        	selectedFuel.price=Float.parseFloat(TextPrice.getText());
			FXClient.client.request("UPDATE fuels SET fuels.price = '" + selectedFuel.getPrice() + "' WHERE type= '" + selectedFuel.type+"'");
			TextPrice.clear();
		} 
    	catch (IOException ex) {
			ex.printStackTrace();
		}
    	TextPrice.setDisable(true);
    	}
    	};
    	
    }
    
    /**
     * Validates all the fields, proceeds to update the DB if all is correct
     */

	private boolean validate() {
		textprice=TextPrice.getText();
		toggleClass(TextPrice, "emptyTextField", textprice.isEmpty());
		return !textprice.isEmpty();
	}
	/**
	 * pre-fills the data
	 */
	private void prefill() {
		if(Storage.get("Price") != null) 
		TextPrice.setText((String) Storage.get("Price"));
		Storage.clear();
	}

}