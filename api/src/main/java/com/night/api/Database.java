package com.night.api;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {
    private Connection connect = getConnection();

    public String executeQuery(String query, Boolean isRestricted) throws SQLException {
        // communicate with Database
        Statement statement = connect.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<String> result = new ArrayList<String>();
        int nCols = resultSet.getMetaData().getColumnCount();

        // loop all selected rows
        while (resultSet.next()) {
            String[] aRow = new String[nCols];
            
            for (int i=1; i <= nCols; i++) {
                // extract column data from ResultSet
                String colName = resultSet.getMetaData().getColumnName(i);
                String colValue = resultSet.getString(i);

                // format column data into JSON format
                aRow[i-1] = String.format("{\"%s\": \"%s\"}", colName, colValue);
            }

            result.add(Arrays.toString(aRow));

            // get one row data only
            // prevent extracting multiple user data at once
            if (isRestricted) break;
        }
        
        return result.toString();
    }

    private Connection getConnection() {
        try {
            // search and load JDBC from mysql connector in lib folder
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
        }

        try {
            // for test only
            // database account shouldn't place at here directly
            String url = "jdbc:mysql://localhost:3306/night";
            return DriverManager.getConnection(url, "root", "night");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        
        return null;
    }
}
