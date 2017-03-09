package com.zandero.sitemap;

import com.zandero.utils.ResourceUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class SitemapReaderTest {

	@Test
	public void read() throws Exception {

		String sitemap = ResourceUtils.getResourceAsString("/sitemap.xml", this.getClass());
		SitemapReader reader = new SitemapReader();

		List<WebPage> pages = reader.read(sitemap);
		assertEquals(227, pages.size());

		WebPage page = pages.get(0);
		assertEquals("http://www.devscore.co/login", page.getUrl());
		assertEquals((Double) 1D, page.getPriority());
		assertEquals(ChangeFrequency.monthly, page.getChangeFrequency());
		assertNull(page.getLastModified());

		page = pages.get(5);
		assertEquals("http://www.devscore.co/public/github/resteasy/Resteasy", page.getUrl());
		assertEquals((Double) 0.8D, page.getPriority());
		assertEquals(ChangeFrequency.weekly, page.getChangeFrequency());
		assertEquals((Long) 1476215802000L, page.getLastModified()); //Tue, 11 Oct 2016 19:56:42
	}

	@Test
	public void getLastModfifiedTime() {

		long time = SitemapReader.getTimestamp("2016-10-11T19:56:42.000Z");
		assertEquals(1476215802000L, time); //Tue, 11 Oct 2016 19:56:42
	}
}