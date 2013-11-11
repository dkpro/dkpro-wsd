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

import java.util.*;

/**
 * An <code>IliRecord</code> consists of a German word, corresponding English word
 * (or words), and a relation connecting them.
 *
 * Methods are provided to get each of the attributes.<br><br>
 *
 * Other variables include IDs from Princeton WordNet (PWN) 2.0 and 3.0, as well as
 * English paraphrases from PWN 2.0.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 8.0
 */
public class IliRecord {

    private int lexUnitId;
    private EwnRel ewnRelation;
    private String pwnWord;
    private String pwn20Id;
    private String pwn30Id;
    private String pwn20paraphrase = "";
    private String source;
    private ArrayList<String> englishSynonyms;

    /**
     * Constructs an <code>IliRecord</code> with the specified attributes.
     * @param lexUnitId the identifier of the <code>LexUnit</code>
     * @param gnWord the orthographic form of this <code>IliRecord</code>
     * @param ewnRelation the EuroWordNet cross-language relation
     * @param pwnWord the corresponding English word
     * @param pwn20Sense the sense number of the corresponding English word from PWN 2.0
     * @param pwn20Id the identifier of the corresponding English word from PWN 2.0
     * @param pwn30Id the identifier of the corresponding English word from PWN 3.0
     * @param pwn20paraphrase the paraphrase for the corresponding English word from PWN 2.0
     * @param source the source of this <code>IliRecord</code>
     */
    IliRecord(int lexUnitId, EwnRel ewnRelation,
            String pwnWord, String pwn20Id, String pwn30Id, String pwn20paraphrase, String source) {
        this.lexUnitId = lexUnitId;
        this.ewnRelation = ewnRelation;
        this.pwnWord = pwnWord;
        this.pwn20Id = pwn20Id;
        this.pwn30Id = pwn30Id;
        if (pwn20paraphrase != null) this.pwn20paraphrase = pwn20paraphrase;
        this.source = source;
        this.englishSynonyms = new ArrayList<String>();
    }

    /**
     * Adds an English synonym to this <code>IliRecord</code>.
     * @param synonym the English synonym to add
     */
    protected void addEnglishSynonym(String synonym) {
        this.englishSynonyms.add(synonym);
    }

    
    /**
     * Returns the identifier of the <code>LexUnit</code>
     * corresponding to this <code>IliRecord</code>.
     * @return the identifier of the <code>LexUnit</code>
     */
    public int getLexUnitId() {
        return lexUnitId;
    }

    /**
     * Returns the EuroWordNet cross-language relation of this <code>IliRecord</code>.
     * @return the EuroWordNet cross-language relation of this <code>IliRecord</code>
     */
    public EwnRel getEwnRelation() {
        return ewnRelation;
    }

    /**
     * Returns the corresponding English word from PWN 2.0 (can be null,
     * in which case see<code>getEnglishSynonyms()</code>).
     * @return the corresponding English word from PWN 2.0
     */
    public String getPwnWord() {
        return pwnWord;
    }

    /**
     * Returns the identifier of the corresponding English word from PWN 2.0.
     * @return the identifier of the corresponding English word from PWN 2.0
     */
    public String getPwn20Id() {
        return pwn20Id;
    }

    /**
     * Returns the identifier of the corresponding English word from PWN 3.0.
     * @return the identifier of the corresponding English word from PWN 3.0
     */
    public String getPwn30Id() {
        return pwn30Id;
    }

    /**
     * Returns the paraphrase for this word from PWN 2.0.
     * @return the paraphrase for this word from PWN 2.0
     */
    public String getPwn20paraphrase() {
        return pwn20paraphrase;
    }

    /**
     * Returns the source of the <code>IliRecord</code>.
     * @return the source of the <code>IliRecord</code>
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns a <code>List</code> of this <code>IliRecord</code>'s
     * English synonyms from PWN 2.0.
     * @return a <code>List</code> of this <code>IliRecord</code>'s
     * English synonyms from PWN 2.0
     */
    public List<String> getEnglishSynonyms() {
        return (List<String>) this.englishSynonyms.clone();
    }
    
    /**
     * Returns a <code>String</code> representation of this <code>IliRecord</code>.
     * @return a <code>String</code> representation of this <code>IliRecord</code>
     */
    @Override
    public String toString() {
        String stringIli = "LexUnit ID: " + this.lexUnitId +
                ", EWN relation: " + this.ewnRelation;
        if (this.pwnWord != null) {
            stringIli += ", PWN word: " + this.pwnWord;
        }
        stringIli += ", PWN 2.0 ID: " + this.pwn20Id +
                ", PWN 3.0 ID: " + this.pwn30Id +
                ", source: " + this.source;
        if (englishSynonyms.size() > 0) {
            stringIli += "\nEnglish synonyms from PWN 2.0: ";
            for (String synonym : englishSynonyms)
                stringIli += synonym + ", ";
            stringIli = stringIli.substring(0, stringIli.length() - 2);
        }
        if (pwn20paraphrase.length() > 0)
            stringIli += "\nEnglish paraphrase from PWN 2.0: " + pwn20paraphrase;
        return stringIli;
    }
}

