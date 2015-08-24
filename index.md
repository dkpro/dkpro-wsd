---
#
# Use the widgets beneath and the content will be
# inserted automagically in the webpage. To make
# this work, you have to use › layout: frontpage
#
layout: frontpage
title: "DKPro WSD"
---

*DKPro WSD* is a modular, extensible Java framework for word sense disambiguation.  It is based on [Apache UIMA](https://uima.apache.org/), an industry standard for text processing.

## What is DKPro WSD?

DKPro WSD provides *UIMA components* which encapsulate [corpus readers](/dkpro-wsd/corpora/), linguistic annotators, [lexical semantic resources](/dkpro-wsd/lsr/), WSD algorithms, and evaluation and reporting tools.  You configure the components, or write new ones, and arrange them into a *data processing pipeline*.

DKPro WSD is *modular* and *flexible*.  Components which provide the same functionality can be *freely swapped*. You can easily run the same algorithm on different data sets, or test several different algorithms on the same data set.

### Evaluation and reporting tools

With DKPro WSD, you can...

  * evaluate any number of WSD algorithms simultaneously
  * view or save the *raw sense assignments*
  * compute common evaluation metrics: *coverage*, *precision*, *recall*, *F,,1,, score*
  * use binary (“correct” vs. “incorrect”) or *real-valued scoring*
  * select from various *tie-breaking strategies* for sense assignments
  * plot *precision–recall graphs*
  * produce *confusion matrices* to compare pairs of algorithms
  * *filter* or break down results by part of speech, etc.
  * show results with and without user-specified *backoff algorithms*
  * automatically rescore results with a user-specified *sense clustering*
  * see reports in your *web browser*, or save them as XML, CSV, etc.

### Additional features

  * integrates well with off-the-shelf linguistic UIMA components, such as those from *[DKPro Core](https://dkpro.github.io/dkpro-core)*
  * supports *parameter sweeping* to find optimal system configurations
  * supports related tasks such as *entity linking* and *word sense induction*
  * features *animated, interactive visualizations* of the disambiguation process

### Supported resources

DKPro WSD has out-of-the-box support for many different data sets, WSD algorithms, and lexical semantic resources:

  * *[sense-annotated corpora and data sets](/dkpro-wsd/corpora/)*
    * Senseval and SemEval tasks (all-words, lexical sample, WSI)
    * AIDA CoNLL-YAGO
    * TAC KBP entity linking tasks
    * MASC
    * SemCor
    * WebCAGe
  * *disambiguation algorithms*
    * most frequent sense (MFS) baseline
    * (computed) random sense baseline
    * Lesk variants (original, simplified, extended, lexically expanded, …)
    * graph connectivity–based algorithms
    * Personalized PageRank
    * supervised TWSI
    * It Makes Sense (IMS)
  * *[word sense inventories](/dkpro-wsd/lsr/)*
    * WordNet 1.7 through 3.1
    * EuroWordNet
    * TWSI
    * Wiktionary
    * Wikipedia
    * GermaNet
    * FrameNet
    * OmegaWiki
    * VerbNet
    * OpenThesaurus
    * DKPro LSR
    * UBY
