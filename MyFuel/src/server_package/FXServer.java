package server_package;

import java.io.IOException;

import core.LayoutManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FXServer extends Application {

	public static LayoutManager lm;
	public static Server server;

	/**
	 * starts the server application
	 * @param stage loads the initial stage FXML
	 * @throws Exception if FXML couldn't be loaded
	 */
	@Override
	public void start(Stage stage) throws Exception
	{
		try {lm = new LayoutManager(stage, "server"); }
		catch (Exception e) { e.printStackTrace();}
	}

	/**
	 * stops the server from listening to connections
	 * @throws Exception if server couldn't be closed
	 */
	@Override
	public void stop() throws Exception {
		Platform.runLater(() -> {
			try { if(server != null) server.close(); }
			catch (IOException e) { } });
		super.stop();
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}