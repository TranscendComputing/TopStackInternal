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
import com.msi.tough.query.Action;
import com.msi.tough.query.ActionRequest;
import com.msi.tough.query.ActionTestHelper;

/**
 * Test delete accounts locally.
 *
 * @author jgardner
 *
 */
public class ProcessTerminatedInstancesLocalTest extends AbstractBaseAutoscaleTest {

    private final String baseUserName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "crLoc:1:" + baseUserName;
    String name2 = "crLoc:2:" + baseUserName;

    @Autowired
    private ActionTestHelper actionHelper = null;

    @Autowired
    private ProcessTerminatedInstances processTerminated = null;

    @Test
    public void testProcessTerminated() throws Exception {
        String result = null;
        final ActionRequest request = new ActionRequest();
        result = actionHelper.invokeProcess(processTerminated,
                request.getRequest(), request.getResponse(), request.getMap());
        assertEquals(Action.ACTION_SUCCESS, result);
    }

}
