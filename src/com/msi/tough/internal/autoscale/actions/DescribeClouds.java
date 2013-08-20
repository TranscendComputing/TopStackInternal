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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.UnsecuredAction;

public class DescribeClouds extends UnsecuredAction{

    @SuppressWarnings("unchecked")
    @Override
    public String process0(Session session, HttpServletRequest req,
            HttpServletResponse resp, Map<String, String[]> map)
            throws Exception {

        final Map<String, Object> cfg = Appctx.getConfiguration();
        final Map<String, ArrayList<String> > cloudMap = new HashMap<String, ArrayList<String> >();
        for(String key: cfg.keySet()){
            //If map entry contains CloudType, then it is a config for an availability zone.
            Object entry = cfg.get(key);
            if(entry instanceof LinkedHashMap && ((HashMap<String,Object>)entry).containsKey("CloudType")){
                String cloudType = ((HashMap<String, String>)entry).get("CloudType");
                if(!cloudMap.containsKey(cloudType)){
                    cloudMap.put(cloudType, new ArrayList<String>());
                }
                cloudMap.get(cloudType).add(key);
            }
        }
        JSONObject result = new JSONObject();
        for(String key: cloudMap.keySet()){
            result.put(key, new JSONArray(cloudMap.get(key)));
        }
        return result.toString();
    }

}
