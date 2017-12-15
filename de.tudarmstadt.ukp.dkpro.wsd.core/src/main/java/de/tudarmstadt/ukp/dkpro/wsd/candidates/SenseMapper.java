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

package de.tudarmstadt.ukp.dkpro.wsd.candidates;

import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.wsd.WSDUtils;

/**
 * SenseMapper converts all senses from a given source sense inventory to
 * those of a target sense inventory.  The sense IDs to search for and their
 * replacements are given by columns of a delimited text file.
 *
 * @author <a href="mailto:miller@ukp.informatik.tu-darmstadt.de">Tristan Miller</a>
 */
public class SenseMapper
	extends SenseConverter
{

	public static final String PARAM_FILE = "File";
	@ConfigurationParameter(name=PARAM_FILE,
			mandatory=true,
			description="A delimited text file containing sense mappings"
	)
	private String fileName;

	public static final String PARAM_KEY_COLUMN = "Key_Column";
	@ConfigurationParameter(name=PARAM_KEY_COLUMN,
			mandatory=true,
			description="The index of the mapping file key column"
	)
	private int keyColumn;

	public static final String PARAM_VALUE_COLUMN = "Value_Column";
	@ConfigurationParameter(name=PARAM_VALUE_COLUMN,
			mandatory=true,
			description="The index of the mapping file value column"
	)
	private int valueColumn;

	public static final String PARAM_DELIMITER_REGEX = "Delimiter_Regex";
	@ConfigurationParameter(name=PARAM_DELIMITER_REGEX,
			mandatory=false,
			description="A regular expression matching the mapping file column delimiter",
			defaultValue="[\\t ]+"
	)
	private String delimiterRegex;

	protected Map<String,String> senseMap;

	@Override
	public void initialize(UimaContext context)
		throws ResourceInitializationException
	{
		super.initialize(context);

		try {
			senseMap = WSDUtils.readMap(
					ResourceUtils.resolveLocation(fileName, this, context),
					keyColumn, String.class, valueColumn, String.class,
					delimiterRegex);
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public String convert(String senseId)
	{
		return senseMap.get(senseId);
	}

}
