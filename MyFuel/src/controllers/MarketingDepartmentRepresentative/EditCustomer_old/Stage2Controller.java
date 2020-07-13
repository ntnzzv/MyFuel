package controllers.MarketingDepartmentRepresentative.EditCustomer_old;

import java.net.URL;
import java.util.ResourceBundle;

import core.Storage;
import javafx.application.Platform;
import javafx.fxml.Initializable;

public class Stage2Controller implements Initializable {

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		Platform.runLater(() -> {
			System.out.println(Storage.map);
		});
		
	}

}
