package controllers.MarketingDepartmentRepresentative;

import core.IStorable;
import core.Storage;
import elements.CustomButton;
import elements.CustomTooltip;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.net.URL;
import java.util.ResourceBundle;
import static core.UIHelper.*;

public class AddCreditCardController implements Initializable, IStorable
{
    @FXML
    private   Pane creditCardDialog;
    @FXML private TextField creditCardNumberTxt;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<String> yearComboBox;
    @FXML private TextField cvvTxt;

    private String creditCardNumber, month, year, cvv;
    private CustomButton monthCombo, yearCombo;

    /**
     * This method implements the IStorable interface that allows to store in a hashmap information and pass it between classes
     */
    @Override
    public void store()
    {
        Storage.add("creditCardNumber", creditCardNumber);
        Storage.add("month", month);
        Storage.add("year", year);
        Storage.add("cvv", cvv);
    }

    /**
     * This method gets existing information in storage and places it in the correct field
     */
    private void prefill()
    {
        if(Storage.get("creditCardNumber") != null)	creditCardNumberTxt.setText((String) Storage.get("creditCardNumber"));
        if(Storage.get("month") != null) monthComboBox.getSelectionModel().select((String) Storage.get("month"));
        if(Storage.get("year") != null) yearComboBox.getSelectionModel().select((String) Storage.get("year"));
        if(Storage.get("cvv") != null)	cvvTxt.setText((String) Storage.get("cvv"));
    }

    /**
     * initialises all needed information and elements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        numbersOnly(cvvTxt, 3);
        numbersOnly(creditCardNumberTxt,20);
        toggleClass(creditCardNumberTxt,"textfield1");
        toggleClass(cvvTxt,"textfield1");

        CustomButton save = new CustomButton("save");
        save.setSize("normal");
        save.setPosition(creditCardDialog.getPrefWidth() -  save.width - 30, creditCardDialog.getPrefHeight() - save.height - 20);
        save.setOnMouseClicked(saveCreditCardDetails());
        addElement(creditCardDialog, save);

        //cvv tooltip
        CustomTooltip cvvTT = new CustomTooltip("?", "3 digits on back of card", 90, 233);
        addElement(creditCardDialog, cvvTT);


        monthCombo = new CustomButton("month", 33, 180);
        monthCombo.setSize(85, 25);
        monthCombo.setCustomStyle("button2");
        addElement(creditCardDialog, monthCombo);
        monthComboBox.setFocusTraversable(false);
        monthCombo.setOnMouseClicked(e -> { monthComboBox.show(); toggleClass(monthCombo, "emptyTextField", false); });
        monthComboBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        monthComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            monthCombo.setText(monthComboBox.getSelectionModel().getSelectedItem());
        });

        yearCombo = new CustomButton("Year", 135, 180);
        yearCombo.setSize(85, 25);
        yearCombo.setCustomStyle("button2");
        addElement(creditCardDialog, yearCombo);
        yearComboBox.setFocusTraversable(false);
        yearCombo.setOnMouseClicked(e -> { yearComboBox.show(); toggleClass(yearCombo, "emptyTextField", false); });
        yearComboBox.getItems().addAll("2020", "2021", "2022", "2023", "2024", "2025", "2026");
        yearComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            yearCombo.setText(yearComboBox.getSelectionModel().getSelectedItem());
        });

        removeStyleOnFocus("emptyTextField", creditCardNumberTxt, cvvTxt);
        prefill();
    }

    /**
     * A method to save and store credit card details after validation
     *
     * @return Invokes saving credit card event
     */
    public EventHandler<Event> saveCreditCardDetails()
    {
        return e ->
        {
            if(validate())
                try { store();
                    AddNewCustomer_PersonalInfo_Controller.hasCreditCard=true;
                AddNewCustomer_PersonalInfo_Controller.creditCardDialog.hide();}
                catch (Exception ex) { ex.printStackTrace(); }
        };
    }

    /**
     * This method makes sure all the data is correct logically
     * @return boolean value which indicates if all tests passes
     */
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
}
