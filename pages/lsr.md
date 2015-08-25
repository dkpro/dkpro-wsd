---
layout: page-fullwidth
title: "Lexical semantic resources"
permalink: "/lsr/"
---

## Lexical semantic resources

This page lists the lexical semantic resources (sense inventories) most commonly used for word sense disambiguation experiments, and includes links to where they can be downloaded.

### WordNet

Versions 1.5 to 3.1 of WordNet can be obtained from the [WordNet home page](http://wordnet.princeton.edu/).

DKPro WSD can automatically convert between versions of WordNet.  In order to use this feature you need to obtain synset mapping files such as the [WN-Map mappings](http://www.talp.upc.edu/index.php/technology/resources/multilingual-lexicons-and-machine-translation-resources/multilingual-lexicons/98-wordnet-mappings) from the UPC TALP Research Center.

To access WordNet through the `de.tudarmstadt.ukp.dkpro.wsd.si.wordnet` module, you need to prepare an [extJWNL properties file](https://github.com/dkpro/dkpro-wsd/blob/master/de.tudarmstadt.ukp.dkpro.wsd.examples-gpl/src/main/resources/extjwnl_properties.xml) which points to your WordNet installation.

You can also access WordNet through the `de.tudarmstadt.ukp.dkpro.wsd.si.lsr` module (in which case you need to prepare a DKPro LSR `resources.xml` file which points to your WordNet installation) or the `de.tudarmstadt.ukp.dkpro.wsd.si.uby` module (in which case you need to produce or [download a UBY database](http://uby.ukp.informatik.tu-darmstadt.de/uby/) containing WordNet).

### EuroWordNet

EuroWordNet (EWN) sense inventories are generally not freely available; they must be purchased from the individual publishers. The `de.tudarmstadt.ukp.dkpro.wsd.si.wordnet` module can read EWN resources, provided they are in Princeton WordNet format.  You can use [JMWNL](https://code.google.com/p/jmwnl/) to convert EWN sense inventories to Princeton WordNet format.

### WordNet++

[WordNet++](http://lcl.uniroma1.it/wordnetplusplus/) is no longer available for download from its authors; it has been superseded by the BabelNet project.  If you happen to have a copy of WordNet++ already, the `de.tudarmstadt.ukp.dkpro.wsd.si.wordnet` module can read it.

### Turk Bootstrap Word Sense Inventory (TWSI)

The [TWSI sense inventory](https://www.lt.informatik.tu-darmstadt.de/de/data/twsi-turk-bootstrap-word-sense-inventory/) is available for download from its authors.

### Wiktionary and Wikipedia

Database dumps of [Wiktionary](https://www.wiktionary.org/) and [Wikipedia](https://www.wikipedia.org/) for use with the `de.tudarmstadt.ukp.dkpro.wsd.si.lsr` module are available from the [Wikimedia Downloads](http://dumps.wikimedia.org/) page.  Alternatively you can [download a UBY database](http://uby.ukp.informatik.tu-darmstadt.de/uby/) containing Wiktionary and Wikipedia for use with the `de.tudarmstadt.ukp.dkpro.wsd.si.uby` module.

### GermaNet

Instuctions for obtaining GermaNet can be found on the [GermaNet home page](http://www.sfs.uni-tuebingen.de/lsd/).  DKPro WSD can access GermaNet directly through its `de.tudarmstadt.ukp.dkpro.wsd.si.germanet` module, or via UBY and the `de.tudarmstadt.ukp.dkpro.wsd.si.uby` module.  (Since GermaNet is not freely redistributable, you need to produce your own UBY database containing it.)

### FrameNet

You can [download a UBY database](http://uby.ukp.informatik.tu-darmstadt.de/uby/) containing [FrameNet](https://framenet.icsi.berkeley.edu/fndrupal/) for use with the `de.tudarmstadt.ukp.dkpro.wsd.si.uby` module.

### VerbNet

You can [download a UBY database](http://uby.ukp.informatik.tu-darmstadt.de/uby/) containing [VerbNet](http://verbs.colorado.edu/~mpalmer/projects/verbnet.html) for use with the `de.tudarmstadt.ukp.dkpro.wsd.si.uby` module.

### OmegaWiki

You can [download a UBY database](http://uby.ukp.informatik.tu-darmstadt.de/uby/) containing [OmegaWiki](http://www.omegawiki.org/) for use with the `de.tudarmstadt.ukp.dkpro.wsd.si.uby` module.

### OpenThesaurus

You can [download OpenThesaurus](http://www.openthesaurus.de/about/download) for use with the `de.tudarmstadt.ukp.dkpro.wsd.si.lsr` module.

### UBY

[UBY](https://dkpro.github.io/dkpro-uby) is a network of interlinked lexical resources, including support for English WordNet, Wiktionary, Wikipedia, FrameNet, and VerbNet; the German Wikipedia, Wiktionary, GermaNet, and IMSLex-Subcat; and the multilingual OmegaWiki.  You can [download a UBY database](http://uby.ukp.informatik.tu-darmstadt.de/uby/) containing most of these resources.