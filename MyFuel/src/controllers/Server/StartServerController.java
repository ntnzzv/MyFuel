package controllers.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;

import core.UIHelper;
import core.GenericObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import server_package.Database;
import server_package.FXServer;
import server_package.Server;

public class StartServerController {
	
    @FXML private Pane startButton;
    @FXML private TextField ip;
    @FXML private TextField schema;
    @FXML private TextField username;
    @FXML private PasswordField password;

    @FXML
    void startServerButton(MouseEvent event) throws IOException
    {
    	HashMap<String, String> fields = UIHelper.getFields(ip, schema, username, password);
    	
    	Server.address = 	fields.get("ip");
    	Server.user = 		fields.get("username");
    	Server.password =	fields.get("password");
    	Server.schema =		fields.get("schema");  
    	
    	FXServer.lm.setDisable(true, "#startButton");

    	FXServer.server = new Server(Server.PORT);
    	
    	FXServer.server.startServerObs.addObserver(new GenericObserver()
    	{
			@Override
			public void update(Observable o, Object arg) {
				Platform.runLater(() ->
				{
						if(Database.connected)
						{
							try { FXServer.lm.load("RunningServer"); } catch (IOException e) { }
						}
						else
						{
							try { FXServer.server.close(); } catch (IOException e) { }
							finally
							{
								FXServer.lm.frame.lookup(".errorMessage").setVisible(true);
								FXServer.lm.setDisable(false, "#startButton");
							}
						}
				});
			}
        });
    }
}