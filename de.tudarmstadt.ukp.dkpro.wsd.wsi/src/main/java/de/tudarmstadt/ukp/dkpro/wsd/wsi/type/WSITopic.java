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

/* First created by JCasGen Fri Jul 26 16:04:18 CEST 2013 */
package de.tudarmstadt.ukp.dkpro.wsd.wsi.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent;


/**
 * Updated by JCasGen Fri Jul 26 16:04:18 CEST 2013
 * XML source: /home/zorn/workspace/de.tudarmstadt.ukp.dkpro.wsd/de.tudarmstadt.ukp.dkpro.wsd.wsi/src/main/resources/desc/WSITopic.xml
 * @generated */
public class WSITopic extends Annotation {
  /** @generated
   * @ordered
   */
  public final static int typeIndexID = JCasRegistry.register(WSITopic.class);
  /** @generated
   * @ordered
   */
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}

  /** Never called.  Disable default constructor
   * @generated */
  protected WSITopic() {/* intentionally empty block */}

  /** Internal - constructor used by generator
   * @generated */
  public WSITopic(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /** @generated */
  public WSITopic(JCas jcas) {
    super(jcas);
    readObject();
  }

  /** @generated */
  public WSITopic(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}



  //*--------------*
  //* Feature: subjectOfDisambiguation

  /** getter for subjectOfDisambiguation - gets
   * @generated */
  public String getSubjectOfDisambiguation() {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_subjectOfDisambiguation == null) {
        jcasType.jcas.throwFeatMissing("subjectOfDisambiguation", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSITopic_Type)jcasType).casFeatCode_subjectOfDisambiguation);}

  /** setter for subjectOfDisambiguation - sets
   * @generated */
  public void setSubjectOfDisambiguation(String v) {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_subjectOfDisambiguation == null) {
        jcasType.jcas.throwFeatMissing("subjectOfDisambiguation", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    jcasType.ll_cas.ll_setStringValue(addr, ((WSITopic_Type)jcasType).casFeatCode_subjectOfDisambiguation, v);}


  //*--------------*
  //* Feature: constituents

  /** getter for constituents - gets Array of all constituents for this WSITopic.
   * @generated */
  public FSArray getConstituents() {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_constituents == null) {
        jcasType.jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((WSITopic_Type)jcasType).casFeatCode_constituents)));}

  /** setter for constituents - sets Array of all constituents for this WSITopic.
   * @generated */
  public void setConstituents(FSArray v) {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_constituents == null) {
        jcasType.jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    jcasType.ll_cas.ll_setRefValue(addr, ((WSITopic_Type)jcasType).casFeatCode_constituents, jcasType.ll_cas.ll_getFSRef(v));}

  /** indexed getter for constituents - gets an indexed value - Array of all constituents for this WSITopic.
   * @generated */
  public LexicalItemConstituent getConstituents(int i) {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_constituents == null) {
        jcasType.jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WSITopic_Type)jcasType).casFeatCode_constituents), i);
    return (LexicalItemConstituent)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WSITopic_Type)jcasType).casFeatCode_constituents), i)));}

  /** indexed setter for constituents - sets an indexed value - Array of all constituents for this WSITopic.
   * @generated */
  public void setConstituents(int i, LexicalItemConstituent v) {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_constituents == null) {
        jcasType.jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WSITopic_Type)jcasType).casFeatCode_constituents), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WSITopic_Type)jcasType).casFeatCode_constituents), i, jcasType.ll_cas.ll_getFSRef(v));}


  //*--------------*
  //* Feature: id

  /** getter for id - gets
   * @generated */
  public String getId() {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_id == null) {
        jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSITopic_Type)jcasType).casFeatCode_id);}

  /** setter for id - sets
   * @generated */
  public void setId(String v) {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_id == null) {
        jcasType.jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    jcasType.ll_cas.ll_setStringValue(addr, ((WSITopic_Type)jcasType).casFeatCode_id, v);}


  //*--------------*
  //* Feature: pos

  /** getter for pos - gets
   * @generated */
  public String getPos() {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_pos == null) {
        jcasType.jcas.throwFeatMissing("pos", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    return jcasType.ll_cas.ll_getStringValue(addr, ((WSITopic_Type)jcasType).casFeatCode_pos);}

  /** setter for pos - sets
   * @generated */
  public void setPos(String v) {
    if (WSITopic_Type.featOkTst && ((WSITopic_Type)jcasType).casFeat_pos == null) {
        jcasType.jcas.throwFeatMissing("pos", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
    }
    jcasType.ll_cas.ll_setStringValue(addr, ((WSITopic_Type)jcasType).casFeatCode_pos, v);}
  }

