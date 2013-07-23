/*******************************************************************************
 * IMS (It Makes Sense) -- NUS WSD System
 * Copyright (c) 2013 National University of Singapore.
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
 *
 ******************************************************************************/

package sg.edu.nus.comp.nlp.ims.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * filter stop words and words that contains no alphabet.
 *
 * @author zhongzhi
 *
 */
public class CSurroundingWordFilter {

	// default surrounding word filter
	protected static CSurroundingWordFilter DEFAULT = new CSurroundingWordFilter();

	// pattern to check alphabet
	protected static Pattern ALPHABET = Pattern.compile("[a-zA-Z]");

	// stop words list
	protected HashSet<String> m_StopWords = new HashSet<String>();

	/**
	 * constructor
	 */
	protected CSurroundingWordFilter() {
		this.m_StopWords.add("a");
		this.m_StopWords.add("about");
		this.m_StopWords.add("above");
		this.m_StopWords.add("across");
		this.m_StopWords.add("after");
		this.m_StopWords.add("afterwards");
		this.m_StopWords.add("again");
		this.m_StopWords.add("against");
		this.m_StopWords.add("albeit");
		this.m_StopWords.add("all");
		this.m_StopWords.add("almost");
		this.m_StopWords.add("alone");
		this.m_StopWords.add("along");
		this.m_StopWords.add("already");
		this.m_StopWords.add("also");
		this.m_StopWords.add("although");
		this.m_StopWords.add("always");
		this.m_StopWords.add("among");
		this.m_StopWords.add("amongst");
		this.m_StopWords.add("an");
		this.m_StopWords.add("and");
		this.m_StopWords.add("another");
		this.m_StopWords.add("any");
		this.m_StopWords.add("anyhow");
		this.m_StopWords.add("anyone");
		this.m_StopWords.add("anything");
		this.m_StopWords.add("anywhere");
		this.m_StopWords.add("are");
		this.m_StopWords.add("around");
		this.m_StopWords.add("as");
		this.m_StopWords.add("at");
		this.m_StopWords.add("b");
		this.m_StopWords.add("be");
		this.m_StopWords.add("became");
		this.m_StopWords.add("because");
		this.m_StopWords.add("become");
		this.m_StopWords.add("becomes");
		this.m_StopWords.add("becoming");
		this.m_StopWords.add("been");
		this.m_StopWords.add("before");
		this.m_StopWords.add("beforehand");
		this.m_StopWords.add("behind");
		this.m_StopWords.add("being");
		this.m_StopWords.add("below");
		this.m_StopWords.add("beside");
		this.m_StopWords.add("besides");
		this.m_StopWords.add("between");
		this.m_StopWords.add("beyond");
		this.m_StopWords.add("both");
		this.m_StopWords.add("but");
		this.m_StopWords.add("by");
		this.m_StopWords.add("c");
		this.m_StopWords.add("can");
		this.m_StopWords.add("cannot");
		this.m_StopWords.add("co");
		this.m_StopWords.add("could");
		this.m_StopWords.add("d");
		this.m_StopWords.add("down");
		this.m_StopWords.add("during");
		this.m_StopWords.add("e");
		this.m_StopWords.add("each");
		this.m_StopWords.add("eg");
		this.m_StopWords.add("either");
		this.m_StopWords.add("else");
		this.m_StopWords.add("elsewhere");
		this.m_StopWords.add("enough");
		this.m_StopWords.add("etc");
		this.m_StopWords.add("even");
		this.m_StopWords.add("ever");
		this.m_StopWords.add("every");
		this.m_StopWords.add("everyone");
		this.m_StopWords.add("everything");
		this.m_StopWords.add("everywhere");
		this.m_StopWords.add("except");
		this.m_StopWords.add("f");
		this.m_StopWords.add("few");
		this.m_StopWords.add("for");
		this.m_StopWords.add("former");
		this.m_StopWords.add("formerly");
		this.m_StopWords.add("from");
		this.m_StopWords.add("further");
		this.m_StopWords.add("g");
		this.m_StopWords.add("h");
		this.m_StopWords.add("had");
		this.m_StopWords.add("has");
		this.m_StopWords.add("have");
		this.m_StopWords.add("he");
		this.m_StopWords.add("hence");
		this.m_StopWords.add("her");
		this.m_StopWords.add("here");
		this.m_StopWords.add("hereafter");
		this.m_StopWords.add("hereby");
		this.m_StopWords.add("herein");
		this.m_StopWords.add("hereupon");
		this.m_StopWords.add("hers");
		this.m_StopWords.add("herself");
		this.m_StopWords.add("him");
		this.m_StopWords.add("himself");
		this.m_StopWords.add("his");
		this.m_StopWords.add("how");
		this.m_StopWords.add("however");
		this.m_StopWords.add("i");
		this.m_StopWords.add("ie");
		this.m_StopWords.add("if");
		this.m_StopWords.add("in");
		this.m_StopWords.add("inc");
		this.m_StopWords.add("indeed");
		this.m_StopWords.add("into");
		this.m_StopWords.add("is");
		this.m_StopWords.add("it");
		this.m_StopWords.add("its");
		this.m_StopWords.add("itself");
		this.m_StopWords.add("j");
		this.m_StopWords.add("k");
		this.m_StopWords.add("l");
		this.m_StopWords.add("latter");
		this.m_StopWords.add("latterly");
		this.m_StopWords.add("least");
		this.m_StopWords.add("less");
		this.m_StopWords.add("ltd");
		this.m_StopWords.add("m");
		this.m_StopWords.add("many");
		this.m_StopWords.add("may");
		this.m_StopWords.add("me");
		this.m_StopWords.add("meanwhile");
		this.m_StopWords.add("might");
		this.m_StopWords.add("more");
		this.m_StopWords.add("moreover");
		this.m_StopWords.add("most");
		this.m_StopWords.add("mostly");
		this.m_StopWords.add("much");
		this.m_StopWords.add("must");
		this.m_StopWords.add("my");
		this.m_StopWords.add("myself");
		this.m_StopWords.add("n");
		this.m_StopWords.add("namely");
		this.m_StopWords.add("neither");
		this.m_StopWords.add("never");
		this.m_StopWords.add("nevertheless");
		this.m_StopWords.add("next");
		this.m_StopWords.add("no");
		this.m_StopWords.add("nobody");
		this.m_StopWords.add("none");
		this.m_StopWords.add("noone");
		this.m_StopWords.add("nor");
		this.m_StopWords.add("not");
		this.m_StopWords.add("nothing");
		this.m_StopWords.add("now");
		this.m_StopWords.add("nowhere");
		this.m_StopWords.add("o");
		this.m_StopWords.add("of");
		this.m_StopWords.add("off");
		this.m_StopWords.add("often");
		this.m_StopWords.add("on");
		this.m_StopWords.add("once");
		this.m_StopWords.add("one");
		this.m_StopWords.add("only");
		this.m_StopWords.add("onto");
		this.m_StopWords.add("or");
		this.m_StopWords.add("other");
		this.m_StopWords.add("others");
		this.m_StopWords.add("otherwise");
		this.m_StopWords.add("our");
		this.m_StopWords.add("ours");
		this.m_StopWords.add("ourselves");
		this.m_StopWords.add("out");
		this.m_StopWords.add("over");
		this.m_StopWords.add("own");
		this.m_StopWords.add("p");
		this.m_StopWords.add("per");
		this.m_StopWords.add("perhaps");
		this.m_StopWords.add("q");
		this.m_StopWords.add("r");
		this.m_StopWords.add("rather");
		this.m_StopWords.add("s");
		this.m_StopWords.add("same");
		this.m_StopWords.add("seem");
		this.m_StopWords.add("seemed");
		this.m_StopWords.add("seeming");
		this.m_StopWords.add("seems");
		this.m_StopWords.add("several");
		this.m_StopWords.add("she");
		this.m_StopWords.add("should");
		this.m_StopWords.add("since");
		this.m_StopWords.add("so");
		this.m_StopWords.add("some");
		this.m_StopWords.add("somehow");
		this.m_StopWords.add("someone");
		this.m_StopWords.add("something");
		this.m_StopWords.add("sometime");
		this.m_StopWords.add("sometimes");
		this.m_StopWords.add("somewhere");
		this.m_StopWords.add("still");
		this.m_StopWords.add("such");
		this.m_StopWords.add("t");
		this.m_StopWords.add("than");
		this.m_StopWords.add("that");
		this.m_StopWords.add("the");
		this.m_StopWords.add("their");
		this.m_StopWords.add("them");
		this.m_StopWords.add("themselves");
		this.m_StopWords.add("then");
		this.m_StopWords.add("thence");
		this.m_StopWords.add("there");
		this.m_StopWords.add("thereafter");
		this.m_StopWords.add("thereby");
		this.m_StopWords.add("therefore");
		this.m_StopWords.add("therein");
		this.m_StopWords.add("thereupon");
		this.m_StopWords.add("these");
		this.m_StopWords.add("they");
		this.m_StopWords.add("this");
		this.m_StopWords.add("those");
		this.m_StopWords.add("though");
		this.m_StopWords.add("through");
		this.m_StopWords.add("throughout");
		this.m_StopWords.add("thru");
		this.m_StopWords.add("thus");
		this.m_StopWords.add("to");
		this.m_StopWords.add("together");
		this.m_StopWords.add("too");
		this.m_StopWords.add("toward");
		this.m_StopWords.add("towards");
		this.m_StopWords.add("u");
		this.m_StopWords.add("under");
		this.m_StopWords.add("until");
		this.m_StopWords.add("up");
		this.m_StopWords.add("upon");
		this.m_StopWords.add("v");
		this.m_StopWords.add("very");
		this.m_StopWords.add("via");
		this.m_StopWords.add("w");
		this.m_StopWords.add("was");
		this.m_StopWords.add("we");
		this.m_StopWords.add("well");
		this.m_StopWords.add("were");
		this.m_StopWords.add("what");
		this.m_StopWords.add("whatever");
		this.m_StopWords.add("whatsoever");
		this.m_StopWords.add("when");
		this.m_StopWords.add("whence");
		this.m_StopWords.add("whenever");
		this.m_StopWords.add("whensoever");
		this.m_StopWords.add("where");
		this.m_StopWords.add("whereafter");
		this.m_StopWords.add("whereas");
		this.m_StopWords.add("whereat");
		this.m_StopWords.add("whereby");
		this.m_StopWords.add("wherefrom");
		this.m_StopWords.add("wherein");
		this.m_StopWords.add("whereinto");
		this.m_StopWords.add("whereof");
		this.m_StopWords.add("whereon");
		this.m_StopWords.add("whereto");
		this.m_StopWords.add("whereunto");
		this.m_StopWords.add("whereupon");
		this.m_StopWords.add("wherever");
		this.m_StopWords.add("wherewith");
		this.m_StopWords.add("whether");
		this.m_StopWords.add("which");
		this.m_StopWords.add("whichever");
		this.m_StopWords.add("whichsoever");
		this.m_StopWords.add("while");
		this.m_StopWords.add("whilst");
		this.m_StopWords.add("whither");
		this.m_StopWords.add("who");
		this.m_StopWords.add("whoever");
		this.m_StopWords.add("whole");
		this.m_StopWords.add("whom");
		this.m_StopWords.add("whomever");
		this.m_StopWords.add("whomsoever");
		this.m_StopWords.add("whose");
		this.m_StopWords.add("whosoever");
		this.m_StopWords.add("why");
		this.m_StopWords.add("will");
		this.m_StopWords.add("with");
		this.m_StopWords.add("within");
		this.m_StopWords.add("without");
		this.m_StopWords.add("would");
		this.m_StopWords.add("x");
		this.m_StopWords.add("yet");
		this.m_StopWords.add("you");
		this.m_StopWords.add("your");
		this.m_StopWords.add("yours");
		this.m_StopWords.add("yourself");
		this.m_StopWords.add("yourselves");
		this.m_StopWords.add("z");
		this.m_StopWords.add("say");
		this.m_StopWords.add("says");
		this.m_StopWords.add("said");
		this.m_StopWords.add("do");
		this.m_StopWords.add("n't");
		this.m_StopWords.add("'ve");
		this.m_StopWords.add("'d");
		this.m_StopWords.add("'m");
		this.m_StopWords.add("'s");
		this.m_StopWords.add("'re");
		this.m_StopWords.add("'ll");
		this.m_StopWords.add("-lrb-");
		this.m_StopWords.add("-rrb-");
		this.m_StopWords.add("-lsb-");
		this.m_StopWords.add("-rsb-");
		this.m_StopWords.add("-lcb-");
		this.m_StopWords.add("-rcb-");
	}

