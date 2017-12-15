/**
 * Copyright (C) 2012 Department of General and Computational Linguistics,
 * University of Tuebingen
 *
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tuebingen.uni.sfs.germanet.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides high-level look-up access to GermaNet data. Intended as a read-only
 * resource - no public methods are provided for changing or adding data.<br><br>
 *
 * GermaNet is a collection of German lexical units (<code>LexUnits</code>)
 * organized into sets of synonyms (<code>Synsets</code>).<br>
 * A <code>Synset</code> has a
 * <code>WordCategory</code> (adj, nomen, verben) and consists of a paraphrase
 * and Lists of <code>LexUnit</code>s. The List of <code>LexUnit</code>s is
 * never empty.<br>
 * A <code>LexUnit</code> consists of an orthForm (represented as a Strings),
 * an orthVar (can be empty), an oldOrthForm (can be empty), and an oldOrthVar
 * (can be empty). <code>Examples</code>, <code>Frames</code>, <code>IliRecords</code>,
 * and <code>WiktionaryParaphrases</code> can belong to a
 * <code>LexUnit</code> as well as the following
 * attributes: styleMarking (boolean), sense (int), styleMarking (boolean),
 * artificial (boolean), namedEntity (boolean), and source (String).<br>
 * A <code>Frame</code> is simply a container for frame data (String).<br>
 * An <code>Example</code> consists of text (String) and zero or one
 * <code>Frame</code>(s).<br><br>
 *
 * To construct a <code>GermaNet</code> object, provide the location of the
 * GermaNet data and (optionally) a flag indicating whether searches should be
 * done ignoring case. This data location can be set with a <code>String</code>
 * representing the path to the directory containing the data, or with a
 * <code>File</code> object. If no flag is used, then case-sensitive
 * searching will be performed:<br><br>
 * <code>
 *    // Use case-sensitive searching<br>
 *    GermaNet gnet = new GermaNet("/home/myName/germanet/GN_V60");<br>
 * </code>
 * or<br>
 * <code>
 *    // Ignore case when searching<br>
 *    File gnetDir = new File("/home/myName/germanet/GN_V60");<br>
 *    GermaNet gnet = new GermaNet(gnetDir, true);<br><br>
 * </code>
 * The <code>GermaNet</code> class has methods that return <code>Lists</code> of
 * <code>Synsets</code> or <code>LexUnits</code>, given
 * an orthForm or a WordCategory.  For example,<br><br><code>
 *    List&lt;LexUnit&gt; lexList = gnet.getLexUnits("Bank");<br>
 *    List&lt;LexUnit&gt; verbenLU = gnet.getLexUnits(WordCategory.verben);<br>
 *    List&lt;Synset&gt; synList = gnet.getSynsets("gehen");<br>
 *    List&lt;Synset&gt; adjSynsets = gnet.getSynsets(WordCategory.adj);<br><br>
 * </code>
 *
 * Unless otherwise stated, methods will return an empty List rather than null
 * to indicate that no objects exist for the given request. <br><br>
 *
 * <b>Important Note:</b><br>
 * Loading GermaNet requires more memory than the JVM allocates by default. Any
 * application that loads GermaNet will most likely need to be run with JVM
 * options that increase the memory allocated, like this:<br><br>
 *
 * <code>java -Xms128m -Xmx128m MyApplication</code><br><br>
 *
 * Depending on the memory needs of the application itself, the 128's may
 * need to be changed to 256's or higher.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 8.0
 */
public class GermaNet {

    private final Log logger = LogFactory.getLog(getClass());

