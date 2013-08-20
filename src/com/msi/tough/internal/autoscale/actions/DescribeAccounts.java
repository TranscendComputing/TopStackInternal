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
