package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import elements.CustomButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import static core.UIHelper.*;

public class CustomLoginController implements Initializable {

	@FXML private TextField ip, port;
	@FXML private Pane customPane;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		numbersOnly(port, 0);
		
		CustomButton save = new CustomButton("Save", 102, 70);
		save.setSize(60, 30);
		addElement(customPane, save);
		
		save.setOnMouseClicked(e -> save());
	}
	
	private void save()
	{
		LoginController.ip = ip.getText();
		LoginController.port = Integer.parseInt(port.getText());
		LoginController.custom.hide();
	}

}