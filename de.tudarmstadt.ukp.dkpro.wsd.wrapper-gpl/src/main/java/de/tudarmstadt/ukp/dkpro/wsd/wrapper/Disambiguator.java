/**
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
package de.tudarmstadt.ukp.dkpro.wsd.wrapper;

import java.io.IOException;
import java.util.List;

/**
 * A disambiguator that returns senses for an input text
 * @author nico.erbs@gmail.com
 *
 */
public interface Disambiguator {

    /**
     * @param inputText The input text.
     *
     * @return
     *   The list of string with the senses disambiguated in the input text.
     */
	List<String> disambiguate(String inputText) throws IOException;

    /**
     * @return The name of the disambiguator.
     */
	String getName();

    /**
     * @return Returns a string with the configuration details of this word sense disambiguator.
     */
	String getConfigurationDetails();

}
