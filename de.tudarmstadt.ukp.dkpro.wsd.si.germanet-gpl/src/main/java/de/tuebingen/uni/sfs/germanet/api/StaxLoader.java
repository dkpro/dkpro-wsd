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
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Stax loader for GermaNet xml files. All Synsets must be loaded before
 * any relations can be loaded.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 8.0
*/
class StaxLoader {

    private final Log logger = LogFactory.getLog(getClass());

    private final File germaNetDir;
    private final List<InputStream> germaNetStreams;
    private final SynsetLoader synLoader;  // loader for synsets
    private final RelationLoader relLoader; // loader for relations
    private final List<String> xmlNames;

    /**
     * Constructs a <code>StaxLoader</code> for data files in directory
     * <code>germanetDirectory</code> and existing <code>GermaNet</code> object
     * <code>germaNet</code>.
     * @param germanetDirectory location of GermaNet data files
     * @param germaNet <code>GermaNet</code> object to load into
     * @throws java.io.FileNotFoundException
     */
    protected StaxLoader(File germaNetDir, GermaNet germaNet) throws
            FileNotFoundException {
        this.germaNetDir = germaNetDir;
        this.germaNetStreams = null;
        this.synLoader = new SynsetLoader(germaNet);
        this.relLoader = new RelationLoader(germaNet);
        this.xmlNames = null;

        if (!germaNetDir.isDirectory()) {
            throw new FileNotFoundException("Unable to load GermaNet from \"" +
                    germaNetDir + "\"");
        }
    }

    /**
     * Constructs a <code>StaxLoader</code> for data streams in
     * <code>germanetStreams</code> and existing <code>GermaNet</code> object
     * <code>germaNet</code>.
     * @param germanetDirectory location of GermaNet data files
     * @param germaNet <code>GermaNet</code> object to load into
     * @throws java.io.FileNotFoundException
     */
    protected StaxLoader(List<InputStream> germaNetStreams, List<String> xmlNames, GermaNet germaNet) throws
            StreamCorruptedException {
        this.germaNetStreams = germaNetStreams;
        this.germaNetDir = null;
        this.synLoader = new SynsetLoader(germaNet);
        this.relLoader = new RelationLoader(germaNet);
        this.xmlNames = xmlNames;

        if (germaNetStreams.isEmpty()) {
            throw new StreamCorruptedException("Unable to load GermaNet from \"" +
                    germaNetStreams + "\"");
        }
    }

    /**
     * Loads all synset files or streams (depending on what exists) and then all relation files.
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void load() throws XMLStreamException {
        if (this.germaNetDir != null) { // load GermaNet from file
            FilenameFilter filter = new SynsetFilter(); //get only synset files

            File[] germaNetFiles = germaNetDir.listFiles(filter);

            try {
                if (germaNetFiles == null || germaNetFiles.length == 0) {
                    throw new FileNotFoundException("Unable to load GermaNet from \"" + this.germaNetDir.getPath() + "\"");
                }

                // load all synset files first with a SynsetLoader
                for (File germaNetFile : germaNetFiles) {
                    logger.debug("Loading " +
                            germaNetFile.getName() + "...");
                    synLoader.loadSynsets(germaNetFile);
                }

                filter = new RelationFilter(); //get only relatin files

                germaNetFiles = germaNetDir.listFiles(filter);

                // load relations with a RelationLoader
                for (File germaNetFile : germaNetFiles) {
                    logger.debug("Loading " +
                            germaNetFile.getName() + "...");
                    relLoader.loadRelations(germaNetFile);
                }
                logger.debug("Done.");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(StaxLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else { // load GermaNet from InputStream list
            if (germaNetStreams == null || germaNetStreams.isEmpty()) {
                try {
                    throw new StreamCorruptedException("Unable to load GermaNet from input stream \"" + this.germaNetStreams.toString() + "\"");
                } catch (StreamCorruptedException ex) {
                    Logger.getLogger(StaxLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // load all synset input streams first with a SynsetLoader
            for (int i = 0; i < germaNetStreams.size(); i++) {

                if (xmlNames.get(i).endsWith("xml")
                        && (xmlNames.get(i).startsWith("nomen")
                        || xmlNames.get(i).startsWith("verben")
                        || xmlNames.get(i).startsWith("adj"))) {
                    logger.debug("Loading input stream "
                            + xmlNames.get(i) + "...");
                    synLoader.loadSynsets(germaNetStreams.get(i));
                }
            }

            // load relations with a RelationLoader
            for (int i = 0; i < germaNetStreams.size(); i++) {
                if (xmlNames.get(i).equals("gn_relations.xml")) {
                    logger.debug("Loading input stream "
                            + xmlNames.get(i) + "...");
                    relLoader.loadRelations(germaNetStreams.get(i));
                }
            }
            logger.debug("Done.");
        }
    }

    /**
     * Filters out synset files by name.
     */
    private class SynsetFilter implements FilenameFilter {
        @Override
        public boolean accept(File directory, String name) {
            return (name.endsWith("xml") &&
                    (name.startsWith("nomen") ||
                    name.startsWith("verben") ||
                    name.startsWith("adj")));
        }
    }

    /**
     * Filters out relation files by name.
     */
    private class RelationFilter implements FilenameFilter {
        @Override
        public boolean accept(File directory, String name) {
            return name.equals("gn_relations.xml");
        }
    }
}
