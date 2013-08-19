package com.msi.tough.internal.autoscale.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.security.AESSecurity;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.KeyPairUtils;

public class UpdateAccount extends UnsecuredAction {

	@SuppressWarnings("unchecked")
	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {

        final String id = map.get("Id") != null ? map.get("Id")[0] : null;
		final String name = map.get("UserName") != null ? map.get("UserName")[0]
				: null;
		final String accessKey = map.get("AccessKey") != null ? map
				.get("AccessKey")[0] : null;
		final String secretKey = map.get("SecretKey") != null ? map.get("SecretKey")[0] : null;
		final String defZone = map.get("CloudName") != null ? map
				.get("CloudName")[0] : null;
		if (id == null  || name == null) {
			return "A required field is missing.";
		}
		final String apiUser = map.get("APIUsername") != null ? map
				.get("APIUsername")[0] : null;
		final String apiPwd = map.get("APIPassword") != null ? map
				.get("APIPassword")[0] : null;
		final String apiTenant = map.get("APITenant") != null ? map
				.get("APITenant")[0] : null;
		final String email = map.get("Email") != null ? map.get("Email")[0] : null;
        final String roleName = map.get("RoleName") != null ? map.get("RoleName")[0] : null;
        final boolean enabled = map.get("Enabled") != null ? Boolean.valueOf(map.get("Enabled")[0]) : null;
		final Query q = s.createQuery("from AccountBean where id='" + id + "'");
		final List<AccountBean> l = q.list();
		if (l == null || l.size() != 1) {
			return "Account Not Found";
		}
		final AccountBean ac = l.get(0);
    		final String keyName = ac.getDefKeyName();
    		
    		ac.setAccessKey(accessKey);
    		if(secretKey != null && !secretKey.equals("") && !secretKey.equals("*****")){
                ac.setSecretKey(secretKey);
            }
            else{
                ac.setSecretKey(ac.getSecretKey());
            }
            final String defName = KeyPairUtils.getKeyName(ac.getAccessKey());
    		if (!defName.equalsIgnoreCase(keyName)){
                final TemplateContext ctx = new TemplateContext(null);
                KeyPairUtils.deleteKeyPair(AccountUtil.toAccount(ac), ctx, null,
                        "__" + StringHelper.randomStringFromTime(),
                        ac.getDefZone(), keyName);
                
                try {
                    KeyPairUtils.createKeyPair(AccountUtil.toAccount(ac), ctx, null,
                            "__" + StringHelper.randomStringFromTime(),
                            ac.getDefZone(), defName);
                } catch (final Exception e) {
                    e.printStackTrace();
                    return "Cannot validate account with backend";
                }
            }
            
    		ac.setDefSecurityGroups("default");
    		ac.setDefZone(defZone);
    		ac.setName(name);
    		
    		if(apiUser == null || apiUser.equals(""))
    		    ac.setApiUsername(accessKey);
    		else
    		    ac.setApiUsername(apiUser);
    		if(apiPwd == null || apiUser.equals(""))
    		    ac.setApiPassword(ac.getSecretKey());
    		else
    		    ac.setApiPassword(apiPwd);
    		ac.setTenant(apiTenant);
            
    		ac.setDefKeyName(defName);
    		ac.setEmails(email);
    		ac.setRoleName(roleName);
    		ac.setEnabled(enabled);
    		s.save(ac);

            return JsonUtil.accBeanToJsonObject(ac);
	}
}
