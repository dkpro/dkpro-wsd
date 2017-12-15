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

package de.tudarmstadt.ukp.dkpro.wsd.senseval.reader;

import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;

import org.dom4j.Element;

/**
 * SensevalAWReader is the abstract base class for all collection readers
 * which read the XML data sets for the Senseval and Semeval all-words
 * tasks.
 * 
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public abstract class SensevalAWReader
	extends SensevalReader
{
	protected static final String TEXT_ELEMENT_NAME = "text";
	protected Iterator<Element> textIterator;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(UimaContext context)
		throws ResourceInitializationException
	{
		super.initialize(context);
		numTexts = corpus.elements(TEXT_ELEMENT_NAME).size();
		if (numTexts < 1)
			throw new ResourceInitializationException("element_not_found", new Object[]{TEXT_ELEMENT_NAME, CORPUS_ELEMENT_NAME});
		textIterator = corpus.elementIterator(TEXT_ELEMENT_NAME);
	}

	@Override
	public boolean hasNext()
		throws IOException, CollectionException
	{
		return textIterator.hasNext();
	}
	
}
