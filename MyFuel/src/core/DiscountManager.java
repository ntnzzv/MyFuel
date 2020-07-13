package core;


import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.sql.Date;

import entities.ActivesSale;
import entities.Car;
import entities.Customer;
import entities.Fuel;
import entities.GasStation;
import entities.PurchasePlan;
import enums.GasServicePlan;

public class DiscountManager
{
	// Helper variables
	public static ArrayList<Fuel> 			fuels;
	public static ArrayList<PurchasePlan> 	plan;
	public static ArrayList<ActivesSale>	sales;
	public static ArrayList<Car> 			customerCars;
	public static GasStation				station = new GasStation();
	
	public static Customer					customer;
	public static Car						specificCar;
	
	public static float getPurchasePlanDiscount()
	{
		// Current Temporary placeholder
		station.name = "Ten";
		
		if(plan.size() == 1)
			if(station.name.compareTo(plan.get(0).gasStation) == 0)
				return 0.05f;
		
		if(plan.size()  > 1)
			for(PurchasePlan p : plan)
				if(p.gasStation.compareTo(station.name) == 0)
					return 0.05f; // Arbitrary 5% discount

		return 0.0f;
	}
	
	public static float getServicePlanDiscount()
	{
		GasServicePlan gsp = null;
		for(GasServicePlan t :GasServicePlan.values())
			if(t.toString().compareTo(customer.gasServicePlan) == 0) gsp = t; 
		
		switch(gsp)
		{
			case OCCASIONAL_REFUEL: return 0;
			case MONTHLY_SINGLE_CAR_REFUEL: return 0.04f;
			case MONTHLY_MULTIPLE_CARS_REFUEL: return customerCars.size() * 0.04f + 0.1f;
			case MONTHLY_FIXED_SINGLE_CAR: return customerCars.size() * 0.04f + 0.13f;
			default: return 0;
		}
	}
	
	public static float getSaleDiscount()
	{
		float best = 0.0f;
		
		Time time = Time.valueOf(LocalTime.now());
		Date date = Date.valueOf(LocalDate.now());
		
		for(ActivesSale sale : sales)
			if(date.after(sale.startDate) && date.before(sale.endDate) && time.after(sale.activeHoursStart) && time.before(sale.activeHoursEnd))
				if(sale.discountPercent > best) best = sale.discountPercent;
				
		return best;
	}
	
	public static ActivesSale getSaleDiscountObject()
	{
		float best = 0.0f;
		ActivesSale s = null;
		
		Time time = Time.valueOf(LocalTime.now());
		Date date = Date.valueOf(LocalDate.now());
		
		for(ActivesSale sale : sales)
			if(date.after(sale.startDate) && date.before(sale.endDate) && time.after(sale.activeHoursStart) && time.before(sale.activeHoursEnd))
				if(sale.discountPercent > best) { best = sale.discountPercent; s = sale; }
		return s;
	}
}