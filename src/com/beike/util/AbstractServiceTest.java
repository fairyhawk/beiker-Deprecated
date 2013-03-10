package com.beike.util;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * <p>Title: 抽象的测试类，所有需要测试的service必须继承此类</p> 
 * <p>Description: 
 * <p>Copyright: </p> 
 * <p>Company: qianpin.com</p>
 * @author xxxx
 * @version 1.0
 */
public abstract class AbstractServiceTest extends TestCase {

	protected Log log = LogFactory.getLog(getClass());

	protected ServiceLocator locator;
	protected ApplicationContext context;

	abstract protected String getConfigName();

	protected AutowireCapableBeanFactory findAutoWiringBeanFactory(ApplicationContext context) {
        if (context instanceof AutowireCapableBeanFactory) {
            // Check the context
            return (AutowireCapableBeanFactory) context;
        } else if (context instanceof ConfigurableApplicationContext) {
            // Try and grab the beanFactory
            return ((ConfigurableApplicationContext) context).getBeanFactory();
        } else if (context.getParent() != null) {
            // And if all else fails, try again with the parent context
            return findAutoWiringBeanFactory(context.getParent());
        }
        return null;
    }
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServiceLocator.BEAN_REFERENCE_LOCATION = getConfigName();
		locator=ServiceLocator.instance();
		context=locator.getContext();
		AutowireCapableBeanFactory factory = findAutoWiringBeanFactory(context);
		factory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		locator.shutdown();
	}
}
