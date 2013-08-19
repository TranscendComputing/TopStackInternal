package com.msi.tough.internal.autoscale.integration;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpRequest;
import com.msi.tough.core.HttpUtils;
import com.msi.tough.internal.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.internal.autoscale.helper.AccountHelper;

public class DeleteAccountTest extends AbstractBaseAutoscaleTest {

    //private static Logger logger = Appctx
    //        .getLogger(DeleteAccountTest.class.getName());

    private final String baseUserName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "delete:1:" + baseUserName;
    String name2 = "delete:2:" + baseUserName;

    @Autowired
    public String endpoint;

    @Autowired
    public String tenant;

    @Autowired
    public String defaultAvailabilityZone;

    @Autowired
    public AWSCredentials testUser1;

    @Autowired
    public AWSCredentials testUser2;

    @Before
    @Transactional
    public void putAccounts() throws Exception {
        AccountHelper.createAccount(name1,
                testUser1.getAWSAccessKeyId(),
                testUser1.getAWSSecretKey());
    }

    @Test
    public void testDeleteAccountNotThere() {
        final HttpRequest request = new HttpRequest(HttpMethodName.GET)
        .withParameter("Action", "DeleteAccount");
        Map<String,String> requestParams = request.getParameters();
        String result = new HttpUtils().sendGetRequest(endpoint, requestParams);
        assertEquals("Insufficient Data", result);
    }

    @After
    public void deleteAccounts() throws Exception {
        AccountHelper.deleteAllCreatedAccounts();
    }
}
