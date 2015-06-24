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

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Penn Treebank tokenizer. before you use this class, please read the
 * introduction below first. CPennTreeBankTokenizer could only deal with a
 * string which contains only one line in it!!! If you input a string with many
 * lines, there would be some errors unexpected.
 *
 * @author zhongzhi
 *
 */
public class CPennTreeBankTokenizer implements ITokenizer {
	// regular expression
	protected static ArrayList<String> PREREGEX;
	// replacement
	protected static ArrayList<String> PREREPLACE;
	//
	protected static Pattern[] PREPATTERN;
	protected static Pattern SEGMENTER = Pattern.compile("\\s+");
	static {
		PREREGEX = new ArrayList<String>();
		PREREPLACE = new ArrayList<String>();
		PREREGEX.add("\\[(/?\\w+)\\]");
		PREREPLACE.add(" |$1| "); // according to yk's perl code, not part of
		// tokenizer.sed
		PREREGEX.add("^\"");
		PREREPLACE.add("`` ");
		PREREGEX.add("([ \\(\\[\\{<])\"");
		PREREPLACE.add("$1 `` ");
		PREREGEX.add("\\.\\.\\.");
		PREREPLACE.add(" ... ");
		PREREGEX.add("([,;:@#$%&])");
		PREREPLACE.add(" $1 ");
		PREREGEX.add("([^\\.])(\\.)([\\]\\)\\}>\"\\']*)\\s*$");
		PREREPLACE.add("$1 $2$3 ");
		PREREGEX.add("([?!])");
		PREREPLACE.add(" $1 ");
		PREREGEX.add("([\\]\\[\\(\\)\\{\\}<>])");
		PREREPLACE.add(" $1 ");
		PREREGEX.add("\\(");
		PREREPLACE.add("-LRB-");
		PREREGEX.add("\\)");
		PREREPLACE.add("-RRB-");
		PREREGEX.add("\\[");
		PREREPLACE.add("-LSB-");
		PREREGEX.add("\\]");
		PREREPLACE.add("-RSB-");
		PREREGEX.add("\\{");
		PREREPLACE.add("-LCB-");
		PREREGEX.add("\\}");
		PREREPLACE.add("-RCB-");
		PREREGEX.add("--");
		PREREPLACE.add(" -- ");
		PREREGEX.add("$");
		PREREPLACE.add(" ");
		PREREGEX.add("^");
		PREREPLACE.add(" ");
		PREREGEX.add("\"");
		PREREPLACE.add(" '' ");
		PREREGEX.add("([^'])' ");
		PREREPLACE.add("$1 ' ");
		PREREGEX.add("''");
		PREREPLACE.add(" '' ");
		PREREGEX.add("``");
		PREREPLACE.add(" `` ");
		PREREGEX.add("'([sSmMdD]) ");
		PREREPLACE.add(" '$1 ");
		PREREGEX.add("'ll ");
		PREREPLACE.add(" 'll ");
		PREREGEX.add("'re ");
		PREREPLACE.add(" 're ");
		PREREGEX.add("'ve ");
		PREREPLACE.add(" 've ");
		PREREGEX.add("n't ");
		PREREPLACE.add(" n't ");
		PREREGEX.add("'LL ");
		PREREPLACE.add(" 'LL ");
		PREREGEX.add("'RE ");
		PREREPLACE.add(" 'RE ");
		PREREGEX.add("'VE ");
		PREREPLACE.add(" 'VE ");
		PREREGEX.add("N'T ");
		PREREPLACE.add(" N'T ");
		PREREGEX.add(" ([Cc])annot ");
		PREREPLACE.add(" $1an not ");
		PREREGEX.add(" ([Dd])'ye ");
		PREREPLACE.add(" $1' ye ");
		PREREGEX.add(" ([Gg])imme ");
		PREREPLACE.add(" $1im me ");
		PREREGEX.add(" ([Gg])onna ");
		PREREPLACE.add(" $1on na ");
		PREREGEX.add(" ([Gg])otta ");
		PREREPLACE.add(" $1ot ta ");
		PREREGEX.add(" ([Ll])emme ");
		PREREPLACE.add(" $1em me ");
		PREREGEX.add(" ([Mm])ore'n ");
		PREREPLACE.add(" $1ore 'n ");
		PREREGEX.add(" '([Tt])is ");
		PREREPLACE.add(" '$1 is ");
		PREREGEX.add(" '([Tt])was ");
		PREREPLACE.add(" '$1 was ");
		PREREGEX.add(" ([Ww])anna ");
		PREREPLACE.add(" $1an na ");
		// Added by Zhong Zhi to solve left open single quote problem
		PREREGEX.add(" '((?!['tTnNsSmMdD] |\\s|[2-9]0s |em |till |cause |ll |LL |ve |VE |re |RE )[^\\s]+) ");
		PREREPLACE.add(" ` $1 ");

		PREREGEX.add("  *");
		PREREPLACE.add(" ");
		PREREGEX.add("^ *");
		PREREPLACE.add("");
		PREPATTERN = new Pattern[PREREGEX.size()];
		for (int i = 0; i < PREREGEX.size(); i++) {
			PREPATTERN[i] = Pattern.compile(PREREGEX.get(i));
		}

		/**
		 * March 29, 2010 Zhong Zhi
		 * Problem of Penn TreeBank Tokenzier: Cannot handle the left open single quote
		 * Risk: the input sentence may already been tokenzed
		 * Solution:
		 * 	" '((?!['tTnNsSmMdD] |\\s|[2-9]0s |em |till |cause |ll |LL |ve |VE |re |RE )[^ ]+) " => " ` $1 " // not the one with a character defined in tokenizer.sed
		 */
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ITokenizer#tokenize(java.lang.String)
	 */
	@Override
	public String[] tokenize(String p_Sentence) {
		if (p_Sentence != null) {
			for (int i = 0; i < PREPATTERN.length; i++) {
				p_Sentence = PREPATTERN[i].matcher(p_Sentence).replaceAll(
						PREREPLACE.get(i));
			}
		}
		return SEGMENTER.split(p_Sentence);
	}

	public static void main(String[] args) {
		CPennTreeBankTokenizer tokenizer = new CPennTreeBankTokenizer();
		for (String tok:tokenizer.tokenize("'abbas agree.''")) {
			System.out.println(tok);
		}
	}

}
