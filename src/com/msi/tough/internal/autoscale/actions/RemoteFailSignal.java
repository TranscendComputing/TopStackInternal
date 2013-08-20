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
