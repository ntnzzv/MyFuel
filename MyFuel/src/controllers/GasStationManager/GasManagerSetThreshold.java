package controllers.GasStationManager;

import client_package.FXClient;
import core.UIHelper;
import elements.CustomButton;
import entities.FuelStock;
import entities.GasStation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import server_package.FXServer;
import server_package.Response;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static core.UIHelper.addElement;
import static core.UIHelper.toggleClass;

public class GasManagerSetThreshold implements Initializable {


    @FXML private AnchorPane pane;
    @FXML private TextField petrolField;
    @FXML private Text returnedText;
    @FXML private Line petrolIndicator;
    @FXML private Line dieselIndicator;
    @FXML private Line scooterIndicator;
    @FXML private Line homeIndicator;
    @FXML private TextField dieselField;
    @FXML private TextField scooterField;
    @FXML private TextField homeField;
    @FXML private ProgressBar fuelbarPetrol;
    @FXML private ProgressBar fuelbarDiesel;
    @FXML private ProgressBar fuelbarScooter;
    @FXML private ProgressBar fuelbarHome;
    @FXML private VBox thresholdError;

    private ArrayList<FuelStock> stocks;
    private final double maxFuel = 1000000;
    private String gasStationID;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CustomButton update = new CustomButton("Update");
        update.setPosition(pane.getPrefWidth() -  update.width - 200, pane.getPrefHeight() - update.height - 70);
        update.setSize("wide");
        addElement(pane, update);
        Platform.runLater(this::getStation);
        update.setOnMouseClicked(e->onUpdate());

        UIHelper.numbersOnly(dieselField,9);
        UIHelper.numbersOnly(homeField,9);
        UIHelper.numbersOnly(scooterField,9);
        UIHelper.numbersOnly(petrolField,9);
    }
    /*
     * onUpdate sets the threshold level when the user click the update button and corrects the indicator's position
      */
    @FXML
    private void onUpdate() {

        double newAmountPetrol = Double.parseDouble(petrolField.getText());
        double newAmountHome = Double.parseDouble(homeField.getText());
        double newAmountScooter = Double.parseDouble(scooterField.getText());
        double newAmountDiesel = Double.parseDouble(dieselField.getText());

        if(newAmountPetrol <= maxFuel && newAmountPetrol >= 0) {
            petrolIndicator.setLayoutX(((newAmountPetrol / maxFuel) * fuelbarPetrol.getPrefWidth())+ fuelbarPetrol.getLayoutX());
        }
        if(newAmountDiesel <= maxFuel  && newAmountDiesel >= 0){
            dieselIndicator.setLayoutX((newAmountDiesel / maxFuel) * fuelbarDiesel.getPrefWidth()+fuelbarDiesel.getLayoutX());
        }
        if(newAmountHome <= maxFuel  && newAmountHome >= 0){
            homeIndicator.setLayoutX((newAmountHome / maxFuel) * fuelbarHome.getPrefWidth()+fuelbarHome.getLayoutX());
        }
        if(newAmountScooter <= maxFuel  && newAmountScooter>= 0){
            scooterIndicator.setLayoutX((newAmountScooter / maxFuel) * fuelbarScooter.getPrefWidth()+fuelbarScooter.getLayoutX());
        }
        validate();
    }

    private void getStation(){
        try { FXClient.client.request("SELECT stationID FROM gasStations WHERE manager = '"+FXClient.user.username+"'", GasStation.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg)-> {
            ArrayList<GasStation> stationID = ((Response<GasStation>) arg).result();
            if(stationID.size() > 0) { gasStationID = stationID.get(0).stationID;}
            Platform.runLater(this::fetchThresholdFromDB);
        });
    }
    /**
     * Validates all the fields, proceeds to update the DB if all is correct
     */
    private void validate() {
        toggleClass(petrolField, "emptyTextField", checkFields(petrolField));
        toggleClass(dieselField, "emptyTextField", checkFields(dieselField));
        toggleClass(homeField, "emptyTextField", checkFields(homeField));
        toggleClass(scooterField, "emptyTextField", checkFields(scooterField));

        thresholdError.setVisible(checkFields(petrolField) || checkFields(dieselField) || checkFields(homeField) || checkFields(scooterField));
        if(!thresholdError.isVisible()){updateDB();}
    }

    private boolean checkFields(TextField checkedField){

        return checkedField.getText().isEmpty() || Double.parseDouble(checkedField.getText()) > maxFuel || Double.parseDouble(checkedField.getText()) < 0;
    }

    /**
     * fetches the current threshold level in the DB
     */
    private void fetchThresholdFromDB(){

        try { FXClient.client.request("SELECT * from fuelStock WHERE gasStationID = '"+gasStationID+"'", FuelStock.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg)->{
            stocks = ((Response<FuelStock>)arg).result();
            Platform.runLater(()->{
                for(FuelStock s : stocks){
                    if(s.type.equals("Petrol95")) {
                        petrolField.setText(String.valueOf(s.threshold));
                        fuelbarPetrol.setProgress(s.amount/maxFuel);
                    }
                    else if(
                            s.type.equals("Diesel")){dieselField.setText(String.valueOf(s.threshold));
                            fuelbarDiesel.setProgress(s.amount/maxFuel);
                    }
                    else if(
                            s.type.equals("HeatingFuel")){homeField.setText(String.valueOf(s.threshold));
                            fuelbarHome.setProgress(s.amount/maxFuel);
                    }
                    else if(s.type.equals("Scooter")){
                        scooterField.setText(String.valueOf(s.threshold));
                        fuelbarScooter.setProgress(s.amount/maxFuel);
                    }
                }
                onUpdate();
            });
        });
    }

    /**
     * Updates the database to hold the new threshold levels;
     */
    private void updateDB(){

        try {
         FXClient.client.request("UPDATE fuelStock " +
                "SET threshold = '" +petrolField.getText()+"' "+
                "WHERE type = 'Petrol95' AND gasStationID = '"+gasStationID+"'");
         FXClient.client.request("UPDATE fuelStock " +
                "SET threshold = '" +dieselField.getText()+"' "+
                "WHERE type = 'Diesel' AND gasStationID = '"+gasStationID+"'");
         FXClient.client.request("UPDATE fuelStock " +
                "SET threshold = '" +homeField.getText()+"' "+
                "WHERE type = 'HeatingFuel' AND gasStationID = '"+gasStationID+"'");
         FXClient.client.request("UPDATE fuelStock " +
                "SET threshold = '" +scooterField.getText()+"' "+
                "WHERE type = 'Scooter' AND gasStationID = '"+gasStationID+"'"); }
        catch (IOException e) { e.printStackTrace(); }

    }

}
