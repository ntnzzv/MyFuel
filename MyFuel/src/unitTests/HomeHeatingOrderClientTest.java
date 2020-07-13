package unitTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controllers.Customer.OrderHeatingFuel.IHomeHeatingStage1Manager;
import controllers.Customer.OrderHeatingFuel.IHomeHeatingStage2Manager;
import core.DiscountManager;
import core.Storage;
import entities.CreditCard;

class HomeHeatingOrderClientTest {

	IHomeHeatingStage1Manager stub;
	IHomeHeatingStage2Manager controller2Stub;
	@BeforeEach
	void setUp() throws Exception
	{
		Storage.clear();
		stub = new Stage1Stub();
		controller2Stub = new Stage2Stub();
	}
	
	
	@Test
	void emptyStreet_Fail()
	{
		boolean valid = stub.isInputValid("", "15", "Karmiel", "123456", true, true, true);
		assertFalse(valid);
	}
	
	@Test
	void emptyCity_Fail()
	{
		boolean valid = stub.isInputValid("Snonit", "5", "", "789798", true, true, true);
		assertFalse(valid);
	}
	
	@Test
	void allFilled_Success()
	{
		boolean valid = stub.isInputValid("Snonit", "5", "Karmiel", "789798", true, true, true);
		assertTrue(valid);
	}
	
	@Test
	void urgent_Success()
	{
		boolean valid = stub.isInputValid("Snonit", "5", "Karmiel", "789798", true, false, true);
		assertTrue(valid);
	}
	
	@Test
	void notUrgent_Success()
	{
		boolean valid = stub.isInputValid("Snonit", "5", "Karmiel", "789798", true, true, false);
		assertTrue(valid);
	}
	
	@Test
	void fetchExistingCC_Success()
	{
		String customerID = "147258369";
		stub.fetchCC(customerID);
		
		CreditCard cc = (CreditCard) Storage.get("cc");
		
		assertNotNull(cc);
	}
	
	@Test
	void fetchExistingCC_Fail()
	{
		String customerID = "384723191";
		stub.fetchCC(customerID);
		
		CreditCard cc = (CreditCard) Storage.get("cc");
		
		assertNull(cc);
	}
	
	@Test
	void fetchDiscounts_Success()
	{
		int salesCount = 1;
		stub.setupDiscountManager();
		
		assertEquals(DiscountManager.sales.size(), salesCount);
	}
	
	@Test
	void allFilledStub2_Success() {
		boolean valid = controller2Stub.isInputValid("30",10,"Pavel Goli","124134423423","2021",
				"444","09",true,true,60);
		assertTrue(valid);
	}
	
	@Test
	void emptyAmount_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("",10,"Pavel Goli","124134423423","2021",
				"444","09",true,true,60);
		assertFalse(valid);
	}
	
	@Test
	void amountNumLowerThanMinimum_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"Pavel Goli","124134423423","2021",
				"444","09",true,true,5);
		assertFalse(valid);
	}
	
	@Test
	void cardNumberEmpty_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"Pavel Goli","","2021",
				"444","09",true,true,29);
		assertFalse(valid);
	}
	
	@Test
	void cardNumberLessThan9_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"Pavel Goli","1241","2021",
				"444","09",true,true,41);
		assertFalse(valid);
	}
	@Test
	void cvvLessThan3_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"Pavel Goli","124134423423","2021",
				"12","09",true,true,65);
		assertFalse(valid);
	}
	@Test
	void cvvMoreThan3_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"Pavel Goli","124134423423","2021",
				"125142","09",true,true,65);
		assertFalse(valid);
	}
	
	@Test
	void fullNameEmpty_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"","124134423423","2021",
				"125142","09",true,true,65);
		assertFalse(valid);
	}
	@Test
	void yearNotSelected_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"","124134423423","2021",
				"125142","09",false,true,65);
		assertFalse(valid);
	}
	@Test
	void monthNotSelected_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"","124134423423","2021",
				"125142","09",true,false,65);
		assertFalse(valid);
	}
	@Test
	void yearNotSelectedMonthNotSelected_Controller2_Fail() {
		boolean valid = controller2Stub.isInputValid("42",10,"","124134423423","2021",
				"125142","09",false,false,65);
		assertFalse(valid);
	}
	
}
