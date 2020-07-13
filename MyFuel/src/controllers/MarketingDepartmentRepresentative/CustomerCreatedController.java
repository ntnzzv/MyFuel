package controllers.MarketingDepartmentRepresentative;

import client_package.FXClient;
import core.Storage;
import elements.CustomAnimation;
import entities.Car;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomerCreatedController implements Initializable
{
    @FXML private Pane createdPane;
    @FXML private ImageView cloud1, cloud2, cloud3;
    @FXML private HBox msg;

    private String firstName, lastName, customerID, email, customerType,
            creditCardNumber,month,year, cvv, ServicePlan, hasDalkanStorage, purchasePlanStorage, option1, option2, option3;
    private Boolean hasDalkan = false;
    private Boolean exclusivePurchasePlan=null;


    /**
     * initialises all needed information and elements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println(Storage.map);
        getFromStorage();
        System.out.println(customerID);

        createCustomer(customerID,email,customerType,hasDalkan,exclusivePurchasePlan,ServicePlan,firstName,lastName);

        try {
            addCreditCard(customerID,creditCardNumber,cvv,year,month);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            addCarsToDB(customerID,AddNewCustomer_vehicle_Controller.cars);
            Platform.runLater(()-> {
                AddNewCustomer_vehicle_Controller.cars.clear();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

            try {
                addPurchasePlan(customerID, option1, option2, option3);
            } catch (IOException e) {
                e.printStackTrace();
            }

        animate();
    }

    /**
     * method to schedule entrance of image views
     */
    private void animate()
    {
        new CustomAnimation(cloud1, Duration.seconds(0.5)).fadeIn().translate(-10, 0).delay(Duration.seconds(0.2)).play();
        new CustomAnimation(cloud2, Duration.seconds(0.5)).fadeIn().translate(10, 0).delay(Duration.seconds(0.3)).play();
        new CustomAnimation(cloud3, Duration.seconds(0.5)).fadeIn().translate(10, 0).delay(Duration.seconds(0.4)).play();
        new CustomAnimation(msg, Duration.seconds(0.5)).fadeIn().translate(0, -10).delay(Duration.seconds(0.4)).play();
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
    public void createCustomer(String customerID, String email, String type, Boolean hasDalkan, Boolean exclusivePurchasePlan, String gasServicePlan,
                                      String firstName, String lastName)
    {
        String password = "1234";
        String userType = "Customer";

        //customers
        String customerQuery = "INSERT INTO customers (customerID, email, type, hasDalkan, exclusivePurchasePlan, gasServicePlan) VALUES ";
        customerQuery += "('" + customerID + "','" + email + "','" + type + "'," + hasDalkan + "," + exclusivePurchasePlan + ",'" + gasServicePlan + "')";


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
    //creditCards
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
     * A method to insert new data to tables in DB : cars
     *
     * @param customerID field in cars table, pk in table
     * @param cars an arrayList of cars
     */
    public static void addCarsToDB(String customerID, ArrayList<Car> cars) throws IOException
    {
        try {
            if (!cars.isEmpty()) {
                for (Car c : cars) {
                    String carsQuery = "INSERT INTO cars (customerID, carID, fuelType) VALUES ";
                    carsQuery += "('" + customerID + "','" + c.getCarID() + "','" + c.getFuelType() + "')";
                    FXClient.client.request(carsQuery);
                }
            }
        }catch (IOException e) { e.printStackTrace(); }

    }

    /**
     * A method to insert new data to tables in DB : purchase plans, options of gas stations
     *
     * @param customerID field in credit cards table, pk in table
     * @param option1 gas station option
     * @param option2 gas station option
     * @param option3 gas station option
     */
    public static void addPurchasePlan(String customerID ,String option1, String option2, String option3) throws IOException
    {
            try {
                if(AddNewCustomer_vehicle_Controller.hasVehicles) {
                    //purchasePlans - 1
                    if (option1 != null) {
                        String option1Query = "INSERT INTO purchasePlans (customerID, gasStation) VALUES ";
                        option1Query += "('" + customerID + "','" + option1 + "')";
                        FXClient.client.request(option1Query);
                    }

                    //purchasePlans - 2
                    if (option2 != null) {
                        String option2Query = "INSERT INTO purchasePlans (customerID, gasStation) VALUES ";
                        option2Query += "('" + customerID + "','" + option2 + "')";
                        FXClient.client.request(option2Query);
                    }

                    //purchasePlans - 3
                    if (option3 != null) {
                        String option3Query = "INSERT INTO purchasePlans (customerID, gasStation) VALUES ";
                        option3Query += "('" + customerID + "','" + option3 + "')";
                        FXClient.client.request(option3Query);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

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

        if(AddNewCustomer_vehicle_Controller.hasVehicles)
        {
            ServicePlan = (String) Storage.get("ServicePlan");

            hasDalkanStorage = (String) Storage.get("hasDalkan");
            if (hasDalkanStorage.equals("yes")) hasDalkan = true;
            if (hasDalkanStorage.equals("no")) hasDalkan = false;


            purchasePlanStorage = (String) Storage.get("purchasePlan");
            if (purchasePlanStorage.equals("Exclusive")) exclusivePurchasePlan = true;
            if (purchasePlanStorage.equals("Non Exclusive")) exclusivePurchasePlan = false;


            option1 = (String) Storage.get("option1");
            option2 = (String) Storage.get("option2");
            option3 = (String) Storage.get("option3");

        }

        Platform.runLater(()-> {
            Storage.clear();
            AddNewCustomer_vehicle_Controller.carList.removeAllCars(AddNewCustomer_vehicle_Controller.cars);
        });
    }
}
