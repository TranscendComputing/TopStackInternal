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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.ComputeServices;
import org.dasein.cloud.compute.VirtualMachineProduct;
import org.dasein.cloud.compute.VirtualMachineSupport;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.MapUtil;
import com.msi.tough.core.QueryBuilder;
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.aws.ec2.Instance;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.ASActivityLog;
import com.msi.tough.model.ASGroupBean;
import com.msi.tough.model.ASScheduledBean;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.ASUtil;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.InstanceUtil;

public class ProcessAutoScaling extends UnsecuredAction implements Constants {
    private final static Logger logger = Appctx
            .getLogger(ProcessAutoScaling.class.getName());

    private void addInstance(final Session session, final ASGroupBean grp,
            final AccountBean ac) throws Exception {
        final Date startTime = new Date();
        String avz = grp.getAvzones();
        if (StringHelper.isBlank(avz)) {
            avz = ac.getDefZone();
        }
        final String slaunch = grp.getLaunchConfig();
        final LaunchConfigBean lcb = ASUtil.readLaunchConfig(session, ac.getId(),
                slaunch);
        if (lcb == null) {
            logger.error("Launch Config Not found " + slaunch + " for ac "
                    + ac.getId());
            return;
        }

        final CallStruct call = new CallStruct();
        call.setName(InstanceUtil.getHostName(avz));
        call.setAc(AccountUtil.toAccount(ac));
        call.setParentId(grp.getName());
        call.setAvailabilityZone(avz);
        call.setCtx(new TemplateContext(null));
        call.setStackId(grp.getStackId());
        // call.setWaitHookClass(lcb
        // .getWaitHookClass());
        final CloudProvider cloudProvider = call.getCloudProvider();
        final ComputeServices compute = cloudProvider.getComputeServices();
        final VirtualMachineSupport vmSupport = compute
                .getVirtualMachineSupport();
        String instanceType = lcb.getInstType();
        if (cloudProvider.getProviderName().equals("OpenStack")) {
            instanceType = toFlavorId(vmSupport, instanceType);
        }
        logger.debug("Setting instanceType = " + instanceType);

        final Map<String, Object> prop = MapUtil.create(AVAILABILITYZONE, avz,
                SECURITYGROUPIDS, lcb.getSecGrps(),
                IMAGEID, lcb.getImageId(),
                INSTANCETYPE, instanceType,
                AUTOSCALEGROUPID, grp.getId()
                );
        if (lcb.getKernel() != null) {
            prop.put(KERNELID, lcb.getKernel());
        }
        if (lcb.getRamdisk() != null) {
            prop.put(RAMDISKID, lcb.getRamdisk());
        }
        if (lcb.getUserData() != null) {
            prop.put(USERDATA, lcb.getUserData());
        }
        if (lcb.getKey() != null) {
            prop.put(KEYNAME, lcb.getKey());
        }
        if (lcb.getChefRoles() != null) {
            prop.put(CHEFROLES, lcb.getChefRoles());
        }
        if (lcb.getDatabag() != null) {
            prop.put(DATABAG, lcb.getDatabag());
        }
        call.setProperties(prop);
        call.setType(Instance.TYPE);
        // launch instance
        final InstanceType res = (InstanceType) CFUtil.createResource(call);
        final ASActivityLog log = new ASActivityLog();
        log.setCause("AutoScaling");
        log.setDescription("Instance added  " + res.getInstanceId());
        log.setDetails("Instance added " + res.getInstanceId());
        log.setEndTime(new Date());
        log.setGrpId(grp.getId());
        log.setProgress(100);
        log.setStartTime(startTime);
        log.setStatusCode("Successful");
        log.setStatusMsg("Successful");
        log.setUserId(ac.getId());
        session.save(log);
    }

    private List<InstanceBean> applyPolicy(final Session s,
            final AccountBean ac, final List<InstanceBean> linst,
            final String policy) {
        final List<InstanceBean> nl = new ArrayList<InstanceBean>();
        if (policy.equals("NewestInstance")) {
            nl.add(linst.get(linst.size() - 1));
        }
        if (policy.equals("OldestInstance")) {
            nl.add(linst.get(0));
        }
        if (policy.equals("ClosestToNextInstanceHour")) {
            final InstanceBean i = linst.get(0);
            nl.add(i);
        }
        if (policy.equals("OldestLaunchConfiguration")) {
            final InstanceBean i = linst.get(0);
            nl.add(i);
        }
        return nl;
    }

