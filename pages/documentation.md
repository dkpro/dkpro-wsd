---
layout: page-fullwidth
title: "Documentation"
permalink: "/documentation/"
---

{% assign stable = (site.data.releases | where:"status", "stable" | first) %}
{% assign unstable = (site.data.releases | where:"status", "unstable" | first) %}

## Getting started with DKPro WSD

### System requirements ###

DKPro WSD is not a standalone WSD system, but rather a development framework and library consisting of a type system, a Java API, and Apache UIMA components.  To use it you will need a Java development framework and runtime environment supporting Java 7 or later.

### Obtaining data and resources

Data and resources such as sense inventories and sense-annotated corpora are not included with DKPro WSD.  These can be many gigabytes in size, and are usually not freely redistributable by third parties such as us, so you need to obtain them yourself.                                                                                    
                                                                                                                
Our [table of WSD corpora](/corpora/) includes links to where you can download the most commonly used WSD data sets, including those from the Senseval and SemEval tasks.  Be aware that these data sets often have syntax errors and other problems which make them impossible to use as-is.  We provide patches and conversion scripts to fix many of these problems.                                                                                 
                                                                                                                
Our [table of lexical semantic resources](/lsr/) includes links to where you can download the most commonly used sense inventories.

### Example code

You can get a good feel for DKPro WSD's capabilities by examining and running the example code.  The `de.tudarmstadt.ukp.dkpro.wsd.examples` module contains several short example programs.  Read through the example documentation, make the indicated changes to the variables so that they point to the required resources on your file system, and then run them.

## Reference Documentation

Full reference documentation for DKPro WSD is under construction.

{% unless stable.version == null %}
### {{ site.title }} {{ stable.version }}
_latest release_

{% unless stable.user_guide_url == null %}* [User Guide]({{ stable.user_guide_url }}){% endunless %}
{% unless stable.developer_guide_url == null %}* [Developer Guide]({{ stable.developer_guide_url }}){% endunless %}
{% endunless %}


{% unless unstable.version == null %}
### {{ site.title }} {{ unstable.version }}
_upcoming release - links may be temporarily broken while a build is in progress_

{% unless unstable.user_guide_url == null %}* [User Guide]({{ unstable.user_guide_url }}){% endunless %}
{% unless unstable.developer_guide_url == null %}* [Developer Guide]({{ unstable.developer_guide_url }}){% endunless %}
{% endunless %}
