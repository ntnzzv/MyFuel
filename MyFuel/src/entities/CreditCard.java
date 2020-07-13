package entities;

import java.io.Serializable;

public class CreditCard implements Serializable {
/*
 * class used to build a table CreditCard and the data used to for inventory and orders by the Customers.
 */
    public String customerID;
    public String cardNumber;
    public String cvv;
    public String expYear;
    public String expMonth;
    public String firstName;
    public String lastName;

    public CreditCard(){}

    public CreditCard(String customerID,String cardNumber,String cvv,String expYear,String expMonth){
        this.customerID = customerID;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expYear = expYear;
        this.expMonth = expMonth;

    }
    public String getCustomerID() {
        return customerID;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public String getExpYear() {
        return expYear;
    }

    public String getExpMonth() {
        return expMonth;
    }
}