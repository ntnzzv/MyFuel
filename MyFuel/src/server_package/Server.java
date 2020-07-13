package server_package;

import core.Console;
import core.ObservableObject;
import entities.User;
import ocsf.server.ConnectionToClient;
import ocsf.server.ObservableServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import client_package.Request;
import client_package.RequestType;

public class Server extends ObservableServer {

	final public static int PORT = 5550;
	public static String address;
	public static String user;
	public static String password;
	public static String schema;
	
	private String lastMessage;	
	public ObservableObject requestFromClientObs, loginToServerObs, startServerObs, disconnectedObs;
	
	Set<String> connectedUsers = new HashSet<>();

	public Server(int port)
	{
		super(port);
		try { this.listen(); }
		catch (Exception e) { Console.err("Could not initialize the server"); }
		finally
		{
			requestFromClientObs = new ObservableObject();
			loginToServerObs = new ObservableObject();
			startServerObs = new ObservableObject();
			disconnectedObs = new ObservableObject();
		}
	}
	
	@Override
	protected void clientConnected(ConnectionToClient client)
	{
		super.clientConnected(client);
	}
	
	@Override
	protected synchronized void clientDisconnected(ConnectionToClient client) {
		super.clientDisconnected(client);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {	
		try
		{
			lastMessage = "Received message from client: " + msg;
			Console.announce(lastMessage);
			
			if(!(msg instanceof Request))
			{
				Console.err(client + " sent a non-request message. (" + msg.getClass() + ")");
				return;
			}
			
			Request req = (Request) msg;
			Response res;
			
			switch(req.operation)
			{
				case QUERY:
				{
					res = new Response(req.type, Database.query(req.type, req.query));
					client.sendToClient(res);
					break;
				}
				case EXECUTE:
				{
					Database.execute(req.query);
					break;
				}
				case LOGIN:
				{
					ArrayList<User> user = Database.query(req.type, req.query);
					
					if(user.size() > 0)
					{
						User u = user.get(0);
						u.connected = !connectedUsers.add(u.username);
					}
					
					loginToServerObs.setValuePersist(user);
					
					res = new Response(req.type, user);
					client.sendToClient(res);
					break;
				}
				case LOGOUT:
				{
					connectedUsers.remove(req.query);
					disconnectedObs.setValuePersist(req.query);
					break;
				}

				default: break;
			}
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally { requestFromClientObs.setValuePersist(lastMessage); }
	}

	@Override
	protected void serverStarted()
	{
		Database.connect(address, user, password, schema);
		Console.announce("Listening for connections @ port " + PORT + ".");
		startServerObs.setValue(null);
		super.serverStarted();
	}

	@Override
	protected void serverClosed()
	{
		try { Database.close(); }
		catch (Exception e) { }
		finally { Console.announce("Stopped listening for connections."); }
		super.serverClosed();
	}
}