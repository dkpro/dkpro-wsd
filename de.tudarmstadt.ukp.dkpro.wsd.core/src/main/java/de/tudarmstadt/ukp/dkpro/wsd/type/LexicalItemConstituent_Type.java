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


/* First created by JCasGen Thu Oct 13 18:53:06 CEST 2011 */
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
public class LexicalItemConstituent_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (LexicalItemConstituent_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = LexicalItemConstituent_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new LexicalItemConstituent(addr, LexicalItemConstituent_Type.this);
  			   LexicalItemConstituent_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new LexicalItemConstituent(addr, LexicalItemConstituent_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = LexicalItemConstituent.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
 
  /** @generated */
  final Feature casFeat_constituentType;
  /** @generated */
  final int     casFeatCode_constituentType;
  /** @generated */ 
  public String getConstituentType(int addr) {
        if (featOkTst && casFeat_constituentType == null)
      jcas.throwFeatMissing("constituentType", "de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
    return ll_cas.ll_getStringValue(addr, casFeatCode_constituentType);
  }
  /** @generated */    
  public void setConstituentType(int addr, String v) {
        if (featOkTst && casFeat_constituentType == null)
      jcas.throwFeatMissing("constituentType", "de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
    ll_cas.ll_setStringValue(addr, casFeatCode_constituentType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated */ 
  public String getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /** @generated */    
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.dkpro.wsd.type.LexicalItemConstituent");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public LexicalItemConstituent_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_constituentType = jcas.getRequiredFeatureDE(casType, "constituentType", "uima.cas.String", featOkTst);
    casFeatCode_constituentType  = (null == casFeat_constituentType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_constituentType).getCode();

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

  }
}



    