    @SuppressWarnings("unchecked")
    private long newCapacity(final Session s, final AccountBean ac,
            final ASGroupBean grp) {
        long capacity = grp.getCapacity();
        if (capacity < grp.getMinSz()) {
            capacity = grp.getMinSz();
        }
        if (capacity > grp.getMaxSz()) {
            capacity = grp.getMaxSz();
        }
        final Query q = s.createQuery("from ASScheduledBean where userId="
                + ac.getId() + " and grpName='" + grp.getName() + "'");
        final List<ASScheduledBean> l = q.list();
        if (l != null) {
            for (final ASScheduledBean b : l) {
                if (b.getStartTime() != null
                        && b.getStartTime().getTime() > System
                                .currentTimeMillis()) {
                    continue;
                }
                if (b.getEndTime() != null
                        && b.getEndTime().getTime() < System
                                .currentTimeMillis()) {
                    continue;
                }
                if (b.getMinSize() != 0 && b.getMinSize() > capacity) {
                    capacity = b.getMinSize();
                }
                if (b.getMaxSize() != 0 && b.getMaxSize() < capacity) {
                    capacity = b.getMaxSize();
                }
            }
        }
        return capacity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String process0(final Session s, final HttpServletRequest req,
            final HttpServletResponse resp, final Map<String, String[]> map)
            throws Exception {
        final Query qry = s.createQuery("from AccountBean");
        final List<AccountBean> list = qry.list();
        for (final AccountBean ac : list) {
            final Query gq = s.createQuery("from ASGroupBean where userId="
                    + ac.getId());
            for (final ASGroupBean grp : (List<ASGroupBean>) gq.list()) {
                QueryBuilder builder = new QueryBuilder("from InstanceBean");
                builder.equals("asGroup", grp);
                builder.equals("health", "Healthy");
                builder.toQuery(s);
                List<InstanceBean> instances = builder.toQuery(s).list();
                final int numinst = instances.size();
                final long capacity = newCapacity(s, ac, grp);
                int addInst = (int) capacity - numinst;
                String instId = null;
                if (grp.getTerminateInstance() != null) {
                    addInst = -1;
                    instId = grp.getTerminateInstance();
                }
                logger.debug("Account " + ac.getId() + " Group " + grp.getId()
                        + "Current instances=" + numinst + " desired capacity="
                        + capacity + " adding=" + addInst);
                if (addInst > 0) {
                    addInstance(s, grp, ac);
                } else if (addInst < 0) {
                    if (instId == null) {
                        instId = removeInstance(s, ac, grp, instances);
                    }
                    if (grp.getReduceCapacity() != null
                            && grp.getReduceCapacity() && grp.getCapacity() > 0) {
                        grp.setCapacity(grp.getCapacity() - 1);
                    }
                    CFUtil.deleteStackResources(AccountUtil.toAccount(ac),
                            grp.getStackId(), grp.getName(), instId);
                    grp.setTerminateInstance(null);
                    grp.setReduceCapacity(null);
                    s.save(grp);
                    ASUtil.reconfigLBInstance(s, grp, true, instId);
                }
            }
        }
        return "DONE";
    }

    private String removeInstance(final Session s, final AccountBean ac,
            final ASGroupBean grp, List<InstanceBean> instances) {
        final Date startTime = new Date();
        final CommaObject policies = new CommaObject(
                grp.getTerminationPolicies());
        if (policies.toList().size() == 0) {
            policies.add("default");
        }
        if (policies.toArray()[0].equals("default")) {
            policies.setList(Arrays.asList(new String[] {
                    "OldestLaunchConfiguration", "ClosestToNextInstanceHour",
                    "OldestInstance" }));
        }
        for (final String policy : policies.toList()) {
            if (instances.size() == 1) {
                break;
            }
            instances = applyPolicy(s, ac, instances, policy);
        }
        final String instId = instances.get(0).getInstanceId();
        final ASActivityLog log = new ASActivityLog();
        log.setCause("AutoScaling");
        log.setDescription("Instance removed " + instId);
        log.setDetails("Instance removed " + instId);
        log.setEndTime(new Date());
        log.setGrpId(grp.getId());
        log.setProgress(100);
        log.setStartTime(startTime);
        log.setStatusCode("Successful");
        log.setStatusMsg("Successful");
        log.setUserId(ac.getId());
        s.save(log);
        return instId;
    }

    private String toFlavorId(VirtualMachineSupport vmSupport, String flavorName)
            throws InternalException, CloudException {
        String flavorId = null;

        Iterable<Architecture> listArchitectures = vmSupport
                .listSupportedArchitectures();
        for (Architecture arch : listArchitectures) {
            Iterable<VirtualMachineProduct> products = vmSupport
                    .listProducts(arch);
            for (VirtualMachineProduct product : products) {
                if (product.getName().equals(flavorName)) {
                    flavorId = product.getProviderProductId();
                    break;
                }
            }

            if (flavorId != null) {
                break;
            }
        }

        return flavorId;
    }
}
