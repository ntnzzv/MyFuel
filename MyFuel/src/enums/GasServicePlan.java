package enums;

public enum GasServicePlan
{
    OCCASIONAL_REFUEL("Occasional Refuel"),
    MONTHLY_SINGLE_CAR_REFUEL("Monthly Single Car Refuel"),
    MONTHLY_MULTIPLE_CARS_REFUEL("Monthly Multiple Cars Refuel"),
    MONTHLY_FIXED_SINGLE_CAR("Monthly Fixed Single Car");

    private final String gasServicePlan;

    GasServicePlan(String string) { gasServicePlan = string; }

    public String toString()
    {
        return this.gasServicePlan;

    }
}
