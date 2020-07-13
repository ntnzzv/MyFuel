package core;

import java.io.IOException;
import java.time.LocalDate;

import client_package.FXClient;
import entities.Fuel;
import entities.FuelStock;
import enums.FuelType;

public class  TransactionManager {

	/**
	 * order method is connected to the order home heating fuel and updates all tables according to the transcation
	 * @param customerID the ID of the customer making the transaction
	 * @param deliveryDate the date to be delivered
	 * @param deliveryTime the time of the delivery
	 * @param urgent if the order is urgent then 1
	 * @param street the street of the customer
	 * @param house the house of the customer
	 * @param city the city of the customer
	 * @param zipcode the zipcode of the customer
	 * @param price the total price of the order
	 * @param amount the amount of liters to be supplied
	 * @param saleID the saleID for the table
	 * @param gasStationID the gasstationID the provides the order
	 */
	public static void order(String customerID, LocalDate deliveryDate, String deliveryTime, boolean urgent, String street, String house, String city, String zipcode, float price, String amount, int saleID,int gasStationID)
	{
		String orderQuery = "INSERT INTO orders (customerID, orderDate, deliveryTime, deliveryDate, urgent, street, house, city, zipcode, amount) VALUES ";
		orderQuery += "('" + customerID + "','" + LocalDate.now().toString() + "','" + deliveryTime + "','" + deliveryDate + "'," + urgent + ",'" + street + "','" + house + "','" + city + "','" + zipcode + "'," + amount + ")";
		String purchaseQuery = "INSERT INTO purchase (customerID, price, gasStation, quantity, fuelType, saleID, purchaseDateTime) VALUES ";
		purchaseQuery += "('" + customerID + "'," + price  + ",(SELECT name FROM gasStations WHERE stationID = '"+gasStationID+"'),'" + amount + "','" + FuelType.HeatingFuel + "', " + saleID +", DATE_ADD(NOW(), INTERVAL 3 HOUR))";
		String subtractStock = "UPDATE fuelStock SET `amount` = `amount` - " + amount + " WHERE type = 'HeatingFuel' AND gasStationID = "+gasStationID;

		try
		{
			FXClient.client.request(orderQuery);
			FXClient.client.request(purchaseQuery);
			FXClient.client.request(subtractStock);
			FXClient.client.request("CALL tryRequestRestock('HeatingFuel', " + Integer.parseInt(amount) + ")");
		}
		catch (IOException e) { e.printStackTrace(); }
		
	}

	/**
	 * refuel method is connected to the purchase fuel (refuel) at the gas station, updates all the tables accordingly to the purchase
	 * @param customerID the customer's ID
	 * @param price the price of the refuel
	 * @param amount the amount in liters that was refueled
	 * @param fueltype the fuel type that was refilled
	 * @param currentFuelStock the current fuel stock from the specific station
	 * @param saleID the sale ID
	 * @param gasStationID the gas station ID that the refuel was purchased
	 */
	public static void refuel(String customerID, float price, Integer amount, Fuel fueltype, FuelStock currentFuelStock, int saleID,Integer gasStationID)
	{
		String purchaseQuery = "INSERT INTO purchase (customerID, price, gasStation, quantity, fuelType, saleID, purchaseDateTime) VALUES ";
		purchaseQuery += "('" + customerID + "'," + price + ",(SELECT name FROM gasStations WHERE stationID = '"+gasStationID+"'), '" + amount + "', '" + fueltype.type + "'," + saleID + ", DATE_ADD(NOW(), INTERVAL 3 HOUR))";
		int newStock = (currentFuelStock.amount - amount);
		String stockQuery = "UPDATE fuelStock SET amount = '" + newStock + "' WHERE type = '" + fueltype.type + "' AND gasStationID = "+gasStationID;
		String stockRequestQuery = "INSERT INTO fuelRestockRequest (orderDate,stationID,fuelType,amount,status) VALUES ";
		stockRequestQuery += "('"+ LocalDate.now().toString() + "'," + gasStationID + ",'"+currentFuelStock.type+"'," + (1000000-newStock) + ",'" + "Pending" + "')";

		try {
			FXClient.client.request(purchaseQuery);
			FXClient.client.request(stockQuery);
			if (newStock < currentFuelStock.threshold) {
				FXClient.client.request(stockRequestQuery);
			}
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	}
