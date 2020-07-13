package entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class GasRestockBySupplier implements Serializable {
/**
 * this class used to build a table
 * the data used to test for inventory and orders.
 */
    public Integer orderID;
    public Date orderDate;
    public Integer stationID;
    public String fuelType;
    public Integer amount;
    public String status;
    public String comment;

    public GasRestockBySupplier() {}

    public GasRestockBySupplier(Integer orderID, Date orderDate, Integer stationID, String fuelType, Integer amount, String status, String comment) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.stationID = stationID;
        this.fuelType = fuelType;
        this.amount = amount;
        this.status = status;
        this.comment = comment;
    }

    public String getOrderID() {
        return orderID.toString();
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate.toString();
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStationID() {
        return stationID.toString();
    }

    public void setStationID(Integer stationID) {
        this.stationID = stationID;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getAmount() {
        return amount.toString();
    }


    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comments) {
        this.comment = comments;
    }
}
