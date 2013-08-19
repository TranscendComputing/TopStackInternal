package com.msi.tough.internal.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.msi.tough.query.UnsecuredAction;

public class PublishASMetrics extends UnsecuredAction {

	@Override
	public String process0(Session session, HttpServletRequest req,
			HttpServletResponse resp, Map<String, String[]> map)
			throws Exception {
		// final Query qry = session.createQuery("from AccountBean");
		// final List<AccountBean> list = qry.list();
		//
		// ASHedwigClient ashclient = new ASHedwigClient();
		//
		// for (final AccountBean ac : list) {
		// Account aco = AccountUtil.toAccount(ac);
		//
		// Query gq = session.createQuery("from ASGroupBean where userId="
		// + ac.getId());
		// for (final ASGroupBean grp : (List<ASGroupBean>) gq.list()) {
		// TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		// Calendar cal = Calendar.getInstance();
		// long time = cal.getTimeInMillis();
		//
		// String grpName =grp.getName();
		// String grpins = grp.getInstances();
		// long running = 0;
		// long pending = 0;
		// // long terminated = 0;
		// if (grpins == null || grpins.length() == 0) {
		// }
		// else{
		// CommaObject co = new CommaObject(grpins);
		// for (String instanceId : co.toList()) {
		// DescribeInstance di = new DescribeInstance();
		// Instance inst = di.process(instanceId, aco);
		// if (inst.getStatus().equals("running")) {
		// ++running;
		// }
		// if (inst.getStatus().equals("pending")) {
		// ++pending;
		// }
		// /* if (inst.getStatus().equals("terminated")) {
		// ++terminated;
		// }*/
		// }
		// }
		//
		// ashclient.publish(grpName, "GroupMinSize",
		// Long.toString(grp.getMinSz()), Long.toString(time));
		// ashclient.publish(grpName, "GroupMaxSize",
		// Long.toString(grp.getMaxSz()), Long.toString(time));
		// ashclient.publish(grpName, "GroupDesiredCapacity",
		// Long.toString(grp.getCapacity()), Long.toString(time));
		//
		// ashclient.publish(grpName, "GroupInServiceInstances",
		// Long.toString(running), Long.toString(time));
		// ashclient.publish(grpName, "GroupPendingInstances",
		// Long.toString(pending), Long.toString(time));
		// // ashclient.publish(grpName, "GroupterminatingInstances",
		// Long.toString(terminated));
		// ashclient.publish(grpName, "GroupTotalInstances",
		// Long.toString(running+pending), Long.toString(time));
		//
		// }
		// }
		// ashclient.stop();
		return null;
	}
}
