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
package com.msi.tough.internal.openstack.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.openstack.OpenstackUtil;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.UnsecuredAction;

public class RunInstances extends UnsecuredAction {

	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final AccountBean ac = getAccountBean();
		String avz = req.getPathInfo();
		if (avz != null) {
			avz = avz.substring(1);
		}
		final String imageId = map.get("ImageId")[0];
		final String instanceType = map.get("InstanceType")[0];
		final String[] userData = map.get("UserData");
		final StringBuilder json = new StringBuilder("{\"server\" : {")
				.append("\"imageRef\":\"").append(imageId).append("\",")
				.append("\"flavorRef\" : \"").append(instanceType).append("\"");
		if (userData != null) {
			json.append(",\"user-data\":\"").append(userData[0]).append("\"");
		}
		json.append("}}");
		final String r = OpenstackUtil.post(s, ac, "v2/" + ac.getTenant()
				+ "/servers.json", json.toString(), true, "servers");
		final JsonNode n = JsonUtil.load(r);
		final JsonNode srv = n.get("server");
		final Map<String, Object> srvMap = JsonUtil.toMap(srv);
		final String instanceId = (String) srvMap.get("id");

		final XMLNode xn = new XMLNode("RunInstancesResponse");
		xn.addAttr("xmlns", "http://ec2.amazonaws.com/doc/2012-06-15/");
		final XMLNode nr = QueryUtil.addNode(xn, "instancesSet");
		final XMLNode i = QueryUtil.addNode(nr, "item");
		QueryUtil.addNode(i, "instanceId", instanceId);
		return xn.toString();
	}
}
