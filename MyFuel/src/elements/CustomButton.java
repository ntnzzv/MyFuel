package elements;

import java.util.HashMap;

import static core.UIHelper.*;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CustomButton extends HBox {

	/**
	 * A helper class that is used to easily set button sizes.
	 */
	class Size
	{
		int width, height;
		public Size(String size)
		{
			switch(size)
			{
			case "small": 	{ width = 60; height = 42; } 	break;
			case "normal":	{ width = 120; height = 42; } 	break;
			case "large":	{ width = 180; height = 60; } 	break;
			case "wide":	{ width = 150; height = 42; }	break;
			case "square":	{ width = 42; height = 42; }
            break;
			default:		{ width = 0; height = 0;}		break;
			}
		}
	}
	
	Text text;
	ImageView img;
	public int x, y, width, height;
	
	/**
	 * Instantiates the custom button with a default style.
	 * @param label The button text
	 * @param x The relative x position to its parent
	 * @param y The relative y position to its parent
	 */
	public CustomButton(String label, int x, int y)
	{
		text = new Text(label);
		VBox v = new VBox(text);
		this.x = x;
		this.y = y;

		addElement(this, v);
		toggleClass(this, "button1");
		
		setLayoutX(x);
		setLayoutY(y);
		setPrefSize(width, height);
		setAlignment(Pos.CENTER);
		v.setAlignment(Pos.CENTER);
	}
	
	/**
	 * Instantiates the custom button with no particular position (0,0).
	 * @param label The button text
	 */
	public CustomButton(String label)
	{
		text = new Text(label);
		VBox v = new VBox(text);

		addElement(this, v);
		toggleClass(this, "button1");

		setPrefSize(width, height);
		setAlignment(Pos.CENTER);
		v.setAlignment(Pos.CENTER);
	}
	
	/**
	 * Instantiates a button with an image instead of a label.
	 * @param img The image name/path
	 */
	public CustomButton(ImageView img)
	{
		VBox v = new VBox(img);
		
		addElement(this, v);
		toggleClass(this, "buttonImg1");
		setAlignment(Pos.CENTER);
		v.setAlignment(Pos.CENTER);
	}
	
	/**
	 * Centers the button horizontally relative to its parent
	 */
	public void centerHorizontally()
	{
		double parentWidth = (((Pane)this.getParent()).getPrefWidth());
		setLayoutX(parentWidth / 2 - width / 2);
	}
	
	
	/**
	 * Sets the button's width/height
	 * @param width Width in pixels
	 * @param height Height in pixels
	 */
	public void setSize(int width, int height)
	{
		this.width = width; this.height = height;
		this.setPrefSize(width, height);
	}
	
	/**
	 * Sets the button's size based on specific string sizes
	 * @param size (small, wide, normal, large, square) sizes
	 */
	public void setSize(String size)
	{
		Size s = new Size(size);
		this.width = s.width; this.height = s.height;
		this.setPrefSize(s.width, s.height);
	}
	
	/**
	 * Sets the button's position relative to its parent
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y)
	{
		this.x = (int) x; this.y = (int) y;
		setLayoutX(x);
		setLayoutY(y);
	}
	
	/**
	 * Sets a custom CSS class for the button
	 * @param style The CSS class that is applied to the button
	 */
	public void setCustomStyle(String style)
	{
		toggleClass(this, "button1", false);
		toggleClass(this, style);
	}
	
	public void setText(String text)
	{
		this.text.setText(text);
	}
}
