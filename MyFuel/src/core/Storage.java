package core;

import java.util.HashMap;

/**
 * Helper class which stores data in a hashmap, using a string as a key, stores various data
 * used mainly to store data between pages.
 */

public class Storage {
	
	public static HashMap<String, Object> map = new HashMap<String, Object>();
	
	public static void add(String key, Object value)
	{
		if(get(key) != null) map.remove(key);
		map.put(key, value);
	}
	
	public static void addAll(HashMap<String, Object> fragment)
	{
		for(String key : fragment.keySet())
			Storage.add(key, fragment.get(key));
	}
	
	public static Object get(String key)
	{
		return map.get(key);
	}
	
	public static void clear() { map.clear(); }

	public static boolean isEmpty(){
		return map.isEmpty();
	}
}