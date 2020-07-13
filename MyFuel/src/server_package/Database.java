package server_package;

import core.Console;
import java.sql.*;
import java.util.ArrayList;

public class Database {

	public static Connection connection;
	public static boolean connected = false;

	public static void connect(String address, String user, String password,String schema) {
		try { Class.forName("com.mysql.cj.jdbc.Driver").newInstance(); }
		catch (Exception ex) { System.out.println(ex); }

		try
		{
			DriverManager.setLoginTimeout(10);
//			connection = DriverManager.getConnection("jdbc:mysql://" + address + "/" + schema + "?serverTimezone=IST", user, password);
			connection = DriverManager.getConnection("jdbc:mysql://" + user + ":jKnTpVowm84GhG5D9yO9@" + address + "/" + schema + "?serverTimezone=Asia/Jerusalem");
			Console.announce("Connected to the database.");
			connected = true;
		}
		catch (SQLException ex) { System.out.println(ex); }
	}
	
	public static void close() {
		try { connection.close(); connected = false; }
		catch (SQLException e) { e.printStackTrace(); }
	}

	private static <T> ArrayList<T> createListOf(Class<T> type) {
		return new ArrayList<T>();
	}

	/**
	 * Executes a MySQL query (SELECT) and returns the result as an ArrayList
	 * where T is a core.models class that has corresponding fields of said result.
	 * @param type The class of the expected entity to be returned as a result.
	 * @param query The passed MySQL query the DBMS executed.
	 * @return Yields an ArrayList of corresponding entities of <i>type</i> with expressive fields.
	 */
	public static <T> ArrayList<T> query(Class<T> type, String query) {
		Statement statement;
		ArrayList<T> list = createListOf(type);
		
		try
		{
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next()) {
				Object temp = type.newInstance();
				
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					try {
						temp.getClass().getField(rs.getMetaData().getColumnName(i)).set(temp, rs.getObject(i));
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				list.add((T)temp);
			}
		}
		catch (SQLException | InstantiationException | IllegalAccessException e) { e.printStackTrace(); }
		return list;
	}
	
	/**
	 * Executes a MySQL query (ADD, DELETE, UPDATE, etc.) that yields no results.
	 * @param query The passed MySQL query the DBMS executes.
	 */
	public static void execute(String query)
	{
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.execute(query);
		} catch (SQLException e) { e.printStackTrace(); }
	}
}