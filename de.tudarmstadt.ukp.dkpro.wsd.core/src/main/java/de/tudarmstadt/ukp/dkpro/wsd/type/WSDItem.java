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



/* First created by JCasGen Wed Sep 28 10:37:18 CEST 2011 */
package de.tudarmstadt.ukp.dkpro.wsd.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Jul 18 16:45:45 CEST 2014
 * XML source: /home/miller/workspace/de.tudarmstadt.ukp.dkpro.wsd/de.tudarmstadt.ukp.dkpro.wsd.core/src/main/resources/desc/type/WSDResult.xml
 * @generated */
public class WSDItem extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(WSDItem.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected WSDItem() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public WSDItem(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public WSDItem(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public WSDItem(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  *
   * @generated modifiable 
   */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: subjectOfDisambiguation

  /** getter for subjectOfDisambiguation - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSubjectOfDisambiguation() {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_subjectOfDisambiguation == null)
      jcasType.jcas.throwFeatMissing("subjectOfDisambiguation", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSDItem_Type)jcasType).casFeatCode_subjectOfDisambiguation);}
    
  /** setter for subjectOfDisambiguation - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSubjectOfDisambiguation(String v) {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_subjectOfDisambiguation == null)
      jcasType.jcas.throwFeatMissing("subjectOfDisambiguation", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    jcasType.ll_cas.ll_setStringValue(addr, ((WSDItem_Type)jcasType).casFeatCode_subjectOfDisambiguation, v);}    
   
    
  //*--------------*
  //* Feature: constituents

  /** getter for constituents - gets Array of all constituents for this WSDItem.
   * @generated
   * @return value of the feature 
   */
  public FSArray getConstituents() {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_constituents == null)
      jcasType.jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((WSDItem_Type)jcasType).casFeatCode_constituents)));}
    
  /** setter for constituents - sets Array of all constituents for this WSDItem. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setConstituents(FSArray v) {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_constituents == null)
      jcasType.jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    jcasType.ll_cas.ll_setRefValue(addr, ((WSDItem_Type)jcasType).casFeatCode_constituents, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for constituents - gets an indexed value - Array of all constituents for this WSDItem.
   * @generated */
  public LexicalItemConstituent getConstituents(int i) {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_constituents == null)
      jcasType.jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WSDItem_Type)jcasType).casFeatCode_constituents), i);
    return (LexicalItemConstituent)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WSDItem_Type)jcasType).casFeatCode_constituents), i)));}

  /** indexed setter for constituents - sets an indexed value - Array of all constituents for this WSDItem.
   * @generated */
  public void setConstituents(int i, LexicalItemConstituent v) { 
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_constituents == null)
      jcasType.jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WSDItem_Type)jcasType).casFeatCode_constituents), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WSDItem_Type)jcasType).casFeatCode_constituents), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSDItem_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    jcasType.ll_cas.ll_setStringValue(addr, ((WSDItem_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: pos

  /** getter for pos - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPos() {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSDItem_Type)jcasType).casFeatCode_pos);}
    
  /** setter for pos - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPos(String v) {
    if (WSDItem_Type.featOkTst && ((WSDItem_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem");
    jcasType.ll_cas.ll_setStringValue(addr, ((WSDItem_Type)jcasType).casFeatCode_pos, v);}    
  }

    