    public static final String XML_SYNSETS = "synsets";
    public static final String XML_SYNSET = "synset";
    public static final String XML_ID = "id";
    public static final String XML_PARAPHRASE = "paraphrase";
    public static final String XML_WORD_CATEGORY = "category";
    public static final String XML_WORD_CLASS = "class";
    public static final String XML_LEX_UNIT = "lexUnit";
    public static final String XML_ORTH_FORM = "orthForm";
    public static final String XML_ORTH_VAR = "orthVar";
    public static final String XML_OLD_ORTH_FORM = "oldOrthForm";
    public static final String XML_OLD_ORTH_VAR = "oldOrthVar";
    public static final String XML_SOURCE = "source";
    public static final String XML_SENSE = "sense";
    public static final String XML_STYLE_MARKING = "styleMarking";
    public static final String XML_NAMED_ENTITY = "namedEntity";
    public static final String XML_ARTIFICIAL = "artificial";
    public static final String XML_EXAMPLE = "example";
    public static final String XML_TEXT = "text";
    public static final String XML_EXFRAME = "exframe";
    public static final String XML_FRAME = "frame";
    public static final String XML_RELATIONS = "relations";
    public static final String XML_RELATION = "relation";
    public static final String XML_CON_REL = "con_rel";
    public static final String XML_LEX_REL = "lex_rel";
    public static final String XML_RELATION_NAME = "name";
    public static final String XML_RELATION_DIR = "dir";
    public static final String XML_RELATION_INV = "inv";
    public static final String XML_RELATION_TO = "to";
    public static final String XML_RELATION_FROM = "from";
    //for ILI
    public static final String XML_ILI_RECORD = "iliRecord";
    public static final String XML_LEX_UNIT_ID = "lexUnitId";
    public static final String XML_EWN_RELATION = "ewnRelation";
    public static final String XML_PWN_WORD = "pwnWord";
    public static final String XML_PWN20_SENSE = "pwn20Sense";
    public static final String XML_PWN20_ID = "pwn20Id";
    public static final String XML_PWN30_ID = "pwn30Id";
    public static final String XML_PWN20_PARAPHRASE = "pwn20paraphrase";
    public static final String XML_PWN20_SYNONYMS = "pwn20Synonyms";
    public static final String XML_PWN20_SYNONYM = "pwn20Synonym";
    public static final String YES = "yes";
    public static final String NO = "no";
    //for Wiktionary
    public static final String XML_WIKTIONARY_PARAPHRASE = "wiktionaryParaphrase";
    public static final String XML_WIKTIONARY_ID = "wiktionaryId";
    public static final String XML_WIKTIONARY_SENSE_ID = "wiktionarySenseId";
    public static final String XML_WIKTIONARY_SENSE = "wiktionarySense";
    public static final String XML_WIKTIONARY_EDITED = "edited";
    public static final String XML_WIKTIONARY_POS = "pos";
    //for Compounds
    public static final String XML_COMPOUND = "compound";
    public static final String XML_PROPERTY = "property";
    public static final String XML_CATEGORY = "category";
    public static final String XML_COMPOUND_MODIFIER = "modifier";
    public static final String XML_COMPOUND_HEAD = "head";
    private final EnumMap<WordCategory, HashMap<String, ArrayList<LexUnit>>> wordCategoryMap;
    private final EnumMap<WordCategory, HashMap<String, ArrayList<LexUnit>>> wordCategoryMapAllOrthForms;
    private final TreeSet<Synset> synsets;
    private final ArrayList<IliRecord> iliRecords;
    private final ArrayList<WiktionaryParaphrase> wiktionaryParaphrases;
    private final HashMap<Integer, LexUnit> lexUnitID;
    private final HashMap<Integer, Synset> synsetID;
    private File dir = null;
    private List<InputStream> inputStreams = null;
    private List<String> xmlNames = null;
    private boolean ignoreCase = false;

    /**
     * Constructs a new <code>GermaNet</code> object by loading the the data
     * files in the specified directory/archive path name - searches are case sensitive.
     * @param dirName the directory where the GermaNet data files are located
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     * @throws IOException
     */
    public GermaNet(String dirName) throws FileNotFoundException, XMLStreamException, IOException {
        this(new File(dirName), false);
    }

    /**
     * Constructs a new <code>GermaNet</code> object by loading the the data
     * files in the specified directory/archive path name.
     * @param dirName the directory where the GermaNet data files are located
     * @param ignoreCase if true ignore case on lookups, otherwise do case
     * sensitive searches
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     * @throws IOException
     */
    public GermaNet(String dirName, boolean ignoreCase) throws FileNotFoundException, XMLStreamException, IOException {
        this(new File(dirName), ignoreCase);
    }

    /**
     * Constructs a new <code>GermaNet</code> object by loading the the data
     * files in the specified directory/archive File - searches are case sensitive.
     * @param dir location of the GermaNet data files
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     * @throws IOException
     */
    public GermaNet(File dir) throws FileNotFoundException, XMLStreamException, IOException {
        this(dir, false);
    }

