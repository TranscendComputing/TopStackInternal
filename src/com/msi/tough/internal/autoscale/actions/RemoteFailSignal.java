package com.msi.tough.internal.autoscale.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.CFUtil;

public class RemoteFailSignal extends UnsecuredAction {

	@Override
	public String process0(Session session, HttpServletRequest req,
			HttpServletResponse resp, Map<String, String[]> map)
			throws Exception {
		final long acid = QueryUtil.getLong(map, "AcId");
		final String stackId = QueryUtil.getString(map, "StackId");
		
		CFUtil.failStack(acid, stackId);
		return "Failure Acknowledged";
	}

}
