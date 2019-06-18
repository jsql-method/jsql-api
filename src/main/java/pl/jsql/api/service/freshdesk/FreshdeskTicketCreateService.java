package pl.jsql.api.service.freshdesk;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import pl.jsql.api.model.stats.ReportError;
import pl.jsql.api.utils.TokenUtil;
import pl.jsql.api.utils.Utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class FreshdeskTicketCreateService {

    private final String CREATE_TICKET_URL = "https://jsql.freshdesk.com/api/v2/tickets";
    private final String API_KEY = "QribNLT5oD0lzCLsm7G";
    private final String PASSWORD = "helpdesk#123";

    public void createCli(ReportError reportError) {

        HttpURLConnection conn = null;

        try {

            URL url = new URL(CREATE_TICKET_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((API_KEY + ":" + PASSWORD).getBytes()));
            conn.setUseCaches(false);

            OutputStream os = conn.getOutputStream();

            HashMap<String, Object> request = new HashMap<>();
            request.put("name", "CLI issue " + reportError.id);
            request.put("subject", "CLI issue " + reportError.id);
            request.put("status", 2);
            request.put("priority", 3);
            request.put("description", reportError.details);
            request.put("email", reportError.developer.email);
            request.put("cc_emails", new String[]{"dawid.senko@jsql.it", "pawel.stachurski@jsql.it"});
            request.put("source", 2);

            os.write(new Gson().toJson(request).getBytes());
            System.out.println("request: " + new Gson().toJson(request));

            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {

                String response = Utils.readInputStreamToString(conn, true);
                conn.disconnect();

                throw new Exception("HTTP error code : " + conn.getResponseCode() + "\nHTTP error message : freshdesk error " + response);
            }

            String jsonStr = Utils.readInputStreamToString(conn, false);

            conn.disconnect();

            System.out.println("jsonStr : " + jsonStr);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }

        }

    }

    public void createApi(String details) {
        this.createApi(details, "support@jsql.it");
    }

    public void createApi(String details, String email) {

        HttpURLConnection conn = null;

        try {

            URL url = new URL(CREATE_TICKET_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((API_KEY + ":" + PASSWORD).getBytes()));
            conn.setUseCaches(false);

            OutputStream os = conn.getOutputStream();

            HashMap<String, Object> request = new HashMap<>();
            request.put("name", "API issue " + TokenUtil.randomSalt());
            request.put("subject", "API issue " + TokenUtil.randomSalt());
            request.put("status", 2);
            request.put("priority", 3);
            request.put("description", details);
            request.put("email", email);
            request.put("cc_emails", new String[]{"support@jsql.it", "pawel.stachurski@jsql.it"});
            request.put("source", 2);

            os.write(new Gson().toJson(request).getBytes());
            System.out.println("request: " + new Gson().toJson(request));

            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {

                String response = Utils.readInputStreamToString(conn, true);
                conn.disconnect();

                throw new Exception("HTTP error code : " + conn.getResponseCode() + "\nHTTP error message : freshdesk error " + response);
            }

            String jsonStr = Utils.readInputStreamToString(conn, false);

            conn.disconnect();

            System.out.println("jsonStr : " + jsonStr);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }

        }

    }

}
