package com.night.api.Account;

import java.sql.SQLException;
import org.json.JSONObject;
import com.night.api.Database;
import com.night.api.Response;

public abstract class Authn {
    public static JSONObject validateSession(final String sessionID) throws SQLException {
        // communicate with the database
        String query = "SELECT uid, expiredDate  FROM session  WHERE sessionID=\"%s\"";

        JSONObject[] result = Database.executeQuery(query, sessionID);

        // database server returns a empty array
        if (result.length == 0)
            return new Response(404, "Unfounded Sesson.");

        // check is SessionID expired
        if (System.currentTimeMillis() > result[0].getLong("expiredDate"))
            return new Response(419, "Session expired.");

        // construct response
        JSONObject data = new JSONObject();
        data.put("uid", result[0].getString("uid"));

        return new Response(200, data);
    }

    public static JSONObject login(final String UID, final String PWD) throws Exception {
        // communicate with the database
        String query = "SELECT uid, pwd  FROM account  WHERE uid=\"%s\" && pwd=\"%s\"";
    
        JSONObject[] result = Database.executeQuery(query, UID, PWD);
        
        // matched the account
        if (result.length == 1) {
            // construct response
            JSONObject data = new JSONObject();
            data.put("sessionID", Session.generateID(UID));

            return new Response(200, data, "Login Success.");
        }

        // doesn't match any account in the database
        return new Response(404, "Username and/or Password is incorrect.");
    }
}
