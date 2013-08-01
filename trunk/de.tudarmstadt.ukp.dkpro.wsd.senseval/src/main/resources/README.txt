This directory contains various files for reading and patching the Senseval
and SemEval data sets:

fix_mihalcea_senseval2.sh - a Bash script which fixes the "SemCor XML" versions
of the Senseval-2 all-words data published at
http://www.cse.unt.edu/~rada/downloads.html#sensevalsemcor so that they are 
well-formed XML and conform to the SemCor schema.

fix_mihalcea_senseval3.sh - a Bash script which fixes the "SemCor XML" versions
of the Senseval-3 all-words data published at
http://www.cse.unt.edu/~rada/downloads.html#sensevalsemcor so that they are 
well-formed XML and conform to the SemCor schema.

semeval1-en-cgaw-test-key.sh - a Bash script which fixes the SemEval-2007
English coarse-grained all-words answer keys by lowercasing the sense keys to
match the representations in WordNet 2.1.

senseval2-en-aw-test.patch - a patch file which fixes numerous errors (i.e.,
heads referencing nonexistent satellite IDs) in the Senseval-2 English 
all-words test corpus.

senseval2-en-ls-train-key.patch - a patch file which removes sense keys in
the Senseval-2 English lexical sample training corpus test key which do not
exist in WordNet 1.7, or replaces them with senses which do exist in
WordNet 1.7.

wordnet_senseval.tsv - a mapping of Senseval/SemEval sense keys to WordNet
sense keys.  This works for all versions of WordNet from 1.7 to 3.0.  This 
mapping can be applied on the fly using the SenseMapper class; there is no
need to use it to convert the Senseval/SemEval data sets in advance. 
