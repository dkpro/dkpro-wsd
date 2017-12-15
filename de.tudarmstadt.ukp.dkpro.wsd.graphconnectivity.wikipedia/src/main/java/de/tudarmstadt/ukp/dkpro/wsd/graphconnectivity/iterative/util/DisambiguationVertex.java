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

package de.tudarmstadt.ukp.dkpro.wsd.graphconnectivity.iterative.util;

/**
 * A vertex for disambiguations with the triple sod-sense-weight
 * 
 * @author nico.erbs@gmail.com
 *
 */
public class DisambiguationVertex {
	
	private String sense;
	
	private double weight;
	
	private String sod;
	
	public DisambiguationVertex(String sod, String sense, double weight){
		this.sod = sod;
		this.sense = sense;
		this.weight = weight;
	}

	public String getSense() {
		return sense;
	}

	public void setSense(String sense) {
		this.sense = sense;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getSod() {
		return sod;
	}

	public void setSod(String sod) {
		this.sod = sod;
	}

}
