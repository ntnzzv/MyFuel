package controllers.Customer.OrderHeatingFuel;

public interface IHomeHeatingStage2Manager {
	
	public void fetchPrice();
	public void fetchStock();
	
	
	public default boolean validate(String amount, int amountNum, int minimum, String cardNumber, String cvv, String fullName, boolean yearSelected,boolean monthSelected)
	{
		return !amount.isEmpty() && amountNum >= minimum && cardNumber.length() > 9 && cvv.length() == 3 && !fullName.isEmpty() && yearSelected && monthSelected;
	}
	
	public boolean isInputValid(String amount,int minimum,String fullname,String cardnumber,String year, String cvv,String month,boolean yearSelected,boolean monthSelected,int amountNum);
}
