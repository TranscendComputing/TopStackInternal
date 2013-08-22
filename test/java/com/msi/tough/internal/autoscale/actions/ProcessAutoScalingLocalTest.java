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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.msi.tough.internal.autoscale.AbstractBaseAutoscaleTest;
import com.msi.tough.query.Action;
import com.msi.tough.query.ActionRequest;
import com.msi.tough.query.ActionTestHelper;

/**
 * Test delete accounts locally.
 *
 * @author jgardner
 *
 */
public class ProcessAutoScalingLocalTest extends AbstractBaseAutoscaleTest {

    @Autowired
    private ActionTestHelper actionHelper = null;

    @Test
    public void testProcessRun() throws Exception {
        String result = null;
        final ActionRequest request = new ActionRequest();
        ProcessAutoScaling processAutoScaling = new ProcessAutoScaling();
        result = actionHelper.invokeProcess(processAutoScaling,
                request.getRequest(), request.getResponse(), request.getMap());
        assertEquals(Action.ACTION_SUCCESS, result);
    }

}
