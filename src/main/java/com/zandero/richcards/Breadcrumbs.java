package com.zandero.richcards;

import com.zandero.utils.Assert;
import com.zandero.utils.UrlUtils;

import java.util.LinkedHashMap;

/**
 * Breadcrumb list
 *
 * @link https://developers.google.com/search/docs/data-types/breadcrumbs
 */
public class Breadcrumbs {

	/**
	 * Produces a list element of breadcrumbs
	 *
	 * @param baseUrl base url of given links
	 * @param data    map of relative links / name pairs, sorted by position (first item is in first link)
	 * @return generated breadcrumb list or "" if no data given
	 */
	public static String get(String baseUrl, LinkedHashMap<String, String> data) {

		Assert.isTrue(UrlUtils.isUrl(baseUrl), "Invalid base URL given!");

		if (data == null || data.size() == 0) {
			return "";
		}

		String out = "{\"@context\": \"http://schema.org\"," + System.lineSeparator() +
			"\"@type\": \"BreadcrumbList\"," + System.lineSeparator() +
			"\"itemListElement\": [" + System.lineSeparator();

		int count = 1;
		for (String key : data.keySet()) {

			out = out +
				"  {\"@type\": \"ListItem\"," + System.lineSeparator() +
				"  \"position\": " + count + "," + System.lineSeparator() +
				"  \"item\": {" + System.lineSeparator() +
				"    \"@id\": \"" + UrlUtils.composeUrl(baseUrl, key) + "\"," + System.lineSeparator() +
				"    \"name\": \"" + data.get(key) + "\"}" + System.lineSeparator() +
				"  }";

			if (count < data.size()) {
				out = out + "," + System.lineSeparator();
			}

			count++;
		}

		out = out + "]}";
		return out;
	}
}
