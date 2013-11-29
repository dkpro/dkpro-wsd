#!/bin/bash

# This script fixes the "SemCor XML" versions of the Senseval-2 all-words data
# published at  http://www.cse.unt.edu/~rada/downloads.html#sensevalsemcor so
# that they are well-formed XML and conform to the SemCor schema.

for f in d00 d01 d02
do
    sed '1s/^/<contextfile concordance=senseval2>\n/;s/&/&amp;/g;s/=\([^> ]\+\)/="\1"/g;$s,$,\n</contextfile>,' $f.semcor.lexsn.key > $f.xml
done