	/**
	 * constructor
	 * @param p_StopWords stop word list
	 */
	public CSurroundingWordFilter(HashSet<String> p_StopWords) {
		this.m_StopWords.addAll(p_StopWords);
	}

	/**
	 * constructor given stop word list
	 * @param p_StopwordStream stop word inputstream
	 * @throws IOException exception while loading stop words
	 */
	public CSurroundingWordFilter(InputStream p_StopwordStream) throws IOException {
		String word = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(p_StopwordStream));
		while ((word = reader.readLine()) != null) {
			this.m_StopWords.add(word);
		}
		reader.close();
	}

	/**
	 * constructor given stop word list
	 * @param p_StopwordFile stop word file
	 * @throws IOException exception while loading stop words
	 */
	public CSurroundingWordFilter(String p_StopwordFile) throws IOException {
		this(new FileInputStream(p_StopwordFile));
	}

	/**
	 * check whether word is in stop word list or contains no alphabet
	 * @param p_Word word
	 * @return true if it should be filtered, else false
	 */
	public boolean filter(String p_Word) {
		p_Word = p_Word.toLowerCase();
		if (this.m_StopWords.contains(p_Word)
				|| !ALPHABET.matcher(p_Word).find()) {
			return true;
		}
		return false;
	}

	/**
	 * get an surrouding word filter with default stop word list
	 *
	 * @return surrounding feature filter
	 */
	public static CSurroundingWordFilter getInstance() {
		return DEFAULT;
	}
}
