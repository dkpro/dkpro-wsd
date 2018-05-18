---
layout: page-fullwidth
title: "Getting started"
permalink: "/gettingstarted/"
---

{% assign stable = site.data.releases | where: "status", "stable" | first %}
{% assign unstable = site.data.releases | where: "status", "unstable" | first %}

## Getting started with DKPro WSD

### System requirements ###

DKPro WSD is not a standalone WSD system, but rather a development framework and library consisting of a type system, a Java API, and Apache UIMA components.  To use it you will need a Java development framework and runtime environment supporting Java 7 or later.

### Obtaining DKPro WSD ###

The GitHub repository contains [https://github.com/dkpro/dkpro-wsd](source code for DKPro WSD).

If you are a Maven user, you can obtain prepackaged artifacts by adding the following repository section to your project:

```xml
<repositories>
  <repository>
    <id>ukp-oss-releases</id>
    <url>http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-releases</url>
   </repository>
</repositories>
```

You can then add individual DKPro WSD artifacts to your POM.  For example:
```xml
<dependency>
    <groupId>de.tudarmstadt.ukp.dkpro.wsd</groupId>
    <artifactId>de.tudarmstadt.ukp.dkpro.wsd.core</artifactId>
    <version>1.2.0</version>
</dependency>
```

(Of course, you will probably always want to use the latest available version.)


### Obtaining data and resources

Data and resources such as sense inventories and sense-annotated corpora are not included with DKPro WSD.  These can be many gigabytes in size, and are usually not freely redistributable by third parties such as us, so you need to obtain them yourself.                                                                                    
                                                                                                                
Our [table of WSD corpora](/dkpro-wsd/corpora/) includes links to where you can download the most commonly used WSD data sets, including those from the Senseval and SemEval tasks.  Be aware that these data sets often have syntax errors and other problems which make them impossible to use as-is.  We provide [patches and conversion scripts](https://github.com/dkpro/dkpro-wsd/tree/master/de.tudarmstadt.ukp.dkpro.wsd.senseval/src/main/resources) to fix many of these problems.                                                                                 
                                                                                                                
Our [table of lexical semantic resources](/dkpro-wsd/lsr/) includes links to where you can download the most commonly used sense inventories.

### Example code

You can get a good feel for DKPro WSD's capabilities by examining and running the example code.  The `de.tudarmstadt.ukp.dkpro.wsd.examples` module contains several short example programs.  Read through the example documentation, make the indicated changes to the variables so that they point to the required resources on your file system, and then run them.
