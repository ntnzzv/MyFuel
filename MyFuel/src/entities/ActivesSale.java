package entities;

import java.io.Serializable;

public class ActivesSale extends DiscountTemplate implements Serializable {
	/**
	 * this class Linked to a table in DB, so that the data is linked by the column names in the activeSale's table in DB 
	 */
	public Integer saleID;
	public Integer templateID;
	
	public ActivesSale() {};
	
	public ActivesSale(Integer saleID, Integer templateID)
	{
		this.saleID = saleID;
		this.templateID = templateID;
	}
}
