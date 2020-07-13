package unitTests;

import java.util.ArrayList;

import controllers.Customer.OrderHeatingFuel.IHomeHeatingStage1Manager;
import core.DiscountManager;
import core.Storage;
import entities.ActivesSale;
import entities.CreditCard;

public class Stage1Stub implements IHomeHeatingStage1Manager
{
	public ArrayList<CreditCard> cards;
	public ArrayList<ActivesSale> sales;
	
	public Stage1Stub()
	{
		sales = new ArrayList<ActivesSale>();
		cards = new ArrayList<CreditCard>();
		
		CreditCard cc1 = new CreditCard("147258369", "1234123412341234", "500", "2023", "06");
		CreditCard cc2 = new CreditCard("987654321", "2587258714796314", "133", "2025", "12");
		
		cards.add(cc1);
		cards.add(cc2);
		
		ActivesSale s1 = new ActivesSale(1, 10);
		s1.discountPercent = 0.05f;
		
		sales.add(s1);
	}
	
	@Override
	public void fetchCC(String customerID)
	{
		for(CreditCard cc : cards)
			if(cc.customerID.compareTo(customerID) == 0) Storage.add("cc", cc);
	}

	@Override
	public void setupDiscountManager()
	{
		DiscountManager.sales = sales;
	}

	@Override
	public boolean isInputValid(String street, String house, String city, String zipcode, boolean dateSelected, boolean timeSelected, boolean checkbox)
	{
		return validate(street, house, city, zipcode, dateSelected, timeSelected, checkbox);
	}


}