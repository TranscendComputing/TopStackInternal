/*
 * TopStack (c) Copyright 2012-2013 Transcend Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msi.tough.internal.autoscale.integration;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HttpUtils;
import com.msi.tough.internal.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.internal.autoscale.helper.AccountHelper;
import com.msi.tough.internal.autoscale.helper.AccountLocalHelper;
import com.msi.tough.query.ErrorResponse;

public class CreateAccountTest extends AbstractBaseAutoscaleTest {

    private static Logger logger = Appctx.getLogger(CreateAccountTest.class
            .getName());

    private final String userName = "create:"
            + UUID.randomUUID().toString().substring(0, 8);

    @Autowired
    public String endpoint;

    @Autowired
    public String tenant;

    @Autowired
    public String defaultAvailabilityZone;

    @Autowired
    public AWSCredentials testUser1;

    public static AWSCredentials _testUser1;

    @Autowired
    public AWSCredentials testUser2;

    @Before
    public void setUp() throws Exception {
        // Remember our user, for static after class teardown.
        _testUser1 = testUser1;
    }

    @Before
    @Transactional
    public void cleanupLeftovers() throws Exception {
        // Just in case, clean up previous accounts, before a run.
        try {
            AccountLocalHelper.deleteAccount(_testUser1.getAWSAccessKeyId());
        } catch (ErrorResponse err) {
            if (err.getCode() == ErrorResponse.CODE_RESOURCE_NOT_FOUND) {
                return;
            }
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Just in case, delete our test account, in case it was left over
        // from a failed run; it should not exist at test run.
        try {
            AccountLocalHelper.deleteAccount(_testUser1.getAWSAccessKeyId());
        } catch (ErrorResponse err) {
            if (err.getCode() == ErrorResponse.CODE_RESOURCE_NOT_FOUND) {
                return;
            }
        }
    }

    @Test
    public void testCreateAccountMissingInfo() {
        final HttpRequest request = new HttpRequest(HttpMethodName.GET);
        request.withParameter("Action", "CreateAccount").withParameter(
                "AccessKey", "sadfsdaf");
        String result = new HttpUtils().sendGetRequest(endpoint,
                request.getParameters());
        assertEquals("Insufficient Data", result);
    }

    @Test
    public void testCreateAccount() throws Exception {
        logger.info("Create account at: " + endpoint);
        AccountHelper.createAccount(userName, testUser1.getAWSAccessKeyId(),
                testUser1.getAWSSecretKey());
    }

    @After
    @Transactional
    public void cleanupCreated() throws Exception {
        AccountHelper.deleteAllCreatedAccounts();
    }

}
