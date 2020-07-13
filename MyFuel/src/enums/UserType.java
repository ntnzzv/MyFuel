package enums;

public enum UserType {
	CEO ("CEO"),
	GasStationManager ("Gas Station Manager"),
	MarketRep ("Marketing Representative"),
	MarketManager ("Marketing Manager"),
	Customer ("Customer"),
	Supplier ("Supplier");

	private final String name;
	
	UserType(String string) { name = string; }
	
	public String toString()
	{
		return this.name;
	}
	
	public static UserType cast(String type)
	{
		for(UserType u : values())
			if(type.equals(u.toString())) return u;
		
		return null;
	}
}