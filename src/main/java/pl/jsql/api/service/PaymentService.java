package pl.jsql.api.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.ForgotPasswordRequest;
import pl.jsql.api.dto.request.PabblyPaymentRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.PaymentResponse;
import pl.jsql.api.dto.response.PlanResponse;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.payment.Webhook;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.security.service.SecurityService;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Transactional
@Service
public class PaymentService {

    private final String GET_CUSTOMER = "https://payments.pabbly.com/api/v1/customer/";
    private final String GET_HOSTED = "https://payments.pabbly.com/api/v1/verifyhosted";

    @Value("${pabbly.api.key}")
    private String pabblyApiKey;

    @Value("${pabbly.secret.key}")
    private String pabblySecret;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserService userService;

    @Autowired
    private WebhookDao webhookDao;

    public void activeOrUnactivePlan(Map<String, Object> request) {

        String eventTypeStr = (String) request.get("event_type");
        PabblyStatus eventType = PabblyStatus.valueOf(eventTypeStr.toUpperCase());
        Map<String, Object> requestData = (Map<String, Object>) request.get("data");
        Map<String, Object> requestPlan = (Map<String, Object>) requestData.get("plan");

        String userEmail;
        User user;

        if (eventType == PabblyStatus.SUBSCRIPTION_ACTIVATE || eventType == PabblyStatus.SUBSCRIPTION_CREATE) {

            userEmail = (String) requestData.get("email_id");
            String planDescription = (String) requestPlan.get("plan_code");
            PlansEnum plan = PlansEnum.valueOf(planDescription.toUpperCase());
           // int trialPeriod = (int) requestPlan.get("trial_period");

            user = userDao.findByEmail(userEmail);

            if (user == null) {

                UserRequest userRequest = getCustomer((String) requestData.get("customer_id"));
                userRequest.plan = plan;
                authService.register(userRequest);

            }

          //  user = userDao.findByEmail(userEmail);
            // userService.forgotPassword(new ForgotPasswordRequest(user.email));

        } else if (eventType == PabblyStatus.PAYMENT_FAILURE) {

            Map<String, Object> requestTransaction = (Map<String, Object>) requestData.get("transaction");
            Map<String, Object> requestPaymentMethod = (Map<String, Object>) requestTransaction.get("payment_method");

            userEmail = (String) requestPaymentMethod.get("email");
            user = userDao.findByEmail(userEmail);

            if (user == null) {
                return;
            }

            Plan plan = planDao.findFirstByCompany(user.company);
            plan.active = false;
            planDao.save(plan);

        }


    }

    public PlanResponse getPlan() {

        User companyAdmin = securityService.getCompanyAdmin();

        int activeUsers = userDao.countActiveUsersByCompany(companyAdmin.company);
        int activeApplications = applicationDao.countActiveApplicationsByCompanyAdmin(companyAdmin);

        Plan plan = planDao.findFirstByCompany(companyAdmin.company);

        PlanResponse planResponse = new PlanResponse();

        planResponse.activationDate = plan.activationDate;
        planResponse.active = plan.active;
        planResponse.maxApps = plan.plan.maxApps;
        planResponse.usedApps = activeApplications;
        planResponse.maxUsers = plan.plan.maxUsers;
        planResponse.usedUsers = activeUsers;
        planResponse.name = plan.plan.name;

        return planResponse;

    }

    private UserRequest getCustomer(String customerId) {

        UserRequest userRequest = new UserRequest();

        HttpURLConnection conn = null;

        try {

            URL url = new URL(GET_CUSTOMER + customerId);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic "+ Base64.encodeBase64String((pabblyApiKey+":"+pabblySecret).getBytes()));
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

            System.out.println("jsonStr : " + jsonStr);

            Webhook webhook = new Webhook();
            webhook.requestText = jsonStr;
            webhook.pabblyStatus = PabblyStatus.GET_CLIENT;
            webhookDao.save(webhook);

            HashMap json = new Gson().fromJson(builder.toString(), HashMap.class);
            LinkedTreeMap requestData = (LinkedTreeMap) json.get("data");

            String companyName = (String) requestData.get("company_name");

            userRequest.lastName = (String) requestData.get("last_name");
            userRequest.email = (String) requestData.get("email_id");
            userRequest.firstName = (String) requestData.get("first_name");
            userRequest.password = "";

            userRequest.companyName = companyName != null ? companyName : (userRequest.firstName + " " + userRequest.lastName);

            System.out.println("userRequest.companyName  : "+userRequest.companyName );

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }

        }


        return userRequest;
    }

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
            conn.setRequestProperty("Authorization", "Basic "+ Base64.encodeBase64String((pabblyApiKey+":"+pabblySecret).getBytes()));
            conn.setUseCaches(false);


                OutputStream os = conn.getOutputStream();

                HashMap<String, String> request = new HashMap<>();
                request.put("hostedpage", token);

                os.write(new Gson().toJson(request).getBytes());
                System.out.println("request: " + new Gson().toJson(request));

                os.flush();


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

            System.out.println("jsonStr : " + jsonStr);

            Webhook webhook = new Webhook();
            webhook.requestText = jsonStr;
            webhook.pabblyStatus = PabblyStatus.HOSTED_VERIFY;
            webhookDao.save(webhook);

            HashMap json = new Gson().fromJson(builder.toString(), HashMap.class);
            LinkedTreeMap requestData = (LinkedTreeMap) json.get("data");

            String active = (String) requestData.get("plan_active");

            if(active.equals("true")){
                paymentResponse.active = true;
            }else{
                paymentResponse.active = false;
            }

            paymentResponse.plan = (String) requestData.get("plan_name");
            paymentResponse.price = (BigDecimal) requestData.get("price");

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
