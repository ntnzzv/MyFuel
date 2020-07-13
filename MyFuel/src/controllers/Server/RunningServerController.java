package controllers.Server;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import core.GenericObserver;
import entities.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import server_package.FXServer;
import server_package.Server;

public class RunningServerController implements Initializable {

    @FXML private TextArea console;
    @FXML private Pane closeButton;
    @FXML private Text connectedIP;
    @FXML private HBox connectedIPBox;
    @FXML private ListView<String> userList;	
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		FXServer.server.requestFromClientObs.addObserver(new GenericObserver() {
			@Override
			public void update(Observable o, Object arg) {
				Platform.runLater(() -> {
					console.appendText(arg.toString() + "\n");
				});
			}});
		
		FXServer.server.loginToServerObs.addObserver(new GenericObserver() {

			@Override
			public void update(Observable o, Object arg) {
				Platform.runLater(() -> {

					try
					{
						User u = ((ArrayList<User>) arg).get(0);
						if (!u.connected)  userList.getItems().add(u.username);
					}
					catch (Exception e) { }
					
				});
			}
		});
		
		FXServer.server.disconnectedObs.addObserver(new GenericObserver() {
			
			@Override
			public void update(Observable o, Object arg) {
				Platform.runLater(() -> {
					userList.getItems().remove(arg);
				});
			}
		});
		
		connectedIP.setText(Server.address);
		
		Platform.runLater(() -> {
			connectedIP.setText(Server.address);
			connectedIPBox.setLayoutX(connectedIPBox.getLayoutX() - connectedIP.getLayoutBounds().getWidth() - 30);
		});
	}

    @FXML
    void close(MouseEvent event) throws IOException {
    	FXServer.server.close();
    	
    	FXServer.lm.load("StartServer");
    	Platform.runLater(() -> { FXServer.lm.frame.lookup(".errorMessage").setVisible(false); });
    }
}