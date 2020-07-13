package controllers.MarketingDepartmentRepresentative;

import static core.UIHelper.addElement;
import static core.UIHelper.removeStyleOnFocus;
import static core.UIHelper.roundNumber;
import static core.UIHelper.toggleClass;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import client_package.FXClient;
import core.GenericObserver;
import core.IStorable;
import core.Storage;
import core.UIHelper;
import elements.CustomButton;
import entities.DiscountTemplate;
import entities.SaleTemplateProduct;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import server_package.Response;

public class CreatDiscountTemplateController implements Initializable,IStorable{
    
	@FXML private Pane MarketingRepCreatDiscount;
	@FXML private TextField TextSaleName;
	@FXML private DatePicker DateStart;
	@FXML private DatePicker DateEnd;
	@FXML private TextField HoursON;
	@FXML private TextField HoursOFF;
	@FXML private TextField TextDiscount;
	private Date SDate;
	private Date EDate;
	private DiscountTemplate temp;
	private Integer ID;
	public static SaleTemplateProduct sale;
	private int index;
	private CustomButton CreateB,RemoveB,dateS,dateE,UpdateB;
	private String id,templateName,hourS,hourE,discount; 
	@FXML private TableView<DiscountTemplate> TemplateTable;
	private ArrayList<DiscountTemplate> TemplateArray;
	private ArrayList<DiscountTemplate> Template;

	/**
	 * This method implements the IStorable interface that allows to store in a hashmap information and pass it between classes
	 */
	@Override
	public void store() {
			Storage.add("templateName", templateName);
			Storage.add("dateS", DateStart.getValue());
			Storage.add("dateE", DateEnd.getValue());
			Storage.add("hourS", hourS);
			Storage.add("hourE",hourE);
			Storage.add("discount", discount);
	}

	/**
	 * initialises all needed information and elements
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		CreateB = new CustomButton("Create",882,353);
		CreateB.setSize("normal");
        CreateB.setCustomStyle("button3");
		try {CreateB.setOnMouseClicked(CreateFunc());} 
		catch (IOException e1) {e1.printStackTrace();}
		addElement(MarketingRepCreatDiscount, CreateB);
		
		UpdateB = new CustomButton("Update",882,493);
		UpdateB.setSize("normal");
		UpdateB.setCustomStyle("button3");
		UpdateB.setDisable(true);
		try {UpdateB.setOnMouseClicked(DescriptionFunc());} 
		catch (IOException e1) {e1.printStackTrace();}
		addElement(MarketingRepCreatDiscount, UpdateB);

		
		RemoveB = new CustomButton("Remove",882,421);	
		RemoveB.setSize("normal");
		RemoveB.setCustomStyle("button3");
		RemoveB.setDisable(true);
		RemoveB.setOnMouseClicked(RemoveFunc());
		addElement(MarketingRepCreatDiscount, RemoveB);
		RemoveB.setDisable(true);
		
		dateS = new CustomButton("dd/mm/yyyy", 450, 404);
		dateS.setSize(174, 25);
		dateS.setCustomStyle("button2");
		addElement(MarketingRepCreatDiscount, dateS);
		
		DateStart.setFocusTraversable(false);
		dateS.setOnMouseClicked(e -> { DateStart.show(); toggleClass(dateS, "emptyTextField", false); });
		DateStart.valueProperty().addListener((obs, oldValue, newValue) -> { dateS.setText(newValue.toString()); });
		dateE = new CustomButton("dd/mm/yyyy", 450, 474);
		dateE.setSize(174, 25);
		dateE.setCustomStyle("button2");
		addElement(MarketingRepCreatDiscount, dateE);
		
		DateEnd.setFocusTraversable(false);
		dateE.setOnMouseClicked(e -> { DateEnd.show(); toggleClass(dateE, "emptyTextField", false); });
		DateEnd.valueProperty().addListener((obs, oldValue, newValue) -> { dateE.setText(newValue.toString()); });
		
		try {FXClient.client.request("SELECT * FROM saleTemplates", DiscountTemplate.class);}
		catch(IOException e) {e.printStackTrace();}

		FXClient.observable.addObserver(new GenericObserver() {
			@Override
			public void update(Observable o, Object arg) {
				TemplateArray=((Response<DiscountTemplate>)arg).result();
				Platform.runLater(() -> {
					UIHelper.FillTable(TemplateTable, TemplateArray);
					});
			}
		});
		UIHelper.lettersOnly(TextSaleName, 10);
		UIHelper.numbersOnly(TextDiscount, 3);
		removeStyleOnFocus("emptyTextField",TextSaleName,TextDiscount, HoursON, HoursOFF);
		prefill();
	}

	/**
	 * This method selects a row in table and gets it's cell's information
	 * @param event this method is called after clicking on a FXML element
	 */
	@FXML
    void SelectRow(MouseEvent event) {
    	if(!TemplateTable.getSelectionModel().isEmpty())
    	{
    	RemoveB.setDisable(false);
    	UpdateB.setDisable(false);
    	CreateB.setDisable(true);
    	index=TemplateTable.getSelectionModel().getSelectedIndex();
    	temp=TemplateTable.getSelectionModel().getSelectedItem();
    	ID=TemplateTable.getSelectionModel().getSelectedItem().templateID;
    	TextSaleName.setText(temp.getTemplateName());
//		Float discount=Float.parseFloat(temp.getDiscountPercent())*100;
//    	TextDiscount.setText(discount.toString());
    	DateStart.setValue(temp.startDate.toLocalDate());
    	DateEnd.setValue(temp.endDate.toLocalDate());
    	HoursON.setText(temp.getActiveHoursStart());
    	HoursOFF.setText(temp.getActiveHoursEnd());
		
		
    	}
    }

