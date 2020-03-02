package com.tenchael.metrics.extension.common.logger.support;

import com.tenchael.metrics.extension.common.PropertiesManager;
import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.utils.NetUtils;

import static com.tenchael.metrics.extension.common.Constants.MODULE_NAME;
import static com.tenchael.metrics.extension.common.Constants.PropsKey;


public class FailsafeLogger implements Logger {


	private final String appTag;


	private Logger logger;

	public FailsafeLogger(Logger logger) {
		this.logger = logger;
		this.appTag = PropertiesManager.getInstance()
				.getStringOrDefault(PropsKey.LOGGER_APP, "java-service");
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	private String appendContextMessage(String msg) {
		return String.format("[%s] [%s] [%s] %s",
				MODULE_NAME, appTag, NetUtils.getLocalHost(), msg);
	}

	public void trace(String msg, Throwable e) {
		try {
			logger.trace(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}

	public void trace(Throwable e) {
		try {
			logger.trace(e);
		} catch (Throwable t) {
		}
	}

	public void trace(String msg) {
		try {
			logger.trace(appendContextMessage(msg));
		} catch (Throwable t) {
		}
	}

	public void debug(String msg, Throwable e) {
		try {
			logger.debug(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}

	public void debug(Throwable e) {
		try {
			logger.debug(e);
		} catch (Throwable t) {
		}
	}

	public void debug(String msg) {
		try {
			logger.debug(appendContextMessage(msg));
		} catch (Throwable t) {
		}
	}

	public void info(String msg, Throwable e) {
		try {
			logger.info(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}

	public void info(String msg) {
		try {
			logger.info(appendContextMessage(msg));
		} catch (Throwable t) {
		}
	}

	public void warn(String msg, Throwable e) {
		try {
			logger.warn(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}

	public void warn(String msg) {
		try {
			logger.warn(appendContextMessage(msg));
		} catch (Throwable t) {
		}
	}

	public void error(String msg, Throwable e) {
		try {
			logger.error(appendContextMessage(msg), e);
		} catch (Throwable t) {
		}
	}

	public void error(String msg) {
		try {
			logger.error(appendContextMessage(msg));
		} catch (Throwable t) {
		}
	}

	public void error(Throwable e) {
		try {
			logger.error(e);
		} catch (Throwable t) {
		}
	}

	public void info(Throwable e) {
		try {
			logger.info(e);
		} catch (Throwable t) {
		}
	}

	public void warn(Throwable e) {
		try {
			logger.warn(e);
		} catch (Throwable t) {
		}
	}

	public boolean isTraceEnabled() {
		try {
			return logger.isTraceEnabled();
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean isDebugEnabled() {
		try {
			return logger.isDebugEnabled();
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean isInfoEnabled() {
		try {
			return logger.isInfoEnabled();
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean isWarnEnabled() {
		try {
			return logger.isWarnEnabled();
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean isErrorEnabled() {
		try {
			return logger.isErrorEnabled();
		} catch (Throwable t) {
			return false;
		}
	}

}