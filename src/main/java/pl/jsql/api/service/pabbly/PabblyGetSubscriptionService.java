package pl.jsql.api.service.pabbly;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class PabblyGetSubscriptionService {

    private final String GET_SUBSCRIPTION = "https://payments.pabbly.com/api/v1/subscription/{subscription_id}";

    @Value("${pabbly.api.key}")
    private String pabblyApiKey;

    @Value("${pabbly.secret.key}")
    private String pabblySecret;

    @Autowired
    private WebhookDao webhookDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PlanDao planDao;

    public Integer getSubscriptionTrialDays(String subscriptionId) {

        Integer trialDays = null;

        HttpURLConnection conn = null;

        try {

            URL url = new URL(GET_SUBSCRIPTION.replace("{subscription_id}", subscriptionId));
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((pabblyApiKey + ":" + pabblySecret).getBytes()));
            conn.setUseCaches(false);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                InputStream inputStream = conn.getErrorStream();

                if (inputStream == null) {
                    conn.disconnect();
                    throw new Exception("HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                StringBuilder builder = new StringBuilder();
                while (br.ready()) {
                    builder.append(br.readLine());
                }

                conn.disconnect();

                String response = builder.toString().trim();

                if (response.length() > 0 && response.contains("<div>")) {
                    response = response.substring(response.lastIndexOf("</div><div>") + 11, response.lastIndexOf("</div></body></html>"));
                }

                throw new Exception("HTTP error code : " + conn.getResponseCode() + "\nHTTP error message : " + response);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            StringBuilder builder = new StringBuilder();

            while (br.ready()) {
                builder.append(br.readLine());
            }

            conn.disconnect();

            String jsonStr = builder.toString();

            Webhook webhook = new Webhook();
            webhook.requestText = jsonStr;
            webhook.pabblyStatus = PabblyStatus.SUBSCRIPTION_GET;
            webhookDao.save(webhook);

            HashMap json = new Gson().fromJson(builder.toString(), HashMap.class);
            LinkedTreeMap requestData = (LinkedTreeMap) json.get("data");

            trialDays = (int) requestData.get("trial_days");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }

        }

        return trialDays;

    }
}
