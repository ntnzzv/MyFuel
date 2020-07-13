package entities;

import java.io.Serializable;

import enums.FuelType;

public class SaleTemplateProduct implements Serializable {
/*
 * You can use this to characterize products that are included in the sale.
 */
	public Integer templateID;
	public String fuelType;
	public Float discount;
	
	public SaleTemplateProduct() { }
	
	public SaleTemplateProduct(Integer templateID, String fuelType, Float discount)
	{
		this.templateID = templateID;
		this.fuelType=fuelType;
		this.discount = discount;
	}
	
	public String getTemplateID() 	{ return templateID.toString(); }
	public String getFuelType() 	{ return fuelType; } 
	public String getDiscount()		{ return discount.toString(); }
}
