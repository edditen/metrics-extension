package com.tenchael.metrics.extension.utils;

/**
 * Name utilities
 * Created by Tenchael on 2019/11/26.
 */
public class NameUtils {

	private NameUtils() throws IllegalAccessException {
		throw new IllegalAccessException("Illegal access!");
	}


	public static String name(String prefix, char split, String... names) {
		final StringBuilder builder = new StringBuilder();
		append(builder, split, prefix);
		if (names != null) {
			for (String s : names) {
				append(builder, split, s);
			}
		}
		return builder.toString();
	}

	private static void append(StringBuilder builder, char split, String part) {
		if (part != null && !part.isEmpty()) {
			if (builder.length() > 0) {
				builder.append(split);
			}
			builder.append(part);
		}
	}


}
