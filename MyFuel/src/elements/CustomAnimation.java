package elements;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class CustomAnimation
{
	FadeTransition 		fade;
	TranslateTransition translate;
	ScaleTransition		scale;
	RotateTransition	rotate;
	Node				node;
	boolean				clickable = true;

	Duration			duration;
	Duration			delay;
	
	/**
	 * Instantiates the custom animation transitions.
	 * @param node The node on which the animation will play
	 * @param duration The length of the animation
	 */
	public CustomAnimation(Node node, Duration duration)
	{
		node.setMouseTransparent(true);
		delay = Duration.seconds(0);
		
		this.duration = duration;
		this.node = 		node;
	}

	/**
	 * 
	 * Instantiates the custom animation transitions.
	 * @param node The node on which the animation will play
	 * @param duration The length of the animation
	 * @param clickable Determines whether the node is click-able or not during and by the end of the animation
	 */
	public CustomAnimation(Node node, Duration duration, boolean clickable)
	{
		node.setMouseTransparent(clickable);
		delay = Duration.seconds(0);
		
		this.clickable = 	true;
		this.duration = 	duration;
		this.node = 		node;
	}
	
	/**
	 * Fades the element in (opacity = 0 -> 1).
	 * @return
	 */
	public CustomAnimation fadeIn()
	{
		node.setOpacity(0);
		fade = new FadeTransition(duration, node);
		fade.setFromValue(0);
		fade.setToValue(1);
		return this;
	}
	
	/**
	 * Fades the element out (opacity = 1 -> 0).
	 * @return
	 */
	public CustomAnimation fadeOut()
	{
		node.setOpacity(1);
		fade = new FadeTransition(duration, node);
		fade.setFromValue(1);
		fade.setToValue(0);
		return this;
	}
	
	/**
	 * Translates the element to a different coordinate based on his initial position.
	 * @param x The amount of pixels to translate to on the x-axis.
	 * @param y The amount of pixels to translate to on the y-axis.
	 * @return Returns the animation itself, in order to chain more transitions
	 */
	public CustomAnimation translate(double x, double y)
	{
		translate = new TranslateTransition(duration, node);
		translate.setToX(x);
		translate.setToY(y);
		return this;
	}
	
	/**
	 * Scales the element from and to a specific magnitude based on the initial size.
	 * @param from The initial scaled size of the element
	 * @param to The final scaled size of the element
	 * @return Returns the animation itself, in order to chain more transitions
	 */
	public CustomAnimation scale(double from, double to)
	{
		scale = new ScaleTransition(duration, node);
		scale.setFromX(from);	scale.setToX(to);
		scale.setFromY(from);	scale.setToY(to);
		return this;
	}
	
	/**
	 * Creates a delay until the animation may begin.
	 * @param delay The duration of the delay
	 * @return Returns the animation itself, in order to chain more transitions
	 */
	public CustomAnimation delay(Duration delay)
	{
		this.delay = delay;
		return this;
	}
	
	/**
	 * Rotates the element around itself by the degree input
	 * @param degree The degree by which the element will rotate
	 * @return  Returns the animation itself, in order to chain more transitions
	 */
	public CustomAnimation rotate(double degree)
	{
		rotate = new RotateTransition(duration, node);
		rotate.setByAngle(degree);
		return this;
	}

	/**
	 * 
	 * @return Returns the animation itself, in order to chain more transitions
	 */
	public CustomAnimation setClickable()
	{
		node.setMouseTransparent(false);
		return this;
	}
	
	/**
	 * Starts the animation.
	 */
	public void play()
	{
		if(fade != null)		{ fade.setDelay(delay); fade.play(); }
		if(translate != null) 	{ translate.setDelay(delay); translate.play(); }
		if(scale != null)		{ scale.setDelay(delay); scale.play(); }
		if(rotate != null)		{ rotate.setDelay(delay); rotate.play(); }
	}
	
}