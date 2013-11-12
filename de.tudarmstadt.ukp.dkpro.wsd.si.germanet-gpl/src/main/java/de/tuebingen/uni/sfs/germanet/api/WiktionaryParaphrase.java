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

/**
 * A <code>WiktionaryParaphrase</code> consists of the Wiktionary sense
 * definition (represented as a String), the Wiktionary word and sense ids,
 * the corresponding GermaNet LexUnit id, and an attribute edited (boolean).
 *
 * Methods are provided to get each of the attributes.<br><br>
 *
 * The Wiktionary sense definition can be retrieved:<br>
 * <code>
 * &nbsp;&nbsp;&nbsp;String sense = aWiktionaryParaphrase.getWiktionarySense();<br><br>
 * </code>
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 8.0
 */
public class WiktionaryParaphrase {

    private int lexUnitId;
    private int wiktionaryId;
    private int wiktionarySenseId;
    private String wiktionarySense;
    private boolean edited;

    /**
     * Constructs a <code>WiktionaryParaphrase</code> with the specified attributes.
     * @param lexUnitId the identifier of the <code>LexUnit</code>
     * @param wiktionaryId unique identifier
     * @param wiktionarySenseId the identifier of the Wiktionary sense
     * @param wiktionarySense the Wiktionary sense definition
     * @param edited boolean attribute
     */
    WiktionaryParaphrase(int lexUnitId, int wiktionaryId, int wiktionarySenseId,
            String wiktionarySense, boolean edited) {
        this.lexUnitId = lexUnitId;
        this.wiktionaryId = wiktionaryId;
        this.wiktionarySenseId = wiktionarySenseId;
        this.wiktionarySense = wiktionarySense;
        this.edited = edited;
    }
    
    /**
     * Returns the identifier of the <code>LexUnit</code>
     * corresponding to this <code>WiktionaryParaphrase</code>.
     * @return the identifier of the <code>LexUnit</code>
     */
    public int getLexUnitId() {
        return lexUnitId;
    }

    /**
     * Returns the unique identifier of this <code>WiktionaryParaphrase</code>.
     * @return the unique identifier of this <code>WiktionaryParaphrase</code>
     */
    public int getWiktionaryId() {
        return wiktionaryId;
    }

    /**
     * Returns the unique identifier of the Wiktionary sense.
     * @return the unique identifier of the Wiktionary sense
     */
    public int getWiktionarySenseId() {
        return wiktionarySenseId;
    }

    /**
     * Returns the Wiktionary sense definition.
     * @return the Wiktionary sense definition
     */
    public String getWiktionarySense() {
        return wiktionarySense;
    }

    /**
     * Returns true if the <code>WiktionaryParaphrase</code> was edited,
     * false otherwise.
     * @return true if the <code>WiktionaryParaphrase</code> was edited,
     * false otherwise
     */
    public boolean hasBeenEdited() {
        return edited;
    }

    /**
     * Returns a <code>String</code> representation of this <code>WiktionaryParaphrase</code>.
     * @return a <code>String</code> representation of this <code>WiktionaryParaphrase</code>
     */
    @Override
    public String toString() {
        String sense = this.wiktionarySense.replaceAll("&quot;", "\"");
        sense = sense.replaceAll("&lt;", "<");
        sense = sense.replaceAll("&gt;", ">");
        sense = sense.replaceAll("&amp;", "&");
        String stringIli = "LexUnit ID: " + this.lexUnitId + ", Wiktionary ID: " +
                this.wiktionaryId + ", Wiktionary sense definition: " + sense +
                ", edited: " + this.edited;
        return stringIli;
    }
}
