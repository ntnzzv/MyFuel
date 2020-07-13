package entities;

import java.io.Serializable;

public class Fuel implements Serializable {
	/**
	 * class used to build a table fuel in DB.
	 * kind of Persona's Fuel.
	 */
	public String	name;
	public Float    maxPrice;
	public String 	type;
	public Float 	price;
	public Fuel() { }

	public Fuel(String name, String type, Float price,Float maxPrice)
	{
		this.name = name;
		this.type=	type;
		this.price=	price;
		this.maxPrice=maxPrice;
	}
	
	public String getName()		{ return name; }
	public String getType() 	{ return type; }
	public String getPrice() 	{ return price.toString(); }
	public String getMaxPrice() 	{ return maxPrice.toString(); }
}