	/**
	 * This method creates a new template and inserts it into DB
	 * @return event, the method execution
	 */
    private EventHandler<Event> CreateFunc() throws IOException {
		return e->
		{
	    	RemoveB.setDisable(true);
	    	UpdateB.setDisable(true);
			if(validate()) {	
			try 
			{
			store();
			Float discount=Float.parseFloat(TextDiscount.getText())/100;
			roundNumber(discount, 2);
	    	SDate=Date.valueOf(DateStart.getValue());
	    	EDate=Date.valueOf(DateEnd.getValue());
	    	temp=new DiscountTemplate(TextSaleName.getText(),SDate,EDate,Time.valueOf(HoursON.getText()),Time.valueOf(HoursOFF.getText()),discount,"No Active");
	    	FXClient.client.request("INSERT INTO saleTemplates(startDate,endDate,activeHoursStart,activeHoursEnd,templateName,discountPercent,launched)" + "VALUES('"+temp.getStartDate()+"','"+temp.getEndDate()+"','"+temp.getActiveHoursStart()+"','"+temp.getActiveHoursEnd()+"','"+temp.getTemplateName()+"','"+temp.getDiscountPercent()+"','"+temp.getLaunched()+"');");
	    	FXClient.client.request("SELECT * FROM saleTemplates WHERE templateID = (SELECT MAX(templateID) FROM saleTemplates)",DiscountTemplate.class);
	    	FXClient.observable.addObserver(new GenericObserver() {
				@Override
				public void update(Observable o, Object arg) {
					Template=((Response<DiscountTemplate>)arg).result();
					ID = Template.get(0).templateID;

					Platform.runLater(() -> {
						temp.templateID=ID;
						TemplateTable.getItems().add(temp);
						TextSaleName.clear();
						dateS.setText("dd/mm/yyyy");
						dateE.setText("dd/mm/yyyy");
						HoursON.clear();
						HoursOFF.clear();
						TextDiscount.clear();
					});
				}
			});
	    	}
			catch (Exception ex) {ex.printStackTrace();}
			}

		};
	}

