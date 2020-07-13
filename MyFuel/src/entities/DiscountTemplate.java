package entities;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import client_package.FXClient;

@SuppressWarnings("serial")
public class DiscountTemplate implements Serializable {
	/*
	 * class used to build a table Template and the data used to check and upDate the fuel price and for Sales.
	 */
	public Integer templateID;
	public String templateName;
	public Date startDate;
	public Date endDate;
	public Time activeHoursStart;
	public Time activeHoursEnd;
	public Float discountPercent;
	public String launched;
	
	public DiscountTemplate() {}
	
	public DiscountTemplate(String salename, Date startdate, Date enddate, Time hourson, Time hoursoff,Float discount,String la)
	{
		this.templateName = 	salename;
		this.startDate    = 	startdate;
		this.endDate      =     enddate;
		this.activeHoursStart = hourson;
		this.activeHoursEnd   = hoursoff;
		this.discountPercent  = discount;
		this.launched=la;
	}
	
	public String getTemplateID() 		{ return templateID.toString(); }
	public String getTemplateName() 	{ return templateName; }
	public String getStartDate() 		{ return startDate.toString(); }
	public String getEndDate() 			{ return endDate.toString(); }
	public String getActiveHoursStart() { return activeHoursStart.toString(); }
	public String getActiveHoursEnd() 	{ return activeHoursEnd.toString(); }
	public String getDiscountPercent() 	{ return discountPercent.toString(); }
	public String getLaunched() 			{ return launched; }

}
