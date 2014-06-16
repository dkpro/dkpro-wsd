#!/bin/bash

# This script fixes the SemEval-2007 English coarse-grained all-words answer 
# keys by lowercasing the sense keys to match their representations in 
# WordNet 2.1.

tr '[[:upper:]]' '[[:lower:]]' < dataset21.test.key > dataset21.test.key.fixed
tr '[[:upper:]]' '[[:lower:]]' < fs_baseline.key > fs_baseline.key.fixed
