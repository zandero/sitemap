package com.zandero.sitemap;

import com.zandero.utils.Assert;
import com.zandero.utils.UrlUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hold sitemap info for one page
 */
public class WebPage {

	final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //2015-08-17T08:01:15.562+00:00

	private String url;

	private ChangeFrequency frequency;

	private Double priority;

	private Long lastModified;

	public WebPage(String pageUrl) {

		Assert.notNullOrEmptyTrimmed(pageUrl, "Missing page url");
		url = pageUrl;
	}

	public WebPage change(ChangeFrequency value) {

		frequency = value;
		return this;
	}

	public WebPage priority(double value) {

		Assert.isTrue(value >= 0, "Priority can't be lower than 0!");
		Assert.isTrue(value <= 1, "Priority can't be higher than 1!");

		priority = value;
		return this;
	}

	public WebPage modified(long time) {

		lastModified = time;
		return this;
	}

	public String getUrl() {

		return url;
	}

	public Double getPriority() {

		return priority;
	}

	public double getSortPriority() {

		if (priority == null) {
			return 0.5D;
		}

		return priority;
	}

	public ChangeFrequency getChangeFrequency() {

		if (frequency == null) {
			return ChangeFrequency.never;
		}

		return frequency;
	}

	public Long getLastModified() {

		return lastModified;
	}

	public String getLastModifiedAsString() {

		if (lastModified != null) {
			return DATE_FORMAT.format(new Date(lastModified));
		}

		return "";
	}

	public String getUrl(String rootUrl) {

		String pageUrl = escape(url);
		pageUrl = UrlUtils.composeUrl(rootUrl, pageUrl);
		return UrlUtils.urlEncode(pageUrl);
	}

	/**
	 * Replaces         with
	 * Ampersand	&	&amp;
	 * Single Quote	'	&apos;
	 * Double Quote	"	&quot;
	 * Greater Than	>	&gt;
	 * Less Than	<	&lt;
	 * @param url to escape
	 * @return escaped URL
	 */
	private String escape(String url) {

		Assert.notNull(url, "Missing url");
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < url.length(); i++) {
			char ch = url.charAt(i);

			switch (ch) {
				case '&' :
					builder.append("&amp;");
					break;

				case '\'' :
					builder.append("&apos;");
					break;

				case '"' :
					builder.append("&quot;");
					break;

				case '>' :
					builder.append("&gt;");
					break;

				case '<' :
					builder.append("&lt;");
					break;

				case ' ' :
					builder.append("%20");
					break;

				default:
					builder.append(ch);
			}
		}

		return builder.toString();
	}
}
