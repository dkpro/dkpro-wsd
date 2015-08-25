---
layout: page-fullwidth
title: "Documentation"
permalink: "/documentation/"
---

{% assign stable = (site.data.releases | where:"status", "stable" | first) %}
{% assign unstable = (site.data.releases | where:"status", "unstable" | first) %}

Full reference documentation for DKPro WSD is under construction.  In the meantime, the following resources will help you get up and running with DKPro WSD:

* [Getting started with DKPro WSD](/dkpro-wsd/gettingstarted/)
* [Frequently Asked Questions about DKPro WSD](/dkpro-wsd/faq/)

<!--

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

-->