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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.ExecutorHelper;
import com.msi.tough.core.ExecutorHelper.Executable;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.InstanceUtil;

public class BootstrapChefRoles extends UnsecuredAction {
	private final static Logger logger = Appctx
			.getLogger(BootstrapChefRoles.class.getName());

	@Override
	public String process0(final Session s, final HttpServletRequest req,
			final HttpServletResponse resp, final Map<String, String[]> map)
					throws Exception {

		final String hostname = map.get("Hostname")[0];
		final long acid = Long.parseLong(map.get("Acid")[0]);
		logger.debug("BootstapChefRoles Called for " + acid + " " + hostname);
		final AccountBean ac = AccountUtil.readAccount(s, acid);
		final InstanceBean inst = InstanceUtil.getInstanceByHostName(s,
				ac.getId(), hostname);
		if (inst == null) {
			logger.debug("Called for non registered instance " + hostname);
			return null;
		}
		final String chefRoles = inst.getChefRoles();
		if (chefRoles == null) {
			return null;
		}
		final String databag = inst.getDatabag();
		final AccountType acb = AccountUtil.toAccount(ac);

		final Executable r = new ExecutorHelper.Executable() {
			@Override
			public void run() {
				final String sleeps = (String) ConfigurationUtil
						.getConfiguration(Arrays
								.asList(new String[] { "BootstrapSleep" }));
				final long sleep = Long.parseLong(sleeps);
				try {
					Thread.sleep(sleep);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}

				int failCount = 1;
				boolean ipFound = false;
				while(!ipFound && failCount <= 5){
					logger.debug("Attempting to get the public IP address of the target instance: " + failCount + "/5");
					ipFound = HibernateUtil.withNewSession(new HibernateUtil.Operation<Boolean>() {
						@Override
						public Boolean ex(final Session s,
								final Object... as) throws Exception {
							final AccountBean ac = AccountUtil.readAccount(
									s, acb.getId());
							final InstanceBean inst = InstanceUtil
									.getInstanceByHostName(s, ac.getId(),
											hostname);
							if (inst.getPublicIp() == null
									|| inst.getPublicIp().equals("0.0.0.0")) {
								return false;
							}
							return true;
						}
					});
					logger.debug("*&* returned with " + ipFound);
					++failCount;
				}
				if(!ipFound){
					logger.debug("Public IP address of the target instance could not be retrieved, expect a halt!");
				}

				HibernateUtil.withNewSession(
						new HibernateUtil.Operation<Object>() {

							@Override
							public Object ex(final Session s,
									final Object... as) throws Exception {
								final AccountBean ac = AccountUtil.readAccount(
										s, acb.getId());
								final InstanceBean inst = InstanceUtil
										.getInstanceByHostName(s, ac.getId(),
												hostname);
								if (inst.getPublicIp() == null
										|| inst.getPublicIp().equals("0.0.0.0")) {
									logger.error("Instance did not have an IP: "
											+ inst.getPublicIpId());
									return null;
								}
								final String keyDir = (String) ConfigurationUtil
										.getConfiguration(Arrays
												.asList(new String[] { "KEYS_DIR" }));
								final String key = keyDir + "/"
										+ acb.getDefKeyName() + ".pem";
								final String ip = InstanceUtil.getIP(
										inst.getPublicIp(),
										inst.getPrivateIp(), inst.getAvzone());
								final boolean bootstrapChef = ((String) ConfigurationUtil
										.getConfiguration(Arrays
												.asList(new String[] {
														"BOOTSTRAP_CHEF",
														inst.getAvzone() })))
														.toLowerCase().startsWith("y");
								final boolean runChefClient = ((String) ConfigurationUtil
										.getConfiguration(Arrays
												.asList(new String[] {
														"RUN_CHEFCLIENT",
														inst.getChefRoles() })))
														.toLowerCase().startsWith("y");
								// shutdown running chef client
								{
									CFUtil.executeCommand(acb, null, "ssh",
											"-i", key, "-o",
											"StrictHostKeyChecking=false",
											"-o",
											"UserKnownHostsFile=/dev/null",
											"root@" + ip,
											"/etc/init.d/chef-client stop");
								}

								// bootstrap chef: this will copy
								// chef-validator.pem, register client and node with
								// chef, set role to CHEF_DEFAULT_ROLE
								if (bootstrapChef) {
								    final String dotchef = (String) ConfigurationUtil
											.getConfiguration(Arrays
													.asList(new String[] { "DOTCHEF_DIR" }));
									final List<String> args = new ArrayList<String>();
									args.add("knife");
									args.add("bootstrap");
									args.add("-i");
									args.add(key);
									args.add("-N");
									args.add(hostname);
									args.add("-x");
									args.add("root");
									args.add(ip);
									args.add("-r");
									args.add("role["
											+ (String) ConfigurationUtil.getConfiguration(Arrays
													.asList(new String[] { "CHEF_DEFAULT_ROLE" }))
													+ "]");
									args.add("-c");
									args.add(dotchef + "/knife.pem");
									CFUtil.executeCommand(acb, dotchef, args);
								} else {
									CFUtil.executeCommand(acb, null, "scp",
											"-i", key, "-o",
											"StrictHostKeyChecking=false",
											"-o",
											"UserKnownHostsFile=/dev/null",
											"/etc/chef/chef-validator.pem", "root@"
													+ ip + ":/etc/chef");
									CFUtil.executeCommand(acb, null, "ssh",
											"-i", key, "-o",
											"StrictHostKeyChecking=false",
											"-o",
											"UserKnownHostsFile=/dev/null",
											"root@" + ip, "chef-client");
								}

								// make node admin required for database update
								// by instance
								{
									ChefUtil.putClientAsAdmin(hostname);
								}

								// set databag property for the node
								if (databag != null && databag.length() != 0) {
									ChefUtil.putNodeAttribute(hostname,
											"__TRANSCEND__DATABAG__", databag);
								}

								// set the role for the instance
								{
									ChefUtil.putNodeRunlist(hostname, "role["
											+ chefRoles + "]");
									// CFUtil.executeCommand(s, acb, null,
									// "knife", "node", "run_list", "add",
									// hostname, "role[" + chefRoles + "]");
								}

								// restart the chef-client as we shutdown it
								// earlier
								if (runChefClient) {
									final String cm = "chef-client -d -P /var/run/chef/client.pid -L /var/log/chef/client.log -i "
											+ (String) ConfigurationUtil
											.getConfiguration(Arrays
													.asList(new String[] {
															"CHEF_CLIENT_SLEEP",
															chefRoles }))
															+ " -s 10 </dev/null >/dev/null 2&>1 &";
									CFUtil.executeCommand(acb, null, "ssh",
											"-i", key, "-o",
											"StrictHostKeyChecking=false",
											"-o",
											"UserKnownHostsFile=/dev/null",
											"root@" + ip, cm);
								} else {
									CFUtil.executeCommand(acb, null, "ssh",
											"-i", key, "-o",
											"StrictHostKeyChecking=false",
											"-o",
											"UserKnownHostsFile=/dev/null",
											"root@" + ip, "chef-client");
								}
								return null;
							}
						}, map);
			}
		};
		ExecutorHelper.execute(r);
		return "STARTED";
	}
}
