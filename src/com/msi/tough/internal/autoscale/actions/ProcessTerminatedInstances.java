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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.QueryBuilder;
import com.msi.tough.message.CoreMessage.ErrorResult;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.query.Action;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.workflow.WorkflowSubmitter;
import com.transcend.compute.message.DescribeInstancesMessage.DescribeInstancesRequestMessage;
import com.transcend.compute.message.DescribeInstancesMessage.DescribeInstancesResponseMessage;
import com.transcend.compute.message.DescribeInstancesMessage.DescribeInstancesRequestMessage.InstanceDescribeDepth;
import com.transcend.compute.message.InstanceMessage.Instance;
import com.transcend.compute.message.ReservationMessage.Reservation;

public class ProcessTerminatedInstances extends UnsecuredAction {
    private final static Logger logger = Appctx
            .getLogger(ProcessTerminatedInstances.class);

    /** How long to wait for describe. */
    private static final int MAX_SECS = 30;

    @Resource
    private WorkflowSubmitter toCompute = null;

    @Override
    public String process0(final Session s, final HttpServletRequest req,
            final HttpServletResponse resp, final Map<String, String[]> map)
            throws Exception {
        QueryBuilder builder = new QueryBuilder("from InstanceBean");
        builder.equals("health", "Healthy");
        final Query query = builder.toQuery(s);
        @SuppressWarnings("unchecked")
        final List<InstanceBean> list = query.list();
        Map<String, InstanceBean> idToInstance = new HashMap<String, InstanceBean>();
        for (InstanceBean instance : list) {
            DescribeInstancesRequestMessage.Builder request =
                    DescribeInstancesRequestMessage.newBuilder();
            request.setTypeId(true);
            idToInstance.put(instance.getInstanceId(), instance);
            AccountBean account = AccountUtil.readAccount(s, instance.getUserId());
            request.setCallerAccessKey(account.getAccessKey());
            request.setRequestId(req.getRequestURI());
            request.addInstanceIds(instance.getInstanceId());
            request.setInstanceDescribeDepth(InstanceDescribeDepth.BASIC_ONLY);
            DescribeInstancesResponseMessage response = null;
            Object result = toCompute.submitAndWait(request.build(), MAX_SECS);
            if (result instanceof DescribeInstancesResponseMessage) {
                response = (DescribeInstancesResponseMessage) result;
            } else if (result instanceof ErrorResult) {
                ErrorResult error = (ErrorResult) result;
                if ("InvalidInstance.NotFound".equals(error.getErrorCode())) {
                    removeInstanceFromService(s, null,
                            idToInstance.get(instance.getInstanceId()));
                }
                logger.debug("Got error describing instance: " + error.toString());
                continue;
            }
            for (Reservation reservation : response.getReservationsList()) {
                for (Instance found : reservation.getInstanceList()) {
                    if (! found.getState().getName().equals("running")) {
                        if (! found.getState().getName().equalsIgnoreCase("pending")) {
                            removeInstanceFromService(s, found,
                                    idToInstance.get(instance.getInstanceId()));
                        }
                    }
                }
            }
        }
        return Action.ACTION_SUCCESS;
    }

    private void removeInstanceFromService(Session s, Instance instance,
            InstanceBean instanceBean) {
        ASGroupBean group = instanceBean.getAsGroup();
        logger.info("Removing instance "+instanceBean.getInstanceId()+
                " from service in group " +
                (group != null? group.getName() : "[none]"));
        instanceBean.setHealth("Unhealthy");
        if (instance == null) {
            instanceBean.setStatus("terminated");
        } else {
            logger.info("Instance is "+ instance.getState());
            instanceBean.setStatus(instance.getState().getName());
        }
        s.save(instanceBean);
    }
}
