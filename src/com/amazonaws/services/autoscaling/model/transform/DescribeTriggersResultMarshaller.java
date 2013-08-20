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
package com.amazonaws.services.autoscaling.model.transform;

import com.amazonaws.services.autoscaling.model.DescribeTriggersResult;
import com.amazonaws.services.autoscaling.model.Trigger;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;

/**
 * Create Load Balancer Request Marshaller
 */
public class DescribeTriggersResultMarshaller implements
        Marshaller<String, MarshallStruct<DescribeTriggersResult>> {

    @Override
    public String marshall(final MarshallStruct<DescribeTriggersResult> input)
            throws Exception {
        final XMLNode xn = new XMLNode("DescribeTriggersResponse");
        final XMLNode xr = QueryUtil.addNode(xn, "DescribeTriggersResult");
        DescribeTriggersResult o = input.getMainObject();
        for (Trigger t : o.getTriggers()) {
            final XMLNode m = QueryUtil.addNode(xr, "member");
            QueryUtil.addNode(m, "AutoScalingGroupName",
                    t.getAutoScalingGroupName());
            QueryUtil.addNode(m, "BreachDuration", t.getBreachDuration());
            QueryUtil.addNode(m, "CreatedTime", t.getCreatedTime());
            QueryUtil.addNode(m, "CustomUnit", t.getCustomUnit());
            XMLNode dims = QueryUtil.addNode(m, "Dimensions");
            for (Dimension d : t.getDimensions()) {
                final XMLNode md = QueryUtil.addNode(dims, "member");
                QueryUtil.addNode(md, "Name", d.getName());
                QueryUtil.addNode(md, "Value", d.getValue());
            }
            QueryUtil.addNode(m, "LowerBreachScaleIncrement",
                    t.getLowerBreachScaleIncrement());
            QueryUtil.addNode(m, "LowerThreshold", t.getLowerThreshold());
            QueryUtil.addNode(m, "MeasureName", t.getMeasureName());
            QueryUtil.addNode(m, "Namespace", t.getNamespace());
            QueryUtil.addNode(m, "Period", t.getPeriod());
            QueryUtil.addNode(m, "Statistic", t.getStatistic());
            QueryUtil.addNode(m, "Status", t.getStatus());
            QueryUtil.addNode(m, "TriggerName", t.getTriggerName());
            QueryUtil.addNode(m, "Unit", t.getUnit());
            QueryUtil.addNode(m, "UpperBreachScaleIncrement",
                    t.getUpperBreachScaleIncrement());
            QueryUtil.addNode(m, "UpperThreshold", t.getUpperThreshold());
        }
        //
        // final XMLNode mmeta = QueryUtil.addNode(xn, "ResponseMetadata");
        // QueryUtil.addNode(mmeta, "RequestId", in.getRequestId());
        return xn.toString();
    }
}
