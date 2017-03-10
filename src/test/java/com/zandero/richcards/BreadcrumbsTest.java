package com.zandero.richcards;

import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 *
 */
public class BreadcrumbsTest {

	@Test
	public void emptyTest() {

		assertEquals("", Breadcrumbs.get("http://some.domain.com",null));
		assertEquals("", Breadcrumbs.get("http://some.domain.com", new LinkedHashMap<>()));

		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("/link/one", "One");
		map.put("/link/one/two", "Two");

		String output = Breadcrumbs.get("http://some.domain.com", map);
		assertEquals("{\"@context\": \"http://schema.org\",\n" +
			"\"@type\": \"BreadcrumbList\",\n" +
			"\"itemListElement\": [\n" +
			"  {\"@type\": \"ListItem\",\n" +
			"  \"position\": 1,\n" +
			"  \"item\": {\n" +
			"    \"@id\": \"http://some.domain.com/link/one\",\n" +
			"    \"name\": \"One\"}\n" +
			"  },\n" +
			"  {\"@type\": \"ListItem\",\n" +
			"  \"position\": 2,\n" +
			"  \"item\": {\n" +
			"    \"@id\": \"http://some.domain.com/link/one/two\",\n" +
			"    \"name\": \"Two\"}\n" +
			"  }]}", output);
	}
}