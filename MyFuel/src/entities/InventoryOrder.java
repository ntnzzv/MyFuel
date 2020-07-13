package entities;

import java.io.Serializable;
import java.sql.Date;

public class InventoryOrder implements Serializable {
	/**
	 * this class Linked to a table in DB, so that the data is linked by the column names in the Inventory's table in DB 
	 */
    public Integer Id;
    public String FuelType;
    public String Amount;
    public String Price;
    public Date Date;

    public InventoryOrder() {}

    public InventoryOrder(Integer id,String fuelType,String amount,String price,Date date) {
        this.Id =id;
        this.FuelType =fuelType;
        this.Amount=amount;
        this.Price=price;
        this.Date = date;
    }

    public String getId() { return Id.toString(); }

    public String getFuelType() {
        return FuelType;
    }

    public String getAmount() {
        return Amount;
    }

    public String getPrice() {
        return Price;
    }

    public String getDate(){ return Date.toString(); }

}
