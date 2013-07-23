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

package sg.edu.nus.comp.nlp.ims.corpus;

import java.io.Reader;

/**
 * corpus interface. a corpus consists of several sentences
 *
 * @author zhongzhi
 *
 */
public interface ICorpus {

	/**
	 * clear the corpus
	 */
	public void clear();

	/**
	 * get the number of instances
	 *
	 * @return size
	 */
	public int size();

	/**
	 * get the number of sentences
	 *
	 * @return number of sentence
	 */
	public int numOfSentences();

	/**
	 * get special value of key of instance index
	 *
	 * @param p_Index
	 *            instance index
	 * @param p_Key
	 *            value key
	 * @return value
	 */
	public String getValue(int p_Index, String p_Key);

	/**
	 * get the index of an instance in sentence
	 *
	 * @param p_Index
	 *            instance index
	 * @return index in sentence
	 */
	public int getIndexInSentence(int p_Index);

	/**
	 * get number of words of instance p_Index
	 * @param p_Index instance index
	 * @return number of words
	 */
	public int getLength(int p_Index);

	/**
	 * get the id of sentence which contains the instance
	 *
	 * @param p_Index
	 *            instance index
	 * @return sentence number
	 */
	public int getSentenceID(int p_Index);

	/**
	 * get the class of an instance
	 *
	 * @param p_Index
	 *            instance index
	 * @return instance tags
	 */
	public String[] getTag(int p_Index);

	/**
	 * get the sentence
	 *
	 * @param p_SentenceID
	 *            sentence number
	 * @return sentence
	 */
	public ISentence getSentence(int p_SentenceID);

	/**
	 * get upper boundary
	 *
	 * @param p_SentenceID
	 *            sentence number
	 * @return upper boundary
	 */
	public int getUpperBoundary(int p_SentenceID);

	/**
	 * get lower boundary
	 *
	 * @param p_SentenceID
	 *            sentence number
	 * @return lower boundary
	 */
	public int getLowerBoundary(int p_SentenceID);

	/**
	 * load data into corpus
	 *
	 * @param p_XmlReader
	 *            reader of the input stream
	 * @exception Exception
	 *                exception while loading file
	 * @return ready or not
	 */
	public boolean load(Reader p_XmlReader) throws Exception;
}
