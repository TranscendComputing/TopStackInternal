package com.msi.tough.internal.autoscale.basic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.msi.tough.core.Appctx;
import com.msi.tough.internal.autoscale.AbstractBaseAutoscaleTest;

public class ConfigTest extends AbstractBaseAutoscaleTest
implements ApplicationContextAware{

    @Autowired
    public String targetServer;

	@Test
	public void testSuccessfulConfigure() {
	    // Executing this method means no stacktrace on config load.
        assertEquals("getBean shouldn't do anything stupid.", Appctx.instance(),
                Appctx.getBean("appctx"));
	}

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext appctx)
            throws BeansException {
        Appctx.instance().setAppctx(appctx);
    }

}
