package entities;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
	/**
	 * class used to build a table Order and the data used for any orders.
	 */
	public Integer orderID;
	public Integer amount;
	public Date orderDate;
	public Date deliveryDate;
	public Integer urgent;
	public String customerID;
	public String deliveryTime;
	public String street;
	public String house;
	public String city;
	public String zipcode;
	
}
