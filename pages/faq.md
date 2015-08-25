---
layout: page-fullwidth
title: "Frequently Asked Questions about DKPro WSD"
permalink: "/faq/"
---

## About DKPro WSD

### What is DKPro WSD?

DKPro WSD is a general-purpose framework for word sense disambiguation (WSD) which is both modular and extensible.  DKPro WSD was designed primarily to support the needs of WSD researchers, who will appreciate the convenience and flexibility it affords in tuning and comparing algorithms and data sets.  However, as a general-purpose toolkit it could also be used to implement a WSD module for a real-world natural language processing application.  Its support for interactive visualization of the disambiguation process also makes it a powerful tool for learning or teaching the principles of WSD.

By _modular_ we mean that the framework makes a logical separation between the _data sets_ (e.g., the corpora to be annotated, the answer keys, manually annotated training examples, etc.), the _sense inventories_ (i.e., the lexical-semantic resources enumerating the senses to which words in the corpora are assigned), and the _algorithms_ (i.e., code which actually performs the sense assignments and prerequisite linguistic annotations), and provides a standard interface for each of these component types.  Components which provide the same functionality can be freely swapped, so that one can easily run the same algorithm on different data sets (irrespective of which sense inventory they use), or test several different algorithms on the same data set.

By _extensible_ we mean that it is easy to adapt to work with new methods and resources.  The system is written in Java and is based on [Apache UIMA](https://uima.apache.org/), an industry-standard architecture for analysis of unstructured information.  Support for new corpus formats, sense inventories, and WSD algorithms can be added by implementing new UIMA components for them, or more conveniently by writing UIMA wrappers around existing code.

### What is DKPro?

[DKPro](https://dkpro.github.io/) is a family of projects for natural language processing.  DKPro WSD is one of these projects; it provides a unified framework and set of tools geared specifically towards word sense disambiguation.  Many of the modules in DKPro WSD make use of or extend components from DKPro Core, UBY, and other DKPro projects.

### What licence is DKPro WSD released under?

DKPro WSD consists of a number of different modules.  Some modules are released under the terms of the [Apache Licence 2.0](http://www.apache.org/licenses/LICENSE-2.0), and some are licensed under the terms of the [GNU General Public License, Version 3](https://www.gnu.org/licenses/gpl.html).  The licence for each module can be found in the `LICENSE.txt` file of its source tree.

### How do I get DKPro WSD?

Our GitHub repository contains [source code for DKPro WSD](http://github.com/dkpro/dkpro-wsd).

If you are a Maven user, you can obtain [prepackaged Maven artifacts](https://dkpro.github.io/dkpro-wsd/downloads/).

### How can I cite DKPro WSD in a paper I'm writing?

To reference DKPro WSD, please cite the following paper:

> Tristan Miller, Nicolai Erbs, Hans-Peter Zorn, Torsten Zesch, and Iryna Gurevych. [DKPro WSD: A generalized UIMA-based framework for word sense disambiguation](http://aclweb.org/anthology//P/P13/P13-4007.pdf). In _Proceedings of the 51st Annual Meeting of the Association for Computational Linguistics (System Demonstrations) (ACL 2013)_, pages 37–42, August 2013.

You can use the following BibTeX entry:

<pre>
@InProceedings{miller2013dkpro,
  author =    {Tristan Miller and Nicolai Erbs and Hans-Peter Zorn and Torsten Zesch and Iryna Gurevych},
  title =     {\{DKPro} {WSD}: A Generalized {UIMA}-based Framework for Word Sense Disambiguation},
  booktitle = {Proceedings of the 51st Annual Meeting of the Association for Computational Linguistics (System Demonstrations) (ACL 2013)},
  year =      2013,
  month =     aug,
  pages =     {37--42},
  pdf =       {http://aclweb.org/anthology//P/P13/P13-4007.pdf}
}
</pre>

## Using DKPro WSD

### What corpus readers should I use for the various Senseval, SemEval, and other WSD data sets?

Refer to the two tables on our [WSD corpora](/dkpro-wsd/corpora/) page.  The first table indicates the file format used by each data set.  The second table lists the formats DKPro WSD can read, along with the relevant reader classes.

## What sense inventories should I use for the various Senseval, SemEval, and other WSD data sets?

First, refer to our table of [WSD corpora](/dkpro-wsd/corpora/) to find out which sense inventory is used by the data set you are interested in.  DKPro WSD supports many (but not all) of the sense inventories in this table.  Consult the table below to see if DKPro has an interface to your data set's sense inventory:

| **Sense inventory** | **Classes supporting this inventory** |
|:--------------------|:--------------------------------------|
| EuroWordNet         | `de.tudarmstadt.ukp.dkpro.wsd.si.wordnet` |
| FrameNet            | `de.tudarmstadt.ukp.dkpro.wsd.si.uby` |
| GermaNet            | `de.tudarmstadt.ukp.dkpro.wsd.si.uby` |
| Google              | `de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.GoogleDictionaryInventory` |
| OmegaWiki           | `de.tudarmstadt.ukp.dkpro.wsd.si.uby` |
| TWSI                | `de.tudarmstadt.ukp.dkpro.wsd.si.twsi.TwsiSenseInventory` |
| UKB                 | `de.tudarmstadt.ukp.dkpro.wsd.si.dictionary.UkbDictionaryInventory` |
| VerbNet             | `de.tudarmstadt.ukp.dkpro.wsd.si.uby` |
| Wikipedia           | `de.tudarmstadt.ukp.dkpro.wsd.si.lsr`, `de.tudarmstadt.ukp.dkpro.wsd.si.uby` |
| Wiktionary          | `de.tudarmstadt.ukp.dkpro.wsd.si.lsr`, `de.tudarmstadt.ukp.dkpro.wsd.si.uby` |
| WordNet             | `de.tudarmstadt.ukp.dkpro.wsd.si.wordnet`, `de.tudarmstadt.ukp.dkpro.wsd.si.lsr`, `de.tudarmstadt.ukp.dkpro.wsd.si.uby` |

Note that for some sense inventories, you can use various modules, depending on the underlying library you prefer to use. Please refer to our [list of LSRs](/dkpro-wsd/lsr/) for further details.

### Why am I getting a lot of errors or warnings when reading in the Senseval and SemEval data sets?

Many of the Senseval and SemEval data sets, as originally distributed by the task organizers, contain errors such as incorrect POS tags, references to nonexistent satellite IDs, and references to nonexistent WordNet sense keys.  You can instruct some collection readers and annotators to ignore certain types of errors (e.g., with the `PARAM_IGNORE_MISSING_SATELLITES` configuration parameter), though of course this may lead to reduced accuracy or unpredictable effects with your disambiguation algorithm.  Alternatively you can use the various [patches and conversion scripts](https://github.com/dkpro/dkpro-wsd/tree/master/de.tudarmstadt.ukp.dkpro.wsd.senseval/src/main/resources) we provide to fix the data sets.

Some third-party redistributions of the Senseval data sets also contain errors.  Most notably, [Rada Mihalcea's "SemCor XML" conversions of the Senseval-2 and -3 data sets](https://web.eecs.umich.edu/~mihalcea/downloads.html#sensevalsemcor) are not actually valid XML.  To use these data sets with DKPro WSD (or almost any other tool that reads XML) you must first convert them to XML using [the scripts we provide](https://github.com/dkpro/dkpro-wsd/tree/master/de.tudarmstadt.ukp.dkpro.wsd.senseval/src/main/resources).  [Ted Pedersen's conversions](http://www.d.umn.edu/~tpederse/data.html) also do not validate as conforming XML, though we haven't yet produced a conversion script.

### Why am I getting a lot of errors when reading in the SemCor corpus?

Most likely you are trying to use [the SemCor files distributed by Rada Mihalcea](https://web.eecs.umich.edu/~mihalcea/downloads.html#semcor), which are not actually valid XML.  To use these data sets with DKPro WSD (or almost any other tool that reads XML) you must first convert them to XML.  The NLTK project has already produced [an XML version of SemCor 3.0](http://nltk.github.com/nltk_data/packages/corpora/semcor.zip), so if you want a WordNet 3.0–annotated version of SemCor, you can just download that one.

### Where can I get the WordNet 1.7 pre-release used by the English Senseval-2 tasks?

You probably can't.  This version of WordNet was never officially published, but was made available only to the participants.  We contacted both the WordNet maintainers and the Senseval-2 organizers, and neither was able to locate a copy of this version.  A Senseval-2 participant sent us a copy he had archived, but its sense keys turned out to be incompatible with those of the Senseval-2 data sets.  If you must use this data set, then you could use WordNet 1.7, but keep in mind that you will not be able to achieve full accuracy, since some of the sense keys found in the answer key won't exist in the sense inventory.

### Where can I get a distributional thesaurus for use with the lexically expanded Lesk module?

You can get data sets and precomputed models, and instructions for producing your own, from the [JobimText Project](http://sourceforge.net/p/jobimtext/wiki/Home/).

### How can I get help with DKPro WSD?

For help with DKPro WSD, please use the [dkpro-wsd-users](http://groups.google.com/group/dkpro-wsd-users) mailing list.

### How can I report a bug with DKPro WSD?

Consider first discussing the problem on the [dkpro-wsd-users](http://groups.google.com/group/dkpro-wsd-users) mailing list.  If you're fairly sure it's a bug, you can report the problem on the [DKPro WSD issue tracker](http://github.com/dkpro/dkpro-wsd/issues).  Patches are welcome.