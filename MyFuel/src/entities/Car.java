package entities;

import java.io.Serializable;

public class Car implements Serializable
{
/*
 * class used to build a table cars and the data used to test for purchase fuel.
 */
    public String customerID;
    public String carID;
    public String fuelType;

    public Car() { }

    public Car(String carId, String gasType)
    {
        this.carID = carId;
        this.fuelType = gasType;
    }
    
    public Car(String customerID, String carID, String fuelType)
    {
        this.customerID = 	customerID;
        this.carID = 		carID;
        this.fuelType = 	fuelType;
    }


    public String getCustomerID() { return customerID; }

    public String getCarID() { return carID; }

    public String getFuelType() { return fuelType; }
}