package enums;

public enum CustomerType
{
    PRIVATE ("Private"),
    BUSINESS ("Business");


    private final String customerType;

    CustomerType(String string) { customerType = string; }

    public String toString()
    {
        return this.customerType;

    }
}
