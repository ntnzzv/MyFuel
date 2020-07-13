package elements;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import static core.UIHelper.*;

public class CustomTooltip extends Pane {

	/**
	 * Creates a tooltip label with a text and an explanation.
	 * @param title The label's text
	 * @param exp The explanation which is shown on mouse-hover
	 * @param x The position along the x-axis relative to its parent
	 * @param y The position along the y-axis relative to its parent
	 */
	public CustomTooltip(String title, String exp, int x, int y) {
		
		VBox titleContainer = new VBox(new Text(title));
		titleContainer.setAlignment(Pos.CENTER);
		toggleClass(titleContainer, "tooltipTitle");
		
		addElement(this, titleContainer);
		toggleClass(this, "tooltip1");
		
		VBox expContainer = new VBox(new Text(exp));
		expContainer.setAlignment(Pos.CENTER);
		expContainer.setLayoutX(0);
		expContainer.setLayoutY(36);
		toggleClass(expContainer, "tooltipExp");
		
		titleContainer.setOnMouseEntered(e -> { addElement(this, expContainer); });
		titleContainer.setOnMouseExited(e -> { this.getChildren().remove(expContainer); });
		
		setLayoutX(x);
		setLayoutY(y);
	}
}
