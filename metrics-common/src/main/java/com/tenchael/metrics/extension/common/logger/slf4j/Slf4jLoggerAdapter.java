package com.tenchael.metrics.extension.common.logger.slf4j;

import com.tenchael.metrics.extension.common.logger.Level;
import com.tenchael.metrics.extension.common.logger.Logger;
import com.tenchael.metrics.extension.common.logger.LoggerAdapter;

import java.io.File;

public class Slf4jLoggerAdapter implements LoggerAdapter {

	private Level level;
	private File file;

	public Logger getLogger(String key) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
	}

	public Logger getLogger(Class<?> key) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
