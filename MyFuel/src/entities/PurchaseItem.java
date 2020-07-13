package entities;

import enums.FuelType;

public class PurchaseItem
{
	/*
	 * Created during a home heating fuel order
	 */
	public String name;
	public float value;
	public boolean discount;
	public FuelType type;
	
	public PurchaseItem(String name, float value, boolean discount, FuelType type)
	{
		this.name = name;
		this.value = value;
		this.discount = discount;
		this.type = type;
	}
}