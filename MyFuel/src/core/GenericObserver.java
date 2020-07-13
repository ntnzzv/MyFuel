package core;

import java.util.Observable;
import java.util.Observer;

/*
 Generic Observer to be assigned for observing the ObservableObject
 when creating this object it must implement update for its own use
 */

public abstract class GenericObserver implements Observer {

    @Override
    public abstract void update(Observable o, Object arg);
}