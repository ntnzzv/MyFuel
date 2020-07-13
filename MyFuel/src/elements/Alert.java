package elements;

import java.io.IOException;

import client_package.FXClient;
import client_package.RequestType;
import core.UIHelper;
import entities.User;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Alert extends Pane {

	private String message, agree;
	private Text text;
	private Pane parent, div;
	private double width, height;
	private VBox v;
	private HBox h;
	EventHandler<Event> yesEvent;

	public Alert() { }

	/**
	 * Creates an overlay Alert dialog, was used first for the 'log out' functionality, later on replaced with CustomDialog.
	 * @param parent The parent element, usually the <i>LayoutManager.frame</i>
	 * @param message The message to be displayed.
	 * @param agreeText The "OK" button text
	 * @param width Element's width
	 * @param height Element's height
	 * @param yesEvent The event that happens when the "OK" button is clicked
	 */
	public Alert(Pane parent, String message, String agreeText, double width, double height, EventHandler<Event> yesEvent)
	{
		this.parent = parent;
		this.message = message;
		this.agree = agreeText;
		this.yesEvent = yesEvent;
		this.width = width;
		this.height = height;
		create();
		toFront();
		setVisible(false);
	}
	
	/**
	 * Sets default width and height attributes among few others.
	 * @param width
	 * @param height
	 */
	private void setDefaultStyles(double width, double height)
	{
		this.width = width;
		this.height = height;
		
		div.setMaxHeight(width);
		div.setMaxHeight(height);
		div.setPrefWidth(width);
		div.setPrefHeight(height);
		
		text.setWrappingWidth(width - 24);
	}
	
	/**
	 * Builds the element, using JavaFX basic elements, such as HBox, VBox and so on.
	 */
	private void create()
	{
		v = new VBox();
		h = new HBox();
		HBox agreeH = new HBox();
		div = new Pane();
		text = new Text();
		Text no = new Text("Cancel"), agreeText = new Text(agree);
		Pane yes = new Pane();
		
		text.setX(12);
		text.setY(30);
		
		setDefaultStyles(width, height);
		
		v.setStyle("-fx-background-color: rgba(0,0,0,0.3)");
		
		v.setAlignment(Pos.CENTER);
		h.setAlignment(Pos.CENTER);
		
		v.setPrefSize(parent.getScene().getWindow().getWidth(), parent.getScene().getWindow().getHeight());
		v.setPrefSize(parent.getScene().getWindow().getWidth(), parent.getScene().getWindow().getHeight());
		
		text.setText(message);
		text.setTextAlignment(TextAlignment.CENTER);
		
		yes.setPrefSize(120, 40);
		yes.setLayoutX(12);
		yes.setLayoutY(height - 52);
		yes.setOnMouseClicked(yesEvent);
		
		no.setLayoutX(width - 82);
		no.setLayoutY(height - 26);
		no.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				hide();
			}
		});
		
		agreeH.setAlignment(Pos.CENTER);
		agreeH.setLayoutY(8);
		agreeH.setPrefWidth(120);
		
		UIHelper.addElement(agreeH, agreeText);
		UIHelper.addElement(yes, agreeH);
		
		UIHelper.toggleClass(yes, "yes", true);
		UIHelper.toggleClass(no, "no", true);
				
		UIHelper.addElement(div, yes);	
		UIHelper.addElement(div, no);
		UIHelper.addElement(div, text);
		UIHelper.addElement(h, div);
		UIHelper.addElement(v, h);
		UIHelper.addElement(this, v);
		UIHelper.addElement(parent, this);
		UIHelper.toggleClass(div, "alert", true);
	}
	
	public void show() { setVisible(true); }
	public void hide() { setVisible(false); }
	public void destroy() { parent.getChildren().remove(this); }

	/**
	 * Creates the "log out" alert used across all pages.
	 * @return Returns the predefined alert.
	 */
	public static Alert signOutAlert()
	{
		return new Alert(FXClient.lm.frame, "Are you sure you want to sign out?", "Sign out", 300, 100, new EventHandler<Event>() {
								@Override
								public void handle(Event event) {
									FXClient.client.request(FXClient.user.username, User.class, RequestType.LOGOUT);
									try { FXClient.lm.load("Login"); }
									catch (IOException e) { e.printStackTrace(); }
								}});
	}
}
