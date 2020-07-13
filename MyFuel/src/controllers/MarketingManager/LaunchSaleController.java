package controllers.MarketingManager;

import client_package.FXClient;
import core.GenericObserver;
import core.UIHelper;
import elements.CustomButton;
import elements.CustomDialog;
import entities.DiscountTemplate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import server_package.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import static core.UIHelper.addElement;

public class LaunchSaleController implements Initializable {

    @FXML
    private Pane Pane;

    @FXML
    private TableView<DiscountTemplate> TemplateTable;
    private CustomButton launchSale,RemoveSale;
    private Integer templateID;
    private DiscountTemplate temp;
    private ArrayList<DiscountTemplate> templateArray;
    @FXML private DialogPane SuccessfulMassage;
    int Index;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtons();
        populateTable();
    }
    /**
     * set Style and information per Button.
     */
    void setButtons(){
        launchSale = new CustomButton("Launch Sale",565,420);
        launchSale.setSize("normal");
		launchSale.setCustomStyle("button3");
        launchSale.setDisable(true);
        addElement(Pane,launchSale);
        
        /**
         * Creat new sale in DB.
         */
        launchSale.setOnMouseClicked(e->{
            try {
                FXClient.client.request("INSERT INTO activeSales (templateID)\n" +
                        "SELECT * FROM (SELECT '"+templateID+"') AS tmp\n" +
                        "WHERE NOT EXISTS (\n" +
                        "SELECT templateID FROM activeSales WHERE templateID = '"+templateID+"'\n" +
                        ") LIMIT 1;");
                temp.launched="Active";
            	TemplateTable.getItems().set(Index, temp);
    	    	FXClient.client.request("UPDATE saleTemplates SET launched='"+temp.getLaunched()+"'WHERE templateID="+ templateID);
            } catch (IOException exc) {
                exc.printStackTrace();
            }
            SuccessfulMassage.setVisible(true);
        });
        RemoveSale = new CustomButton("Remove Sale",363,420);
        RemoveSale.setSize("normal");
		RemoveSale.setCustomStyle("button3");
        RemoveSale.setDisable(true);
        addElement(Pane,RemoveSale);
        
        RemoveSale.setOnMouseClicked(e->{
        	try {
        		temp.launched="No active";
        		TemplateTable.getItems().set(Index, temp);
		    	FXClient.client.request("DELETE FROM activeSales WHERE templateID=" + templateID);
    	    	FXClient.client.request("UPDATE saleTemplates SET launched='"+temp.getLaunched()+"'WHERE templateID="+ templateID);
        	}
        	catch(Exception e1) {e1.printStackTrace();}
            SuccessfulMassage.setVisible(true);
        });
    }
    /**
     * set Data information from DB.
     */
    void populateTable() {
        try {
            FXClient.client.request("SELECT * FROM saleTemplates", DiscountTemplate.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FXClient.observable.addObserver(new GenericObserver() {
            @Override
            public void update(Observable o, Object arg) {
                templateArray = ((Response<DiscountTemplate>) arg).result();
                Platform.runLater(() -> {
                    UIHelper.FillTable(TemplateTable, templateArray);

                });
            }
        });
    }
	/**
	 * handles row selection from the table
	 * @param event mouseEvent that triggered the method
	 */
    @FXML
    void SelectRow(MouseEvent event) {
        if(!TemplateTable.getSelectionModel().isEmpty())
        {
        	RemoveSale.setDisable(false);
            launchSale.setDisable(false);
            SuccessfulMassage.setVisible(false);
            temp=TemplateTable.getSelectionModel().getSelectedItem();
            templateID = TemplateTable.getSelectionModel().getSelectedItem().templateID;
            Index=TemplateTable.getSelectionModel().getSelectedIndex();
        }
    }
}
