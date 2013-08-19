package com.msi.tough.internal.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.engine.aws.ec2.Volume;
import com.msi.tough.engine.aws.rds.DBInstance;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.RDSUtil;

public class BootstrapDBInstance extends BootstrapChefRoles{
	private final static Logger logger = Appctx
			.getLogger(BootstrapDBInstance.class.getName());
	
	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
					throws Exception {
		/*String dbId = QueryUtil.getString(map, "DBInstanceIdentifier");
		long acid = QueryUtil.getLong(map, "Acid");
		AccountBean ab = AccountUtil.readAccount(s, acid);
		AccountType at = new AccountType();
		at.setAccessKey(ab.getApiUsername());
		at.setSecretKey(ab.getApiPassword());
		at.setTenant(ab.getTenant());
		at.setId(ab.getId());
		at.setDefZone(ab.getDefZone());
		
		logger.debug("Checking if the volume and the instance are ready for bootstrap.");
		DBInstance dbInstEngine = new DBInstance();
		int failCount = 0;
		while(!dbInstEngine.dbInstanceBootstrapHelper(s, acid, at, dbId) && failCount < 10){
			++failCount;
			logger.debug("Waiting for the volume attachment to be completed... " + failCount + "/10");
			Thread.sleep(5000);
		}
		logger.debug("Volume is attached now. Continue with the bootstrapping process with Chef.");*/
		
		
		return super.process0(s, req, resp, map);
	}
}
