package controllers.Customer.PurchaseFuel;

import client_package.FXClient;
import core.IStorable;
import core.Storage;
import core.UIHelper;
import elements.CustomButton;
import entities.CreditCard;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static core.UIHelper.*;

public class CreditCardDialogController implements Initializable, IStorable
{
    @FXML
    private Pane creditCardDialog;
    @FXML private TextField creditCardNumberTxt;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<String> yearComboBox;
    @FXML private TextField cvvTxt;

    private String creditCardNumber, month, year, cvv;
    private CustomButton monthCombo, yearCombo,save;
    @FXML private ImageView cvvQuestionMark;
    @FXML private Button saveCreditCard;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        setupButtons();
        setupEvents();
        refill();

    }

    private void setupButtons(){
        save = new CustomButton("save");
        save.setSize("normal");
        save.setPosition(creditCardDialog.getPrefWidth() -  save.width - 30, creditCardDialog.getPrefHeight() - save.height - 20);
        addElement(creditCardDialog, save);

        monthCombo = new CustomButton("month", 33, 180);
        monthCombo.setSize(85, 25);
        monthCombo.setCustomStyle("button2");
        addElement(creditCardDialog, monthCombo);
        monthComboBox.setFocusTraversable(false);

        monthComboBox.getItems().addAll("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        monthComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            monthCombo.setText(monthComboBox.getSelectionModel().getSelectedItem());
        });

        yearCombo = new CustomButton("Year", 135, 180);
        yearCombo.setSize(85, 25);
        yearCombo.setCustomStyle("button2");
        addElement(creditCardDialog, yearCombo);
        yearComboBox.setFocusTraversable(false);

        yearComboBox.getItems().addAll("2020", "2021", "2022", "2023", "2024", "2025", "2026");
        yearComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            yearCombo.setText(yearComboBox.getSelectionModel().getSelectedItem());
        });

        removeStyleOnFocus("emptyTextField", creditCardNumberTxt, cvvTxt);
    }

    private void setupEvents(){
        save.setOnMouseClicked((event)->{
            if(validate())
                try { store();
                    Stage1Controller.creditCardDialog.hide();}
                catch (Exception ex) { ex.printStackTrace(); }
        });

        monthCombo.setOnMouseClicked(e -> { monthComboBox.show(); toggleClass(monthCombo, "emptyTextField", false); });
        yearCombo.setOnMouseClicked(e -> { yearComboBox.show(); toggleClass(yearCombo, "emptyTextField", false); });

        UIHelper.numbersOnly(creditCardNumberTxt,16);
        UIHelper.numbersOnly(cvvTxt,3);

    }
    private boolean validate()
    {
        creditCardNumber=creditCardNumberTxt.getText();
        cvv=cvvTxt.getText();

        boolean monthSelected = monthComboBox.getSelectionModel().getSelectedIndex() != -1;
        if(monthSelected) month = monthComboBox.getSelectionModel().getSelectedItem();
        toggleClass(monthCombo, "emptyTextField", !monthSelected);

        boolean yearSelected = yearComboBox.getSelectionModel().getSelectedIndex() != -1;
        if(yearSelected) year = yearComboBox.getSelectionModel().getSelectedItem();
        toggleClass(yearCombo, "emptyTextField", !yearSelected);

        toggleClass(creditCardNumberTxt, "emptyTextField", creditCardNumber.isEmpty());
        toggleClass(cvvTxt, "emptyTextField", cvv.isEmpty());

        return !creditCardNumber.isEmpty() && monthSelected && yearSelected && !cvv.isEmpty();
    }

    private void refill(){
        if(Storage.get("creditCardNumber") != null)creditCardNumberTxt.setText((String)Storage.get("creditCardNumber"));
        if(Storage.get("month") != null)monthComboBox.setValue((String)Storage.get("month"));
        if(Storage.get("year") != null)yearComboBox.setValue((String)Storage.get("year"));
        if(Storage.get("cvv") != null)cvvTxt.setText((String)Storage.get("cvv"));
    }

    @Override
    public void store()
    {
        Storage.add("creditCardNumber", creditCardNumber);
        Storage.add("month", month);
        Storage.add("year", year);
        Storage.add("cvv", cvv);
    }


}
