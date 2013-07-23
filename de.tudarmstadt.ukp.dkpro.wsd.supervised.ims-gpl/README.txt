IMS (It Makes Sense) version 0.9.2.1 ---- April 11th, 2013.

=========================
CONTENTS

1. introduction
2. files and directories
3. install
4. requirement
5. quick start
  a. train model
  b. test lexical-sample task file
  c. test fine-grained all-words task file
  d. test coarse-grained all-words task file
  e. test plain file
6. more details
7. license
8. versions and changes


========================
1. INTRODUCTION

    This software is a supervised learning based Word Sense Disambiguation (WSD) system.
    This release version is prepared by Zhong Zhi.


========================
2. FILES & DIRECTORIES

    a. IMS_v0.9.2.tar.gz - source code package, containing source code, binary files, java documents, license, and some other basic files
        *.dtd - xml Document Type Definition (DTD) files for SensEval/SemEval tasks
        amend_se3.bash - convert SensEval-3 fine-grained all-words task test data to SensEval-2 format
        train_one.bash - bash script to train a model
        test_one.bash - bash script to test a lexical task file
        testCoarse.bash - bash script to test a SensEval/SemEval coarse-grained task file
        testFine.bash - bash script to test a SensEval/SemEval fine-grained task file
        testPlain.bash - bash script to test a plain file
        scorer.bash - bash script to evaluate result
        LICENSES - GNU Version 3 License
        README - this file
        Makefile - make file
        build.xml - build.xml for ant
        ims.jar - a jar package with binary files
        javadoc.xml - file used to build javadoc
        bin - java class directory
        doc - javadoc directory
        src - source code directory

    b. lib.tar.gz - package contains all the necessary jar files and their related model files
        commons-logging.jar - commons-logging package
        jdom.jar - jdom package
        jwnl.jar - JWNL package
        liblinear-1.33-with-deps.jar - liblinear package
        libsvm.jar  - libsvm package
        maxent-2.4.0.jar - opennlp maxent package
        opennlp-tools-1.3.0.jar - opennlp toolkit package
        prop.xml - prop.xml file for JWNL
        tag.bin.gz - model for opennlp POS tagger
        tagdict.txt - tagdict for opennlp POS tagger
        trove.jar - trove package
        weka-3.2.3.jar - weka package
        EnglishSD.bin.gz - model for opennlp sentence boundary detector
        EnglishTok.bin.gz - model for opennlp tokenizer
        dict - wordnet-1.7.1 dict directory

    c. models.tar.gz - model and statistic files for all word types with WordNet-1.7.1 as the sense inventory
        The classifier is Liblinear.
        The training data of these models include examples from parallel texts, SemCor and DSO corpus.
            For more details about training data set, please refer to http://acl.ldc.upenn.edu/W/W07/W07-2054.pdf

    d. examples.tar.gz - package contains some example files
        bank.n.train.xml - a sample xml training file
        bank.n.train.key - a sample key training file
        bank.n.test.xml - a sample xml test file
        bank.n.test.key - a sample key test file
        bank.n.result.sample - a sample output file for bank.n.test.xml
        test.txt - a sample of plain text
        test.out.sample - a sample output file for text.txt
        test.tok.pos.txt - a sample of plain text which has been tokenized and contains pos tags
        test.tok.pos.out.sample - a sample output file for text.tok.pos.txt
        senseval-2.readme - a readme file for testing SensEval-2 tasks
        senseval-3.readme - a readme file for testing SensEval-3 tasks
        semeval-2007.readme - a readme file for testing SemEval-2007 tasks
        se2.eng-all-words.test.lexelt - a file contains the predicted lexelt information of the instances in SensEval-2 English all-words task
        se2.eng-all-words.test.result.sample - a sample output file for SensEval-2 English all-words task
        se3.eng-all-words.test.lexelt - a file contains the predicted lexelt information of the instances in SensEval-3 English all-words task
        se3.eng-all-words.test.result.sample - a sample output file for SensEval-3 English all-words task
        se4.eng-all-words.test.lexelt - a file contains the predicted lexelt information of the instances in SemEval-2007 English all-words task
        se4.eng-all-words.test.result.sample - a sample output file for SemEval-2007 English all-words task
        se4.eng-coarse-all-words.test.result.sample - a sample output file for SemEval-2007 English coarse-grained all-words task using WordNet-1.7.1 models
        se4.eng-coarse-all-words.test.wn21c.result.sample - a sample output file for SemEval-2007 English coarse-grained all-words task using coarse-grained WordNet-2.1 models
        wn21.index.sense - index.sense file of WordNet-2.1
        (** se[234].eng-all-words.test.lexelt are generated from the given .mrg files in each task **)

    e. Please download the SensEval/SemEval training and test data from:
        SensEval-2: http://193.133.140.102/senseval2/Results/senseval2-corpora.tgz
        SensEval-3: http://www.senseval.org/senseval3/data.html
        SemEval-2007: http://nlp.cs.swarthmore.edu/semeval/tasks/index.php


