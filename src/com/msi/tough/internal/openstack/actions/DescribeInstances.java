package com.msi.tough.internal.openstack.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.hibernate.Session;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.openstack.OpenstackUtil;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.UnsecuredAction;

public class DescribeInstances extends UnsecuredAction {

	private static void marshall(final XMLNode nr, final JsonNode srv) {
		final String instanceId = srv.get("id").getTextValue();
		final String ec2Id = srv.get("OS-EXT-SRV-ATTR:instance_name")
				.getTextValue();
		final String[] parts = ec2Id.split("-");
		final XMLNode i = QueryUtil.addNode(nr, "item");
		QueryUtil.addNode(i, "instanceId", instanceId + "(i-" + parts[1] + ")");
	}

	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final AccountBean ac = getAccountBean();
		String avz = req.getPathInfo();
		if (avz != null) {
			avz = avz.substring(1);
		}
		String path = null;
		if (map.get("insatance-id") != null) {
			final String instanceId = map.get("insatance-id")[0];
			path = "v2/" + ac.getTenant() + "/servers/" + instanceId
					+ "/detail.json";
		} else {
			path = "v2/" + ac.getTenant() + "/servers/detail.json";
		}

		final String r = OpenstackUtil.get(s, ac, path, null, true, "servers");

		final XMLNode xn = new XMLNode("DescribeInstancesResponse");
		xn.addAttr("xmlns", "http://ec2.amazonaws.com/doc/2012-06-15/");
		final XMLNode nr = QueryUtil.addNode(xn, "instancesSet");

		final JsonNode n = JsonUtil.load(r);
		final JsonNode srv = n.get("servers");

		if (srv instanceof ArrayNode) {
			final ArrayNode an = (ArrayNode) srv;
			for (int i = 0; i < an.size(); i++) {
				final JsonNode sn = an.get(i);
				marshall(nr, sn);
			}
		} else {
			marshall(nr, srv);
		}
		return xn.toString();
	}
}
