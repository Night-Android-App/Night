package night.app.networks;

import org.json.JSONObject;

public class AccountRequest extends Request {
    public static Boolean validateSessionID(String sessionID) {
            try {
                JSONObject writeData = new JSONObject() {{
                    put("sessionID", sessionID);
                }};

                String responseData = createConnection("login", "POST", writeData.toString());

                return new JSONObject(responseData).get("status") == "200";
            }
            catch (Exception e) {

            }
            return null;
    }

    public JSONObject register(String uid, String hashedPwd) throws Exception {
        JSONObject writeData = new JSONObject() {{
            put("uid", uid);
            put("pwd", hashedPwd);
        }};

        return new JSONObject(createConnection("register", "POST", writeData.toString()));
    }

    public static JSONObject login(String uid, String hashedPwd) throws Exception {
        JSONObject writeData = new JSONObject() {{
            put("uid", uid);
            put("pwd", hashedPwd);
        }};

        return new JSONObject(createConnection("login", "POST", writeData.toString()));
    }
}
