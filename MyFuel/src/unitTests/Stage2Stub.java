package unitTests;

import java.util.ArrayList;

import controllers.Customer.OrderHeatingFuel.IHomeHeatingStage1Manager;
import controllers.Customer.OrderHeatingFuel.IHomeHeatingStage2Manager;
import core.DiscountManager;
import core.Storage;
import entities.ActivesSale;
import entities.CreditCard;

public class Stage2Stub implements IHomeHeatingStage2Manager
{
	public float price;
	public int stock;
	
	public Stage2Stub()
	{
		
	}

	@Override
	public void fetchPrice() {
		price = 3.5f;
	}

	@Override
	public void fetchStock() {
		stock = 100000;
	}

	@Override
	public boolean isInputValid(String amount,int minimum,String fullname,String cardnumber,String year, String cvv,String month,boolean yearSelected,boolean monthSelected,int amountNum) {
		return validate(amount,amountNum,minimum,cardnumber,cvv,fullname,yearSelected,monthSelected);
		
	}


}