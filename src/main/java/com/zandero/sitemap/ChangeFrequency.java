package com.zandero.sitemap;

import com.zandero.utils.StringUtils;

/**
 *
 */
public enum ChangeFrequency {

	always,
	hourly,
	daily,
	weekly,
	monthly,
	yearly,
	never;

	public static ChangeFrequency parse(String value) {

		if (StringUtils.isNullOrEmptyTrimmed(value)) {
			return null;
		}

		try {
			return ChangeFrequency.valueOf(value.toLowerCase());
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}
}
