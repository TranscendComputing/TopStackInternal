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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.autoscaling.model.CreateOrUpdateScalingTriggerRequest;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class CreateOrUpdateScalingTriggerRequestUnmarshaller
        implements
        Unmarshaller<CreateOrUpdateScalingTriggerRequest, Map<String, String[]>> {

    private static CreateOrUpdateScalingTriggerRequestUnmarshaller instance;

    public static CreateOrUpdateScalingTriggerRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new CreateOrUpdateScalingTriggerRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public CreateOrUpdateScalingTriggerRequest unmarshall(
            final Map<String, String[]> in) throws Exception {
        final CreateOrUpdateScalingTriggerRequest req = new CreateOrUpdateScalingTriggerRequest();
        req.setAutoScalingGroupName(QueryUtil.requiredString(in,
                "AutoScalingGroupName"));
        req.setBreachDuration(QueryUtil.getInt(in, "BreachDuration"));
        req.setCustomUnit(QueryUtil.getString(in, "CustomUnit"));
        List<Dimension> dims = new ArrayList<Dimension>();
        for (int i = 1;; i++) {
            if (in.get("Dimensions.member." + i + ".Name") == null) {
                break;
            }
            Dimension dim = new Dimension();
            String nm = in.get("Dimensions.member." + i + ".Name")[0];
            dim.setName(nm);
            if (nm.equals("InstanceId")) {
                dim.setValue(nm);
            } else {
                dim.setValue(in.get("Dimensions.member." + i + ".Value")[0]);
            }
            dims.add(dim);
        }
        req.setDimensions(dims);
        req.setLowerBreachScaleIncrement(QueryUtil.getString(in,
                "LowerBreachScaleIncrement"));
        req.setLowerThreshold(QueryUtil.getDouble(in, "LowerThreshold"));
        req.setMeasureName(QueryUtil.getString(in, "MeasureName"));
        req.setNamespace(QueryUtil.getString(in, "Namespace"));
        req.setPeriod(QueryUtil.getInt(in, "Period"));
        req.setStatistic(QueryUtil.getString(in, "Statistic"));
        req.setTriggerName(QueryUtil.getString(in, "TriggerName"));
        req.setUpperBreachScaleIncrement(QueryUtil.getString(in,
                "UpperBreachScaleIncrement"));
        req.setUpperThreshold(QueryUtil.getDouble(in, "UpperThreshold"));
        req.setUnit(QueryUtil.requiredString(in, "Unit"));
        return req;
    }
}
