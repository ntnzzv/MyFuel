package core;

import java.util.Observer;

/**
 * Smart observable object holds an array of observables to prevent synchronization issues when many requests are sent
 * and many threads are needed to be synchronised on different data streams from the server.
 */
public class SmartObservable {
	
	public ObservableObject[] observables;
	int max, current = 0, next = 0;
	
	public SmartObservable(int max)
	{
		this.max = max;
		observables = new ObservableObject[max];
		for(int i = 0; i < max; i++) observables[i] = new ObservableObject();
	}
	
	public void addObserver(Observer o)
	{
		observables[next++ % max].addObserver(o);
	} // keeps the observables in rotation to cancel any data collisions between observers
	
	public void setValue(Object msg)
	{
		observables[current++ % max].setValue(msg);
	}
}