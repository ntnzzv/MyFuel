package controllers.Customer.OrderHeatingFuel;

public interface IHomeHeatingStage1Manager {
	
	public void fetchCC(String customerID);
	public void setupDiscountManager();
	
	public default boolean validate(String street, String house, String city, String zipcode, boolean dateSelected, boolean timeSelected, boolean checkbox)
	{
		return !street.isEmpty() && !house.isEmpty() && !city.isEmpty() && !zipcode.isEmpty() && dateSelected && (timeSelected || checkbox);
	}
	
	public boolean isInputValid(String street, String house, String city, String zipcode, boolean dateSelected, boolean timeSelected, boolean checkbox);
}
