/*
 * Copyright (C) 2012 Department of General and Computational Linguistics,
 * University of Tuebingen
 *
 * This file is part of the Java API to GermaNet.
 *
 * The Java API to GermaNet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java API to GermaNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this API; if not, see <http://www.gnu.org/licenses/>.
 */
package de.tuebingen.uni.sfs.germanet.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Load lex and con relations into a specified GermaNet object.
 * 
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 8.0
 */
class RelationLoader {
    static final String DIR_BOTH = "both";
    static final String DIR_REVERT = "revert";

    private GermaNet germaNet;
    private String namespace;

    /**
     * Constructs a <code>RelationLoader</code> for the specified
     * <code>GermaNet</code> object.
     * @param germaNet the <code>GermaNet</code> object to load the relations
     * into
     */
    protected RelationLoader(GermaNet germaNet) {
        this.germaNet = germaNet;
    }

    /**
     * Loads relations from the specified file into this
     * <code>RelationLoader</code>'s <code>GermaNet</code> object.
     * @param relationFile file containing GermaNet relation data
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    void loadRelations(File relationFile) throws FileNotFoundException,
            XMLStreamException {
        InputStream in = new FileInputStream(relationFile);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(in);
        int event;
        String nodeName;

        //Parse entire file, looking for lex- and con- relations
        while (parser.hasNext()) {
            event = parser.getEventType();
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    namespace = parser.getNamespaceURI();
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_LEX_REL)) {
                        processLexRel(parser);
                    } else if (nodeName.equals(GermaNet.XML_CON_REL)) {
                        processConRel(parser);
                    }
                    break;
            }
            parser.next();
        }
        parser.close();
    }

    /**
     * Loads relations from the specified file into this
     * <code>RelationLoader</code>'s <code>GermaNet</code> object.
     * @param relationFile file containing GermaNet relation data
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.stream.XMLStreamException
     */
    void loadRelations(InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(inputStream);
        int event;
        String nodeName;

        /*
         * Parse entire file, looking for lex- and con- relations
         */
        while (parser.hasNext()) {
            event = parser.getEventType();
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    namespace = parser.getNamespaceURI();
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    nodeName = parser.getLocalName();
                    if (nodeName.equals(GermaNet.XML_LEX_REL)) {
                        processLexRel(parser);
                    } else if (nodeName.equals(GermaNet.XML_CON_REL)) {
                        processConRel(parser);
                    }
                    break;
            }
            parser.next();
        }
        parser.close();
    }

    /**
     * Processes the lexical relation for which the start tag was
     * just encountered.
     * @param parser the <code>XMLStreamParser</code> to get the attributes from
     */
    private void processLexRel(XMLStreamReader parser) {
        String name, direction;
        int fromLexUnitId, toLexUnitId;
        LexUnit fromLexUnit, toLexUnit;
        LexRel invRel;

        // get all the attributes
        name = parser.getAttributeValue(namespace, GermaNet.XML_RELATION_NAME);
        direction = parser.getAttributeValue(namespace, GermaNet.XML_RELATION_DIR);
        fromLexUnitId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_FROM).substring(1));
        toLexUnitId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_TO).substring(1));

        // look up the LexUnits
        fromLexUnit = germaNet.getLexUnitByID(fromLexUnitId);
        toLexUnit = germaNet.getLexUnitByID(toLexUnitId);

        // add relation from "from" to "to"
        fromLexUnit.addRelation(LexRel.valueOf(name), toLexUnit);

        // add the inverse relation, if any
        if (direction.equals(DIR_BOTH)) {
            toLexUnit.addRelation(LexRel.valueOf(name), fromLexUnit);
        } else if (direction.equals(DIR_REVERT)) {
            invRel = LexRel.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_INV));
            toLexUnit.addRelation(invRel, fromLexUnit);
        }
    }

    /**
     * Processes the conceptual relation for which the start tag was
     * just encountered.
     * @param parser the <code>XMLStreamReader</code> to get the attributes from
     */
    private void processConRel(XMLStreamReader parser) {
        String name, direction;
        int fromSynsetId, toSynsetId;
        Synset fromSynset, toSynset;
        ConRel invRel;

        // get all the attributes
        name = parser.getAttributeValue(namespace, GermaNet.XML_RELATION_NAME);
        direction = parser.getAttributeValue(namespace, GermaNet.XML_RELATION_DIR);
        fromSynsetId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_FROM).substring(1));
        toSynsetId = Integer.valueOf(parser.getAttributeValue(namespace, GermaNet.XML_RELATION_TO).substring(1));

        // look up the Synsets
        fromSynset = germaNet.getSynsetByID(fromSynsetId);
        toSynset = germaNet.getSynsetByID(toSynsetId);

        // add relation from "from" to "to"
        fromSynset.addRelation(ConRel.valueOf(name), toSynset);

        // add the inverse relation, if any
        if (direction.equals(DIR_BOTH)) {
            toSynset.addRelation(ConRel.valueOf(name), fromSynset);
        } else if (direction.equals(DIR_REVERT)) {
            invRel = ConRel.valueOf(
                    parser.getAttributeValue(namespace, GermaNet.XML_RELATION_INV));
            toSynset.addRelation(invRel, fromSynset);
        }
    }
}
