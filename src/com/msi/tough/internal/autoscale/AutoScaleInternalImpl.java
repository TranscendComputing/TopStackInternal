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
/**
 * 
 */
package com.msi.tough.internal.autoscale;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.Action;

/**
 * @author tdhite
 */
public class AutoScaleInternalImpl
{
    private final static Logger logger = Appctx.getLogger(AutoScaleInternalImpl.class
        .getName());

    private final Map<String, Action> actionMap;

    public AutoScaleInternalImpl(final Map<String, Action> actionMap)
    {
        this.actionMap = actionMap;
    }

    public void process(final HttpServletRequest req,
        final HttpServletResponse resp) throws Exception
    {
        final Action a = this.actionMap.get(req.getParameter("Action"));
        if (a == null)
        {
            logger.debug("No action exists for " + req.getQueryString());
            logger.debug("Those that exist are:");
            for (Entry<String, Action> item : this.actionMap.entrySet())
            {
                logger.error("\"" + item.getKey() + "\"");
            }
        }
        else
        {
            logger.debug("calling action " + a);
            a.process(req, resp);
        }
    }
}
