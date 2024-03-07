package night.app.networks;

import android.webkit.CookieManager;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Request {
    HttpURLConnection connection;

    private static final String DOMAIN = "https://apifunction.azurewebsites.net/api/";

    public interface Callback {
        void run(@Nullable JSONObject res);
    }

    public Request sendData(String writeData) {
        try {
            OutputStream out = connection.getOutputStream();

            out.write(writeData.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }

        return this;
    }

    public JSONObject getResponse() {
        // different response codes may have its own response construction method
        try {
            if (connection.getResponseCode() == 200) {
                // get response body
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
                );

                // avoid throw error if request body is empty
                String line = reader.readLine();
                if (line == null || line == "") {
                    line = "{}";
                }

                String finalLine = line;
                return new JSONObject() {{
                    put("responseCode", connection.getResponseCode());
                    put("response", new JSONObject(finalLine));
                }};
            }

            return new JSONObject() {{
                put("responseCode", connection.getResponseCode());
            }};
        }
        // connection failed
        catch (IOException e) {
            System.out.println(e);
            return null;
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
        finally {
            connection.disconnect();
        }
    }


    public Request connect(String relativePath, String requestMethod) {
        try {
            // relative path should not has a slash at the front-most
            URL url = new URI(Request.DOMAIN + relativePath).toURL();
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(requestMethod);

            // set format type
            connection.setRequestProperty("content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // enable input & output stream
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();
        }
        catch (Exception e) {

        }

        return this;
    }
}
