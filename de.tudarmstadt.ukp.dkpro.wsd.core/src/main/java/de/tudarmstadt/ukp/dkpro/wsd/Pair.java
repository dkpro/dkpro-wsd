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
 * A class for generic ordered pairs.
 *
 * @param <A>
 *            the type of the first member
 * @param <B>
 *            the type of the second member
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
@SuppressWarnings("serial")
public class Pair<A, B>
    implements java.io.Serializable
{
    protected final A first;
    protected final B second;

    /**
     * Creates an ordered pair with the specified members
     *
     * @param first
     *            the first member
     * @param second
     *            the second member
     */
    public Pair(A first, B second)
    {
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first member of the pair
     */
    public A getFirst()
    {
        return first;
    }

    /**
     * @return the second member of the pair
     */
    public B getSecond()
    {
        return second;
    }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Pair<?, ?>)) {
            return false;
        }
        Pair<?, ?> otherPair = (Pair<?, ?>) other;
        return equals(first, otherPair.getFirst())
                && equals(second, otherPair.getSecond());
    }

    /**
     * Compares two objects for equality taking care of null references
     *
     * @param x
     *            an object
     * @param y
     *            an object
     * @return returns true when <code>x</code> equals <code>y</code> or if both
     *         are null
     */
    protected final boolean equals(Object x, Object y)
    {
        return x == null && y == null || x != null && x.equals(y);
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
            return first.hashCode() + 2;
        }

        return 17 * first.hashCode() + second.hashCode();
    }
}
