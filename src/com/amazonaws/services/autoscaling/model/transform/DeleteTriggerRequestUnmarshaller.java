package com.amazonaws.services.autoscaling.model.transform;

import java.util.Map;

import com.amazonaws.services.autoscaling.model.DeleteTriggerRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.query.QueryUtil;

/**
 * CreateLoadBalancerRequestUnmarshaller
 */
public class DeleteTriggerRequestUnmarshaller implements
		Unmarshaller<DeleteTriggerRequest, Map<String, String[]>> {

	private static DeleteTriggerRequestUnmarshaller instance;

	public static DeleteTriggerRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteTriggerRequestUnmarshaller();
		}
		return instance;
	}

	@Override
	public DeleteTriggerRequest unmarshall(final Map<String, String[]> in)
			throws Exception {
		final DeleteTriggerRequest req = new DeleteTriggerRequest();
		req.setTriggerName(QueryUtil.requiredString(in, "TriggerName"));
		req.setAutoScalingGroupName(QueryUtil.requiredString(in,
				"AutoScalingGroupName"));
		return req;
	}
}
