package controllers.MarketingDepartmentRepresentative;

import client_package.FXClient;
import core.Storage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerNoVehiclesController implements Initializable
{
    @FXML
    private Pane createdNoCarsPane;

    private String firstName, lastName, customerID, email, customerType,
            creditCardNumber,month,year, cvv;
    private final Boolean hasDalkan = false;
    private final Boolean exclusivePurchasePlan=null;
    private final String ServicePlan = null;

    /**
     * initialises all needed information and elements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println(Storage.map);
        getFromStorage();
        System.out.println(customerID);

        createCustomer(customerID,email,customerType,hasDalkan,exclusivePurchasePlan,firstName,lastName,ServicePlan);

        try {
            addCreditCard(customerID,creditCardNumber,cvv,year,month);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to insert data to tables in DB : users, customers
     *
     * @param customerID field in customers table, pk in table
     * @param email  field in customers table
     * @param type field in customers table, customer type (private/business)
     * @param hasDalkan field in customers table, true = has dalkan, false = does not have dalkan
     * @param exclusivePurchasePlan null = no purchase plan, 0 - non exclusive, 1 - exclusive
     * @param gasServicePlan field in data base
     * @param firstName field in users table
     * @param lastName field in users table
     */
    public void createCustomer(String customerID, String email, String type, Boolean hasDalkan, Boolean exclusivePurchasePlan,
                               String firstName, String lastName, String gasServicePlan)
    {
        String password = "1234";
        String userType = "Customer";

        //customers
        String customerQuery = "INSERT INTO customers (customerID, email, type, hasDalkan, exclusivePurchasePlan, gasServicePlan) VALUES ";
        customerQuery += "('" + customerID + "','" + email + "','" + type + "'," + hasDalkan + "," + exclusivePurchasePlan + "," + gasServicePlan + ")";


        //users
        String userQuery = "INSERT INTO users (username, password, firstName, lastName, email, userType) VALUES ";
        userQuery += "('" + customerID + "','" + password + "','" + firstName + "','" + lastName + "','" + email + "','" + userType + "')";

        try
        {
            FXClient.client.request(customerQuery);
            FXClient.client.request(userQuery);
        }
        catch (IOException e) { e.printStackTrace(); }

    }


    /**
     * A method to insert new data to tables in DB : credit cards
     *
     * @param customerID field in credit cards table, pk in table
     * @param cardNumber field in credit cards table
     * @param cvv field in credit cards table
     * @param expMonth field in credit cards table
     * @param expYear field in credit cards table
     */
    public static void addCreditCard(String customerID, String cardNumber, String cvv, String expYear, String expMonth) throws IOException
    {
        try {
            if (AddNewCustomer_PersonalInfo_Controller.hasCreditCard) {
                String creditCardQuery = "INSERT INTO creditCards (customerID, cardNumber, cvv, expYear, expMonth) VALUES ";
                creditCardQuery += "('" + customerID + "','" + cardNumber + "','" + cvv + "','" + expYear + "','" + expMonth + "')";
                FXClient.client.request(creditCardQuery);
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * A method to get all information from storage and clear it (storage)
     */
    public void getFromStorage()
    {
        firstName = (String) Storage.get("firstName");
        lastName = (String) Storage.get("lastName");
        customerID = (String) Storage.get("customerID");
        email = (String) Storage.get("email");
        customerType = (String) Storage.get("customerType");

        if(AddNewCustomer_PersonalInfo_Controller.hasCreditCard)
        {
            creditCardNumber = (String) Storage.get("creditCardNumber");
            month = (String) Storage.get("month");
            year = (String) Storage.get("year");
            cvv = (String) Storage.get("cvv");
        }

        Platform.runLater(()-> {
            Storage.clear();
        });


    }
}

