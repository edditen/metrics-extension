package com.tenchael.metrics.extension.metrics;

import com.tenchael.metrics.extension.utils.NameUtils;

/**
 * name for metrics register
 * Created by tengzhizhang on 2019/11/26.
 */
public interface NameFactory {

	String createName(String domain, String type, String name);

	/**
	 * default name factory with JMX convention that is: specificDomain:type=specificType,name="specificName"
	 */
	class DefaultNameFactory implements NameFactory {


		@Override
		public String createName(String domain, String type, String name) {
			return NameUtils.oname(domain, type, name);
		}
	}
}
