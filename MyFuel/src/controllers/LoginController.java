package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.ResourceBundle;

import client_package.Client;
import client_package.FXClient;
import client_package.RequestType;
import core.GenericObserver;
import static core.UIHelper.*;
import core.Validator;
import elements.CustomAnimation;
import elements.CustomButton;
import elements.CustomDialog;
import entities.User;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import server_package.Response;

public class LoginController implements Initializable {

    @FXML private Pane login;
    @FXML private ImageView loginSettings, innerLogo;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Pane loginButton;
    @FXML private Text errorText, usernamePlaceholder, passwordPlaceholder;
    @FXML private VBox errorVBox;
    @FXML private Circle circle1, circle2, circle3, circle4;

    private final String 	alreadySignedIn 	= "This account is already logged in.";
	private final String wrongCredentials 	= "The username or password you entered is not correct.";
    
    public static CustomDialog	custom;
    public static String 		ip =	"localhost";
    public static int 			port =	5550;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		focusTransition(username);
		focusTransition(password);

		Platform.runLater(() -> {
			loginButton.setFocusTraversable(true);
			loginButton.requestFocus();

			username.setOnKeyPressed(e -> toggleClass(username, "emptyRequiredField", false));
			password.setOnKeyPressed(e -> toggleClass(password, "emptyRequiredField", false));

		});

		Platform.runLater(this::customLogin);

		loginSettings.setOnMouseClicked(e ->  custom.show());

		createDalkan();
		animate();
	}

	@FXML
	void enterEvent(ActionEvent event) throws IOException {
		tryLogin();
	}

	@FXML
	void tryLogin() throws IOException
	{
		HashMap<String, String> credentials = getFields(username, password);

		FXClient.client = new Client(ip, port);
		String query = "SELECT * FROM users WHERE username ='" + credentials.get("username") + "' AND password='" + credentials.get("password") + "'";

		toggleClass(username, "emptyRequiredField", Validator.isEmpty(username));
		toggleClass(password, "emptyRequiredField", Validator.isEmpty(password));

		FXClient.client.request(query, User.class, RequestType.LOGIN);

		FXClient.observable.addObserver(new GenericObserver() {

			@Override
			public void update(Observable o, Object arg) {

				ArrayList<User> res = ((Response<User>) arg).result();

				Platform.runLater(() -> {
					{
						if (res.size() > 0) {
							User u = res.get(0);
							if (!u.connected)
								handleLogin(u);
							else
								handleError(alreadySignedIn);
						} else
							handleError(wrongCredentials);
					}
				});
			}
		});
	}

	/**
	 * Handles the transition from the login screen to the program itself. <br>
	 * @param user Sets the <i>FXClient.user</i> to the user that signed in,
	 * and in turn loads the <i>Menu</i> which uses the <i>user</i> to build an appropriate menu.
	 */
	private void handleLogin(User user)
	{
		FXClient.user = user;
		try { FXClient.lm.load("Menu"); FXClient.lm.set("Welcome"); }
		catch (IOException e) { e.printStackTrace(); }
	}

	private void handleError(String err)
	{
		toggleClass(errorVBox, "errorMessage", true);
		errorText.setText(err);
	}

	private void focusTransition(Node field)
	{
		field.focusedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				if(field.equals(username) && username.getText().isEmpty()) transition(username, usernamePlaceholder, 0.75, -22, -28);
				if(field.equals(password) && password.getText().isEmpty()) transition(password, passwordPlaceholder, 0.75, -22, -28);
			}
			else
			{
				if(field.equals(username) && username.getText().isEmpty()) transition(username, usernamePlaceholder, 1, 0, 0);
				if(field.equals(password) && password.getText().isEmpty()) transition(password, passwordPlaceholder, 1, 0, 0);
			}
		});
	}

	private void transition(Node field, Text text, double size, int x, int y)
	{
		ScaleTransition scale = new ScaleTransition(Duration.seconds(0.2), text);
		scale.setToX(size);
		scale.setToY(size);
		scale.play();

		TranslateTransition translate = new TranslateTransition(Duration.seconds(0.2), text);
		translate.setToX(x);
		translate.setToY(y);
		translate.play();
	}

	private void animate()
	{
		new CustomAnimation(innerLogo, Duration.seconds(0.5)).fadeIn().scale(1.1, 1).play();
		new CustomAnimation(circle1, Duration.seconds(0.5)).scale(0.8, 1).fadeIn().play();
		new CustomAnimation(circle2, Duration.seconds(0.5)).scale(0.8, 1).fadeIn().play();
		new CustomAnimation(circle3, Duration.seconds(0.5)).scale(0.8, 1).fadeIn().play();
		new CustomAnimation(circle4, Duration.seconds(0.5)).scale(0.8, 1).fadeIn().play();
	}

	private void customLogin()
	{
		try { custom = new CustomDialog(FXClient.lm.frame, "Custom Login", "CustomLoginSettings"); }
		catch (IOException e) { e.printStackTrace(); }
	}

	private void createDalkan()
	{
		CustomButton dalkan = new CustomButton("Dalkan", 0, 0);
		dalkan.setSize("normal");
		dalkan.setCustomStyle("dalkanButton");

		dalkan.setOnMouseClicked(e -> {
			try { FXClient.lm.load("Dalkan/DalkanStage1"); }
    		catch(IOException ex) {}
		});

		addElement(login, dalkan);
	}
}