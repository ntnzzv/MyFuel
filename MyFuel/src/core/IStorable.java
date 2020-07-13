package core;

/**
 * An interface for classes which hold information to be stored in a client session, used primarily to store data between page switches
 */
public interface IStorable {
	
	void store();
	
	default void clear() { Storage.clear(); }
}
