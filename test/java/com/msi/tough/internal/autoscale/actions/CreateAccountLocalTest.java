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
package com.msi.tough.internal.autoscale.actions;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.AWSCredentials;
import com.msi.tough.internal.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.internal.autoscale.helper.AccountLocalHelper;
import com.msi.tough.internal.autoscale.helper.AccountLocalHelper.CreateAccountRequest;
import com.msi.tough.query.ActionTestHelper;

/**
 * Test delete accounts locally.
 *
 * @author jgardner
 *
 */
public class CreateAccountLocalTest extends AbstractBaseAutoscaleTest {

    private final String baseUserName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "crLoc:1:" + baseUserName;
    String name2 = "crLoc:2:" + baseUserName;

    @Autowired
    private ActionTestHelper actionHelper = null;

    @Autowired
    public AWSCredentials testUser1;

    @Autowired
    public AWSCredentials testUser2;

    @Before
    @Transactional
    public void putAccounts() throws Exception {
        AccountLocalHelper.createAccount(name1,
                testUser1.getAWSAccessKeyId(),
                testUser1.getAWSSecretKey());
    }

    @Test
    public void testCreateAccountNone() throws Exception {
        String result = null;
        final CreateAccountRequest request = new CreateAccountRequest();
        CreateAccount createAccount = new CreateAccount();
        request.put("UserName", "");
        result = actionHelper.invokeProcess(createAccount,
                request.getRequest(), request.getResponse(), request.getMap());
        assertEquals("Insufficient Data", result);
    }

    @Test
    public void testCreateAccountDup() throws Exception {
        String result = null;
        final CreateAccountRequest request =
                AccountLocalHelper.createAccountRequest(name2);
        CreateAccount createAccount = new CreateAccount();
        request.put("UserName", name2);
        // Currently, reusing access & secret is considered a dup.
        request.put("AccessKey", testUser1.getAWSAccessKeyId());
        request.put("SecretKey", testUser1.getAWSSecretKey());
        request.put("APIUsername", testUser1.getAWSAccessKeyId());
        request.put("APIPassword", testUser1.getAWSSecretKey());
        result = actionHelper.invokeProcess(createAccount,
                request.getRequest(), request.getResponse(), request.getMap());
        assertEquals("Already Exists", result);
    }

    @After
    @Transactional
    public void cleanupCreated() throws Exception {
        AccountLocalHelper.deleteAllCreatedAccounts();
    }

    @Test
    @Ignore
    public void createOneOffAccount() throws Exception {
        String result = null;
        final CreateAccountRequest request = new CreateAccountRequest();
        CreateAccount createAccount = new CreateAccount();
        request.put("UserName", "username_essex");
        request.put("AccessKey", "username");
        request.put("SecretKey", "password");
        request.put("CloudName", "nova");
        request.put("APIUsername", "username");
        request.put("APIPassword", "password");
        request.put("APITenant", "be344db2784445da9415d19c2bb31ac1");
        result = actionHelper.invokeProcess(createAccount,
                request.getRequest(), request.getResponse(), request.getMap());
        assertEquals("DONE", result);
    }

}
