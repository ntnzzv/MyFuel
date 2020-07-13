package controllers.MarketingDepartmentRepresentative.EditCustomer_old;

import client_package.FXClient;
import core.IStorable;
import core.Storage;
import entities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import server_package.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import static core.UIHelper.numbersOnly;

public class SearchCustomerController implements Initializable, IStorable
{
    @FXML private TextField idtxt;
    @FXML private Button searchbtn;
    boolean exist = false;
    String id,firstName,lastName,gasServicePlan, purchasePlan;
    Integer exclusivePurchasePlan;
    public static ArrayList<String> options = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        numbersOnly(idtxt, 9);

        searchbtn.setOnMouseClicked(e->{
            findClient();
        });
    }


    private void findClient()
    {
        try {
            id = idtxt.getText();
            FXClient.client.request("SELECT * FROM customers C WHERE C.customerID = '" + id + "'", Customer.class);
            FXClient.observable.addObserver((o, arg) -> {
                ArrayList<Customer> temp = ((Response<Customer>) arg).result();
                if (temp.size() > 0) {
                    Platform.runLater(() -> {
                            fetchCustomerFromDatabase();
                            fetchUserFromDatabase();

                    });
                } else {
                    System.out.println("error");
                }
            });
        }catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public void store()
    {
        Storage.add("customerID", id);
        Storage.add("firstName", firstName);
        Storage.add("lastName", lastName);
        Storage.add("servicePlan",gasServicePlan);
        Storage.add("purchasePlan", purchasePlan);
        Platform.runLater(()->{
            try {
                FXClient.lm.set("MarketingDepartmentRepresentative/ClientCard/customerDetails", this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * fetches all of the cars the client has registered under his name
     */
    private void fetchCustomerFromDatabase()
    {
        try { FXClient.client.request("SELECT * FROM customers WHERE customerID = '" + id + "'", Customer.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg) -> {
            ArrayList<Customer> temp = ((Response<Customer>) arg).result();
            if(temp.size()>0) {
                for (Customer c : temp) {
                    gasServicePlan = c.gasServicePlan;
                    exclusivePurchasePlan = c.exclusivePurchasePlan;
                }
                if (exclusivePurchasePlan == 1) {
                    purchasePlan = "Exclusive";
                    Platform.runLater(this::fetchOptionsPlanFromDatabase);
                }
                if (exclusivePurchasePlan == 0) {
                    purchasePlan = "Non Exclusive";
                    Platform.runLater(this::fetchOptionsPlanFromDatabase);
                }
                if (exclusivePurchasePlan == null) purchasePlan = "None";
            }

        });
    }

    private void fetchUserFromDatabase()
    {
        try { FXClient.client.request("SELECT * FROM users WHERE username = '" + id + "'", User.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg) -> {
            ArrayList<User> temp = ((Response<User>) arg).result();
            if(temp.size()>0) {
                for (User u : temp) {
                    firstName = u.firstName;
                    lastName = u.lastName;
                }
            }
            if(firstName != null && lastName != null){
                store();

            }
        });
    }

    /**
     * fetches all of
     */
    private void fetchOptionsPlanFromDatabase()
    {
        try { FXClient.client.request("SELECT * FROM purchasePlans WHERE customerID = '" + id + "'", PurchasePlan.class); }
        catch (IOException e) { e.printStackTrace(); }

        FXClient.observable.addObserver((o,arg) -> {
            ArrayList<PurchasePlan> temp = ((Response<PurchasePlan>) arg).result();
            if(temp.size()>0)
            {
                for (PurchasePlan plan : temp)
                {
                    System.out.println(plan.gasStation);
                    options.add(plan.gasStation);
                }
            }
        });
    }
}
