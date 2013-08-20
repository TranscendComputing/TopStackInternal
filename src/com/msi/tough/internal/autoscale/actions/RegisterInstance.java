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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.InstanceUtil;

/**
 * Action to inject metadata about an instance into the datastore.
 *
 * Essex doesn't always seem to provide a valid instance ID, so this action
 * allows an instance to register whatever ID it sees (EC2 ID).
 *
 * @author jgardner
 *
 */
public class RegisterInstance extends UnsecuredAction {

    private final static Logger logger = Appctx
            .getLogger(RegisterInstance.class.getName());

    @Override
    public String process0(final Session s, final HttpServletRequest req,
            final HttpServletResponse resp, final Map<String, String[]> map)
            throws Exception {
        final String instanceId = map.get("Id") != null ? map.get("Id")[0]
                : null;
        final String accountId = map.get("Acid") != null ? map.get("Acid")[0]
                : null;
        final String hostname = map.get("Hostname") != null ? map.get("Hostname")[0]
                : null;
        AccountBean account;
        logger.debug("Got registration of instance: " + instanceId);
        try {
            account = AccountUtil.readAccount(s, new Long(accountId));
        } catch (NumberFormatException e) {
            logger.warn("Unrecognizable account ID: " + accountId);
            return "BAD_ACCOUNT_ID";
        }
        if (account == null) {
            logger.warn("No account with ID: " + accountId);
            return "NO_ACCOUNT_ID";
        }
        final InstanceBean inst = InstanceUtil
                .getInstance(s, instanceId);
        if (inst == null) {
            InstanceBean ib = InstanceUtil
                    .getInstanceByHostName(s,
                            account.getId(),
                            hostname);
            ib.setUserId(account.getId());
            ib.setEc2Id(instanceId);
            s.save(ib);
        }
        return "DONE";
    }
}
