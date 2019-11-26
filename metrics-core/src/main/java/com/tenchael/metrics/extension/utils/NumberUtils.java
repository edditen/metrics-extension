package com.tenchael.metrics.extension.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Number utils
 * Created by tengzhizhang on 2019/11/26.
 */
public class NumberUtils {

	/**
	 * 保留小数点后几位
	 *
	 * @param d
	 * @param precise
	 * @return
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
