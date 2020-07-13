package core;

import javafx.scene.control.TextField;

public class Validator {
	
	public static boolean IsEmail(String email)
	{
		// TODO Add email validation
		return false;
	}
	
	public static boolean isEmpty(TextField field)
	{
		return field.getText().isEmpty();
	}
}
