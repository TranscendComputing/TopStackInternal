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
import com.msi.tough.core.StringHelper;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.KeyPairUtils;

public class DeleteAccount extends UnsecuredAction {

    @SuppressWarnings("unchecked")
    public String innerProcess(final Session s, final HttpServletRequest req,
            final HttpServletResponse resp, final Map<String, String[]> map)
            throws Exception {
        final String idString = QueryUtil.requiredString(map, "Id");
        final long id = Long.valueOf(idString).longValue();
        final String accessKey = QueryUtil.requiredString(map, "AccessKey");
        final String secretKey = QueryUtil.requiredString(map, "SecretKey");
        if (accessKey == null || secretKey == null) {
            return "Insufficient Data";
        }
        final Query q = s.createQuery("from AccountBean where id='"
                + id + "'");
        final List<AccountBean> l = q.list();
        if (l == null || l.size() != 1) {
            return "Account Not Found";
        }
        final AccountBean ac = l.get(0);
        final String keyName = ac.getDefKeyName();
        s.delete(ac);
        if (keyName.startsWith("__key_")) {
            final TemplateContext ctx = new TemplateContext(null);
            KeyPairUtils.deleteKeyPair(AccountUtil.toAccount(ac), ctx, null,
                    "__" + StringHelper.randomStringFromTime(),
                    ac.getDefZone(), keyName);
        }
        return JsonUtil.accBeanToJsonObject(ac);
    }

    @Override
    public String process0(final Session s, final HttpServletRequest req,
            final HttpServletResponse resp, final Map<String, String[]> map)
                    throws Exception {
        try {
            return innerProcess(s, req, resp, map);
        } catch (ErrorResponse e) {
            if (ErrorResponse.CODE_MISSING_PARAMETER.equals(e.getCode())) {
                return "Insufficient Data";
            }
            if (ErrorResponse.CODE_RESOURCE_NOT_FOUND.equals(e.getCode())) {
                return "Account Not Found";
            }
            return "FAIL:" + e.getMessage();
        }
	}
}
