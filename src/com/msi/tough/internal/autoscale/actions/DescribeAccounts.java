package com.msi.tough.internal.autoscale.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.UnsecuredAction;

public class DescribeAccounts extends UnsecuredAction {

	@SuppressWarnings("unchecked")
	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final Query q = s.createQuery("from AccountBean");
		final List<AccountBean> l = q.list();

		String accountsJson = JsonUtil.accBeansToJsonList(l);
		return accountsJson;
	}
}
