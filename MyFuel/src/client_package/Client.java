package client_package;

import ocsf.client.AbstractClient;
import java.io.IOException;

import entities.User;

public class Client extends AbstractClient {

	public Object response;
	
	public Client(String host, int port)
	{
		super(host, port);
		try
		{
			openConnection();
			System.out.println("Connected to server @ " + host + ":" + port + ".");
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	@Override
	protected void handleMessageFromServer(Object msg)
	{
		FXClient.observable.setValue(msg);
	}

	
	public <T> void request(String query, Class<T> type) throws IOException
	{
		try
		{
			sendToServer(new Request(query, type));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public <T> void request(String query, Class<T> type, RequestType operation)
	{
		try
		{
			sendToServer(new Request(query, type, operation));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void request(String query) throws IOException
	{
		try
		{
			sendToServer(new Request(query));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}	
}
