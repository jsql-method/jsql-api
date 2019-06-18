package pl.jsql.api.service.pabbly;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.payment.Webhook;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.PlanDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.repo.WebhookDao;
import pl.jsql.api.service.ApiService;
import pl.jsql.api.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class PabblyOnUpdateCustomerDetailsService {

    private final String UPDATE_CUSTOMER_DETAILS = "https://payments.pabbly.com/api/v1/customer/";

    @Value("${pabbly.api.key}")
    private String pabblyApiKey;

    @Value("${pabbly.secret.key}")
    private String pabblySecret;

    @Autowired
    private WebhookDao webhookDao;

    @Autowired
    private ApiService apiService;

    public void updateCustomerDetails(String customerId, String firstName, String lastName) {

        HttpURLConnection conn = null;

        try {

            URL url = new URL(UPDATE_CUSTOMER_DETAILS+customerId);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((pabblyApiKey + ":" + pabblySecret).getBytes()));
            conn.setUseCaches(false);

            OutputStream os = conn.getOutputStream();

            HashMap<String, String> request = new HashMap<>();
            request.put("first_name", firstName);
            request.put("last_name", lastName);

            os.write(new Gson().toJson(request).getBytes());
            System.out.println("request: " + new Gson().toJson(request));

            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                String response = Utils.readInputStreamToString(conn, true);
                conn.disconnect();

                if (response.length() > 0 && response.contains("<div>")) {
                    response = response.substring(response.lastIndexOf("</div><div>") + 11, response.lastIndexOf("</div></body></html>"));
                }

                apiService.reportError("PabblyOnUpdateCustomerDetailsService "+conn.getResponseCode() + ", " +response);
                throw new Exception("HTTP error code : " + conn.getResponseCode() + "\nHTTP error message : " + response);
            }

            String jsonStr = Utils.readInputStreamToString(conn, false);

            conn.disconnect();

            System.out.println("jsonStr : " + jsonStr);

            Webhook webhook = new Webhook();
            webhook.requestText = jsonStr;
            webhook.pabblyStatus = PabblyStatus.CUSTOMER_DETAILS_UPDATE;
            webhookDao.save(webhook);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }

        }


    }

}
