package elements;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static core.UIHelper.*;

import java.io.IOException;

import client_package.FXClient;

public class MenuButton extends HBox {
	public String label, fxml, icon;
	public boolean selected;
	private final BorderPane iconElement;
    private final BorderPane labelElement;
	
	public static VBox sideMenu;
	
	/**
	 * Builds a button for the menu.
	 * @param label The button's label
	 * @param fxml The FXML which the button loads
	 * @param icon The icon of the button
	 */
	public MenuButton(String label, String fxml, String icon)
	{
		this.label = label;
		this.fxml = fxml;
		this.icon = icon;
		
		selected = false;
		iconElement = setIcon();
		labelElement = setLabel();
		
		setAlignment(Pos.CENTER_LEFT);
		
		addElement(this, iconElement);
		addElement(this, labelElement);
		
		toggleClass(this, "menuButton");
		
		setEvents();
	}
	
	/**
	 * Builds a button which displays an alert.
	 * @param alert The alert which the button loads
	 * @param label The button's label
	 * @param icon The icon of the button
	 */
	public MenuButton(Alert alert, String label, String icon)
	{
		this.label = label;
		this.icon = icon;
		
		selected = false;
		iconElement = setIcon();
		labelElement = setLabel();
		
		setAlignment(Pos.CENTER_LEFT);
		
		addElement(this, iconElement);
		addElement(this, labelElement);
		
		toggleClass(this, "menuButton");
		
		this.setOnMouseClicked(e -> {
			alert.show();
		});
	}
	
	/**
	 * Sets the icon of the button and returns its wrapper
	 * @return Returns the wrapper pane of the button icon
	 */
	private BorderPane setIcon()
	{
		Image img = new Image(getClass().getResource("/files/img/icons/menu/" + icon + ".png").toExternalForm());
		ImageView view = new ImageView(img);
		
		view.setFitHeight(24);
		view.setFitWidth(24);
		
		BorderPane wrapper = new BorderPane(view);
		wrapper.setPrefSize(60, 60);
		
		toggleClass(wrapper, "menuIcon");
		
		return wrapper;
	}
	
	/**
	 * Sets the label of the button and returns its wrapper
	 * @return Returns the wrapper pane of the button label
	 */
	private BorderPane setLabel()
	{
		BorderPane wrapper = new BorderPane(new Text(label));
		toggleClass(wrapper, "menuLabel");

		BorderPane.setAlignment(wrapper.getChildren().get(0), Pos.CENTER_LEFT);
		wrapper.setPrefSize(180, 60);
		
		return wrapper;
	}
	
	public BorderPane getIcon() { return iconElement; }
	public BorderPane getLabel() { return labelElement; } 
	
	/**
	 * Sets the mouse-click event, which loads the page itself using the LayoutManager.
	 */
	private void setEvents()
	{
		this.setOnMouseClicked(e -> {
				try
				{
					FXClient.lm.set(fxml);
					toggleOff(sideMenu, "selected");
					toggleClass(this, "selected");
				}
				catch (IOException ex) { System.out.println("Wrong file name/path, perhaps?"); ex.printStackTrace(); }
		});
	}
	
	
	public String toString()
	{
		return this.label;
	}
}