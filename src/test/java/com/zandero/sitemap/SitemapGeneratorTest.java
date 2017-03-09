package com.zandero.sitemap;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SitemapGeneratorTest {

	@Test
	public void siteMapTest() {

		SitemapGenerator generator = new SitemapGenerator("http://some.domain.com");

		WebPage page = new WebPage("/this/site").change(ChangeFrequency.daily).modified(1476796504000L).priority(0.6D);
		generator.add(page);

		List<String> map = generator.generate();
		assertEquals(4, map.size());

		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", map.get(0).trim());
		assertEquals("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">", map.get(1).trim());
		assertEquals("<url>\n" +
			"   <loc>http://some.domain.com/this/site</loc>\n" +
			"   <lastmod>2016-10-18T15:15:04.000Z</lastmod>\n" +
			"   <changefreq>daily</changefreq>\n" +
			"   <priority>0.6</priority>\n" +
			"</url>\n", map.get(2));
		assertEquals("</urlset>", map.get(3));
	}

	@Test
	public void testGetUrl() {

		WebPage page = new WebPage("/this/site in space").change(ChangeFrequency.daily).modified(1476796504000L).priority(0.6D);
		assertEquals("http://www.sitemaps.org/this/site%20in%20space", page.getUrl("http://www.sitemaps.org"));

		page = new WebPage("/this/site in space").change(ChangeFrequency.daily).modified(1476796504000L).priority(0.6D);
		assertEquals("http://www.sitemaps.org/this/site%20in%20space", page.getUrl("http://www.sitemaps.org"));

		page = new WebPage("/this/site in space?is=some&other=stuff").change(ChangeFrequency.daily).modified(1476796504000L).priority(0.6D);
		assertEquals("http://www.sitemaps.org/this/site%20in%20space?is=some&amp;other=stuff", page.getUrl("http://www.sitemaps.org"));
	}
}