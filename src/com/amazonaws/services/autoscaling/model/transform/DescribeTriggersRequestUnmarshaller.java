package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.DescribeTriggersRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DescribeTriggersRequestUnmarshaller implements
		Unmarshaller<DescribeTriggersRequest, Map<String, String[]>> {

	private static DescribeTriggersRequestUnmarshaller instance;

	public static DescribeTriggersRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeTriggersRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public DescribeTriggersRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final DescribeTriggersRequest req = new DescribeTriggersRequest();
		req.setAutoScalingGroupName(QueryUtil.getString(in,
				"AutoScalingGroupName"));
		return req;
	}
}
