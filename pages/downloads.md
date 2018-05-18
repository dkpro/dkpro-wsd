---
layout: page-fullwidth
title: "Downloads"
permalink: "/downloads/"
---

{% assign stable = site.data.releases | where: "status", "stable" | first %}

## Maven

{{ site.title }} is available via the Maven infrastructure.

{% highlight xml %}
<repositories>
  <repository>
    <id>ukp-oss-releases</id>
    <url>http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-releases</url>
   </repository>
</repositories>

<properties>
  <dkpro.wsd.version>{{ stable.version }}</dkpro.wsd.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>{{ stable.groupId }}<groupId>
      <artifactId>{{ stable.artifactId }}</artifactId>
      <version>${dkpro.wsd.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>{{ stable.groupId }}</groupId>
    <artifactId>de.tudarmstadt.ukp.dkpro.wsd.core</artifactId>
  </dependency>
</dependencies>
{% endhighlight xml %}

<!-- A full list of artifacts is available from [Maven Central][1]!  -->
  
## Sources

Get the sources from [GitHub](https://github.com/dkpro/dkpro-wsd/releases/tag/dkpro-wsd-{{ stable.version }}).

[1]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22{{ stable.groupId }}%22%20AND%20v%3A%22{{ stable.version }}%22


