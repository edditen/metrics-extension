package com.tenchael.metrics.extension.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Number utils
 * Created by Tenchael on 2019/11/26.
 */
public class NumberUtils {

	private NumberUtils() throws IllegalAccessException {
		throw new IllegalAccessException("Illegal access!");
	}

	/**
	 * 保留小数点后几位
	 *
	 * @param d       小数
	 * @param precise 精度
	 * @return 精度后的小数
	 */
	public static double decimalPrecise(double d, int precise) {
		if (Double.isNaN(d) || Double.isInfinite(d)) {
			return 0.0d;
		}
		return BigDecimal.valueOf(d)
				.setScale(precise, RoundingMode.HALF_UP)
				.doubleValue();
	}

}
