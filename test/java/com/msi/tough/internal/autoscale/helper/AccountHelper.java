package com.msi.tough.internal.autoscale.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpRequest;
import com.msi.tough.core.HttpUtils;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ActionTestHelper;

@Component
public class AccountHelper {
    //private static Logger logger = Appctx.getLogger(AccountHelper.class
    //        .getName());

    private static Map<String,AccountBean> userAccounts =
            new HashMap<String,AccountBean>();

    private static ActionTestHelper actionHelper = null;

    private static String defaultAvailabilityZone = null;

    private static String tenant = null;

    private static String endpoint = null;

    /**
     * Construct a minimal valid account request.
     *
     * @param userName
     * @return
     */
    public static HttpRequest createAccountRequest(String userName,
            String accessKey, String secretKey) {
        final HttpRequest request = new HttpRequest(HttpMethodName.GET)
            .withParameter("Action", "CreateAccount")
            .withParameter("UserName", userName)
            .withParameter("AccessKey", accessKey)
            .withParameter("SecretKey", secretKey)
            .withParameter("CloudName", defaultAvailabilityZone)
            .withParameter("APIUsername", accessKey)
            .withParameter("APIPassword", secretKey)
            .withParameter("APITenant", tenant);
        return request;
    }


    /**
     * Construct a minimal valid alarm request.
     *
     * @param alarmName
     * @return
     */
    public static void createAccount(String userName,
            String accessKey, String secretKey) throws Exception {
        final HttpRequest createAccountRequest =
                createAccountRequest(userName, accessKey, secretKey);
        Map<String,String> requestParams = createAccountRequest.getParameters();
        String result = new HttpUtils().sendGetRequest(endpoint, requestParams);
        if (!JsonUtil.isValidJSON(result)) {
            throw new IllegalStateException("Unexpected failure, got:"+result);
        }
        AccountBean createdAccount =
                actionHelper.getAccountBeanByAccessKey(accessKey);
        userAccounts.put(accessKey, createdAccount);
    }

    /**
     * Construct a delete account request.
     *
     * @param userName
     * @return
     */
    public static HttpRequest deleteAccountRequest(AccountBean account) {
        final HttpRequest request = new HttpRequest(HttpMethodName.GET)
        .withParameter("Id", ""+account.getId())
        .withParameter("Action", "DeleteAccount")
        .withParameter("UserName", account.getName())
        .withParameter("AccessKey", account.getAccessKey())
        .withParameter("SecretKey", account.getSecretKey())
        .withParameter("CloudName", account.getDefZone())
        .withParameter("APIUsername", account.getApiUsername())
        .withParameter("APIPassword", account.getApiPassword())
        .withParameter("APITenant", tenant)
        .withParameter("Id", String.valueOf(account.getId()));
        return request;
    }

    /**
     * Delete an account with the given access key.
     *
     * @param accessKey
     */
    public static void deleteAccount(String accessKey) throws Exception {
        AccountBean account = actionHelper.getAccountBeanByAccessKey(accessKey);
        HttpRequest request = deleteAccountRequest(account);
        Map<String,String> requestParams = request.getParameters();
        String result = new HttpUtils().sendGetRequest(endpoint, requestParams);
        if (!JsonUtil.isValidJSON(result)){
            throw new IllegalStateException("Unexpected failure, got:"+result);
        }
        userAccounts.remove(account.getName());
    }

    /**
     * Delete all accounts created by tests (for test-end cleanup).
     */
    public static void deleteAllCreatedAccounts() throws Exception {
        for (String accessKey : userAccounts.keySet()) {
            deleteAccount(accessKey);
        }
        userAccounts.clear();
    }

    @Autowired(required=true)
    public void setActionTestHelper(ActionTestHelper actionTestHelper) {
        AccountHelper.actionHelper = actionTestHelper;
    }

    @Autowired(required=true)
    public void setDefaultAvailabilityZone(String defaultAvailabilityZone) {
        AccountHelper.defaultAvailabilityZone = defaultAvailabilityZone;
    }

    @Autowired(required=true)
    public void setTenant(String tenant) {
        AccountHelper.tenant = tenant;
    }

    @Autowired(required=true)
    public void serviceEndpoint(String endpoint) {
        AccountHelper.endpoint = endpoint;
    }
}
