package com.msi.tough.internal.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.Constants;

public class GatherInstancesData extends UnsecuredAction implements Constants {
	private final static Logger logger = Appctx
			.getLogger(GatherInstancesData.class.getName());


	/**
     * Default constructor.
     */
    public GatherInstancesData() {
        super();
        setLessVerbose(true); // Avoid lots of logging; this is called on timer.
    }


	@Override
	public String process0(final Session s, final HttpServletRequest r,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		// final Query qry = s.createQuery("from AccountBean");
		// final List<AccountBean> list = qry.list();
		// for (final AccountBean ac : list) {
		// final String idType = InstanceUtil.getIdType(ac.getDefZone());
		// if (idType == null || idType.equals(Constants.INSTANCE_ID_TYPE_EC2))
		// {
		// continue;
		// }
		// if (ac.getAccessKey() == null || ac.getSecretKey() == null) {
		// continue;
		// }
		// final BasicAWSCredentials cred = new BasicAWSCredentials(
		// ac.getAccessKey(), ac.getSecretKey());
		//
		// final AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		// final String endpoint = (String) ConfigurationUtil
		// .getConfiguration(Arrays.asList(new String[] { "EC2_URL",
		// ac.getDefZone() }));
		// if (ec2 == null || endpoint == null) {
		// continue;
		// }
		// ec2.setEndpoint(endpoint);
		//
		// logger.debug("GatherInstance for " + ac.getId());
		// final DescribeInstancesRequest req = new DescribeInstancesRequest();
		// DescribeInstancesResult res = null;
		// try {
		// res = ec2.describeInstances(req);
		// } catch (final Exception e) {
		// }
		// if (res == null) {
		// continue;
		// }
		// for (final Reservation i : res.getReservations()) {
		// for (final Instance rins : i.getInstances()) {
		// final InstanceBean b = InstanceUtil.getInstance(s,
		// ac.getId(), rins.getInstanceId());
		// if (b == null) {
		// final InstanceType ins = new InstanceType();
		// InstanceUtils.toResource(ins, rins);
		// ins.setAcId(ac.getId());
		// InstanceBean ib = InstanceUtil.getInstance(s,
		// ac.getId(), ins.getInstanceId());
		// if (ib != null) {
		// continue;
		// }
		// ib = InstanceUtil.createNewInstance(s, ac.getId(),
		// rins.getInstanceId(),
		// ins.getAvailabilityZone(), null, null,
		// ins.getEc2Id());
		// ib.setIpAddress(rins.getPrivateIpAddress());
		// ib.setDns(rins.getPublicIpAddress());
		// s.save(ib);
		// logger.debug("Added Instance " + ib.getInstanceId()
		// + " " + ib.getEc2Id());
		// }
		// }
		// }
		// }
	    if (logger.isDebugEnabled()) {
	        logger.debug("Gather instance data.");
	    }
		return "DONE";
	}
}
