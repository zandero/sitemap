package com.zandero.sitemap;

import com.zandero.utils.Assert;
import com.zandero.utils.DateTimeUtils;
import com.zandero.utils.ResourceUtils;
import com.zandero.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads sitemap from string
 */
public class SitemapReader {

	private static final Logger log = LoggerFactory.getLogger(SitemapReader.class);

	private static final SimpleDateFormat[] FORMATS = new SimpleDateFormat[]{
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
	};

	public List<WebPage> read(File sitemapFile) {

		Assert.notNull(sitemapFile, "Missing sitemap file!");
		Assert.isTrue(sitemapFile.exists(), "Sitemap file does not exist: " + sitemapFile.getAbsolutePath());

		try {
			String map = ResourceUtils.readFileToString(sitemapFile);
			return read(map);
		}
		catch (IOException e) {
			log.error("Failed to open: " + sitemapFile.getAbsolutePath());
		}

		return null;
	}

	/**
	 * Reads out given sitemap XML file and returns list of web pages
	 *
	 * @param map content as string
	 * @return list of web pages or null in case of failed readout
	 */
	public List<WebPage> read(String map) {

		Assert.notNullOrEmptyTrimmed(map, "Missing sitemap content!");

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			InputSource source = new InputSource(new StringReader(map));
			Document doc = builder.parse(source);

			return getWebPages(doc);
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("Failed to parse XML file: ", e);
		}

		return null;
	}

	private List<WebPage> getWebPages(Document doc) {

		List<WebPage> output = new ArrayList<>();

		try {

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList result = (NodeList) xpath.evaluate("//urlset/url", doc, XPathConstants.NODESET);

			for (int i = 0; i < result.getLength(); i++) {

				Node node = result.item(i);

				WebPage page = getWebPage(xpath, node);

				if (page != null) {
					output.add(page);
				}
			}

		}
		catch (XPathExpressionException e) {
			log.warn("Failed to parse sitemap: ", e);
		}

		return output;
	}

	private WebPage getWebPage(XPath xpath, Node node) throws XPathExpressionException {

		String url = xpath.evaluate("./loc/text()", node);
		WebPage page = new WebPage(url);

		String freq = xpath.evaluate("./changefreq/text()", node);
		ChangeFrequency frequency = ChangeFrequency.parse(freq);
		page.change(frequency);

		String pri = xpath.evaluate("./priority/text()", node);
		if (!StringUtils.isNullOrEmptyTrimmed(pri)) {
			try {
				double priority = Double.parseDouble(pri);
				page.priority(priority);
			}
			catch (IllegalArgumentException e) {
				// nothing to do ... priority can't be determined
				log.warn("Failed to parse priority from: " + pri);
			}
		}

		String lastMod = xpath.evaluate("./lastmod/text()", node);
		if (!StringUtils.isNullOrEmptyTrimmed(lastMod)) {

			long timeStamp = getTimestamp(lastMod);
			page.modified(timeStamp);
		}

		return page;
	}

	protected static long getTimestamp(String value) {

		if (StringUtils.isNullOrEmptyTrimmed(value)) {
			return 0;
		}

		try {
			return DateTimeUtils.getTimestamp(value, FORMATS);
		}
		catch (IllegalArgumentException e) {
			log.warn("Failed to parse date: '" + value + "' ", e);
			return 0;
		}
	}
}
