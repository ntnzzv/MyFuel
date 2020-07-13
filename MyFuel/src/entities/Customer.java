package entities;

import enums.CustomerType;
import enums.GasServicePlan;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer extends User implements Serializable
{
/*
 * class used to build a table Customer and do extends to User
 * The data used to get get inside to System.	
 */
	public String customerID;
	public String type;
	public Integer hasDalkan;
	public Integer exclusivePurchasePlan;
	public String gasServicePlan;
	
    public Customer() { }

    public boolean hasDalkan() { return hasDalkan == 1; }
    
//    public Customer(String customerID, String email, CustomerType type, boolean hasDalkan, PurchasePlan purchasePlan, GasServicePlan gasServicePlan) {
//        this.customerID = customerID;
//        this.email = email;
//        this.customerType = type;
//        this.hasDalkan = hasDalkan;
//        this.purchasePlan = purchasePlan;
//        this.gasServicePlan = gasServicePlan;
//    }
//
//    public String getCustomerID() {
//        return customerID;
//    }
//
//    public void setCustomerID(String customerID) {
//        this.customerID = customerID;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//
//    public boolean isHasDalkan() {
//        return hasDalkan;
//    }
//
//    public void setHasDalken(boolean hasDalkan) {
//        this.hasDalkan = hasDalkan;
//    }
//
//    public PurchasePlan getPurchasePlan() {
//        return purchasePlan;
//    }
//
//    public void setPurchasePlan(PurchasePlan purchasePlan) {
//        this.purchasePlan = purchasePlan;
//    }
//
//    public GasServicePlan getGasServicePlan() {
//        return gasServicePlan;
//    }
//
//    public void setGasServicePlan(GasServicePlan gasServicePlan) {
//        this.gasServicePlan = gasServicePlan;
//    }
//
//    public CustomerType getCustomerType() {
//        return customerType;
//    }
//
//    public void setCustomerType(CustomerType customerType) {
//        this.customerType = customerType;
//    }
}
