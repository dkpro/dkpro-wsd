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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;

/**
 * A reader for the TAC KBP format (see http://www.nist.gov/tac/2012/KBP/data.html)
 * @author nico.erbs@gmail.com
 *
 */
public class TacKbpOfficialFormatReader extends JCasCollectionReader_ImplBase

{

	public static final String PARAM_INPUT_PATH = "InputPath";
	@ConfigurationParameter(name=PARAM_INPUT_PATH, mandatory=true)
	private String inputPath;

	public static final String PARAM_DOCUMENT_COLLECTIONS = "DocumentCollections";
	@ConfigurationParameter(name=PARAM_DOCUMENT_COLLECTIONS, mandatory=true)
	private String documentCollections;

	private List<TacKbpDocument> tacKbpDocuments;
	private int position;
	private SAXBuilder saxBuilder;;


	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		try {
			tacKbpDocuments = new ArrayList<TacKbpDocument>();

			String[] inputFiles = new File(inputPath).list(new FilenameFilter() {

				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".xml");
				}
			});

			for(String inputFile : inputFiles){
				tacKbpDocuments.addAll(TacKbpDocumentCreator.getTacKbpDocuments(new File(inputPath, inputFile)));
			}
		} catch (JDOMException e) {
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		saxBuilder = new SAXBuilder();
		position = 0;
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return position < tacKbpDocuments.size();
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {

		TacKbpDocument tacKbpDocument = tacKbpDocuments.get(position);
		File baseDocument = getDocument(tacKbpDocument.getDocumentUri());
		String[] documentContent;
		try {
			documentContent = getDocumentContent(baseDocument);
		} catch (JDOMException e) {
			throw new IOException(e);
		}

		//Set document meta data
		DocumentMetaData metaData = new DocumentMetaData(jCas);
		metaData.setDocumentTitle(documentContent[0]);
		metaData.setDocumentUri(tacKbpDocument.getDocumentUri());
		metaData.setDocumentId(tacKbpDocument.getDocumentId());
		metaData.setDocumentBaseUri(tacKbpDocument.getBaseUri());
		metaData.setLanguage("en");
		metaData.setCollectionId(tacKbpDocument.getBaseUri());

		jCas.setDocumentLanguage("en");

		String documentText = documentContent[1];

		jCas.setDocumentText(documentText);

		//create WSDItem
		WSDItem wsdItem = new WSDItem(jCas);

		String mention = tacKbpDocument.getMention();


		//set begin and end
		int begin;
		int end;
		if(tacKbpDocument.getEnd() != 0){
			begin = tacKbpDocument.getBegin();
			end = tacKbpDocument.getEnd();
		}
		else if(documentText.contains(mention)){
			begin = documentText.indexOf(mention);
			end = begin + mention.length();
		}
		else{
			begin = 0;
			end = documentText.length();
		}
		wsdItem.setBegin(begin);
		wsdItem.setEnd(end);


		wsdItem.setSubjectOfDisambiguation(mention);
		wsdItem.addToIndexes();
		wsdItem.setPos(POS.NOUN.name());
		wsdItem.setId(mention);

		metaData.addToIndexes();

		//		if(position%50==0){
		position++;
//		System.out.println(new Date().toString() + "\t" + tacKbpDocument.getDocumentId() + "\t" + documentContent[1].length() + " (avg. " + characterCounter/position + ")" + "\t" + timer.getStatus(position));
		//		}
//		System.out.println("TITLE:\t" + documentContent[0]);
//		System.out.println("MENTION:\t" + tacKbpDocument.getMention());
//		System.out.println("TEXT:\t" + documentContent[1]);
	}

	protected String[] getDocumentContent (File baseDocument) throws JDOMException, IOException {
		Document document = saxBuilder.build(baseDocument);
		Element rootNode = document.getRootElement();
		//		System.out.println("File: " + baseDocument.getAbsolutePath());
		Element bodyNode = rootNode.getChild("BODY");
		String title = "";
		if(bodyNode.getChild("HEADLINE") != null){
			title = bodyNode.getChild("HEADLINE").getTextNormalize();
		}
		String text;
		Element textNode = bodyNode.getChild("TEXT");
		if(textNode.getChild("POST") != null){
		    text = "";
            List list = bodyNode.getChildren("POST");
            for (int i = 0; i < list.size(); i++) {
                Element node = (Element) list.get(i);
                text += node.getTextNormalize() + "\n";
            }
			text = textNode.getChild("POST").getTextNormalize();
		}
		else{
			text = "";
			List list = bodyNode.getChild("TEXT").getChildren("P");
			for (int i = 0; i < list.size(); i++) {
				Element node = (Element) list.get(i);
				text += node.getTextNormalize() + "\n";
			}
		}
		//		System.out.println("Title: " + title);
		//		System.out.println("Text: " + text);
		return new String[]{title, text};
	}

	protected File getDocument(String documentUri) throws IOException {
		String part0;
		String part1;
		String path;


		if(documentUri.endsWith(".LDC2009T13") || documentUri.endsWith(".LDC2007T07")){
			part0 = documentUri.split("_")[0] + "_" + documentUri.split("_")[1];
			part1 = documentUri.split("_")[2].split("\\.")[0];
			path = new File(documentCollections, "TAC_2010_KBP_Source_Data/data/2009/nw").getCanonicalPath();
			path += "/" + part0.toLowerCase() + "/" + part1 + "/" + documentUri + ".sgm";
		}
		else if(documentUri.endsWith(".LDC2006E32")){
			path = new File(documentCollections, "TAC_2010_KBP_Source_Data/data/2009/wb").getCanonicalPath();
			path += "/" + documentUri + ".sgm";
		}
		else{
			path = new File(documentCollections, "TAC_2010_KBP_Source_Data/data/2010/wb").getCanonicalPath();
			path += "/" + documentUri + ".sgm";
		}

		File file = new File(path);
		if(file.exists()){
			return file;
		}
		else{
			System.err.println("Could not load document " + documentUri + " (Path: " + path +")");
			return null;
		}

	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(position, tacKbpDocuments.size(), "case") };
	}

	protected void initializeForTests(String documentCollections){
		this.documentCollections = documentCollections;
		saxBuilder = new SAXBuilder();
	}


}