	/**
	 * This method deletes a specific discount template from DB
	 * @return event, the method execution
	 */
	private EventHandler<Event> RemoveFunc() {
		return e->
		{
	    	UpdateB.setDisable(true);
	    	RemoveB.setDisable(true);	
	    	CreateB.setDisable(false);
			try {
				store();
	            ID = TemplateTable.getSelectionModel().getSelectedItem().templateID;
		    	TemplateTable.getItems().remove(index);
		    	FXClient.client.request("DELETE FROM saleTemplates WHERE templateID=" + ID);
		    	FXClient.client.request("DELETE FROM activeSales WHERE templateID=" + ID);
		    	TextSaleName.clear();
		    	dateS.setText("dd/mm/yyyy");
		    	dateE.setText("dd/mm/yyyy");
		    	HoursON.clear();
		    	HoursOFF.clear();
		    	TextDiscount.clear();
		    	}
			catch (Exception ex) {ex.printStackTrace();}
				};
	}
	
	private EventHandler<Event> DescriptionFunc() throws IOException{
		return e->
		{
	    	UpdateB.setDisable(true);
	    	RemoveB.setDisable(true);	
	    	CreateB.setDisable(false);
		if(validate())
		{
		try {
	    	SDate=Date.valueOf(DateStart.getValue());
	    	EDate=Date.valueOf(DateEnd.getValue());
            ID = TemplateTable.getSelectionModel().getSelectedItem().templateID;
            Float discount=Float.parseFloat(TextDiscount.getText());
            discount=discount/100;
			roundNumber(discount, 2);
	    	temp=new DiscountTemplate(TextSaleName.getText(),SDate,EDate,Time.valueOf(HoursON.getText()),Time.valueOf(HoursOFF.getText()),discount,"No Active");
			temp.templateID=ID;
	    	TemplateTable.getItems().set(index, temp);
	    	FXClient.client.request("UPDATE saleTemplates SET templateName='"+temp.getTemplateName()+"',startDate='"+temp.getStartDate()+"',endDate='"+temp.getEndDate()+"',activeHoursStart='"+temp.getActiveHoursStart()+"',activeHoursEnd='"+temp.getActiveHoursEnd()+"',discountPercent='"+temp.getDiscountPercent()+"',launched='"+temp.getLaunched()+"'WHERE templateID="+ ID);
	    	TextSaleName.clear();
	    	dateS.setText("dd/mm/yyyy");
	    	dateE.setText("dd/mm/yyyy");
	    	HoursON.clear();
	    	HoursOFF.clear();
	    	TextDiscount.clear();
		}
		catch(Exception ex) {ex.printStackTrace();}
		}

		};
	}

	/**
	 * This method makes sure all the data is correct logically
	 * @return boolean value which indicates if all tests passes
	 */
	private boolean validate() {
		discount=TextDiscount.getText();
		templateName=TextSaleName.getText();
		hourS=HoursON.getText();
		hourE=HoursOFF.getText();
		
		boolean date1selected=DateStart.getValue() !=null;
		toggleClass(dateS, "emptyTextField", !date1selected);
		boolean date2selected=DateEnd.getValue() !=null;
		toggleClass(dateE, "emptyTextField",!date2selected);

		toggleClass(TextDiscount, "emptyTextField", discount.isEmpty());
		toggleClass(TextSaleName, "emptyTextField", templateName.isEmpty());
		toggleClass(HoursON, "emptyTextField", hourS.isEmpty());
		toggleClass(HoursOFF, "emptyTextField", hourE.isEmpty());
		
		return !discount.isEmpty() && !templateName.isEmpty() && !hourS.isEmpty() && !hourE.isEmpty() && date1selected && date2selected;
	}

	/**
	 * This method gets existing information in storage and places it in the correct field
	 */
	private void prefill() {
		if(Storage.get("discount") != null)	TextDiscount.setText((String) Storage.get("discount"));
		if(Storage.get("templateName") != null)	TextSaleName.setText((String) Storage.get("templateName"));
		if(Storage.get("hourS") != null)		HoursON.setText((String) Storage.get("hourS"));
		if(Storage.get("hourE") != null)	HoursOFF.setText((String) Storage.get("hourE"));
		if(Storage.get("dateS") != null)
		{
			DateStart.setValue((LocalDate) Storage.get("dateS"));
			dateS.setText(DateStart.getValue().toString());
		}
		if(Storage.get("dateE") != null)
		{
			DateEnd.setValue((LocalDate) Storage.get("dateE"));
			dateE.setText(DateEnd.getValue().toString());
		}
		Storage.clear();
		
	}


}