    /**
     * Constructs a new <code>GermaNet</code> object by loading the the data
     * files in the specified directory/archive File.
     * @param dir location of the GermaNet data files
     * @param ignoreCase if true ignore case on lookups, otherwise do case
     * sensitive searches
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public GermaNet(File dir, boolean ignoreCase) throws FileNotFoundException, XMLStreamException, IOException {
        checkMemory();
        this.ignoreCase = ignoreCase;
        this.inputStreams = null;
        this.synsets = new TreeSet<Synset>();
        this.iliRecords = new ArrayList<IliRecord>();
        this.wiktionaryParaphrases = new ArrayList<WiktionaryParaphrase>();
        this.synsetID = new HashMap<Integer, Synset>();
        this.lexUnitID = new HashMap<Integer, LexUnit>();
        this.wordCategoryMap = new EnumMap<WordCategory, HashMap<String, ArrayList<LexUnit>>>(WordCategory.class);
        this.wordCategoryMapAllOrthForms = new EnumMap<WordCategory, HashMap<String, ArrayList<LexUnit>>>(WordCategory.class);

        if (!dir.isDirectory() && isZipFile(dir)) {
            ZipFile zipFile = new ZipFile(dir);
            Enumeration entries = zipFile.entries();

            List<InputStream> inputStreamList = new ArrayList<InputStream>();
            List<String> nameList = new ArrayList<String>();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                if (entryName.split(File.separator).length > 1) {
                    entryName = entryName.split(File.separator)[entryName.split(File.separator).length - 1];
                }
                nameList.add(entryName);
                InputStream stream = zipFile.getInputStream(entry);
                inputStreamList.add(stream);
            }
            inputStreams = inputStreamList;
            xmlNames = nameList;
            zipFile.close();
        } else {
            this.dir = dir;
        }

        load();
    }

    /**
     * Prints warning if available memory is low.
     */
    private void checkMemory() {
        long freeMemory = Runtime.getRuntime().freeMemory() / 1000000;
        if (freeMemory < 120) {
            logger.warn("Warning: you may not have enough memory to "
                    + "load GermaNet.");
            logger.warn("Try using \"-Xms128m -Xmx128m\" JVM options:");
            logger.warn("java -Xms128m -Xmx128m <restOfCommand>");
        }
    }

