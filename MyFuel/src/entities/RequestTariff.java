package entities;

import java.io.Serializable;

public class RequestTariff implements Serializable {
	/*
	 *class used to build a table Request for new Tariff that sent from MarketingMan to CEO.
	 *The data used for Update fuel price.
	 */
	public Integer ID;
	public String sentBy;
	public String fuelType;
	public Integer discount;
	public Float newPrice;
	public String status;
	
	public RequestTariff() {}
	
	public RequestTariff(String Sen,String Fue,Integer Dis) {
		this.sentBy=Sen;
		this.fuelType=Fue;
		this.discount=Dis;
	}
	public String getID() {
		return ID.toString();
	}
	public String getSentBy() {
		return sentBy;
	}
	public String getFuelType() {
		return fuelType;
	}
	public String getDiscount() {
		return discount.toString();
	}
	public String getNewPrice() {
		return newPrice.toString();
	}
	public String getStatus() {
		return status;
	}
}
