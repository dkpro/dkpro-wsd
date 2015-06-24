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
 * Enumeration of all conceptual relations.
 * 
 * @author University of Tuebingen, Department of Linguistics (germanetinfo at uni-tuebingen.de)
 * @version 8.0
 */
public enum ConRel {
    has_hypernym(true),
    has_hyponym(true),
    has_component_meronym(true),
    has_component_holonym(true),
    has_member_meronym(true),
    has_member_holonym(true),
    has_substance_meronym(true),
    has_substance_holonym(true),
    has_portion_meronym(true),
    has_portion_holonym(true),
    entails(false),
    is_entailed_by(false),
    is_related_to(false),
    causes(false);
    private boolean transitive;

    private ConRel(boolean transitive) {
        this.transitive = transitive;
    }

    /**
     * Returns true if the <code>String</code> <code>relName</code> represents a
     * valid <code>ConRel</code>.
     * @param relName the name of the relation to verify
     * @return true if the <code>String relName</code> represents a valid
     * <code>ConRel</code>
     */
    public static boolean isConRel(String relName) {
        ConRel[] vals = values();

        for (int i = 0; i < vals.length; i++) {
            if (vals[i].toString().equals(relName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if this is a transitive relationship.
     * @return true if this is a transitive relationship.
     */
    public boolean isTransitive() {
        return transitive;
    }
}
