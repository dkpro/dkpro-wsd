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

package de.tudarmstadt.ukp.dkpro.wsd.tackbp.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * A class that creates a list of TacKbpDocument from an input xml file in TAC KBP format
 * @author nico.erbs@gmail.com
 *
 */
public class TacKbpDocumentCreator {

	public static List<TacKbpDocument> getTacKbpDocuments(File xmlFile) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(xmlFile);
		Element rootNode = document.getRootElement();
		List list = rootNode.getChildren("query");

		List<TacKbpDocument> tacKbpDocuments = new ArrayList<TacKbpDocument>();
		TacKbpDocument tacKbpDocument;
		for (int i = 0; i < list.size(); i++) {

			Element node = (Element) list.get(i);
			tacKbpDocument = new TacKbpDocument();
			tacKbpDocument.setDocumentId(node.getAttributeValue("id"));
			tacKbpDocument.setMention(node.getChildTextNormalize("name"));
			tacKbpDocument.setDocumentUri(node.getChildTextNormalize("docid"));
			tacKbpDocument.setBaseUri(xmlFile.getPath());

			String begin = node.getChildTextNormalize("startoffset");
			if(begin != null){
				tacKbpDocument.setBegin(Integer.valueOf(begin));
			}
			else{
				tacKbpDocument.setBegin(0);
			}

			String end = node.getChildTextNormalize("endoffset");
			if(begin != null){
				tacKbpDocument.setEnd(Integer.valueOf(end));
			}
			else{
				tacKbpDocument.setEnd(0);
			}
			tacKbpDocuments.add(tacKbpDocument);
		}
		return tacKbpDocuments;
	}
}