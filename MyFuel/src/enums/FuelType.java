package enums;

public enum FuelType {
	
	Petrol95 ("Petrol 95"),
	Diesel ("Diesel"),
	Scooter ("Scooter"),
	HeatingFuel ("Heating Fuel");

	private final String name;
	
	FuelType(String string) {
		name = string; 
		}
	
	public String toString()
	{
		return this.name;
	}
}
