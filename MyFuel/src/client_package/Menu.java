package client_package;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import elements.MenuButton;
import enums.UserType;

/**
 * holds all the FXML paths and the images which are used in the side menu with their description
 */
public class Menu {

	public static HashMap<UserType, List<MenuButton>> buttons = setPermissions();

	private static HashMap<UserType, List<MenuButton>> setPermissions()
	{
		HashMap<UserType, List<MenuButton>> buttons = new HashMap<UserType, List<MenuButton>>();

		List<MenuButton> ceo, stationManager, marketingManager, marketRep, customer, supplier;

		ceo = 				Arrays.asList(	new MenuButton("Fuel Prices", "CEO/CEOUpdater", "ceiling"),
											new MenuButton("Adjust Tarrifs", "CEO/CEOConfirmTariff", "maxprice"));


		marketingManager = 	Arrays.asList(	new MenuButton("Adjust Tarrifs","MarketingManager/MarketManRequest","maxprice"),
											new MenuButton("Produce Reports","MarketingManager/Reports","produceReports"),
											new MenuButton("Launch Sale","MarketingManager/launchSale","launchSale"),
											new MenuButton("Customer Analytics","AnalyticSystem","viewAnalytics"));

		marketRep = 		Arrays.asList(	new MenuButton("New Customer", "MarketingDepartmentRepresentative/NewCustomer/PersonalInfo", "addUser"),
											new MenuButton("New Discount Template","MarketingDepartmentRepresentative/CreatDiscountTemplate","discountTemplate"),
											new MenuButton("Customer Analytics","AnalyticSystem","viewAnalytics"),
											new MenuButton("Customer Profile", "MarketingDepartmentRepresentative/EditCustomer", "userProfile"));

		customer = 			Arrays.asList(	new MenuButton("Order Heating Fuel", "Customer/OrderHeatingFuel/Stage1", "heatingfuel"),
											new MenuButton("Track Orders", "Customer/TrackOrders", "track"),
											new MenuButton("Purchase Fuel","Customer/PurchaseFuel/Stage1","purchasefuel"));


		stationManager = 	Arrays.asList(	new MenuButton("Manage Fuel Inventory","GasStationManager/ManageInventory","manageInventory"),
											new MenuButton("Produce Reports","GasStationManager/managerReports","produceReports"),
											new MenuButton("Manage Threshold Alert","GasStationManager/SetThreshold","fuelThreshold"));


		supplier = 			Arrays.asList(	new MenuButton("Update Order Status","Supplier/updateOrderBySupplier","updateOrderStatus"));

		buttons.put(UserType.CEO, ceo);
		buttons.put(UserType.GasStationManager, stationManager);
		buttons.put(UserType.MarketRep, marketRep);
		buttons.put(UserType.MarketManager, marketingManager);
		buttons.put(UserType.Customer, customer);
		buttons.put(UserType.Supplier, supplier);

		return buttons;
	}

	public static List<MenuButton> getButtons(UserType type)
	{
		return buttons.get(type);
	}
}