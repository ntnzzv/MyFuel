package entities;

import java.io.Serializable;
import enums.*;

public class Product implements Serializable
{
/*
 * class used to build a table Product and the data used for orders and for reports.
 */
	public Float Price;
	public Integer ID;
	public FuelType FuelType;
	public Float Stock;
	
	public Product(Integer id,Float price,String gastype,Float stock) {
		this.ID=id;
		enums.FuelType.valueOf(gastype);
		this.Price=price;
		this.Stock=stock;
	}
	public String getID() {
		return ID.toString();
	}
	public void setID(Integer id) {
		this.ID=id;
	}
	public String getPrice() {
		return Price.toString();
	}
	public void setPrice(Float price) {
		this.Price=price;
	}
    public FuelType getFuelType() {
        return FuelType;
    }
    public void setFuelType(FuelType fuelType) {
        this.FuelType = fuelType;
    }
    public String getStock() {
    	return Stock.toString();
    }
    public void setStock(Float stock) {
    	this.Stock=stock;
    }
    public void addStock(Float stock) {
    	Stock+=stock;
    }
    public void subtracStock(Float stock) {
    	Stock-=stock;
    }
	
	
}
