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
