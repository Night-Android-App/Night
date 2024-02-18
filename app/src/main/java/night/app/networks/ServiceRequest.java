package night.app.networks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServiceRequest extends Request {
    public void recovery(Callback Callback) {
        new Thread(() -> {
            // recovery all backup types
            Map<String, Object> params = new HashMap<>() {{
                put("sleep", true);
                put("alarm", true);
                put("dream", true);
            }};

            JSONObject response = connect("data" + parseURLParams(params), "GET")
                    .getResponse();

            Callback.run(response);
        });
    }

    public void backup(String requestBody, Callback Callback) {
        new Thread(() -> {
            JSONObject response = connect("data", "POST")
                    .sendData(requestBody)
                    .getResponse();

            Callback.run(response);
        }).start();
    }
}
