package com.night.api.Account;

import java.sql.SQLException;
import java.util.UUID;
import com.night.api.Database;

public abstract class Session {
    public static String getUID(String sessionID) throws SQLException {
        String query = "SELECT uid  FROM session  WHERE sessionID=\"%s\"";

        return Database.executeQuery(query, sessionID)[0].getString("uid");
    }

    public static String generateID(String uid) throws Exception {
        // generate random number with current timestamp
        String uuid = UUID.randomUUID().toString();

        long createdDate = System.currentTimeMillis(); // for reference only
        long expiredDate = System.currentTimeMillis() + 2592000000l; // 7 days

        // communicate with the database
        String query = 
            "INSERT INTO    session (uid, sessionID, createdDate, expiredDate) " +
            "VALUES         (\"%s\", \"%s\", \"%s\", \"%s\");";

        Database.executeUpdate(
            String.format(query, uid, uuid, Long.toString(createdDate), Long.toString(expiredDate))
        );

        return uuid;
    }
}
