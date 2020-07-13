package entities;

import java.io.Serializable;

import enums.FuelType;

public class FuelStock implements Serializable {
	/**
	 * class used to build a table in DB and the data used for inventory and orders.
	 */
	public String type;
	public Integer amount;
	public Integer threshold;
	public Float price;
	public String gasStationID;

	public FuelStock() { }
	
	public FuelStock(String type, Integer amount,Integer threshold)
	{
		this.type = type;
		this.amount = amount;
		this.threshold = threshold;
	}
	
	public String getType() { return type; }
	
	public String getAmount() { return amount.toString(); }

	public Integer getThreshold() { return threshold; }

}
