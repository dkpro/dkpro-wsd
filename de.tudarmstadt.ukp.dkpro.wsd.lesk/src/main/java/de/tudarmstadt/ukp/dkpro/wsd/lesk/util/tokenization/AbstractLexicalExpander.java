/*******************************************************************************
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization;

import java.util.Arrays;

/**
 * A TokenizationStrategy for use with the Lesk family of algorithms which
 * removes stop words and then applies lexical expansion to the remaining
 * tokens.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 *
 */
public abstract class AbstractLexicalExpander
    implements TokenizationStrategy
{
    protected Expansion expansion;

    protected String filter = null;
    protected String filterPOS = null;

    /**
     * A class which stores the number of lexical expansions to use for
     * adjectives, nouns, adverbs, verbs, and words of other parts of speech
     *
     * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
     *
     */
    public static class Expansion
    {
        public int a, n, r, v, o;

        @SuppressWarnings("unused")
        private Expansion()
        {
        }

        public Expansion(int a, int n, int r, int v, int o)
        {
            this.a = a;
            this.n = n;
            this.v = v;
            this.r = r;
            this.o = o;
        }

        public Expansion(int[] pos)
        {
            if (pos.length != 5) {
                throw new IllegalArgumentException("Cannot parse array: " + Arrays.toString(pos));
            }
            this.a = pos[0];
            this.n = pos[1];
            this.v = pos[2];
            this.r = pos[3];
            this.o = pos[4];
        }

        public Expansion(String s)
        {
            String[] t = s.split(" ");
            if (t.length != 5) {
                throw new IllegalArgumentException("Cannot parse string: " + s);
            }
            a = Integer.parseInt(t[0]);
            n = Integer.parseInt(t[1]);
            r = Integer.parseInt(t[2]);
            v = Integer.parseInt(t[3]);
            o = Integer.parseInt(t[4]);
        }

        public int[] toArray()
        {
            return new int[] { a, n, r, v, o };
        }

        @Override
        public String toString()
        {
            return a + " " + n + " " + r + " " + v + " " + o;
        }
    }

    /**
     * Set the POS-specific expansion strategy for this tokenizer
     *
     * @param expansion
     */
    public void setNumberOfExpansions(Expansion expansion)
    {
        this.expansion = expansion;
    }

    /**
     * Set a filter such that the tokenizer removes all occurrences of this
     * string from its output
     *
     * @param filter
     */
    abstract public void setFilter(String filter);

}
