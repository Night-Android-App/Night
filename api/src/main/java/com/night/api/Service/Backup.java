package com.night.api.Service;

import java.sql.SQLException;
import org.json.JSONObject;
import com.night.api.Database;
import com.night.api.Response;
import com.night.api.Account.Session;

public abstract class Backup {
    public static Response get(final String sessionID) throws SQLException {
        // communicate with the database
        String query = "SELECT data  FROM sleepData  WHERE uid=\"%s\"";

        JSONObject[] result = Database.executeQuery(query, Session.getUID(sessionID));

        // construct a response
        JSONObject data = new JSONObject();
        data.put("sleepData", result[0].get("data"));

        return new Response(200, data);
    }

    public static Response upload(final String sessionID, final String data) throws SQLException {
        String query = "UPDATE sleepData  SET data=\"%s\"  WHERE uid=\"%s\"";

        Database.executeUpdate(query, data, Session.getUID(sessionID));
        return new Response(200, "Backed up.");
    }

    public static Response delete(final String sessionID) throws SQLException {
        String query = "DELETE FROM sleepData  WHERE uid=\"%s\"";
        
        Database.executeUpdate(query, Session.getUID(sessionID));
        return new Response(202, "Deleted Backup.");
    }
}
