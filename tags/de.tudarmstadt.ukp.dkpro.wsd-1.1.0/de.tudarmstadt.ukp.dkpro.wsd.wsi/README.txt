DKPro WSD WSI

This package contains parts of the UKP system participating in the SemEval 2013 Task 11
"Word Sense Induction & Disambiguation within an End-User Application".

In particular it contains 
* a reader for the dataset (AMBIENTReader)
* an annotator for marking the target words (WSIAnnotator), 
* a loadable sense inventory (JSONSenseInventory) which may use an induced inventory
* the WSD step  and a writer that produces input files for the official evaluation package (Semeval2013Task11Evaluator).

What is missing to run the actual system is

* the actual WSI algorithm, which was written in R. An older version of this algorithm is included 
as SimpleGraphClusterinInductionAlgorithm and I am planning to port back the improvements from the
R implementation.

* the co-occurrence database. The code to generate such a database will soon be  available as part of the 
DKPro BigData package (https://code.google.com/p/dkpro-bigdata/).

* the distributional thesaurus for lexical expansion. The code to generate the thesaurus is available
from http://www.jobimtext.org.

The documentation will be updated soon. If you want to make use of this module, do not hesitate to contact
me (at hpzorn@gmail.com)

August 2013,
Hans-Peter Zorn