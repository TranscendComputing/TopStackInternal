package com.msi.tough.internal.autoscale.hedwig;

/*
import org.apache.hedwig.client.netty.HedwigClient;
import org.apache.hedwig.client.netty.HedwigPublisher;
import org.apache.hedwig.exceptions.PubSubException.CouldNotConnectException;
import org.apache.hedwig.exceptions.PubSubException.ServiceDownException;
import org.apache.hedwig.protocol.PubSubProtocol.Message;

import com.google.protobuf.ByteString;
import com.msi.tough.monitor.common.Constants;
import com.msi.tough.monitor.common.hedwig.*;*/

public class ASHedwigClient {
	/*HedwigConf hconf;
	HedwigClient hclient;
	HedwigPublisher pub;*/
	
	public ASHedwigClient(){
		/*hconf = new HedwigConf();
		hclient = new HedwigClient(hconf);
		pub = hclient.getPublisher();*/
	}
	
	public void publish(String groupName, String metricName, String value, String timeInMillis){
		/*String msg = groupName+" "+metricName+" "+value+" "+timeInMillis;
		try {
			pub.publish(ByteString.copyFromUtf8(Constants.AS_TOPIC), Message.newBuilder().setBody(
			ByteString.copyFromUtf8(msg)).build());
		} catch (CouldNotConnectException e) {
			e.printStackTrace();
		} catch (ServiceDownException e) {
			e.printStackTrace();
		}*/
	}
	
	public void stop(){
		/*hclient.stop();*/
	}
}
