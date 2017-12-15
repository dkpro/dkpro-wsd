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

package de.tudarmstadt.ukp.dkpro.wsd;

/**
 * A class for generic unordered pairs
 *
 * @param <A>
 *            Type of both members
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
@SuppressWarnings("serial")
public class UnorderedPair<A>
    extends Pair<A, A>
{
    /**
     * Creates an unordered pair with the specified members.
     *
     * @param first
     *            the first member
     * @param second
     *            the second member
     */
    public UnorderedPair(A first, A second)
    {
        super(first, second);
    }

    @Override
    public String toString()
    {
        return "{" + first + ", " + second + "}";
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof UnorderedPair<?>)) {
            return false;
        }
        UnorderedPair<?> otherPair = (UnorderedPair<?>) other;
        return equals(first, otherPair.getFirst())
                && equals(second, otherPair.getSecond())
                || equals(first, otherPair.getSecond())
                && equals(second, otherPair.getFirst());
    }

    @Override
    public int hashCode()
    {
        if (first == null) {
            if (second == null) {
                return 0;
            }
            return second.hashCode() + 1;
        }

        if (second == null) {
            return first.hashCode() + 1;
        }

        return first.hashCode() ^ second.hashCode();
    }
}
