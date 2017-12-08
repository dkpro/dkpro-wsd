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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 * amend senseval 3 lexical sample task training corpus. because in SensEval 3
 * lexical sample task training corpus, there may be two or more training
 * instances in one &lt;instance&gt; tag. this file is to separate the instances
 * described above. I will add a "-number" at the end of the instance id and
 * create a file with only one instance in one &lt;instance&gt; tag.
 *
 * @author zhongzhi
 *
 */
public final class CAmendLexeltCorpus {

	protected Pattern m_InstancePattern = Pattern.compile("^<instance id=\"([^\"]*)\" docsrc=\"([^\"]*)\">$");
	protected Pattern m_AnswerPattern = Pattern.compile("^<answer instance=\"([^\"]*)\" senseid=\"([^\"]*)\".*");
	protected Pattern m_SplitPattern = Pattern.compile("<[^>]*>");

	public void amend2(String p_XmlFile) throws IOException {
		StringBuilder xmlbuilder = new StringBuilder();
		StringBuilder ansbuilder = new StringBuilder();
		xmlbuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlbuilder.append("<!DOCTYPE corpus SYSTEM \"lexical-sample.dtd\">\n");
		xmlbuilder.append("<corpus lang=\"english\">\n");
		Matcher matcher = null;
		String id = null;
		String did = null;
		ArrayList<String> ins = new ArrayList<String>();
		ArrayList<String> ans = new ArrayList<String>();

		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(p_XmlFile)));
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty()) {
				if (line.startsWith("<")) {
					matcher = this.m_InstancePattern.matcher(line);
					if (matcher.matches()) {
						id = matcher.group(1);
						did = matcher.group(2);
					} else {
						matcher = this.m_AnswerPattern.matcher(line);
						if (matcher.matches()) {
							ans.add(matcher.group(2));
						} else {
							if (line.startsWith("</instance>")) {
								for (int i = 0; i < ins.size(); i++) {
									String nid = id;
									if (i != 0) {
										nid += "-" + i;
									}
									xmlbuilder.append("<instance id=\"");
									xmlbuilder.append(nid);
									xmlbuilder.append("\" docsrc=\"");
									xmlbuilder.append(did);
									xmlbuilder.append("\">\n<context>\n");
									xmlbuilder.append(ins.get(i));
									xmlbuilder.append("</context>\n</instance>\n");
									ansbuilder.append(did + " " + nid);
//									if (ins.size() == ans.size()) {
//										ansbuilder.append(" " + ans.get(i));
//									} else {
										for (String a :ans) {
											ansbuilder.append(" " + a);
										}
//									}
									ansbuilder.append("\n");
								}
								ans.clear();
								ins.clear();
							} else {
								if (line.startsWith("<lexelt")) {
									xmlbuilder.append(line + "\n");
								}
								if (line.startsWith("</lexelt>")) {
									xmlbuilder.append(line + "\n");
								}
							}
						}
					}
				} else {
					line = line.replaceAll("&", "&amp;");
					String[] parts = this.m_SplitPattern.split(line);
//					System.out.println(parts.length);
					for (int i = 1; i < parts.length; i += 2) {
						String instance = "";
						for (int pi = 0; pi < parts.length; pi++) {
							if (pi == i) {
								instance += "<head>" + parts[pi] + "</head>";
							} else {
								instance += parts[pi];
							}
						}
						ins.add(instance + "\n");
					}

				}
			}
		}
		reader.close();
		xmlbuilder.append("</corpus>\n");
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(p_XmlFile + ".xml"));
		writer.write(xmlbuilder.toString());
		writer.flush();
		writer.close();
		writer = new OutputStreamWriter(new FileOutputStream(p_XmlFile + ".key"));
		writer.write(ansbuilder.toString());
		writer.flush();
		writer.close();
	}

	/**
	 * amend an input xml file
	 *
	 * @param p_XmlFile
	 *            input xml file
	 * @throws IOException
	 *             io excpetion
	 * @throws JDOMException
	 *             jdom exception
	 */
	public void amend(String p_XmlFile) throws JDOMException, IOException {
		InputStreamReader reader = new InputStreamReader(new FileInputStream(p_XmlFile));
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(reader);
		Element root = doc.getRootElement();
		Element newRoot = root.clone();
		newRoot.removeChildren("lexelt");
		for (Object lexelt : root.getChildren("lexelt")) {
			newRoot.addContent(this.amendLexelt((Element) lexelt));
		}
		Document newDoc = new Document(newRoot, doc.getDocType()
				.clone());
		XMLOutputter outputter = new XMLOutputter();
		FileWriter writer = new FileWriter(p_XmlFile + ".new");
		outputter.output(newDoc, writer);
		writer.close();
	}

	private Element amendLexelt(Element p_Lexelt) {
		if (p_Lexelt != null) {
			Element amend = p_Lexelt.clone();
			amend.removeChildren("instance");
			for (Object instance : p_Lexelt.getChildren("instance")) {
				amend.addContent(this.amendInstance((Element) instance));
			}
			return amend;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
    private ArrayList<Element> amendInstance(Element p_Instance) {
		ArrayList<Element> instances = new ArrayList<Element>();
		ArrayList<Element> contents = new ArrayList<Element>();
		if (p_Instance != null) {
			Element context = p_Instance.getChild("context");
			int headNum = context.getChildren("head").size();
			if (headNum > 1) {
				int index = 0;
				String id = p_Instance.getAttributeValue("id");
				String docID = p_Instance.getAttributeValue("docsrc");
				List answers = p_Instance.getChildren("answer");
				Element element = p_Instance.clone();
				element.removeContent();
				for (int i = 0; i < context.getChildren("head").size(); i++) {
					Element newInstance = element.clone();
					newInstance.setAttribute("id", id + "-" + i);
					newInstance.setAttribute("docsrc", docID);
					ArrayList<Element> newAnswers = new ArrayList<Element>();
					if (answers != null && answers.size() > 0) {
						for (Object obj : answers) {
							Element answer = ((Element) obj).clone();
							answer.setAttribute("instance", id + "-" + i);
							newAnswers.add(answer);
						}
						newInstance.addContent(newAnswers);
					}
					Element newContext = new Element("context");
					contents.add(newContext);
					newInstance.addContent(newContext);
					instances.add(newInstance);
				}
				for (Object content : context.getContent()) {
					if (content.getClass().equals(org.jdom2.Text.class)) {
						for (Element newContext : contents) {
							newContext.addContent(((Content) content)
									.getValue());
						}
					} else {
						String name = ((Element) content).getName();
						if (name.equals("head")) {
							for (int i = 0; i < instances.size(); i++) {
								if (i == index) {
									contents.get(i).addContent(
											((Element) content)
													.clone());
								} else {
									contents.get(i).addContent(
											((Element) content).getValue());
								}
							}
							index++;
						} else {
							System.err.println(name);
							System.exit(1);
						}
					}
				}
			} else {
				instances.add(p_Instance.clone());
			}
			return instances;
		}
		return null;
	}

	/**
	 * @param p_Args
	 *            arguments
	 */
	public static void main(String[] p_Args) {
		if (p_Args.length != 1) {
			System.err.println("Format: " + CAmendLexeltCorpus.class.getName()
					+ " xml");
			System.exit(1);
		}
		CAmendLexeltCorpus amender = new CAmendLexeltCorpus();
		String xmlFile = p_Args[0];
		try {
			amender.amend2(xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
