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

import com.msi.tough.query.UnsecuredAction;

public class ProcessTerminatedInstances extends UnsecuredAction {

	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		// final Query qry = s.createQuery("from AccountBean");
		// final List<AccountBean> list = qry.list();
		// for (final AccountBean ac : list) {
		// Account aco = AccountUtil.toAccount(ac);
		// Query gq = s.createQuery("from ASGroupBean where userId="
		// + ac.getId());
		// for (final ASGroupBean grp : (List<ASGroupBean>) gq.list()) {
		// String grpins = grp.getInstances();
		// if (grpins == null || grpins.length() == 0) {
		// continue;
		// }
		// CommaObject co = new CommaObject(grpins);
		// for (String instanceId : co.toList()) {
		// DescribeInstance di = new DescribeInstance();
		// Instance inst = di.process(instanceId, aco);
		// if (inst.getStatus().equals("terminated")) {
		// AccountUtil.removeInstace(s, ac, instanceId);
		// // update database
		// CommaObject cog = new CommaObject(grp.getInstances());
		// cog.remove(instanceId);
		// grp.setInstances(cog.toString());
		// s.save(grp);
		// }
		// }
		// }
		// }
		return "";
	}
}
