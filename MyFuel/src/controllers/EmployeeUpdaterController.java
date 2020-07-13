package controllers;

import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import client_package.FXClient;
import core.GenericObserver;
import core.UIHelper;
import entities.Employee;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import server_package.Response;

public class EmployeeUpdaterController implements Initializable {

    @FXML private TableView<Employee> employeesTable;
    @FXML private ComboBox<String> rankComboBox;
    @FXML private Button updateButton;
    
    private Employee selectedEmployee;
    private int selectedIndex;
    private ArrayList<Employee> employees;
    private final ObservableList<String> ranks = FXCollections.observableArrayList(
    		"CEO",
            "Station Manager",
            "Marketing Manager",
            "Supplier",
            "Marketing Representative Manager");    
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		try { FXClient.client.request("SELECT * FROM myfuel.employees", Employee.class); }
		catch (IOException e) { e.printStackTrace(); }
	
		rankComboBox.getItems().addAll(ranks);
		
		FXClient.observable.addObserver(new GenericObserver() {

			@Override
			public void update(Observable o, Object arg) {
				employees = ((Response<Employee>)arg).result();
				
				Platform.runLater(() -> { UIHelper.FillTable(employeesTable, employees); });
			}});
	}

    @FXML
    void selectRow(MouseEvent event) {
    	
    	if(!employeesTable.getSelectionModel().isEmpty()) 
    	{
    		selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();
    		selectedIndex = employeesTable.getSelectionModel().getSelectedIndex();
    		FXClient.lm.setDisable(false, "#updateButton");
    		FXClient.lm.setDisable(false, "#rankComboBox");
    		rankComboBox.getSelectionModel().select(selectedEmployee.Rank);
    	}
    }

    @FXML
    void updateRank(MouseEvent event) throws IOException {
    	selectedEmployee.Rank = rankComboBox.getSelectionModel().getSelectedItem();
    	employeesTable.getItems().set(selectedIndex, selectedEmployee);
    	String rank = rankComboBox.getSelectionModel().getSelectedItem();
    	FXClient.client.request("UPDATE myfuel.employees SET employees.Rank = '" + rank + "' WHERE ID=" + selectedEmployee.ID);
    }
}