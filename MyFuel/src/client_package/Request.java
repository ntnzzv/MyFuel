package client_package;

import java.io.Serializable;


public class Request<T> implements Serializable{
	
	public Class<T> type;
	public String query;
	public RequestType operation;

	
	public Request(String query, Class <T> type)
	{
		this.operation = RequestType.QUERY;
		this.query = query;
		this.type = type;
	}
	
	public Request(String query)
	{
		this.operation = RequestType.EXECUTE;
		this.query = query;
		this.type = null;
	}
	
	public Request(String query, Class<T> type, RequestType operation)
	{
		this.operation = operation;
		this.query = query;
		this.type = type;
	}	
}
	
