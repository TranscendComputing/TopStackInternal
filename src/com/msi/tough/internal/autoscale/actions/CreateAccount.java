package com.msi.tough.internal.autoscale.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.ec2.KeyPairType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.KeyPairUtils;

public class CreateAccount extends UnsecuredAction {
    private final static Logger logger = Appctx
            .getLogger(CreateAccount.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final String name = map.get("UserName") != null ? map.get("UserName")[0]
				: null;
		final String accessKey = map.get("AccessKey") != null ? map
				.get("AccessKey")[0] : null;
		final String secretKey = map.get("SecretKey") != null ? map
				.get("SecretKey")[0] : null;
		final String defZone = map.get("CloudName") != null ? map
				.get("CloudName")[0] : null;
		if (accessKey == null || secretKey == null || defZone == null
				|| name == null) {
		    logger.warn("Insufficient Data..." );
			return "Insufficient Data";
		}
		final String apiUser = (map.get("APIUsername") != null && !map.get("APIUsername")[0].equals(""))? map
				.get("APIUsername")[0] : accessKey;
		final String apiPwd = (map.get("APIPassword") != null && !map.get("APIUsername")[0].equals("")) ? map
				.get("APIPassword")[0] : secretKey;
		final String apiTenant = map.get("APITenant") != null ? map
				.get("APITenant")[0] : null;
		final String email = map.get("Email") != null ? map.get("Email")[0] : null;
		final Query q = s.createQuery("from AccountBean where accessKey='"
				+ accessKey + "'");
		final List<AccountBean> l = q.list();
		if (l != null && l.size() > 0) {
			return "Already Exists";
		}
		final AccountBean ac = new AccountBean();
		ac.setAccessKey(accessKey);
		ac.setDefSecurityGroups("default");
		ac.setDefZone(defZone);
		ac.setName(name);
		ac.setSecretKey(secretKey);
		ac.setApiUsername(apiUser);
		ac.setApiPassword(apiPwd);
		ac.setTenant(apiTenant);
		ac.setEmails(email);
		ac.setRoleName("ROLE_USER");
		ac.setEnabled(true);
		s.save(ac);
		final String keyName = KeyPairUtils.getKeyName(ac.getAccessKey());
		ac.setDefKeyName(keyName);
		s.save(ac);
		final TemplateContext ctx = new TemplateContext(null);
		KeyPairType kp = KeyPairUtils
		        .createKeyPair(AccountUtil.toAccount(ac), ctx, null, "__"
						+ StringHelper.randomStringFromTime(), ac.getDefZone(),
						keyName);
		if (kp == null) {
		    logger.warn("Failed to create keypair.");
		    s.delete(ac);
		    return "FAIL";
		}

		logger.debug("Created account successfully.");
		return JsonUtil.accBeanToJsonObject(ac);
	}
}
