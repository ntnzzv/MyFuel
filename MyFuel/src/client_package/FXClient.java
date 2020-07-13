package client_package;

import java.io.IOException;

import core.LayoutManager;
import core.ObservableObject;
import core.SmartObservable;
import entities.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FXClient extends Application {
	
	public static LayoutManager lm;
	public static Client client;
	public static User user;
	public static SmartObservable observable = new SmartObservable(10); // observables for data sync

	/**
	 * loads the client front-end application
	 * @param stage the stage to be loaded first
	 */
	@Override
	public void start(Stage stage) {
		
		try { lm = new LayoutManager(stage, "client"); }
		catch(Exception e) { e.printStackTrace(); }
	}

	/**
	 * stops the connection with the server
	 * @throws Exception if couldn't close the connection to the server
	 */
	@Override
	public void stop() throws Exception {
		Platform.runLater(() -> {
			if(client != null)
				if(client.isConnected())
					try
					{
						if(user != null) client.request(user.username, User.class, RequestType.LOGOUT);
						client.closeConnection();
					}
					catch (IOException e) { e.printStackTrace(); }
		});
		super.stop();
	}
	public static void main(String[] args) {
		launch(args);
	}
}