package night.app.networks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServiceRequest extends Request {
    public void recovery(String requestBody, Callback Callback) {
        new Thread(() -> {
            JSONObject response = connect("recovery", "POST")
                    .sendData(requestBody)
                    .getResponse();

            Callback.run(response);
        }).start();
    }

    public void backup(String requestBody, Callback Callback) {
        new Thread(() -> {
            JSONObject response = connect("backup", "POST")
                    .sendData(requestBody)
                    .getResponse();

            Callback.run(response);
        }).start();
    }
}
