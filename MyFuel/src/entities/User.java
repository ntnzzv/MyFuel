package entities;

import java.io.Serializable;

import enums.UserType;

public class User implements Serializable {
	/*
	 * customer extends it when user represents the user base in the syste.
	 * The basic information to characterize a user.
	 */
	public String username;
	public String password;
	public boolean connected;
	public String firstName;
	public String lastName;
	public String email;
	public String userType;
	
	public UserType getType()
	{
		return UserType.cast(userType);
	}
}
