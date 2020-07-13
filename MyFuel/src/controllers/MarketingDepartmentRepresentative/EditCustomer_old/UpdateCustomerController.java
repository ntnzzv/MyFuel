package controllers.MarketingDepartmentRepresentative.EditCustomer_old;
import client_package.FXClient;
import core.Storage;
import entities.Car;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UpdateCustomerController implements Initializable
{
    String customerID, servicePlan, option1, option2, option3,old1,old2,old3, purchasePlanStorage;
    Boolean exclusivePurchasePlan = null;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println(Storage.map);
        getFromStorage();
/*
        Platform.runLater(()-> {
        try {
            updateServicePlan(customerID,servicePlan);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            updatePurchasePlan(customerID,exclusivePurchasePlan);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            updateGasStationsPurchasePlan(customerID,option1,option2,option3,old1,old2,old3);
        } catch (IOException e) {
            e.printStackTrace();
        }

            deleteCarsFromDatabase(customerID,CustomerDetailsController.carsToDelete);
            addCarsToDatabase(customerID,CustomerDetailsController.carsToAdd);


        });

 */
    }

    public void updateServicePlan(String customerID ,String servicePlan) throws IOException
    {
        FXClient.client.request("UPDATE customers SET gasServicePlan = '" + servicePlan + "' WHERE ID= '" + customerID + "'");
    }

    public void updatePurchasePlan(String customerID ,Boolean exclusivePurchasePlan) throws IOException
    {
        FXClient.client.request("UPDATE customers SET exclusivePurchasePlan = " + exclusivePurchasePlan + " WHERE ID= '" + customerID + "'");
    }


    public void updateGasStationsPurchasePlan(String customerID ,String option1, String option2, String option3,
                                       String old1,String old2, String old3) throws IOException
    {
        try {
                if (option1 != null)
                {
                    FXClient.client.request("UPDATE purchasePlans SET gasStation = '" + option1 + "' WHERE ID='" + customerID + "' AND gasStation = '" + old1 + "'");
                }

                //purchasePlans - 2
                if (option2 != null) {
                    FXClient.client.request("UPDATE purchasePlans SET gasStation = '" + option2 + "' WHERE ID='" + customerID + "' AND gasStation = '" + old2 + "'");
                }

                //purchasePlans - 3
                if (option3 != null) {
                    FXClient.client.request("UPDATE purchasePlans SET gasStation = '" + option3 + "' WHERE ID='" + customerID + "' AND gasStation = '" + old3 + "'");
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //update cars

    public void deleteCarsFromDatabase(String customerID, ArrayList<Car> carsToDelete)
    {
        try {
            if (!carsToDelete.isEmpty()) {
                for (Car c : carsToDelete)
                {
                    FXClient.client.request("DELETE FROM cars WHERE customerID= '" + customerID + "' AND carID= '" + c.carID + "'");
                }
            }
        }catch (IOException e) { e.printStackTrace(); }
    }

    public void addCarsToDatabase(String customerID, ArrayList<Car> carsToAdd)
    {
        try {
            if (!carsToAdd.isEmpty()) {
                for (Car c : carsToAdd) {
                    String carsQuery = "INSERT INTO cars (customerID, carID, fuelType) VALUES ";
                    carsQuery += "('" + customerID + "','" + c.getCarID() + "','" + c.getFuelType() + "')";
                    FXClient.client.request(carsQuery);
                }
            }
        }catch (IOException e) { e.printStackTrace(); }

    }


    public void getFromStorage()
    {
        customerID=(String) Storage.get("customerID");

        servicePlan = (String) Storage.get("ServicePlan");

        purchasePlanStorage = (String) Storage.get("purchasePlan");
        if (purchasePlanStorage.equals("Exclusive")) exclusivePurchasePlan = true;
        if (purchasePlanStorage.equals("Non Exclusive")) exclusivePurchasePlan = false;


        option1 = (String) Storage.get("option1");
        option2 = (String) Storage.get("option2");
        option3 = (String) Storage.get("option3");

        old1 = (String) Storage.get("op1Old");
        old2 = (String) Storage.get("op2Old");
        old3 = (String) Storage.get("op3Old");

        Platform.runLater(()-> {
            Storage.clear();
        });
    }
}
