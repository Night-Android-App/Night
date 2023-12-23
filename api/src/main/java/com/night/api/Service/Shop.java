package com.night.api.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;
import com.night.api.Database;
import com.night.api.Response;

public abstract class Shop {
    public static Response getItemList(String type) throws SQLException {
        if (type != "theme" || type != "ringtone")
            return new Response(400, "Invalid shop item type");
        
        // get the array of all items belonging to the type from database
        //      It may has performance problem.
        String query = "SELECT *  FROM %s";

        JSONObject[] result = Database.executeQuery(query, type);
        JSONObject data = new JSONObject();
        data.put("data", result);

        return new Response(200, data);
    }

    public static Response getItemAttributes(String type, String itemID) throws SQLException {
        String query = "SELECT *  FROM %s  WHERE id=\"%s\"";

        JSONObject data = new JSONObject();
        data.put("attributes", Database.executeQuery(query, type, itemID)[0]);
        
        return new Response(200, data);
    }

    public static Response buyItem(String uid, String type, String itemID) throws SQLException {
        if (type != "theme" || type != "ringtone")
            return new Response(400, "Invalid shop item type.");

        String query;

        // get the list of items owned by the user from database
        query = "SELECT %s  FROM backpack  WHERE uid=\"%s\"";

        ArrayList<String> ownedItems = 
            new ArrayList<String>(Arrays.asList(
                Database.executeQuery(query, type, uid)[0].getString(type).split(",")));
        
        // check if the user has the item
        for (int i=0; i < ownedItems.size(); i++) {
            if (ownedItems.get(i) == itemID)
                return new Response(400, "Already owned the item.");
        }

        // get user's deposit and selling price from the database
        query = "SELECT coins  FROM backpack  WHERE uid=\"%s\"";
        int userCoins = Database.executeQuery(query, uid)[0].getInt("coins");

        query = "SELECT coins  FROM %s  WHERE id=\"%d\"";
        int requiredCoins = Database.executeQuery(query, itemID)[0].getInt("coins");
        
        // check if the user has enough coins
        if (requiredCoins > userCoins)
            return new Response(400, "Not enough coins.");
        
        // charge for the item
        query = "UPDATE backpack  SET (coins=coins-%d)  WHERE uid=\"%s\"";
        Database.executeUpdate(query, requiredCoins);

        // record that the user owns the bought item
        ownedItems.add("itemID");
        query = "UPDATE backpack  SET (%s=\"%s\")  WHERE uid=\"%s\"";
        Database.executeUpdate(query, String.join(",", ownedItems));

        return new Response(200, "Item bought.");
    }
}
