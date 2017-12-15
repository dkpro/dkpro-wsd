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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Fri Jul 18 16:45:45 CEST 2014
 * @generated */
public class WSDResult_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (WSDResult_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = WSDResult_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new WSDResult(addr, WSDResult_Type.this);
  			   WSDResult_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new WSDResult(addr, WSDResult_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = WSDResult.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
 
  /** @generated */
  final Feature casFeat_senses;
  /** @generated */
  final int     casFeatCode_senses;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSenses(int addr) {
        if (featOkTst && casFeat_senses == null)
      jcas.throwFeatMissing("senses", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return ll_cas.ll_getRefValue(addr, casFeatCode_senses);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSenses(int addr, int v) {
        if (featOkTst && casFeat_senses == null)
      jcas.throwFeatMissing("senses", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    ll_cas.ll_setRefValue(addr, casFeatCode_senses, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getSenses(int addr, int i) {
        if (featOkTst && casFeat_senses == null)
      jcas.throwFeatMissing("senses", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_senses), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_senses), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_senses), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setSenses(int addr, int i, int v) {
        if (featOkTst && casFeat_senses == null)
      jcas.throwFeatMissing("senses", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_senses), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_senses), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_senses), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_disambiguationMethod;
  /** @generated */
  final int     casFeatCode_disambiguationMethod;
  /** @generated */ 
  public String getDisambiguationMethod(int addr) {
        if (featOkTst && casFeat_disambiguationMethod == null)
      jcas.throwFeatMissing("disambiguationMethod", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return ll_cas.ll_getStringValue(addr, casFeatCode_disambiguationMethod);
  }
  /** @generated */    
  public void setDisambiguationMethod(int addr, String v) {
        if (featOkTst && casFeat_disambiguationMethod == null)
      jcas.throwFeatMissing("disambiguationMethod", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    ll_cas.ll_setStringValue(addr, casFeatCode_disambiguationMethod, v);}
    
  
 
  /** @generated */
  final Feature casFeat_senseInventory;
  /** @generated */
  final int     casFeatCode_senseInventory;
  /** @generated */ 
  public String getSenseInventory(int addr) {
        if (featOkTst && casFeat_senseInventory == null)
      jcas.throwFeatMissing("senseInventory", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return ll_cas.ll_getStringValue(addr, casFeatCode_senseInventory);
  }
  /** @generated */    
  public void setSenseInventory(int addr, String v) {
        if (featOkTst && casFeat_senseInventory == null)
      jcas.throwFeatMissing("senseInventory", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    ll_cas.ll_setStringValue(addr, casFeatCode_senseInventory, v);}
    
  
 
  /** @generated */
  final Feature casFeat_wsdItem;
  /** @generated */
  final int     casFeatCode_wsdItem;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getWsdItem(int addr) {
        if (featOkTst && casFeat_wsdItem == null)
      jcas.throwFeatMissing("wsdItem", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return ll_cas.ll_getRefValue(addr, casFeatCode_wsdItem);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setWsdItem(int addr, int v) {
        if (featOkTst && casFeat_wsdItem == null)
      jcas.throwFeatMissing("wsdItem", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    ll_cas.ll_setRefValue(addr, casFeatCode_wsdItem, v);}
    
  
 
  /** @generated */
  final Feature casFeat_comment;
  /** @generated */
  final int     casFeatCode_comment;
  /** @generated */ 
  public String getComment(int addr) {
        if (featOkTst && casFeat_comment == null)
      jcas.throwFeatMissing("comment", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    return ll_cas.ll_getStringValue(addr, casFeatCode_comment);
  }
  /** @generated */    
  public void setComment(int addr, String v) {
        if (featOkTst && casFeat_comment == null)
      jcas.throwFeatMissing("comment", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDResult");
    ll_cas.ll_setStringValue(addr, casFeatCode_comment, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public WSDResult_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_senses = jcas.getRequiredFeatureDE(casType, "senses", "uima.cas.FSArray", featOkTst);
    casFeatCode_senses  = (null == casFeat_senses) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_senses).getCode();

 
    casFeat_disambiguationMethod = jcas.getRequiredFeatureDE(casType, "disambiguationMethod", "uima.cas.String", featOkTst);
    casFeatCode_disambiguationMethod  = (null == casFeat_disambiguationMethod) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_disambiguationMethod).getCode();

 
    casFeat_senseInventory = jcas.getRequiredFeatureDE(casType, "senseInventory", "uima.cas.String", featOkTst);
    casFeatCode_senseInventory  = (null == casFeat_senseInventory) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_senseInventory).getCode();

 
    casFeat_wsdItem = jcas.getRequiredFeatureDE(casType, "wsdItem", "de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem", featOkTst);
    casFeatCode_wsdItem  = (null == casFeat_wsdItem) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_wsdItem).getCode();

 
    casFeat_comment = jcas.getRequiredFeatureDE(casType, "comment", "uima.cas.String", featOkTst);
    casFeatCode_comment  = (null == casFeat_comment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_comment).getCode();

  }
}



    