    /**
     * Loads the data files into this <code>GermaNet</code> object.
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    void load() throws XMLStreamException {
        StaxLoader loader;
        String oldVal = null;

        // use xerces xml parser
        oldVal = System.getProperty("javax.xml.stream.XMLInputFactory");
        System.setProperty("javax.xml.stream.XMLInputFactory",
                "com.sun.xml.internal.stream.XMLInputFactoryImpl");

        // load data
        if (this.dir != null) {
        try {
            loader = new StaxLoader(dir, this);
            loader.load();
            loadIli(false);
            loadWiktionaryParaphrases(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GermaNet.class.getName()).log(Level.SEVERE, null, ex);
        }
        } else {
            try {
                loader = new StaxLoader(inputStreams, xmlNames, this);
                loader.load();
                loadIli(true);
                loadWiktionaryParaphrases(true);
            } catch (StreamCorruptedException ex) {
                Logger.getLogger(GermaNet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        trimAll();

        // set parser back to whatever it was before
        if (oldVal != null) {
            System.setProperty("javax.xml.stream.XMLInputFactory", oldVal);
        }
    }

    /**
     * Gets the absolute path name of the directory where the GermaNet data files
     * are stored.
     * @return the absolute pathname of the location of the GermaNet data files
     */
    public String getDir() {
        if (this.dir != null) {
            return this.dir.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Adds a <code>Synset</code> to this <code>GermaNet</code>'s
     * <code>Synset</code> list.
     * @param synset the <code>Synset</code> to add
     */
    protected void addSynset(Synset synset) {
        ArrayList<LexUnit> luList;
        HashMap<String, ArrayList<LexUnit>> map;
        HashMap<String, ArrayList<LexUnit>> mapAllOrthForms;

        // add synset to synset list and synsetID map
        synsets.add(synset);
        synsetID.put(synset.getId(), synset);

        // add synset to its wordCategory map
        map = wordCategoryMap.get(synset.getWordCategory());
        mapAllOrthForms = wordCategoryMapAllOrthForms.get(synset.getWordCategory());
        if (map == null) {
            map = new HashMap<String, ArrayList<LexUnit>>();
        }
        if (mapAllOrthForms == null) {
            mapAllOrthForms = new HashMap<String, ArrayList<LexUnit>>();
        }

        // add LexUnits of synset to lexUnitID map and add mapping
        // from orthForm to corresponding LexUnits
        for (LexUnit lu : synset.getLexUnits()) {

            lexUnitID.put(lu.getId(), lu);

            String orthForm = lu.getOrthForm();
            if (ignoreCase) {
                orthForm = orthForm.toLowerCase();
            }
            luList = map.get(orthForm);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            luList.add(lu);
            map.put(orthForm, luList);

            luList = mapAllOrthForms.get(orthForm);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            luList.add(lu);
            mapAllOrthForms.put(orthForm, luList);

            String orthVar = lu.getOrthVar();
            if (ignoreCase && orthVar != null) {
                orthVar = orthVar.toLowerCase();
            }
            luList = mapAllOrthForms.get(orthVar);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            luList.add(lu);
            mapAllOrthForms.put(orthVar, luList);

            String oldOrthForm = lu.getOldOrthForm();
            if (ignoreCase && oldOrthForm != null) {
                oldOrthForm = oldOrthForm.toLowerCase();
            }
            luList = mapAllOrthForms.get(oldOrthForm);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            if (!luList.contains(lu)) {
                luList.add(lu);
                mapAllOrthForms.put(oldOrthForm, luList);
            }

            String oldOrthVar = lu.getOldOrthVar();
            if (ignoreCase && oldOrthVar != null) {
                oldOrthVar = oldOrthVar.toLowerCase();
            }
            luList = mapAllOrthForms.get(oldOrthVar);
            if (luList == null) {
                luList = new ArrayList<LexUnit>();
            }
            if (!luList.contains(lu)) {
                luList.add(lu);
                mapAllOrthForms.put(oldOrthVar, luList);
            }
        }
        wordCategoryMap.put(synset.getWordCategory(), map);
        wordCategoryMapAllOrthForms.put(synset.getWordCategory(), mapAllOrthForms);
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code>.
     * @return a <code>list</code> of all <code>Synsets</code>
     */
    @SuppressWarnings("rawtypes")
    public List<Synset> getSynsets() {
        List<Synset> rval = new ArrayList<Synset>(synsets.size());
        Iterator iter = synsets.iterator();
        while (iter.hasNext()) {
            rval.add((Synset) iter.next());
        }
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> in which
     * <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant in one of its <code>LexUnits</code>, using the
     * <code>ignoreCase</code> flag as set in the constructor. Same than calling
     * <code>getSynsets(orthForm, false)</code> with
     * <code>considerMainOrthFormOnly=false</code>.
     * @param orthForm the <code>orthForm</code> to search for
     * @return a <code>List</code> of all <code>Synsets</code> containing
     * orthForm. If no <code>Synsets</code> were found, this is a
     * <code>List</code> containing no <code>Synsets</code>
     */
    public List<Synset> getSynsets(String orthForm) {
        return getSynsets(orthForm, false);
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> in which
     * <code>orthForm</code> occurs as main orthographical form in one of its
     * <code>LexUnits</code> -- in case <code>considerAllOrthForms</code> is
     * true. Else returns a <code>List</code> of all <code>Synsets</code> in
     * which <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant in one of its <code>LexUnits</code> -- in case
     * <code>considerAllOrthForms</code> is false. It uses the
     * <code>ignoreCase</code> flag as set in the constructor.
     * @param orthForm the <code>orthForm</code> to search for
     * @param considerMainOrthFormOnly considering main orthographical form only
     * (<code>true</code>) or all variants (<code>false</code>)
     * @return a <code>List</code> of all <code>Synsets</code> containing
     * orthForm. If no <code>Synsets</code> were found, this is a
     * <code>List</code> containing no <code>Synsets</code>
     */
    public List<Synset> getSynsets(String orthForm, boolean considerMainOrthFormOnly) {
        ArrayList<Synset> rval = new ArrayList<Synset>();
        HashMap<String, ArrayList<LexUnit>> map;
        List<LexUnit> tmpList;
        String mapForm = orthForm;

        if (ignoreCase) {
            mapForm = orthForm.toLowerCase();
        }

        for (WordCategory wc : WordCategory.values()) {
            if (considerMainOrthFormOnly) {
                map = wordCategoryMap.get(wc);
            } else {
                map = wordCategoryMapAllOrthForms.get(wc);
            }
            tmpList = map.get(mapForm);
            if (tmpList != null) {
                for (LexUnit lu : tmpList) {
                    if (!rval.contains(lu.getSynset())) {
                        rval.add(lu.getSynset());
                    }
                }
            }
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> with the
     * specified <code>WordCategory</code> in which <code>orthForm</code> occurs
     * as main orthographical form, as orthographical variant, as old
     * orthographical form, or as old orthographic variant in one of its
     * <code>LexUnits</code>. It uses the <code>ignoreCase</code> flag as set in
     * the constructor. Same than calling
     * <code>getSynsets(orthForm, wordCategory, false)</code> with
     * <code>considerMainOrthFormOnly=false</code>.
     * @param orthForm the <code>orthForm</code> to be found
     * @param wordCategory the <code>WordCategory</code> of the
     * <code>Synsets</code> to be found (e.g. <code>WordCategory.adj</code>)
     * @return a <code>List</code> of <code>Synsets</code> with the specified
     * <code>orthForm</code> and <code>wordCategory</code>.
     */
    public List<Synset> getSynsets(String orthForm, WordCategory wordCategory) {
        return getSynsets(orthForm, wordCategory, false);
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> with the
     * specified <code>WordCategory</code> in which <code>orthForm</code> occurs
     * as main orthographical form in one of its <code>LexUnits</code> -- in
     * case <code>considerAllOrthForms</code> is true. Else returns a
     * <code>List</code> of all <code>Synsets</code> in which
     * <code>orthForm</code> occurs as as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant in one of its <code>LexUnits</code> -- in case
     * <code>considerAllOrthForms</code> is false. It uses the
     * <code>ignoreCase</code> flag as set in the constructor.
     * @param orthForm the <code>orthForm</code> to be found
     * @param wordCategory the <code>WordCategory</code> of the
     * <code>Synsets</code> to be found (e.g. <code>WordCategory.adj</code>)
     * @param considerMainOrthFormOnly considering main orthographical form only
     * (<code>true</code>) or all variants (<code>false</code>)
     * @return a <code>List</code> of <code>Synsets</code> with the specified
     * <code>orthForm</code> and <code>wordCategory</code>.
     */
    public List<Synset> getSynsets(String orthForm, WordCategory wordCategory,
            boolean considerMainOrthFormOnly) {
        /*
         * This method can probably be removed since it is very rare that
         * an orthForm is contained in more than one word class
         */
        ArrayList<Synset> rval = new ArrayList<Synset>();
        HashMap<String, ArrayList<LexUnit>> map;
        List<LexUnit> tmpList;

        if (ignoreCase) {
            orthForm = orthForm.toLowerCase();
        }

        if (considerMainOrthFormOnly) {
            map = wordCategoryMap.get(wordCategory);
        } else {
            map = wordCategoryMapAllOrthForms.get(wordCategory);
        }
        if (map != null) {
            tmpList = map.get(orthForm);
            if (tmpList != null) {
                for (LexUnit lu : tmpList) {
                    if (!rval.contains(lu.getSynset())) {
                        rval.add(lu.getSynset());
                    }
                }
            }
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> in the specified
     * <code>wordCategory</code>.
     * @param wordCategory the <code>WordCategory</code>, for example
     * <code>WordCategory.nomen</code>
     * @return a <code>List</code> of all <code>Synsets</code> in the specified
     * <code>wordCategory</code>. If no <code>Synsets</code> were found, this is
     * a <code>List</code> containing no <code>Synsets</code>.
     */
    public List<Synset> getSynsets(WordCategory wordCategory) {

        ArrayList<Synset> rval = new ArrayList<Synset>();

        for (Synset syn : synsets) {
            if (syn.getWordCategory() == wordCategory) {
                rval.add(syn);
            }
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>Synsets</code> in the specified
     * <code>wordClass</code>.
     * @param wordClass the <code>WordClass</code>, for example
     * <code>WordCategory.Menge</code>
     * @return a <code>List</code> of all <code>Synsets</code> in the specified
     * <code>wordClass</code>. If no <code>Synsets</code> were found, this is
     * a <code>List</code> containing no <code>Synsets</code>.
     */
    public List<Synset> getSynsets(WordClass wordClass) {

        ArrayList<Synset> rval = new ArrayList<Synset>();

        for (Synset syn : synsets) {
            if (syn.getWordClass() == wordClass) {
                rval.add(syn);
            }
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns the <code>Synset</code> with <code>id</code>, or
     * <code>null</code> if it is not found.
     * @param id the ID of the <code>Synset</code> to be found.
     * @return the <code>Synset</code> with <code>id</code>, or <code>null</code>
     * if it is not found..
     */
    public Synset getSynsetByID(int id) {
        return synsetID.get(id);
    }

    /**
     * Returns the <code>LexUnit</code> with <code>id</code>, or
     * <code>null</code> if it is not found.
     * @param id the ID of the <code>LexUnit</code> to be found
     * @return the <code>LexUnit</code> with <code>id</code>, or
     * <code>null</code> if it is not found.
     */
    public LexUnit getLexUnitByID(int id) {
        return lexUnitID.get(id);
    }

    /**
     * Returns the number of <code>Synsets</code> contained in <code>GermaNet</code>.
     * @return the number of <code>Synsets</code> contained in <code>GermaNet</code>
     */
    public int numSynsets() {
        return synsetID.size(); //synsets.size();

    }

    /**
     * Returns the number of <code>LexUnits</code> contained in
     * <code>GermaNet</code>.
     * @return the number of <code>LexUnits</code> contained in
     * <code>GermaNet</code>
     */
    public int numLexUnits() {
        return lexUnitID.size();
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> in which
     * <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant. It uses the
     * <code>ignoreCase</code> flag as set in the constructor. Same than
     * calling <code>getSynsets(orthForm, false)</code> with
     * <code>considerMainOrthFormOnly=false</code>.
     * @param orthForm the <code>orthForm</code> to search for
     * @return a <code>List</code> of all <code>LexUnits</code> containing
     * <code>orthForm</code>. If no <code>LexUnits</code> were found, this is a
     * <code>List</code> containing no <code>LexUnits</code>.
     */
    public List<LexUnit> getLexUnits(String orthForm) {
        return getLexUnits(orthForm, false);
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> in which
     * <code>orthForm</code> occurs as main orthographical form -- in case
     * <code>considerAllOrthForms</code> is true. Else returns a
     * <code>List</code> of all <code>LexUnits</code> in which
     * <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant -- in case
     * <code>considerAllOrthForms</code> is false. It uses the
     * <code>ignoreCase</code> flag as set in the constructor.
     * @param orthForm the <code>orthForm</code> to search for
     * @param considerMainOrthFormOnly considering main orthographical form only
     * (<code>true</code>) or all variants (<code>false</code>)
     * @return a <code>List</code> of all <code>LexUnits</code> containing
     * <code>orthForm</code>. If no <code>LexUnits</code> were found, this is a
     * <code>List</code> containing no <code>LexUnits</code>.
     */
    public List<LexUnit> getLexUnits(String orthForm, boolean considerMainOrthFormOnly) {
        ArrayList<LexUnit> rval = new ArrayList<LexUnit>();

        // get LexUnits from each word class
        for (WordCategory wc : WordCategory.values()) {
            rval.addAll(getLexUnits(orthForm, wc, considerMainOrthFormOnly));
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> with the
     * specified <code>WordCategory</code> in which <code>orthForm</code>
     * occurs as as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant. It uses the <code>ignoreCase</code> flag as set in
     * the constructor. Same than calling
     * <code>getSynsets(orthForm, wordCategory, false)</code> with
     * <code>considerMainOrthFormOnly=false</code>.
     * @param orthForm the <code>orthForm</code> to be found
     * @param wordCategory the <code>WordCategory</code> of the
     * <code>LexUnits</code> to be found (eg <code>WordCategory.nomen</code>)
     * @return a <code>List</code> of <code>LexUnits</code> with the specified
     * <code>orthForm</code> and <code>wordCategory</code>.
     */
    public List<LexUnit> getLexUnits(String orthForm, WordCategory wordCategory) {
        return getLexUnits(orthForm, wordCategory, false);
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> with the
     * specified <code>WordCategory</code> in which <code>orthForm</code> occurs
     * as main orthographical form -- in case <code>considerAllOrthForms</code>
     * is true. Else returns a <code>List</code> of all <code>LexUnits</code> in
     * which <code>orthForm</code> occurs as main orthographical form, as
     * orthographical variant, as old orthographical form, or as old
     * orthographic variant -- in case
     * <code>considerAllOrthForms</code> is false. It uses the
     * <code>ignoreCase</code> flag as set in the constructor.
     * @param orthForm the <code>orthForm</code> to be found
     * @param wordCategory the <code>WordCategory</code> of the
     * <code>LexUnits</code> to be found (eg <code>WordCategory.nomen</code>)
     * @param considerMainOrthFormOnly considering main orthographical form only
     * (<code>true</code>) or all variants (<code>false</code>)
     * @return a <code>List</code> of <code>LexUnits</code> with the specified
     * <code>orthForm</code> and <code>wordCategory</code>.
     */
    @SuppressWarnings("unchecked")
    public List<LexUnit> getLexUnits(String orthForm, WordCategory wordCategory,
            boolean considerMainOrthFormOnly) {
        List<LexUnit> rval = null;
        ArrayList<LexUnit> tmpList;
        HashMap<String, ArrayList<LexUnit>> map;
        String mapForm = orthForm;

        if (ignoreCase) {
            mapForm = orthForm.toLowerCase();
        }

        if (considerMainOrthFormOnly) {
            map = wordCategoryMap.get(wordCategory);
        } else {
            map = wordCategoryMapAllOrthForms.get(wordCategory);
        }

        if (map != null) {
            tmpList = map.get(mapForm);
            if (tmpList == null) {
                rval = new ArrayList<LexUnit>(0);
            } else {
                rval = (List<LexUnit>) tmpList.clone();
            }
        }
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code> in the specified
     * <code>wordCategory</code>.
     * @param wordCategory the <code>WordCategory</code>, (e.g.
     * <code>WordCategory.verben</code>)
     * @return a <code>List</code> of all <code>LexUnits</code> in the specified
     * <code>wordCategory</code>. If no <code>LexUnits</code> were found, this
     * is a <code>List</code> containing no <code>LexUnits</code>.
     */
    @SuppressWarnings("unchecked")
    public List<LexUnit> getLexUnits(WordCategory wordCategory) {
        ArrayList<LexUnit> rval = new ArrayList<LexUnit>();
        HashMap<String, ArrayList<LexUnit>> map;
        map = wordCategoryMap.get(wordCategory);

        for (ArrayList<LexUnit> luList : map.values()) {
            rval.addAll((ArrayList<LexUnit>) luList.clone());
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Returns a <code>List</code> of all <code>LexUnits</code>.
     * @return a <code>List</code> of all <code>LexUnits</code>
     */
    public List<LexUnit> getLexUnits() {
        ArrayList<LexUnit> rval = new ArrayList<LexUnit>();

        for (WordCategory wc : WordCategory.values()) {
            rval.addAll(getLexUnits(wc));
        }
        rval.trimToSize();
        return rval;
    }

    /**
     * Trims all <code>Lists</code> (takes ~0.3 seconds and frees up 2mb).
     */
    protected void trimAll() {
        // trim Synsets, which trim LexUnits
        for (Synset sset : synsets) {
            sset.trimAll();
        }

        // trim lists in wordCategoryMap
        HashMap<String, ArrayList<LexUnit>> map;
        for (WordCategory wc : WordCategory.values()) {
            map = wordCategoryMap.get(wc);
            for (ArrayList<LexUnit> luList : map.values()) {
                luList.trimToSize();
            }
        }
    }

    /**
     * Loads the ILI data files into this <code>GermaNet</code> object
     * from the specified directory File
     * @param path location of the ILI data files
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    private void loadIli(boolean zip) throws XMLStreamException {
        IliLoader loader;
        String oldVal = null;

        // use xerces xml parser
        oldVal = System.getProperty("javax.xml.stream.XMLInputFactory");
        System.setProperty("javax.xml.stream.XMLInputFactory",
                "com.sun.xml.internal.stream.XMLInputFactoryImpl");

        // load data
        try {
            loader = new IliLoader(this);
            if (zip) {
                InputStream iliStream = null;
                for (int i = 0; i < inputStreams.size(); i++) {
                    if (xmlNames.get(i).equals("interLingualIndex_DE-EN.xml")) {
                        iliStream = inputStreams.get(i);
                        break;
                    }
                }
                loader.loadILI(iliStream);
            }
            else {
                loader.loadILI(new File(dir + "/interLingualIndex_DE-EN.xml"));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GermaNet.class.getName()).log(Level.SEVERE, null, ex);
        }

        trimAll();

        // set parser back to whatever it was before
        if (oldVal != null) {
            System.setProperty("javax.xml.stream.XMLInputFactory", oldVal);
        }

        //add the information about corresponding IliRecords to LexUnits
        updateLexUnitsWithIli();
    }

    /**
     * Adds <code>IliRecords</code> to this <code>GermaNet</code>
     * object when IliLoader is called
     * @param ili the <code>IliRecord</code> to be added
     */
    protected void addIliRecord(IliRecord ili) {
        iliRecords.add(ili);
    }

    /**
     * Returns a <code>List</code> of all <code>IliRecords</code>.
     * @return a <code>List</code> of all <code>IliRecords</code>
     */
    @SuppressWarnings("unchecked")
    public List<IliRecord> getIliRecords() {
        return (List<IliRecord>) iliRecords.clone();
    }

    /**
     * Adds the information about corresponding <code>IliRecords</code>
     * to <code>LexUnits</code>
     */
    protected void updateLexUnitsWithIli() {
        for (IliRecord ili : iliRecords) {
            int id = ili.getLexUnitId();
            if (getLexUnitByID(id) != null) {
            LexUnit lu = getLexUnitByID(id);
            lu.addIliRecord(ili);
            lexUnitID.put(id, lu);}
        }
    }


    /**
     * Loads the Wiktionary data files into this <code>GermaNet</code> object
     * from the specified directory File
     * @param path location of the Wiktionary data files
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    private void loadWiktionaryParaphrases(boolean zip) throws XMLStreamException {
        WiktionaryLoader loader;
        String oldVal = null;

        // use xerces xml parser
        oldVal = System.getProperty("javax.xml.stream.XMLInputFactory");
        System.setProperty("javax.xml.stream.XMLInputFactory",
                "com.sun.xml.internal.stream.XMLInputFactoryImpl");

        // load data
        try {
            loader = new WiktionaryLoader(this);
            if (zip) {
                loader.loadWiktionary(inputStreams, xmlNames);
            }
            else {
                loader.loadWiktionary(dir);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GermaNet.class.getName()).log(Level.SEVERE, null, ex);
        }

        trimAll();

        // set parser back to whatever it was before
        if (oldVal != null) {
            System.setProperty("javax.xml.stream.XMLInputFactory", oldVal);
        }

        //add the information about corresponding WiktionaryParaphrases to LexUnits
        updateLexUnitsWithWiktionary();
    }

    /**
     * Adds <code>WiktionaryParaphrases</code> to this <code>GermaNet</code>
     * object when WiktionaryLoader is called
     * @param wiki the <code>WiktionaryParaphrase</code> to be added
     */
    protected void addWiktionaryParaphrase(WiktionaryParaphrase wiki) {
        wiktionaryParaphrases.add(wiki);
    }

    /**
     * Returns a <code>List</code> of all <code>WiktionaryParaphrases</code>.
     * @return a <code>List</code> of all <code>WiktionaryParaphrases</code>
     */
    @SuppressWarnings("unchecked")
    public List<WiktionaryParaphrase> getWiktionaryParaphrases() {
        return (List<WiktionaryParaphrase>) wiktionaryParaphrases.clone();
    }

    public HashMap<LexUnit, CompoundInfo> getLexUnitsWithCompoundInfo() {
        HashMap<LexUnit, CompoundInfo> lexUnitsWithCimpounds = new HashMap<LexUnit, CompoundInfo>();
        for (LexUnit lu : getLexUnits()) {
            if (lu.getCompoundInfo() != null) {
                lexUnitsWithCimpounds.put(lu, lu.getCompoundInfo());
            }
        }
        return lexUnitsWithCimpounds;
    }

    /**
     * Adds the information about corresponding <code>WiktionaryParaphrases</code>
     * to <code>LexUnits</code>
     */
    protected void updateLexUnitsWithWiktionary() {
        for (WiktionaryParaphrase wiki : wiktionaryParaphrases) {
            int id = wiki.getLexUnitId();
            LexUnit lu = getLexUnitByID(id);
            if (lu != null) { // TODO: this might get obsolete once we do a proper Wiktionary XML release
                lu.addWiktionaryParaphrase(wiki);
            }
            lexUnitID.put(id, lu);
        }
    }

    /**
     * Checks whether the <code>File</code> is a <code>ZipFile</code>.
     * @param file the <code>File</code> to check
     * @return true if this <code>File</code> is a <code>ZipFile</code>
     */
    protected static boolean isZipFile(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long n = raf.readInt();
        raf.close();
        if (n == 0x504B0304) {
            return true;
        } else {
            return false;
        }
    }
}
