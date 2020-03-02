package com.tenchael.metrics.extension.metrics;

/**
 * name for metrics register
 * Created by Tenchael on 2019/11/26.
 */
public interface NameFactory {

	String createName(MetricKey key);

	/**
	 * default name "type:category:name"
	 */
	class DefaultNameFactory implements NameFactory {


		@Override
		public String createName(MetricKey key) {
			return String.format("%s", key);
		}
	}
}
