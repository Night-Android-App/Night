package com.night.api;

import java.sql.*;
import java.util.ArrayList;
import org.json.JSONObject;

public abstract class Database {
    public static Connection connect = getConnection();

    public static Boolean isUserExisted(String uid) throws SQLException {
        final String QUERY = "SELECT uid  FROM account  WHERE uid=\"%s\"";
        return executeQuery(QUERY).length == 1;
    }

    public static JSONObject[] executeQuery(String query, Object ...args)
    throws SQLException {
        return executeQuery(String.format(query, args));
    }

    public static JSONObject[] executeQuery(String query) throws SQLException {
        // communicate with Database
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<JSONObject> result = new ArrayList<JSONObject>();
        int nCols = resultSet.getMetaData().getColumnCount();

        // loop all selected rows
        while (resultSet.next()) {
            JSONObject aRow = new JSONObject();
            
            for (int i=1; i <= nCols; i++) {
                // extract column data from ResultSet
                String colName = resultSet.getMetaData().getColumnName(i);
                String colValue = resultSet.getString(i);

                // format column data into JSON format
                aRow.put(colName, colValue);
            }

            result.add(aRow);
        }
        
        return (JSONObject[]) result.toArray();
    }

    public static int executeUpdate(String query, Object ...args) throws SQLException {
        return executeUpdate(String.format(query, args));
    }

    public static int executeUpdate(String query) throws SQLException {
        Statement statement = connect.createStatement();
        return statement.executeUpdate(query);
    }

    private static Connection getConnection() {
        try {
            // search and load JDBC from mysql connector in lib folder
            Class.forName("com.mysql.cj.jdbc.Driver");

            // for test only
            // database account shouldn't place at here directly
            String url = "jdbc:mysql://localhost:3306/night";
            return DriverManager.getConnection(url, "root", "night");
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
