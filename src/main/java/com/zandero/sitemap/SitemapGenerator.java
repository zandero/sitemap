package com.zandero.sitemap;

import com.zandero.http.HttpUtils;
import com.zandero.utils.Assert;
import com.zandero.utils.UrlUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Simple sitemap.xml generator
 */
public class SitemapGenerator {

	private static final Logger log = LoggerFactory.getLogger(SitemapGenerator.class);

	private final static DecimalFormat priorityFormat = new DecimalFormat("0.0");

	private final static String NEW_LINE = System.lineSeparator();

	private static final String DEFAULT_SITEMAP = "sitemap.xml";

	private final String rootUrl;

	private Map<String, WebPage> pages = new HashMap<>();

	public SitemapGenerator(String domain) {

		Assert.notNullOrEmptyTrimmed(domain, "Missing sitemap domain!");
		Assert.isTrue(domain.contains("://"), "Missing url scheme!");

		rootUrl = domain;
	}

	private String startUrlSet() {

		return "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
	}

	private String endUrlSet() {

		return "</urlset>";
	}

	private String getHead() {

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	}

	private String getUrl(WebPage page) {

		StringBuilder sb = new StringBuilder();

		sb.append("<url>").append(NEW_LINE);

		sb.append("   <loc>" + page.getUrl(rootUrl) + "</loc>").append(NEW_LINE);

		if (null != page.getLastModified()) {
			sb.append("   <lastmod>" + page.getLastModifiedAsString() + "</lastmod>") // optional
				.append(NEW_LINE);
		}

		sb.append("   <changefreq>" + page.getChangeFrequency() + "</changefreq>")
			.append(NEW_LINE);

		if (null != page.getPriority()) {
			sb.append("   <priority>" + priorityFormat.format(page.getPriority()) + "</priority>")
				.append(NEW_LINE);
		}

		sb.append("</url>").append(NEW_LINE);
		return sb.toString();
	}

	public void add(WebPage page) {

		pages.put(UrlUtils.composeUrl(rootUrl, page.getUrl()), page);
	}

	public List<String> generate() {

		List<String> out = new ArrayList<>();

		// HEAD
		out.add(getHead() + NEW_LINE);
		out.add(startUrlSet() + NEW_LINE);

		// sort by priority
		pages.values().stream()
			.sorted(Comparator.comparing(WebPage::getSortPriority).reversed())
			.forEach(page -> out.add(getUrl(page)));

		// TAIL
		out.add(endUrlSet());
		return out;
	}

	public void constructAndSaveSiteMap(File file) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		List<String> list = generate();
		for (String item : list) {

			writer.write(item);
		}

		writer.close();
	}

	public void pingGoogle(String siteMapUrl) throws IOException {

		ping("http://www.google.com/webmasters/sitemaps/ping?sitemap=", siteMapUrl);
	}

	public void pingBing(String siteMapUrl) throws IOException {

		ping("http://www.bing.com/ping?sitemap=", siteMapUrl);
	}

	private void ping(String resourceUrl, String sitemapUrl) throws IOException{

		try {
			resourceUrl = resourceUrl + URLEncoder.encode(sitemapUrl, "UTF-8");

			HttpRequestBase request = HttpUtils.get(resourceUrl, null, null);
			HttpResponse response = HttpUtils.execute(request);

			int returnCode = response.getStatusLine().getStatusCode();
			if (returnCode != 200) {
				throw new IOException("Failed to inform: '" + resourceUrl + "', about new sitemap!");
			}
		}
		catch (IOException e) {

			log.error("Failed to ping change of sitemap.xml to: " + resourceUrl, e);
			throw e;
		}
	}

	public void pingGoogle() throws IOException {

		String url = UrlUtils.composeUrl(rootUrl, DEFAULT_SITEMAP);
		pingGoogle(url);
	}

	public void pingBing() throws IOException {

		String url = UrlUtils.composeUrl(rootUrl, DEFAULT_SITEMAP);
		pingBing(url);
	}
}
