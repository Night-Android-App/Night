package night.app.networks;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountRequest extends Request {
    public void validateSession(Callback Callback) {
        new Thread(() -> {
            JSONObject response = connect("session", "POST").getResponse();

            if (response != null && response.optInt("responseCode") != 200) {
                // set session id to empty in cookie
            }

            Callback.run(response);
        });
    }

    public void register(String uid, String hashedPwd, Callback Callback) {
        new Thread(() -> {
            try {
                JSONObject data = new JSONObject() {{
                    put("uid", uid);
                    put("pwd", hashedPwd);
                }};

                JSONObject response = connect("register", "POST")
                        .sendData(data.toString())
                        .getResponse();

                Callback.run(response);
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void login(String uid, String hashedPwd, Callback Callback) {
        new Thread(() -> {
            try {
                JSONObject data = new JSONObject() {{
                    put("uid", uid);
                    put("pwd", hashedPwd);
                }};

                JSONObject response = connect("login", "POST")
                        .sendData(data.toString())
                        .getResponse();

                Callback.run(response);
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
