package unitTests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import entities.*;
import server_package.Database;
import server_package.Server;

class HomeHeatingOrderServerTest
{
	String customerID;

	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		Server.address = "bwdks5pz9oy2ihgpki8i-mysql.services.clever-cloud.com:20456";
		Server.schema = "bwdks5pz9oy2ihgpki8i";
		Server.user = "uhwrnbb2fgrpofai";
		Server.password = "jKnTpVowm84GhG5D9yO9";
		Database.connect(Server.address, Server.user, Server.password, Server.schema);
	}
	
	@AfterAll
	static void killConnection()
	{
		Database.execute("DELETE FROM orders WHERE customerID = 'customerTest'");
		Database.close();
	}

	@Test
	void fetchCCTest_creditCardExists_Success()
	{
		customerID = "123123123";

		ArrayList<CreditCard> customer = Database.query(CreditCard.class, "SELECT customerID, cardNumber, cvv, expYear, expMonth, firstName, lastName FROM creditCards CC, users U WHERE CC.customerID = U.username AND CC.customerID = '" + customerID + "'");
		assertTrue(customer.size() == 1);
	}

	@Test
	void fetchCCTest_creditCardDoesntExist_Success()
	{
		customerID = "98094824";

		ArrayList<CreditCard> customer = Database.query(CreditCard.class, "SELECT customerID, cardNumber, cvv, expYear, expMonth, firstName, lastName FROM creditCards CC, users U WHERE CC.customerID = U.username AND CC.customerID = '" + customerID + "'");
		assertTrue(customer.size() == 0);
	}

	@Test
	void setupDiscountManagerTest_activeSaleExists_Success()
	{
		ArrayList<ActivesSale> sales = Database.query(ActivesSale.class,
				"SELECT * FROM activeSales A, saleTemplates T WHERE A.templateID = T.templateID");
		assertTrue(sales.size() == 2);
	}

	@Test
	void calculateSumPurchaseBySaleIdTest_SumPurchaseEachSale_Success()
	{
		ArrayList<Purchase> sum = Database.query(Purchase.class,
				"SELECT SUM(P.price*P.quantity) AS sum FROM  purchase P WHERE P.saleID = '18'");
		assertTrue(sum.get(0).sum != Double.parseDouble("949"));
	}

	@Test
	void calculateSumPurchaseBySaleIdTest2_SumPurchaseEachSale_Success()
	{
		ArrayList<Purchase> sum = Database.query(Purchase.class, "SELECT SUM(P.price*P.quantity) AS sum FROM  purchase P WHERE P.saleID = '19'");
		assertTrue(sum.get(0).sum != Double.parseDouble("172"));
	}

	@Test
	void countPurchasedBySaleIdTest1_SumPurchaseEachSale_Success()
	{
		ArrayList<Purchase> sum = Database.query(Purchase.class, "SELECT * FROM purchase WHERE saleID = '18' GROUP BY customerID");
		assertTrue(sum.size() >= 1);
	}

	@Test
	void countPurchasedBySaleIdTest2_SumPurchaseEachSale_Success()
	{
		ArrayList<Purchase> sum = Database.query(Purchase.class, "SELECT * FROM purchase WHERE saleID = '19' GROUP BY customerID");
		assertTrue(sum.size() >= 1);
	}
	
	@Test
	void orderPlaced_Success()
	{
		String customerID = "customerTest", street = "Snonit", house = "51", city = "Karmiel", zipcode = "123456", deliveryTime = "12:00 - 16:00";
		int amount = 300;
		boolean urgent = true;
		
		String query = "INSERT INTO orders (customerID, orderDate, deliveryTime, urgent, deliveryDate, street, house, city, zipcode, amount) VALUES ";
		query += "('" + customerID + "', CURDATE(), '" + deliveryTime + "'," + urgent + ", DATE_ADD(CURDATE(), INTERVAL + 30 DAY), '" + street + "', '" + house + "', '" + city + "', '" + zipcode + "', " + amount + ")";
		
		Database.execute(query);
		
		ArrayList<Order> order = Database.query(Order.class, "SELECT * FROM orders WHERE customerID = '" + customerID +"'");
		
		assertTrue(order.size() == 1);
	}
}