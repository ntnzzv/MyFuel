package entities;

import enums.FuelType;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Purchase implements Serializable
{
	/*
	 *  class used to build a table Purchase and the data used for reports and inventory and restock.
	 */
    public Integer purchaseID;
    public String customerID;
    public Float price;
    public String gasStation;
    public String quantity;
    public String fuelType;
    public Integer saleID;
    public Double sum;
    public Double QuarterlyIncome;
    public Long sum2;
    public Long purchaseSum;
    public Timestamp purchaseDateTime;
    public Purchase()
    {
    }

    public Purchase(Integer purch, String cust, Float price, String gs, String qty, String ft, Integer saleid,Double sum,Long purchaseSum,Double QuarterlyIncome) {
        this.purchaseID = purch;
        this.customerID = cust;
        this.price = price;
        this.gasStation = gs;
        this.quantity = qty;
        this.fuelType = ft;
        this.saleID = saleid;
        this.sum = sum;
        this.purchaseSum = purchaseSum;
        this.QuarterlyIncome = QuarterlyIncome;
    }
    public Long getPurchaseSum() {
        return purchaseSum;
    }
    public Integer getPurchaseID() {
        return purchaseID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public Float getPrice() {
        return price;
    }

    public String getGasStation() {
        return gasStation;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getFuelType() {
        return fuelType;
    }

    public Integer getSaleID() {
        return saleID;
    }

    public Double getSum() { return sum; }

    public Double getQuarterlyIncome() {
        return QuarterlyIncome;
    }
    //depends on discounts for customer
    public int calculateBill()
    {
        int totalBill = 0;
        return totalBill;
    }





}