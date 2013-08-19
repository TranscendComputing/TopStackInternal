package com.msi.tough.internal.autoscale.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.internal.autoscale.actions.CreateAccount;
import com.msi.tough.internal.autoscale.actions.DeleteAccount;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ActionRequest;
import com.msi.tough.query.ActionTestHelper;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.security.AESSecurity;

/**
 * Account helper for non-web tests (using actions in-VM).
 *
 * @author jgardner
 *
 */
@Component
public class AccountLocalHelper {

    private static Map<String,AccountBean> userAccounts =
            new HashMap<String,AccountBean>();

    private static ActionTestHelper actionHelper = null;

    private static String accessKey = null;

    private static String secretKey = null;

    private static String defaultAvailabilityZone = null;

    private static String tenant = null;

    /**
     * Construct a minimal valid account request.
     *
     * @param userName
     * @return
     */
    public static CreateAccountRequest createAccountRequest(String userName) {
        final CreateAccountRequest request = new CreateAccountRequest();
        request.put("UserName", userName);
        request.put("AccessKey", accessKey);
        request.put("SecretKey", secretKey);
        request.put("CloudName", defaultAvailabilityZone);
        request.put("APIUsername", accessKey);
        request.put("APIPassword", secretKey);
        request.put("APITenant", tenant);
        return request;
    }

    /**
     * Create a user account.
     *
     * @param userName
     */
    public static void createAccount(String userName,
            String accessKey, String secretKey) throws Exception {
        CreateAccountRequest putRequest = createAccountRequest(userName);
        putRequest.put("AccessKey", accessKey);
        putRequest.put("SecretKey", secretKey);
        putRequest.put("APIUsername", accessKey);
        putRequest.put("APIPassword", secretKey);
        CreateAccount createAccount = new CreateAccount();
        String ret = actionHelper.invokeProcess(createAccount,
                    putRequest.getRequest(), putRequest.getResponse(),
                    putRequest.getMap());
        if (!JsonUtil.isValidJSON(ret)) {
            throw new IllegalStateException("Unexpected failure " +
                    "creating account.");
        }
        AccountBean account = new AccountBean();
        final String id = JsonUtil.getValueFromJSON(ret, "Id");
        account.setName(userName);
        account.setAccessKey(accessKey);
        account.setSecretKey(secretKey);
        account.setId(Long.parseLong(id));
        userAccounts.put(accessKey, account);
    }

    /**
     * Construct a delete account request.
     *
     * @param userName
     * @return
     */
    public static DeleteAccountRequest deleteAccountRequest(AccountBean account) {
        final DeleteAccountRequest request = new DeleteAccountRequest();
        request.put("AccessKey", account.getAccessKey());
        request.put("SecretKey", account.getSecretKey());
        request.put("Id", account.getId()+"");
        return request;
    }

    /**
     * Delete an account with the given name.
     *
     * @param userName
     * @param client
     */
    public static void deleteAccount(String accessKey) throws Exception {
        AccountBean account = userAccounts.get(accessKey);
        if (account == null) {
            account = actionHelper.getAccountBeanByAccessKey(accessKey);
        }
        if (account == null) {
            throw ErrorResponse.notFound();
        }
        DeleteAccountRequest request = deleteAccountRequest(account);
        DeleteAccount deleteAccount = new DeleteAccount();
        actionHelper.invokeProcess(deleteAccount,
                request.getRequest(), request.getResponse(),
                request.getMap());
        userAccounts.remove(accessKey);
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
        AccountLocalHelper.actionHelper = actionTestHelper;
    }

    @Autowired(required=true)
    public void setAccessKey(String accessKey) {
        AccountLocalHelper.accessKey = accessKey;
    }

    @Autowired(required=true)
    public void setSecretKey(String secretKey) {
        AccountLocalHelper.secretKey = secretKey;
    }

    @Autowired(required=true)
    public void setDefaultAvailabilityZone(String defaultAvailabilityZone) {
        AccountLocalHelper.defaultAvailabilityZone = defaultAvailabilityZone;
    }

    @Autowired(required=true)
    public void setTenant(String tenant) {
        AccountLocalHelper.tenant = tenant;
    }

    public static class CreateAccountRequest extends ActionRequest {
    }

    public static class DeleteAccountRequest extends ActionRequest {
    }
}
