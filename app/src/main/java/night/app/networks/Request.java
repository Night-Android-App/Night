package night.app.networks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class Request {
    private static final String DOMAIN = "http://192.168.1.111:8080/";

    public static String createConnection(String relativePath, String requestMethod,
    String writeData) throws Exception {
        URL url = new URL(Request.DOMAIN + relativePath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);

        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.connect();

        OutputStream out = connection.getOutputStream();
        out.write(writeData.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
        );

        return reader.readLine();
    }
}
