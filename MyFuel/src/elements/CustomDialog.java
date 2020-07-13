package elements;
import static core.UIHelper.*;
import client_package.FXClient;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class CustomDialog extends Pane
{
    private Pane div;
    private final Pane parent;
    private Pane content;
    private final String fxml;
    private final String title;
    private double width, height;
    private VBox v;
    private HBox h;
    private BorderPane titlePane, xWrapper;

    /**
     * Instantiates a custom dialog, an overlay which loads an FXML file as its contents.
     * @param parent The parent element which the dialog loads onto
     * @param title The title of the dialog
     * @param fxml The FXML file's path/name
     * @throws IOException
     */
    public CustomDialog(Pane parent, String title, String fxml) throws IOException {
        super();
        this.parent = 	parent;
        this.fxml =		fxml;
        this.title =	title;
        
        toggleClass(this, "dialog");
        
        create();
        toFront();
        hide();
    }

    private void create() throws IOException {
        v = 		new VBox();
        h =			new HBox();
        div = 		new VBox();
        content =	new Pane();
        
        titlePane =	new BorderPane();
        
        Text t = 	new Text(title);
        
        Image i = 			new Image(getClass().getResource("/files/img/icons/x.png").toExternalForm());
        ImageView img = 	new ImageView(i);
        
        CustomButton x = 	new CustomButton(img);
        xWrapper = new BorderPane(x);
        
        xWrapper.setOnMouseClicked(e -> { hide(); });
        
        titlePane.setLeft(t);
        BorderPane.setAlignment(t, Pos.CENTER_LEFT);
        
        titlePane.setRight(xWrapper);
        
        toggleClass(div, "dialogContent");
        toggleClass(titlePane, "dialogTitle");
        
        FXClient.lm.set(content, fxml);
        
        v.setStyle("-fx-background-color: rgba(0,0,0,0.3)");
        
        v.setAlignment(Pos.CENTER);
        h.setAlignment(Pos.CENTER);

        v.setPrefSize(parent.getScene().getWindow().getWidth(), parent.getScene().getWindow().getHeight());
        
        addElement(div, titlePane);
        addElement(div, content);
                
        addElement(h,div);
        addElement(v,h);
        addElement(this,v);
        addElement(parent,this);
    }

    /*
     * Shows the dialog.
     */
    public void show() 		{ setVisible(true); }
    
    /*
     * Hides the dialog.
     */
    public void hide() 		{ setVisible(false); }
    
    /*
     * Completely eliminates the dialog.
     */
    public void destroy()	{ parent.getChildren().remove(this); }
    
    /*
     * Sets an event on dialog exit.
     */
    public void setOnExit(EventHandler<Event> event)
    {
    	xWrapper.setOnMouseClicked(event);
    }
    
}
