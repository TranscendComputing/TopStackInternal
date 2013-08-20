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

import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.CFUtil;

public class PostWait extends UnsecuredAction {

	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
			throws Exception {
		final String physicalId = map.get("PhysicalId") != null ? map
				.get("PhysicalId")[0] : null;
		final String stackId = map.get("StackId") != null ? map.get("StackId")[0]
				: null;
		final long acid = map.get("AcId") != null ? Long.parseLong(map
				.get("AcId")[0]) : 0;
		final String status = map.get("Status") != null ? map.get("Status")[0]
				: null;
		final String hostname = map.get("Hostname") != null ? map
				.get("Hostname")[0] : null;
		CFUtil.postWaitMessage(s, stackId, acid, physicalId, hostname, status,
				true, map);
		return "DONE";
	}
}
