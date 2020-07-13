package elements;

import entities.Car;
import enums.FuelType;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import static core.UIHelper.*;
import java.util.ArrayList;

/**
 *   class for custom scroll pane window in any scene that holds buttons with a customers list of cars
 */

public class CustomCarList extends ScrollPane {

	/**
	 * A helper VBox extension that also holds the car's object and information
	 */
	public class VCar extends VBox
	{
		public Car car;
		
		Text carID, fuelType;
		Image img;
		ImageView iv;
		HBox top, bottom;
		
		public VCar(Car car)
		{
			this.car = 	car;
			carID = 	new Text(car.carID);
			fuelType = 	new Text(car.fuelType);
		}
	}
	

	public 	ArrayList<VCar> vcars;
	public TilePane tp;
	private VCar selected = null;
	private EventHandler<Event> optionalEvent	= null;
	
	/**
	 * Creates a stylized ScrollPane->TilePane and populates it with cars. <br>
	 * Clicking on a car 'selects' it and returns the selected Car object.
	 * @param cars An ArrayList of cars to populate the list with.
	 * @param x The relative layout X position.
	 * @param y The relative layout Y position.
	 * @param optionalEvent An additional onMouseClick event, on top of the car selection functionality.
	 */
	public CustomCarList(ArrayList<Car> cars, int x, int y, EventHandler<Event> optionalEvent)
	{
		setup(cars, x, y, optionalEvent);
	}
	
	/**
	 * Creates a stylized ScrollPane->TilePane and populates it with cars. <br>
	 * Clicking on a car 'selects' it and returns the selected Car object.
	 * @param cars An ArrayList of cars to populate the list with.
	 * @param x The relative layout X position.
	 * @param y The relative layout Y position.
	 */
	public CustomCarList(ArrayList<Car> cars, int x, int y)
	{
		setup(cars, x, y, null);
	}
	
	public void addCar(Car car)
	{
		VCar v = new VCar(car);
		v.setSpacing(3);

		v.top = new HBox();
		v.top.setSpacing(9);
		v.top.setAlignment(Pos.CENTER_LEFT);
		
		Image it = new Image(getClass().getResource("/files/img/icons/hashtag.png").toExternalForm());
		ImageView imgt = new ImageView(it);
		addElement(v.top, imgt);
		addElement(v.top, v.carID);
		
		v.bottom = new HBox();
		v.bottom.setSpacing(9);
		v.bottom.setAlignment(Pos.CENTER_LEFT);
		
		v.img = new Image(getClass().getResource("/files/img/icons/fueltypes/" + car.fuelType  +".png").toExternalForm());
		v.iv = new ImageView(v.img);
		addElement(v.bottom, v.iv);
		addElement(v.bottom, v.fuelType);
		
		toggleClass(v, "carListCar");
		
		addElement(v, v.top);
		addElement(v, v.bottom);
		
		addElement(tp, v);
		
		v.setOnMouseClicked(e -> select(v));
		
		// Optional event goes here
		if(optionalEvent != null) v.addEventHandler(MouseEvent.MOUSE_CLICKED, optionalEvent);
		
		// Used keep track of the VBox elements of the cars
		vcars.add(v);
	}
	

	/**
	 * Edits an existing car and updates the visual VBox accordingly.
	 * @param carID The car to be chang
	 * @param newCarID The new car ID.
	 * @param fuelType The new fuel type.
	 */
	public void editCar(String carID, String newCarID, String fuelType)
	{
		VCar v = null;
		for(VCar i : vcars) if(i.car.carID.compareTo(carID) == 0) v = i;
		if(v == null) return;
		
		v.carID.setText(newCarID);
		v.fuelType.setText(fuelType);
		v.img = new Image(getClass().getResource("/files/img/icons/fueltypes/" + fuelType  +".png").toExternalForm());
		v.iv = new ImageView(v.img);
		v.bottom.getChildren().clear();
		addElement(v.bottom, v.iv);
		addElement(v.bottom, v.fuelType);
	}
	
	/**
	 * Removes a car from the list and updates the visual VBox accordingly.
	 * @param carID Removes the car with this carID.
	 */
	public void removeCar(String carID)
	{
		VCar v = null;
		for(VCar i : vcars) if(i.car.carID.compareTo(carID) == 0) v = i;
		if(v == null) return;
		
		tp.getChildren().remove(v);
	}
	
	private void setup(ArrayList<Car> cars, int x, int y, EventHandler<Event> optionalEvent)
	{
		vcars =	new ArrayList<CustomCarList.VCar>();
		tp = 	new TilePane();
		
		setLayoutX(x);
		setLayoutY(y);
		
		tp.setVgap(12);
		tp.setHgap(12);
		
		this.setContent(tp);
		
		toggleClass(this, "carList");
		
		this.optionalEvent = optionalEvent;
		
		cars.forEach(this::addCar);
	}
	
	/**
	 * Adds all cars from a list.
	 * @param cars The list from which the cars are added to the CustomCarList
	 */
	public void addAllCars(ArrayList<Car> cars) { cars.forEach(car -> addCar(car)); }
	
	/**
	 * Removes all cars from a list.
	 * @param cars The list from which the cars are deleted from the CustomCarList
	 */
	public void removeAllCars(ArrayList<Car> cars) { cars.forEach(car -> removeCar(car.carID)); }

	private void select(VCar v)
	{
		toggleOff(tp, "selectedCar");
		toggleClass(v, "selectedCar");
		
		selected = v;
	}
	
	/**
	 * Gets the selected Car object
	 * @return The selected Car object
	 */
	public Car getSelected() {
		if(selected == null){ return null; }
		return selected.car;
	}
	
    public void hide() { setVisible(false);}
    public void show() { setVisible(true);}
}