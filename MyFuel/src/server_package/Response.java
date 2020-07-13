package server_package;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Helper class holding reponses from the server going to the client side, used for storing table content in array list structures
 * @param <T>
 */
public class Response<T> implements Serializable {

	ArrayList<T> result;
	public Class<T> type;

	public Response(Class<T> type, ArrayList<T> result) {
		this.type = type;
		this.result = result;
	}
	
	/**
	 * Returns the results as an ArrayList based on the client request class.
	 * @return generic arraylist holding the client request data
	 */
	public ArrayList<T> result()
	{
		return result;
	}
}