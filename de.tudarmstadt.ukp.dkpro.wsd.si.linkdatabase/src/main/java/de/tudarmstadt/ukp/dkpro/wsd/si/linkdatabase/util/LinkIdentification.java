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

package de.tudarmstadt.ukp.dkpro.wsd.si.linkdatabase.util;

/**
 * A java representation of a link with anchor id, source id, sense id, and counts
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class LinkIdentification {
	
	private long anchorId;
	private long sourceId;
	private long senseId;
	private long count;
	
	public long getAnchorId() {
		return anchorId;
	}
	public void setAnchorId(long anchorId) {
		this.anchorId = anchorId;
	}
	public long getSourceId() {
		return sourceId;
	}
	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
	public long getSenseId() {
		return senseId;
	}
	public void setSenseId(long senseId) {
		this.senseId = senseId;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}

}
