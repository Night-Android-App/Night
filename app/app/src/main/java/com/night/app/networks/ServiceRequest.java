package com.night.app.networks;

import org.json.JSONObject;

public class ServiceRequest extends Request {
    public static void deleteBackup(String uid) throws Exception {
        JSONObject writeData = new JSONObject();
        writeData.put("uid", uid);

        createConnection("backup", "DELETE", writeData.toString());
    }
}
