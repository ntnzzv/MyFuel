package core;

import java.util.Observable;

/**
 * an Obervable object that is observed by various observers to synchronize responses from the server
 */
public class ObservableObject extends Observable {

    public void setValue(Object obj)
    {
    	setChanged();
    	notifyObservers(obj);
    	this.deleteObservers();
    }
    
    public void setValuePersist(Object obj)
    {
    	setChanged();
    	notifyObservers(obj);
    }
}