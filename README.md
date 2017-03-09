# sitemap
Simple `sitemap.xml` generator and reader

## Setup
```xml
<dependency>      
     <groupId>com.zandero</groupId>      
     <artifactId>sitemap</artifactId>      
     <version>1.0</version>      
</dependency>
```

## Generator

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

## Reader
```java
SitemapReader reader = new SitemapReader();
List<WebPage> pages = reader.read(sitemap);
```