package entities;

import java.io.Serializable;

public class AnalyticSystem implements Serializable
{
	/**
	 * Link several different tables so that the data in class is matched to the new table.
	 *  So that we can use it for data analysis and presentation of analytical reports 
	 */
    public String customerID;
   public String gasServicePlan;
   public String type;
   public Long fuelTypes;
   public Integer hasDalkan;
   public Integer exclusivePurchasePlan;
   public Long carCount;
   public Integer score = 0;
    public Double profit;
   
   public AnalyticSystem() {}

   public AnalyticSystem(String cus,String purch,Integer sco,Integer dalkan,Integer exc,Long carCount,Long fuelTypes,String customerType) {
	   this.customerID=cus;
	   this.gasServicePlan=purch;
	   this.hasDalkan=dalkan;
	   this.carCount=carCount;
	   this.fuelTypes=fuelTypes;
	   this.exclusivePurchasePlan=exc;
	   this.type = customerType;
	   this.score=sco;
   }
    public String getCustomerID() {
        return customerID;
    }

    public String getGasServicePlan() {
        return gasServicePlan;
    }

    public Long getFuelTypes() {
        return fuelTypes;
    }

    public Integer getHasDalkan() {
        return hasDalkan;
    }

    public Integer getExclusivePurchasePlan() {
        return exclusivePurchasePlan;
    }

    public Long getCarCount() {
        return carCount;
    }

    public Integer getScore() {
        return score;
    }
    public String getType() {
        return type;
    }

    public Double getProfit() {
        return profit;
    }


   public void setScore(Integer sco) {
	   score+=sco;
   }

   






}
