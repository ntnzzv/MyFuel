package core;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Helper class to switch between JAVAFX scenes
 */
public class LayoutManager {

	private final Scene	scene;
	private final String type;
	public Pane	frame, content;


	/**
	 * Initializes a layout manager, which manages all FXML loadings of the program.
	 * @param stage The FX application stage
	 * @param type The application type, <i>"client"</i> or <i>"server"</i>
	 * @throws IOException
	 */
	public LayoutManager(Stage stage, String type) throws IOException
	{
		this.type = type;
		String init = type.compareTo("server") == 0 ? "StartServer" : "Login";

		// FXLoader is just an arbitrary class to set an easy relative path for the FXML files
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/files/layout/Frame.fxml"));
		
		addFont("Roboto-Light");
		addFont("Roboto-Regular");
		addFont("Roboto-Bold");

		scene = new Scene(loader.load());
		scene.getStylesheets().add(getClass().getResource("/files/css/" + type + ".css").toExternalForm());

		stage.setScene(scene);
		stage.setTitle("MyFuel " + type.substring(0, 1).toUpperCase() + type.substring(1));
		stage.setResizable(false);
		stage.show();
		
		frame = (Pane) scene.lookup("#frame");
		load(init);
	}

	/**
	 * Loads an FXML onto the frame.
	 * @param layout The FXML to be loaded
	 * @throws IOException
	 */
	public void load(String layout) throws IOException
	{
		insert(frame, layout);
	}

	/**
	 * Loads an FXML onto the content element and clears the storage.
	 * @param layout The FXML to be loaded
	 * @throws IOException
	 */
	public void set(String layout) throws IOException 
	{
		Storage.clear();
		content = (Pane) scene.lookup("#content");
		insert(content, layout);
	}
	
	/**
	 * Loads an FXML onto the content element and keeps the storage.
	 * @param layout The FXML to be loaded
	 * @param storable An IStorable instance to differentiate from the storage-less controllers
	 * @throws IOException
	 */
	public void set(String layout, IStorable storable) throws IOException
	{
		content = (Pane) scene.lookup("#content");
		insert(content, layout);
	}

	
	/**
	 * Loads and FXML onto a specified element.
	 * @param parent The parent element to be loaded onto the layout
	 * @param layout The FXML to be loaded
	 * @throws IOException
	 */
	public void set(Pane parent,String layout) throws IOException
	{
		parent.getChildren().add(FXMLLoader.load(getClass().getResource("/files/layout/" + type + "/" + layout + ".fxml")));
	}
	
	/**
	 * Helper method which invokes the FXMLLoader object
	 * @param pane The element onto which the layout will be loaded
	 * @param layout The FXML to be loaded
	 * @throws IOException
	 */
	private void insert(Pane pane, String layout) throws IOException
	{
		pane.getChildren().clear();
		pane.getChildren().add(FXMLLoader.load(getClass().getResource("/files/layout/" + type + "/" + layout + ".fxml")));
	}
	
	/**
	 * A custom font helper method
	 * @param font The local font to be loaded
	 */
	private void addFont(String font)
	{
		Font.loadFont(getClass().getResource("/files/fonts/" + font + ".ttf").toExternalForm(), 0);
	}
	
	/**
	 * Disables the specified element.
	 * @param disabled The state of the element disable property
	 * @param element The element to be disabled
	 */
	public void setDisable(boolean disabled, String element)
	{
		Node e = frame.lookup(element);
		e.setDisable(disabled);
		
		if(disabled) e.getStyleClass().add("disabled");
		else e.getStyleClass().removeIf(style -> style.equals("disabled"));
		
	}
}