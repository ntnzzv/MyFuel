package entities;

import java.io.Serializable;

public class Employee implements Serializable {
	/*
	 * class used to build a table Employee.
	 * Kind of Persona's Employee.
	 */
	public String FirstName;
	public String LastName;
	public Integer ID;
	public String Email;
	public String Rank;
	public String Department;

	public Employee() { }

	public Employee(String fname,String lname,Integer id,String email,String rank,String dep){
		this.FirstName = fname;
		this.LastName = lname;
		this.ID = id;
		this.Email = email;
		this.Rank = rank;
		this.Department = dep;
	}
	public String getFirstName() {
		return FirstName;
	}

	public String getLastName() {
		return LastName;
	}

	public String getID() {
		return ID.toString();
	}

	public String getEmail() {
		return Email;
	}

	public String getRank() {
		return Rank;
	}

	public String getDepartment() {
		return Department;
	}
}