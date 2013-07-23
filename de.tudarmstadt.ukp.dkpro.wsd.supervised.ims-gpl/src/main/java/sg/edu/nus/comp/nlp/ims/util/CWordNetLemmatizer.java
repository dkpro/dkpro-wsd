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

import java.util.regex.Pattern;

/**
 * wordnet lemmatizer
 *
 * @author zhongzhi
 *
 */
public class CWordNetLemmatizer implements ILemmatizer {

	// pattern for efficiency consideration
	protected Pattern m_EfficientPattern = Pattern.compile("(^[\\- _]|[\\- _]{2}|[\\- _]$)");

	// pattern for efficiency
	protected Pattern m_AlphabeticPattern = Pattern.compile("[a-zA-Z]");

	// delimiter pattern
	protected Pattern m_DelimiterPattern = Pattern.compile("[\\- _]");

	/**
	 * constructor
	 */
	public CWordNetLemmatizer()	{
		CJWNL.checkStatus();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ILemmatizer#lemmatize(java.lang.String[])
	 */
	@Override
	public String lemmatize(String[] input) {
		String word = input[0].toLowerCase();
		String pos = input[1];
		boolean force = false;
		if (input.length > 2 && input[2] != null) {
			force = true;
		}
		String lemma = word.toLowerCase();
		if (force) {
			lemma = CJWNL.getRootForm(word, pos);
		} else if (this.m_AlphabeticPattern.matcher(word).find()
				&& !this.m_EfficientPattern.matcher(word).find()) {
			lemma = CJWNL.getRootForm(word, pos);
			String[] toks = this.m_DelimiterPattern.split(word);
			if (toks.length >= 2) {
				for (int start = 0; start < toks.length; start++) {
					if (lemma.equals(word)) {
						break;
					}
					int end = start + toks.length - 1;
					end = end > toks.length ? toks.length : end;
					StringBuilder buffer = new StringBuilder();
					for (int i = start; i < end; i++) {
						if (i != start) {
							buffer.append(" ");
						}
						buffer.append(toks[i]);
						String subword = buffer.toString();
						if (lemma.equals(subword)) {
							lemma = word;
							break;
						}
						String sublemma = CJWNL.getRootForm(subword, pos);
						if (lemma.equals(sublemma)) {
							lemma = word;
							break;
						}
					}
				}
			}
		}
		return lemma.replace(' ', '_');
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ILemmatizer#guessLexelt(java.lang.String[])
	 */
	@Override
	public String guessLexelt(String[] input) {
		String word = input[0].toLowerCase();
		String lemma = word;
		String pos = "n";
		if (input.length > 1 && input[1] != null) {
			pos = input[1];
		}
		String force = null;
		if (input.length > 2 && input[2] != null) {
			force = input[2];
		}
		String lexelt = null;
		lemma = this.lemmatize(new String[]{lemma, pos, force});
		if (!CJWNL.hasSense(lemma, pos)) {
			for (String p : CJWNL.SHORTs) {
				if (!p.equals(pos)) {
					lemma = this.lemmatize(new String[]{lemma, p, force});
					if (CJWNL.hasSense(lemma, p)) {
						lexelt = lemma + "." + p;
						break;
					}
				}
			}
		} else {
			lexelt = lemma + "." + pos;
		}
		if (lexelt == null && force != null) {
			lexelt = lemma + "." + pos;
		}
		return lexelt;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.util.ILemmatizer#getLexelt(java.lang.String[])
	 */
	@Override
	public String getLexelt(String[] input) {
		String word = input[0].toLowerCase();
		String lemma = word;
		String lexelt = null;
		if (input.length > 1 && input[1] != null && CJWNL.SHORTs.contains(input[1])) {
			String pos = input[1];
			String force = null;
			if (input.length > 2 && input[2] != null) {
				force = input[2];
			}
			lemma = this.lemmatize(new String[]{lemma, pos, force});
			lexelt = lemma + "." + pos;
		}
		return lexelt;
	}
}
