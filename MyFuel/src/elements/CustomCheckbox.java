package elements;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import static core.UIHelper.*;

public class CustomCheckbox extends HBox {

	private boolean checked = false;
	HBox box;
	ImageView view;
	Image img;
	
	/**
	 * Instantiates the custom check-box element with a label and a position relative to its parent
	 * @param message The label of the check-box
	 * @param x The position along the x-axis
	 * @param y The position along the y-axis
	 */
	public CustomCheckbox(String message, int x, int y)
	{
		createBox();		
		addElement(this, new Text(message));
		setEvents();
		setData(x, y, Pos.CENTER);
	}
	
	/**
	 * Instantiates a custom check-box with a label, a tooltip and a position relative to its parent
	 * @param title The label of the check-box
	 * @param exp The label of the tooltip
	 * @param x The position along the x-axis
	 * @param y The position along the y-axis
	 */
	public CustomCheckbox(String title, String exp, int x, int y)
	{
		createBox();		
		addElement(this, new CustomTooltip(title, exp, 0, 0));
		setEvents();
		setData(x, y, Pos.TOP_CENTER);
	}
	
	
	/**
	 * Sets the check-box state
	 * @param state The desired state of the check-box
	 */
	public void 	setChecked(boolean state) 
	{
		checked = state;
		if(checked) view.setImage(img);
		else 		view.setImage(null);
	}
	
	public boolean 	checked() { return checked; }
	
	private void createBox()
	{
		box = new HBox();
		box.setAlignment(Pos.CENTER);
		img = new Image(getClass().getResource("/files/img/icons/checkmark.png").toExternalForm());
		view = new ImageView();
		view.setFitHeight(12);
		view.setFitWidth(12);
		box.setMaxSize(18, 18);
		
		toggleClass(box, "checkboxBox");
		addElement(box, view);
		addElement(this, box);
	}
	
	private void setEvents()
	{
		box.setOnMouseClicked(e -> {
			if(checked)
			{
				checked = false;
				view.setImage(null);
			}
			else
			{
				checked = true;
				view.setImage(img);
			}
		});
	}
	
	/**
	 * Add extra mouse-click event handlers.
	 * @param event The event to add
	 */
	public void setEvents(EventHandler<Event> event) { box.addEventHandler(MouseEvent.MOUSE_CLICKED, event); }
			
	private void setData(int x, int y, Pos alignment)
	{
		toggleClass(this, "checkbox1");
		setAlignment(alignment);
		setSpacing(6);
		setLayoutX(x);
		setLayoutY(y);
	}
	
	public void toggleCheckboxStyle(String style, boolean state)
	{
		toggleClass(box, style, state);
	}
}
