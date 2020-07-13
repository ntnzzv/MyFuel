package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static core.UIHelper.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import client_package.FXClient;
import client_package.Menu;
import elements.Alert;
import elements.MenuButton;
import enums.UserType;

public class MenuController implements Initializable {

    @FXML private Pane menu;
    @FXML private ScrollPane scrollPane;
    @FXML private Pane content;
    @FXML private VBox sideMenu;
    
    PauseTransition delay;
    KeyValue expanded, collapsed;
    KeyFrame c_frame, e_frame;
    Timeline c_timeline, e_timeline;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		MenuButton.sideMenu = sideMenu;
		sideMenu.setFocusTraversable(false);
		
	    expanded	= 	new KeyValue(scrollPane.prefWidthProperty(), 240);
	    collapsed	= 	new KeyValue(scrollPane.prefWidthProperty(), 60);
	    e_frame		=	new KeyFrame(Duration.millis(150), expanded);
	    c_frame		=	new KeyFrame(Duration.millis(150), collapsed);
	    e_timeline	=	new Timeline(e_frame);
	    c_timeline	=	new Timeline(c_frame);
	    
	    
	    delay = new PauseTransition(Duration.seconds(0.3));
	    delay.setOnFinished(e -> { e_timeline.play(); });
	    
	    List<MenuButton> buttons = Menu.getButtons(FXClient.user.getType());
	    
	    MenuButton welcome = new MenuButton("Hey " + FXClient.user.firstName, "Welcome", "welcome");
	    addElement(sideMenu, welcome);
	    
	    for(MenuButton b : buttons)
	    	addElement(sideMenu, b);
	    
	    Platform.runLater(() -> { addElement(sideMenu, new MenuButton(Alert.signOutAlert(), "Sign out", "logout")); }); 
	}
	
    @FXML void expand(MouseEvent event)		{ delay.play(); }
    @FXML void collapse(MouseEvent event) 	{ delay.stop(); c_timeline.play(); }
}