========================
3. INSTALL

    a. Get the above four packages.

    b. Uncompress IMS_v0.9.2.tar.gz to one directory (take "ims" for example).

    c. Uncompress lib.tar.gz into "ims".
        Now, you have the files described in 2.a and directory "lib" in "ims" directory.

    d. If you want to recompile the code, type "make", you will have new class file and ims.jar

    e. Uncompress models.tar.gz.tar.gz to some directory.

    f. Uncompress examples.tar.gz to some directory.


========================
4. REQUIREMENT

    a. This software requires java 6 (JRE 1.6) or higher version. Check java version with command "java -version".

    b. Since we use JWNL to maintain WordNet, some settings in lib/prop.xml may need to be changed.
        In lib/prop.xml, you can find a line "<param name="dictionary_path" value="lib/dict" />" in this file.
        The value "lib/dict" specifies the path of the WordNet dictionary.
        If you change or move WordNet dictionary to somewhere else, you should modify this parameter properly.
        (** we suggest you use the WordNet dictionary version we provided, because all the experiments we did were based on this version. **)

    c. To test SensEval-2/3 tasks, please first read the senseval-2.readme and senseval-3.readme file.


========================
5. QUICK START

    (Assume models.tar.gz and examples.tar.gz both are uncompressed to directory "ims".)

    a. To train one model:
        Type in a shell open to this directory:
          ./train_one.bash train.xml train.key outputDir
            train.xml   SensEval-2 lexical sample format xml file.
                        (** answer tag in the xml file will be omitted, the senses of instances should be speicified in train.key. **)
            train.key   SensEval-2 lexical sample format key file.
            outputDir   directory to save the model file and statistic file.
        For example:
          ./train_one.bash examples/bank.n.train.xml examples/bank.n.train.key examples
            examples/bank.n.train.xml    a SensEval-2 lexical sample format xml file,
            examples/bank.n.train.key    a SensEval-2 lexical sample format key file for bank.n.train.xml,
            examples            the output directory.
          You will find two output files:
                examples/bank.n.stat.gz    the statistic information of the training instances for bank.n
                examples/bank.n.model.gz   the model file for bank.n

    b. To test one lexical sample file:
        Type in a shell open to this directory:
          ./test_one.bash modelDir test.xml outputDir
            modelDir    directory contains the statistic file and model file.
            test.xml    SensEval-2 lexical sample format xml file.
            outputDir   directory to save the result file.
        For example:
          ./test_one.bash examples examples/bank.n.test.xml examples
            examples         the directory you save the model and statistic files
            examples/bank.n.test.xml    a SensEval-2 lexical sample format xml file for test
            examples         the directory you save the output result
          You will find one output file:
                examples/bank.n.result    the result file of the SensEval-2 lexical sample key file format
          Now, you can evaluate the output:
          ./scorer.bash examples/bank.n.result examples/bank.n.key

    c. To test fine-grained all-words task:
        Type in a shell open to this directory:
          ./testFine.bash modelDir test.xml outputFile lexeltFile index.sense
            modelDir    directory contains the statistic and model files.
            test.xml    test SensEval-2 fine-grained all-words format xml file.
            outputFile  the output result file.
            lexeltFile  file contains the lexelt informantion of testing instances. Each line represents one instance, e.g.: inst_id lexelt_id(s)
            index.sense sense definition file of the sense-inventory for current task. (For example, for SemEval-2007 fine-grained all-words task, please use the index.sense from WordNet 2.1).
        For example:
           (please first download the test data from http://193.133.140.102/senseval2/Results/senseval2-corpora.tgz. as the test data format has some problem, please follow examples/senseval-2.readme to amend the test file. suppose the amend test data file of SensEval-2 English all-words task is stored as examples/se2.eng-all-words.test.xml.)
          ./testFine.bash models examples/se2.eng-all-words.test.xml examples/se2.eng-all-words.test.lexelt examples/se2.eng-all-words.test.result lib/dict/index.sense
            models      directory contains model and statistic files
            examples/se2.eng-all-words.test.xml    the test xml file for SensEval-2 fine-grained all-words task
            examples/se2.eng-all-words.test.lexelt  lexelt information of SensEval-2 fine-grained all-words task, it is generated from the given .mrg files
            examples/se2.eng-all-words.test.result    the output result file
            lib/dict/index.sense    index.sense file of WordNet-1.7.1

    d. To test coarse-grained all-words task:
        Type in a shell open to this directory:
          ./testCoarse.bash modelDir test.xml outputFile index.sense
            modelDir    directory contains the statistic and model files.
            test.xml    test coarse-grained all-words xml file.
            outputFile  the output result file.
            index.sense sense definition file of current task.
        For example:
          (please first download the test data and answer file from http://nlp.cs.swarthmore.edu/semeval/tasks/task07/data/test.tar.gz and http://nlp.cs.swarthmore.edu/semeval/tasks/task07/data/key.tar.gz. suppose the test file is saved as examples/se4.eng-coarse-all-words.test.xml.)
          ./testCoarse.bash models examples/se4.eng-coarse-all-words.test.xml examples/se4.eng-coarse-all-words.test.result examples/wn21.index.sense
            models      directory contains model and statistic files
            examples/se4.eng-coarse-all-words.test.xml    the test xml file for SemEval-2007 coarse-grained all-words task
            examples/se4.eng-coarse-all-words.test.result    the output result file
            examples/wn21.index.sense    the index.sense file in WordNet-2.1.
            (** NOTE that, the sense inventory of this task is coarse-grained WordNet-2.1, the accuracy will decrease by using the WordNet-1.7.1 models. **)

    e. To test a plain text:
        Type in a shell open to this directory:
          ./testPlain.bash modelDir test.txt outputFile index.sense split(0/1) tokenized(0/1) pos(0/1) lemmatize(0/1) delimiter(default "/")
            modelDir    directory contains the statistic and model files
            test.txt    test file
            outputFile  the output result file
            index.sense the index.sense file in WordNet
            split       whether the document has been sentence split (1 for true)
            tokenized   whether the document has been tokenized (1 for true)
            pos         whether the document contains pos tag information (1 for true)
            lemmatize   whether the document contains lemmatization information (1 for true)
            delimiter   delemiters between token, pos, and lemma
        For example:
          ./testPlain.bash models examples/test.txt examples/test.out lib/dict/index.sense
            examples/test.txt    a test file contains plain text
            models      directory contains the statistic and model files
            examples/test.out    the output result file
            lib/dict/index.sense    the index.sense file in WordNet-1.7.1
          ./testPlain.bash models examples/test.tok.pos.txt examples/test.tok.pos.out lib/dict/index.sense 1 1 1
            examples/test.tok.pos.txt    a test file contains tokenized plain text with pos tags
            models      directory contains the statistic and model files
            examples/test.tok.pos.out    the output result file
            lib/dict/index.sense    the index.sense file in WordNet-1.7.1
            1    indicate that the text has been sentence split
            1    indicate that the text has been tokenized
            1    indicate that the text contains POS information


========================
6. MORE DETAILS

    Let us start from the sg.edu.nus.comp.nlp.ims.implement package.
    Assume that you are now in the working directory "ims";
      and variable CLASSPATH=lib/weka-3.2.3.jar:lib/jwnl.jar:lib/commons-logging.jar:lib/mxpost.jar:lib/trove.jar:lib/opennlp-tools-1.3.0.jar:lib/maxent-2.4.0.jar:lib/jdom.jar:lib/liblinear-1.33-with-deps.jar:ims.jar
    In this package, you will find two classes:
        a. sg.edu.nus.comp.nlp.ims.implement.CTrainModel -- class for training
            Usage: java -cp $CLASSPATH sg.edu.nus.comp.nlp.ims.implement.CTrainModel [options] train.xml train.key saveDir
              options:
                  -i class name of Instance Extractor(default sg.edu.nus.comp.nlp.ims.instance.CInstanceExtractor)
                  -f class name of Feature Extractor(default sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination)
                  -c class name of Corpus(default sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus)
                  -t class name of Trainer(default sg.edu.nus.comp.nlp.ims.classifiers.CLibLinearTrainer)
                  -m class name of Model Writer(default sg.edu.nus.comp.nlp.ims.io.CModelWriter)
                  -s2 cut off for surrounding word(default 0)
                  -c2 cut off for collocation(default 0)
                  -p2 cut off for pos(default 0)
                  -split 1/0 whether the corpus is sentence splitted(default 0)
                  -ssm path of sentence splitter model
                  -token 1/0 whether the corpus is tokenized(default 0)
                  -pos 1/0 whether the pos tag is provided in corpus(default 0)
                  -ptm path of pos tagger model
                  -dict path of dictionary for opennlp POS tagger(option)
                  -tagdict path of tagdict for opennlp POS tagger(option)
                  -lemma 1/0 whether the lemma is provided in the corpus(default 0)
                  -prop path of prop.xml for JWNL
                  -type type of train.xml
                      directory: train all xml files under directory trainPath
                      list: train all xml files listed in file trainPath
                      file(default): train file trainPath

        b. sg.edu.nus.comp.nlp.ims.implement.CTester -- class for test.
            Usage: java -cp $CLASSPATH sg.edu.nus.comp.nlp.ims.implement.CTester [options] testPath modelDir statisticDir saveDir
             options:
                  -i class name of Instance Extractor(default sg.edu.nus.comp.nlp.ims.instance.CInstanceExtractor)
                  -f class name of Feature Extractor(default sg.edu.nus.comp.nlp.ims.feature.CFeatureExtractorCombination)
                  -c class name of Corpus(default sg.edu.nus.comp.nlp.ims.corpus.CLexicalCorpus)
                  -e class name of Evaluator(default sg.edu.nus.comp.nlp.ims.classifiers.CLibLinearEvaluator)
                  -r class name of Result Writer(default sg.edu.nus.comp.nlp.ims.io.CResultWriter)
                  -lexelt path of lexelt file
                  -is path of index.sense
                  -prop path of prop.xml for JWNL
                  -split 1/0 whether the corpus is sentence splitted(default 0)
                  -ssm path of sentence splitter model
                  -token 1/0 whether the corpus is tokenized(default 0)
                  -pos 1/0 whether the pos tag is provided in corpus(default 0)
                  -ptm path POS tagger model
                  -dict path of dictionary for opennlp POS tagger(option)
                  -tagdict path of tagdict for POS tagger(option)
                  -lemma 1/0 whether the lemma is provided in the corpus(default 0)
                  -delimiter the delimiter to separate tokens, lemmas and POS tags (default "/")
                  -type type of testPath
                      directory: test all xml files under directory testPath
                      list: test all files listed in file testPath
                      file(default): test file testPath

         You can refer to the bash scripts for the use of these two classes.
         Furthermore, these two classes are just examples of how to make use of the API/classes of IMS.
         For more API information of this software, please refer to the javadoc files or the source code.


========================
7. LICENSE

    IMS (It Makes Sense) -- NUS WSD system
    Copyright (c) 2010 National University of Singapore.
    All Rights Reserved.

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

    For more information, bug reports, fixes, please contact:
        Zhong Zhi
        Department of Computer Science
        Computing 1, 13 Computing Drive
        National University of Singapore
        Singapore 117417
        zhongzhi[at]comp[dot]nus[dot]edu[dot]sg
        http://nlp.comp.nus.edu.sg/~zhongzhi


========================
8. VERSIONS & CHANGES

Version 0.9   ---   2009-08-01
Version 0.9.1   ---   2009-11-20
Version 0.9.2   ---   2010-04-27
        Change default feature value from "!" to "!DEF!"
        Add "n't, 've, 'd, 'm, 's, 're, 'll, -lrb-, -rrb-, -lsb-, -rsb-, -lcb-, -rcb-" to stop word list
	Fix the one tokenization bug of Penn Treebank tokenizer
        Fix some small bugs
Version 0.9.2.1   ---   2013-04-11
	Change the strategy of lemmatization in CJWNL.java: boss -> boss instead of bos.
