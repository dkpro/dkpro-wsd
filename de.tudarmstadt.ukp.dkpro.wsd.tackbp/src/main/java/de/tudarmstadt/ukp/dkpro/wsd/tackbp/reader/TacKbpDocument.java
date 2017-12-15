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

/**
 * A helper class to represent a document in TAC KBP format as an instance of this class
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class TacKbpDocument {
	
	private String mention;
	private int begin;
	private int end;
	private String documentId;
	private String documentUri;
	private String baseUri;
	
	
	public String getBaseUri() {
		return baseUri;
	}
	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}
	public String getMention() {
		return mention;
	}
	public void setMention(String mention) {
		this.mention = mention;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public void setDocumentUri(String documentUri) {
		this.documentUri = documentUri;
	}
	public int getBegin() {
		return begin;
	}
	public int getEnd() {
		return end;
	}
	public String getDocumentId() {
		return documentId;
	}
	public String getDocumentUri() {
		return documentUri;
	}

}
