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



/* First created by JCasGen Wed Sep 28 10:37:01 CEST 2011 */
package de.tudarmstadt.ukp.dkpro.wsd.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Jul 18 16:45:45 CEST 2014
 * XML source: /home/miller/workspace/de.tudarmstadt.ukp.dkpro.wsd/de.tudarmstadt.ukp.dkpro.wsd.core/src/main/resources/desc/type/WSDResult.xml
 * @generated */
public class Sense extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Sense.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Sense() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Sense(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Sense(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Sense(JCas jcas, int begin, int end) {
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
  //* Feature: id

  /** getter for id - gets The id of the sense
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (Sense_Type.featOkTst && ((Sense_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.wsd.type.Sense");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sense_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets The id of the sense 
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (Sense_Type.featOkTst && ((Sense_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.wsd.type.Sense");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sense_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: confidence

  /** getter for confidence - gets The confidence of the sense
   * @generated
   * @return value of the feature 
   */
  public double getConfidence() {
    if (Sense_Type.featOkTst && ((Sense_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "de.tudarmstadt.ukp.dkpro.wsd.type.Sense");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Sense_Type)jcasType).casFeatCode_confidence);}
    
  /** setter for confidence - sets The confidence of the sense 
   * @generated
   * @param v value to set into the feature 
   */
  public void setConfidence(double v) {
    if (Sense_Type.featOkTst && ((Sense_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "de.tudarmstadt.ukp.dkpro.wsd.type.Sense");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Sense_Type)jcasType).casFeatCode_confidence, v);}    
   
    
  //*--------------*
  //* Feature: description

  /** getter for description - gets A textual representation of the sense
   * @generated
   * @return value of the feature 
   */
  public String getDescription() {
    if (Sense_Type.featOkTst && ((Sense_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "de.tudarmstadt.ukp.dkpro.wsd.type.Sense");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sense_Type)jcasType).casFeatCode_description);}
    
  /** setter for description - sets A textual representation of the sense 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDescription(String v) {
    if (Sense_Type.featOkTst && ((Sense_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "de.tudarmstadt.ukp.dkpro.wsd.type.Sense");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sense_Type)jcasType).casFeatCode_description, v);}    
  }

    