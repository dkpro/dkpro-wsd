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

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * Updated by JCasGen Fri Jul 26 16:04:18 CEST 2013
 * @generated */
public class WSITopic_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator =
    new FSGenerator() {
      @Override
    public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (WSITopic_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = WSITopic_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new WSITopic(addr, WSITopic_Type.this);
  			   WSITopic_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        }
            else {
                return new WSITopic(addr, WSITopic_Type.this);
            }
  	  }
    };
  /** @generated */
  public final static int typeIndexID = WSITopic.typeIndexID;
  /** @generated
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");

  /** @generated */
  final Feature casFeat_subjectOfDisambiguation;
  /** @generated */
  final int     casFeatCode_subjectOfDisambiguation;
  /** @generated */
  public String getSubjectOfDisambiguation(int addr) {
        if (featOkTst && casFeat_subjectOfDisambiguation == null) {
            jcas.throwFeatMissing("subjectOfDisambiguation", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    return ll_cas.ll_getStringValue(addr, casFeatCode_subjectOfDisambiguation);
  }
  /** @generated */
  public void setSubjectOfDisambiguation(int addr, String v) {
        if (featOkTst && casFeat_subjectOfDisambiguation == null) {
            jcas.throwFeatMissing("subjectOfDisambiguation", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    ll_cas.ll_setStringValue(addr, casFeatCode_subjectOfDisambiguation, v);}



  /** @generated */
  final Feature casFeat_constituents;
  /** @generated */
  final int     casFeatCode_constituents;
  /** @generated */
  public int getConstituents(int addr) {
        if (featOkTst && casFeat_constituents == null) {
            jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    return ll_cas.ll_getRefValue(addr, casFeatCode_constituents);
  }
  /** @generated */
  public void setConstituents(int addr, int v) {
        if (featOkTst && casFeat_constituents == null) {
            jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    ll_cas.ll_setRefValue(addr, casFeatCode_constituents, v);}

   /** @generated */
  public int getConstituents(int addr, int i) {
        if (featOkTst && casFeat_constituents == null) {
            jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    if (lowLevelTypeChecks) {
        return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_constituents), i, true);
    }
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_constituents), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_constituents), i);
  }

  /** @generated */
  public void setConstituents(int addr, int i, int v) {
        if (featOkTst && casFeat_constituents == null) {
            jcas.throwFeatMissing("constituents", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    if (lowLevelTypeChecks) {
        ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_constituents), i, v, true);
    }
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_constituents), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_constituents), i, v);
  }


  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated */
  public String getId(int addr) {
        if (featOkTst && casFeat_id == null) {
            jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /** @generated */
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null) {
            jcas.throwFeatMissing("id", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}



  /** @generated */
  final Feature casFeat_pos;
  /** @generated */
  final int     casFeatCode_pos;
  /** @generated */
  public String getPos(int addr) {
        if (featOkTst && casFeat_pos == null) {
            jcas.throwFeatMissing("pos", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    return ll_cas.ll_getStringValue(addr, casFeatCode_pos);
  }
  /** @generated */
  public void setPos(int addr, String v) {
        if (featOkTst && casFeat_pos == null) {
            jcas.throwFeatMissing("pos", "de.tudarmstadt.ukp.wsd.wsi.type.WSITopic");
        }
    ll_cas.ll_setStringValue(addr, casFeatCode_pos, v);}





  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public WSITopic_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());


    casFeat_subjectOfDisambiguation = jcas.getRequiredFeatureDE(casType, "subjectOfDisambiguation", "uima.cas.String", featOkTst);
    casFeatCode_subjectOfDisambiguation  = (null == casFeat_subjectOfDisambiguation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_subjectOfDisambiguation).getCode();


    casFeat_constituents = jcas.getRequiredFeatureDE(casType, "constituents", "uima.cas.FSArray", featOkTst);
    casFeatCode_constituents  = (null == casFeat_constituents) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_constituents).getCode();


    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();


    casFeat_pos = jcas.getRequiredFeatureDE(casType, "pos", "uima.cas.String", featOkTst);
    casFeatCode_pos  = (null == casFeat_pos) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pos).getCode();

  }
}



