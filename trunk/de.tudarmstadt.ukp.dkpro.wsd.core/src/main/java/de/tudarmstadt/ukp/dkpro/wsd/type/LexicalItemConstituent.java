/*******************************************************************************
 * Copyright 2013
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



/* First created by JCasGen Thu Oct 13 18:53:06 CEST 2011 */
package de.tudarmstadt.ukp.dkpro.wsd.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Dec 19 12:03:46 CET 2011
 * XML source: /home/miller/workspace/de.tudarmstadt.ukp.dkpro.wsd/de.tudarmstadt.ukp.dkpro.wsd.common/src/main/resources/desc/type/WSDResult.xml
 * @generated */
public class LexicalItemConstituent extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(LexicalItemConstituent.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected LexicalItemConstituent() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public LexicalItemConstituent(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public LexicalItemConstituent(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public LexicalItemConstituent(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: constituentType

  /** getter for constituentType - gets Type of the constituent, e.g. head or particle.
   * @generated */
  public String getConstituentType() {
    if (LexicalItemConstituent_Type.featOkTst && ((LexicalItemConstituent_Type)jcasType).casFeat_constituentType == null)
      jcasType.jcas.throwFeatMissing("constituentType", "de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
    return jcasType.ll_cas.ll_getStringValue(addr, ((LexicalItemConstituent_Type)jcasType).casFeatCode_constituentType);}
    
  /** setter for constituentType - sets Type of the constituent, e.g. head or particle. 
   * @generated */
  public void setConstituentType(String v) {
    if (LexicalItemConstituent_Type.featOkTst && ((LexicalItemConstituent_Type)jcasType).casFeat_constituentType == null)
      jcasType.jcas.throwFeatMissing("constituentType", "de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
    jcasType.ll_cas.ll_setStringValue(addr, ((LexicalItemConstituent_Type)jcasType).casFeatCode_constituentType, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated */
  public String getId() {
    if (LexicalItemConstituent_Type.featOkTst && ((LexicalItemConstituent_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
    return jcasType.ll_cas.ll_getStringValue(addr, ((LexicalItemConstituent_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated */
  public void setId(String v) {
    if (LexicalItemConstituent_Type.featOkTst && ((LexicalItemConstituent_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
    jcasType.ll_cas.ll_setStringValue(addr, ((LexicalItemConstituent_Type)jcasType).casFeatCode_id, v);}    
  }

    