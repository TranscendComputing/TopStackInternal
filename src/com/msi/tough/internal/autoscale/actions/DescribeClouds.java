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
            JSONObject cloud = new JSONObject();
            result.put(key, new JSONArray(cloudMap.get(key)));
        }
        return result.toString();
    }

}
