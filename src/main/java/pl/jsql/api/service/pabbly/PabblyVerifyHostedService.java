package pl.jsql.api.service.pabbly;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.response.PaymentResponse;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.model.payment.Webhook;
import pl.jsql.api.repo.WebhookDao;
import pl.jsql.api.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class PabblyVerifyHostedService {

    private final String GET_HOSTED = "https://payments.pabbly.com/api/v1/verifyhosted";

    @Value("${pabbly.api.key}")
    private String pabblyApiKey;

    @Value("${pabbly.secret.key}")
    private String pabblySecret;

    @Autowired
    private WebhookDao webhookDao;

    public PaymentResponse verifyHosted(String token) {

        PaymentResponse paymentResponse = new PaymentResponse();

        HttpURLConnection conn = null;

        try {

            URL url = new URL(GET_HOSTED);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((pabblyApiKey + ":" + pabblySecret).getBytes()));
            conn.setUseCaches(false);


            OutputStream os = conn.getOutputStream();

            HashMap<String, String> request = new HashMap<>();
            request.put("hostedpage", token);

            os.write(new Gson().toJson(request).getBytes());
            System.out.println("request: " + new Gson().toJson(request));

            os.flush();


            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                String response = Utils.readInputStreamToString(conn, true);
                conn.disconnect();

                if (response.length() > 0 && response.contains("<div>")) {
                    response = response.substring(response.lastIndexOf("</div><div>") + 11, response.lastIndexOf("</div></body></html>"));
                }

                throw new Exception("HTTP error code : " + conn.getResponseCode() + "\nHTTP error message : " + response);
            }

            String jsonStr = Utils.readInputStreamToString(conn, false);

            conn.disconnect();

            System.out.println("jsonStr : " + jsonStr);

            Webhook webhook = new Webhook();
            webhook.requestText = jsonStr;
            webhook.pabblyStatus = PabblyStatus.HOSTED_VERIFY;
            webhookDao.save(webhook);

            HashMap json = new Gson().fromJson(jsonStr, HashMap.class);
            LinkedTreeMap requestData = (LinkedTreeMap) json.get("data");
            LinkedTreeMap planData = (LinkedTreeMap) requestData.get("plan");

            String active = (String) planData.get("plan_active");

            if (active.equals("true")) {
                paymentResponse.active = true;
            } else {
                paymentResponse.active = false;
            }

            paymentResponse.plan = (String) planData.get("plan_name");
            paymentResponse.price = (Double) planData.get("price");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }

        }


        return paymentResponse;

    }

}
