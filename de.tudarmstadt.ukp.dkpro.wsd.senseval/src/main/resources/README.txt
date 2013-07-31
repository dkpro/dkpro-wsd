This directory contains various files for reading and patching the Senseval
and SemEval data sets:

wordnet_senseval.tsv - a mapping of Senseval/SemEval sense keys to WordNet
sense keys.  This works for all versions of WordNet from 1.7 to 3.0.

senseval2-en-aw-test.patch - a patch file which fixes numerous errors (i.e.,
heads referencing nonexistent satellite IDs) in the Senseval-2 English 
all-words test corpus.

senseval2-en-ls-train-key.patch - a patch file which removes sense keys in
the Senseval-2 English lexical sample training corpus test key which do not
exist in WordNet 1.7, or replaces them with senses which do exist in
WordNet 1.7.