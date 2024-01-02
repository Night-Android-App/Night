package night.app.networks;

import org.json.JSONObject;

public class AccountRequest extends Request {
    public static Boolean validateSessionID(String sessionID) throws Exception {
        JSONObject writeData = new JSONObject();
        writeData.put("sessionID", sessionID);

        String responseData = createConnection("login", "POST", writeData.toString());

        return new JSONObject(responseData).get("status") == "200";
    }

    public static void login(String uid, String hashedPwd) throws Exception {
        JSONObject writeData = new JSONObject();
        writeData.put("uid", uid);
        writeData.put("pwd", hashedPwd);

        JSONObject responseData
            = new JSONObject(createConnection("login", "POST", writeData.toString()));

        if (responseData.getInt("status") == 200) {
            System.out.println("sucess");
        }
        else {
            System.out.println(responseData.getString("msg"));
        }
        // store sessionID into local storage
        // reload page ()
    }
}
