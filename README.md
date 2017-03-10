# sitemap
Simple `sitemap.xml` generator and reader
Currently supports only

## Setup
```xml
<dependency>      
     <groupId>com.zandero</groupId>      
     <artifactId>sitemap</artifactId>      
     <version>1.0</version>      
</dependency>
```

## Sitemap generator

```java
SitemapGenerator generator = new SitemapGenerator("http://some.domain.com");

// build a page link
WebPage page = new WebPage("/this/site")
   .change(ChangeFrequency.daily)
   .modified(1476796504000L)
   .priority(0.6D);

// add new page to list  
generator.add(page);  

// generate list  
List<String> map = generator.generate();
```

### Output
```xml
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
		<url>
			<loc>http://some.domain.com/this/site</loc>
			<lastmod>2016-10-18T15:15:04.000Z</lastmod>
			<changefreq>daily</changefreq>
			<priority>0.6</priority>
		</url>
</urlset>
```

## Sitemap reader

Reads in a given XML file or String and de-serializes it to list of WebPages

```java
SitemapReader reader = new SitemapReader();
List<WebPage> pages = reader.read(sitemap);
```