package com.tenchael.jmx.ext;

/**
 * JmxConfigMXBean use for setting jmx on or off
 * Created by Tenchael on 2020/2/20.
 */
public interface JmxConfigMXBean extends MBean {

	boolean isEnabled();

	void setEnabled(boolean enabled);

}
