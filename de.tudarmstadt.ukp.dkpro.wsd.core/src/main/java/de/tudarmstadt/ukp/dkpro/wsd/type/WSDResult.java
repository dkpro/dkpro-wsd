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


/* First created by JCasGen Wed Sep 28 10:37:26 CEST 2011 */
package de.tudarmstadt.ukp.dkpro.wsd.type;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/** 
 * Updated by JCasGen Fri Jul 18 16:45:45 CEST 2014
 * XML source: /home/miller/workspace/de.tudarmstadt.ukp.dkpro.wsd/de.tudarmstadt.ukp.dkpro.wsd.core/src/main/resources/desc/type/WSDResult.xml
 * @generated */
public class WSDResult
extends Annotation
{
	/**
	 * @generated
	 * @ordered
	 */
	public final static int typeIndexID = JCasRegistry.register(WSDResult.class);
	/**
	 * @generated
	 * @ordered
	 */
	public final static int type = typeIndexID;

	/** @generated */
	@Override
	public int getTypeIndexID() {return typeIndexID;}
 
	/**
	 * Never called. Disable default constructor
	 *
	 * @generated
	 */
	protected WSDResult() {/* intentionally empty block */}
    
	/**
	 * Internal - constructor used by generator
	 *
	 * @generated
	 */
	public WSDResult(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
	/** @generated */
	public WSDResult(JCas jcas) {
    super(jcas);
    readObject();   
  } 

	/** @generated */
	public WSDResult(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

	/**
	 * <!-- begin-user-doc --> Write your own initialization here <!--
	 * end-user-doc -->
	 *
	 * @generated modifiable
	 */
	private void readObject()
	{
	}

  //*--------------*
  //* Feature: senses

  /** getter for senses - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getSenses() {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_senses == null)
      jcasType.jcas.throwFeatMissing("senses", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((WSDResult_Type)jcasType).casFeatCode_senses)));}
    
  /** setter for senses - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSenses(FSArray v) {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_senses == null)
      jcasType.jcas.throwFeatMissing("senses", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    jcasType.ll_cas.ll_setRefValue(addr, ((WSDResult_Type)jcasType).casFeatCode_senses, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for senses - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public Sense getSenses(int i) {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_senses == null)
      jcasType.jcas.throwFeatMissing("senses", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WSDResult_Type)jcasType).casFeatCode_senses), i);
    return (Sense)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WSDResult_Type)jcasType).casFeatCode_senses), i)));}

  /** indexed setter for senses - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSenses(int i, Sense v) { 
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_senses == null)
      jcasType.jcas.throwFeatMissing("senses", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WSDResult_Type)jcasType).casFeatCode_senses), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WSDResult_Type)jcasType).casFeatCode_senses), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
	// *--------------*
	// * Feature: disambiguationMethod

	/**
	 * getter for disambiguationMethod - gets The method name which is used to
	 * determine senses
	 *
	 * @generated
	 */
	public String getDisambiguationMethod() {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_disambiguationMethod == null)
      jcasType.jcas.throwFeatMissing("disambiguationMethod", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSDResult_Type)jcasType).casFeatCode_disambiguationMethod);}
    
	/**
	 * setter for disambiguationMethod - sets The method name which is used to
	 * determine senses
	 *
	 * @generated
	 */
	public void setDisambiguationMethod(String v) {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_disambiguationMethod == null)
      jcasType.jcas.throwFeatMissing("disambiguationMethod", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((WSDResult_Type)jcasType).casFeatCode_disambiguationMethod, v);}    
   
    
	// *--------------*
	// * Feature: senseInventory

	/**
	 * getter for senseInventory - gets The textual represenatation of the used
	 * sense inventory
	 *
	 * @generated
	 */
	public String getSenseInventory() {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_senseInventory == null)
      jcasType.jcas.throwFeatMissing("senseInventory", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSDResult_Type)jcasType).casFeatCode_senseInventory);}
    
	/**
	 * setter for senseInventory - sets The textual represenatation of the used
	 * sense inventory
	 *
	 * @generated
	 */
	public void setSenseInventory(String v) {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_senseInventory == null)
      jcasType.jcas.throwFeatMissing("senseInventory", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((WSDResult_Type)jcasType).casFeatCode_senseInventory, v);}    
   
    
  //*--------------*
  //* Feature: wsdItem

  /** getter for wsdItem - gets The corresponding WSDItem for this WSDResult
   * @generated
   * @return value of the feature 
   */
  public WSDItem getWsdItem() {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_wsdItem == null)
      jcasType.jcas.throwFeatMissing("wsdItem", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return (WSDItem)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((WSDResult_Type)jcasType).casFeatCode_wsdItem)));}
    
  /** setter for wsdItem - sets The corresponding WSDItem for this WSDResult 
   * @generated
   * @param v value to set into the feature 
   */
  public void setWsdItem(WSDItem v) {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_wsdItem == null)
      jcasType.jcas.throwFeatMissing("wsdItem", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    jcasType.ll_cas.ll_setRefValue(addr, ((WSDResult_Type)jcasType).casFeatCode_wsdItem, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
	// *--------------*
	// * Feature: comment

	/**
	 * getter for comment - gets
	 *
	 * @generated
	 */
	public String getComment() {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_comment == null)
      jcasType.jcas.throwFeatMissing("comment", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSDResult_Type)jcasType).casFeatCode_comment);}
    
	/**
	 * setter for comment - sets
	 *
	 * @generated
	 */
	public void setComment(String v) {
    if (WSDResult_Type.featOkTst && ((WSDResult_Type)jcasType).casFeat_comment == null)
      jcasType.jcas.throwFeatMissing("comment", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    jcasType.ll_cas.ll_setStringValue(addr, ((WSDResult_Type)jcasType).casFeatCode_comment, v);}    
      	private final static Logger logger = Logger.getLogger(WSDResult.class.getName());

	/**
	 * Normalizes all the weights (confidence values) of senses in the
	 * WSDResult so that they sum to 1.0.
	 */
	public void normalize()
	{
		double weight = 0.0;
		int numSenses = getSenses().size();
		for (int i = 0; i < numSenses; i++) {
			weight += getSenses(i).getConfidence();
		}
		if (weight == 0.0) {
			for (int i = 0; i < numSenses; i++) {
				logger.debug("Normalizing " + getWsdItem().getId() + "/"
						+ getDisambiguationMethod() + "/"
						+ getSenses(i).getId() + " from "
						+ getSenses(i).getConfidence() + " to 1.0");
				getSenses(i).setConfidence(1.0 / numSenses);
			}
		}
		else if (weight != 1.0) {
			for (int i = 0; i < numSenses; i++) {
				double newConfidence = getSenses(i).getConfidence() / weight;
				logger.debug("Normalizing " + getWsdItem().getId() + "/"
						+ getDisambiguationMethod() + "/"
						+ getSenses(i).getId() + " from "
						+ getSenses(i).getConfidence() + " to "
						+ newConfidence);
				getSenses(i).setConfidence(
						newConfidence);
			}
		}
	}

	/**
	 * Returns the sense with the highest score (returns first one if multiple exist)
	 */
	public Sense getBestSense(){
		double highestConfidence = Double.NEGATIVE_INFINITY;
		int numSenses = getSenses().size();
		Sense bestSense = null;

		for (int i = 0; i < numSenses; i++) {
			if (getSenses(i).getConfidence() > highestConfidence) {
				highestConfidence = getSenses(i).getConfidence();
				bestSense = getSenses(i);
			}
//			else if (getSenses(i).getConfidence() == highestConfidence) {
//				bestSense = null;
//			}
		}
		return bestSense;
	}

	//    /**
	//     * Removes all those but the highest-scoring sense(s)
	//     * @throws CASException
	//     */
	//    public void keepBestOnly() throws CASException {
	//        double highestConfidence = 0.0;
	//        int numSenses = getSenses().size();
	//        int bestSenses = 0;
	//
	//        // Find the highest confidence value and the number of senses which
	//        // have it
	//        for (int i = 0; i < numSenses; i++) {
	//            if (getSenses(i).getConfidence() > highestConfidence) {
	//                highestConfidence = getSenses(i).getConfidence();
	//                bestSenses = 1;
	//            }
	//            else if (getSenses(i).getConfidence() == highestConfidence) {
	//                bestSenses++;
	//            }
	//        }
	//        FSArray senseArray = new FSArray(getCAS().getJCas(), bestSenses);
	//        for (int i = 0; i < numSenses; i++) {
	//            if (getSenses(i).getConfidence() == highestConfidence) {
	//                senseArray.set(--bestSenses, getSenses(i));
	//            }
	//        }
	//        setSenses(senseArray);
	//
	//    }
}
