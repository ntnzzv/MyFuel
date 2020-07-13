package controllers.MarketingDepartmentRepresentative;
import client_package.FXClient;
import core.IStorable;
import core.Storage;
import elements.CustomAnimation;
import elements.CustomButton;
import elements.CustomDialog;
import elements.CustomTooltip;
import entities.Customer;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import server_package.Response;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static core.UIHelper.*;


public class AddNewCustomer_PersonalInfo_Controller implements Initializable, IStorable
{
    @FXML private Pane personalInfo;
    @FXML private Pane ccPane;
    @FXML private BorderPane customerError;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField customerIDField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> customerTypeComboBox;
    @FXML private ImageView addcc;
    @FXML private HBox registrationProgress;
    @FXML private HBox validateError;
    @FXML private ImageView circle, hills, girl, greenTree, cloud1, cloud2, cloud3,biggerTree,brownTree,circle2;
    private String firstName, lastName, customerID, email, customerType;
    private CustomButton combo;
    public static CustomDialog creditCardDialog;
    public static boolean hasCreditCard = false;


    /**
     * initialises all needed information and elements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println(Storage.map);

        numbersOnly(customerIDField, 9);
        lettersOnly(firstNameField,20);
        lettersOnly(lastNameField,20);

        toggleClass(firstNameField,"textfield1");
        toggleClass(lastNameField,"textfield1");
        toggleClass(customerIDField,"textfield1");
        toggleClass(emailField,"textfield1");

        //next button
        CustomButton next = new CustomButton("Next");
        next.setSize("normal");
        next.setPosition(personalInfo.getPrefWidth() -  next.width - 30, personalInfo.getPrefHeight() - next.height - 20);
        next.setOnMouseClicked(goToVehicleInfo());
        addElement(personalInfo, next);

        //cc tooltip
        CustomTooltip cc = new CustomTooltip("Add Credit Card Details (optional)", "For future purchases and Dalkan service", 155, 485);
        addElement(personalInfo, cc);

        //type combo
        combo = new CustomButton("Choose Customer Type", 108, 425);
        combo.setSize(170, 34);
        combo.setCustomStyle("button2");
        addElement(personalInfo, combo);
        customerTypeComboBox.setFocusTraversable(false);
        combo.setOnMouseClicked(e -> { customerTypeComboBox.show(); toggleClass(combo, "emptyTextField", false); });
        customerTypeComboBox.getItems().addAll("Private", "Business");
        customerTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            combo.setText(customerTypeComboBox.getSelectionModel().getSelectedItem());
        });

        //CustomDialog credit card
        Platform.runLater(()->
        {
            try {
                creditCardDialog = new CustomDialog(FXClient.lm.frame, "", "MarketingDepartmentRepresentative/NewCustomer/CreditCardDialog");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        removeStyleOnFocus("emptyTextField", firstNameField, lastNameField, customerIDField, emailField);
        prefill();
        animate();
    }

    /**
     * This method implements the IStorable interface that allows to store in a hashmap information and pass it between classes
     */
    @Override
    public void store()
    {
        Storage.add("firstName", firstName);
        Storage.add("lastName", lastName);
        Storage.add("customerID", customerID);
        Storage.add("email", email);
        Storage.add("customerType", customerType);
    }

    /**
     * This method gets existing information in storage and places it in the correct field
     */
    private void prefill()
    {
        if(Storage.get("firstName") != null)	firstNameField.setText((String) Storage.get("firstName"));
        if(Storage.get("lastName") != null)	lastNameField.setText((String) Storage.get("lastName"));
        if(Storage.get("customerID") != null)		customerIDField.setText((String) Storage.get("customerID"));
        if(Storage.get("email") != null)	emailField.setText((String) Storage.get("email"));
        if(Storage.get("customerType") != null) customerTypeComboBox.getSelectionModel().select((String) Storage.get("customerType"));
    }

    /**
     * This method makes sure all the data is correct logically
     * @return boolean value which indicates if all tests passes
     */
    private boolean validate()
    {
        firstName = firstNameField.getText();
        lastName = lastNameField.getText();
        customerID = customerIDField.getText();
        email = emailField.getText();

        boolean typeSelected = customerTypeComboBox.getSelectionModel().getSelectedIndex() != -1;
        if(typeSelected) customerType = customerTypeComboBox.getSelectionModel().getSelectedItem();
        toggleClass(combo, "emptyTextField", !typeSelected);
        toggleClass(firstNameField, "emptyTextField", firstName.isEmpty());
        toggleClass(lastNameField, "emptyTextField", lastName.isEmpty());
        toggleClass(customerIDField, "emptyTextField", customerID.isEmpty());
        toggleClass(emailField, "emptyTextField", email.isEmpty());

        return !firstName.isEmpty() && !lastName.isEmpty() && !customerID.isEmpty() && !email.isEmpty() && typeSelected;
    }

    /**
     * This method makes sure the client is not in DB already, after checking it, uses validation method for rest of the fields
     */
    public void findClient()
    {
            try {
                customerID = customerIDField.getText();
                FXClient.client.request("SELECT * FROM customers C WHERE C.customerID = '" + customerID + "'", Customer.class);
                FXClient.observable.addObserver((o, arg) -> {
                    ArrayList<Customer> temp = ((Response<Customer>) arg).result();
                    if (temp.size() == 0)
                    {
                        if (validate())
                            try {
                                store();
                                Platform.runLater(() -> {
                                    try {
                                        FXClient.lm.set("MarketingDepartmentRepresentative/NewCustomer/VehicleInfo", this);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        else validateError.setVisible(true);
                    } else {
                        customerError.setVisible(true);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    /**
     * A method to save all personal information in storage and move to next stage
     *
     * @return move to next stage event
     */
    private EventHandler<Event> goToVehicleInfo()
    {
        return e ->
        {
            findClient();
        };
    }

    /**
     * A method to open customed dialog when pressing on a fxml element
     */
    @FXML
    void openCreditCardDialog(MouseEvent event) throws IOException
    {
        creditCardDialog.show();
    }


    /**
     * method to schedule entrance of image views
     */
    public void animate()
    {
        new CustomAnimation(girl, Duration.seconds(0.5)).fadeIn().translate(0, -20).delay(Duration.seconds(0.7)).play();
        new CustomAnimation(hills, Duration.seconds(0.5)).fadeIn().translate(0, -10).delay(Duration.seconds(0.2)).play();
        new CustomAnimation(cloud1, Duration.seconds(0.5)).fadeIn().translate(-10, 0).delay(Duration.seconds(0.4)).play();
        new CustomAnimation(cloud2, Duration.seconds(0.5)).fadeIn().translate(-10, 0).delay(Duration.seconds(0.5)).play();
        new CustomAnimation(cloud3, Duration.seconds(0.5)).fadeIn().translate(-10, 0).delay(Duration.seconds(0.6)).play();
        new CustomAnimation(biggerTree, Duration.seconds(0.5)).fadeIn().translate(0, -10).delay(Duration.seconds(0.7)).play();
        new CustomAnimation(greenTree, Duration.seconds(0.5)).fadeIn().translate(0, 0).delay(Duration.seconds(0.7)).play();
        new CustomAnimation(brownTree, Duration.seconds(0.5)).fadeIn().translate(0, 0).delay(Duration.seconds(0.7)).play();
    }
}