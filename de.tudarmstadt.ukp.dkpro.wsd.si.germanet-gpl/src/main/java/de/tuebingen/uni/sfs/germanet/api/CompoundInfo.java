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
 * A <code>CompoundInfo</code> shows constituent parts of a German word, their
 * word categories and attributes.
 *
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 8.0
 */
public class CompoundInfo {
    private CompoundProperty modifierProperty;
    private String modifier1;
    private CompoundCategory modifier1Category;
    private String modifier2;
    private CompoundCategory modifier2Category;
    private String head;
    private CompoundProperty headProperty;

    /**
     * Constructs a <code>CompoundInfo</code> with the specified attributes.
     * @param modifierProperty
     * @param modifier1
     * @param modifier1Category
     * @param modifier2
     * @param modifier2Category
     * @param head
     * @param headProperty
     */
    public CompoundInfo (CompoundProperty modifierProperty,
            String modifier1, CompoundCategory modifier1Category,
            String modifier2, CompoundCategory modifier2Category,
            String head, CompoundProperty headProperty) {
        this.modifierProperty = modifierProperty;
        this.modifier1 = modifier1;
        this.modifier1Category = modifier1Category;
        this.modifier2 = modifier2;
        this.modifier2Category = modifier2Category;
        this.head = head;
        this.headProperty = headProperty;
    }

    /**
     * Returns the <code>CompoundProperty</code> of the modifier
     * or null if it has not been set
     * @return the <code>CompoundProperty</code> of the modifier
     */
    public CompoundProperty getModifierProperty() {
        return this.modifierProperty;
    }

    /**
     * Returns the first modifier of the compound
     * @return the first modifier of the compound
     */
    public String getModifier1() {
        return this.modifier1;
    }

    /**
     * Returns the <code>CompoundCategory</code> of the first modifier
     * or null if it has not been set
     * @return the <code>CompoundCategory</code> of the first modifier
     */
    public CompoundCategory getModifier1Category() {
        return this.modifier1Category;
    }

    /**
     * Returns the second, alternative modifier of the compound
     * or null if it has not been set
     * @return the second modifier of the compound
     */
    public String getModifier2() {
        return this.modifier2;
    }

    /**
     * Returns the <code>CompoundCategory</code> of the second modifier
     * or null if it has not been set
     * @return the <code>CompoundCategory</code> of the second modifier
     */
    public CompoundCategory getModifier2Category() {
        return this.modifier2Category;
    }

    /**
     * Returns the head of the compound
     * @return the head of the compound
     */
    public String getHead() {
        return this.head;
    }

    /**
     * Returns the <code>CompoundProperty</code> of the head
     * or null if it has not been set
     * @return the <code>CompoundProperty</code> of the head
     */
    public CompoundProperty getHeadProperty() {
        return this.headProperty;
    }

    /**
     * Return a String representation of the compound.
     * @return a String representation of the compound.
     */
    @Override
    public String toString() {
        String compAsString = "";

        if (this.modifierProperty != null) compAsString += "<" + this.modifierProperty + "> ";
        compAsString += this.modifier1;
        if (this.modifier1Category != null) compAsString += " (" + this.modifier1Category + ")";

        if (this.modifier2 != null) {
            compAsString += " / " + this.modifier2;
            if (this.modifier2Category != null) compAsString += " (" + this.modifier2Category + ")";
        }

        compAsString += " + ";
        if (this.headProperty != null) compAsString += "<" + this.headProperty + "> ";
        compAsString += this.head;

        return compAsString;
    }

}
