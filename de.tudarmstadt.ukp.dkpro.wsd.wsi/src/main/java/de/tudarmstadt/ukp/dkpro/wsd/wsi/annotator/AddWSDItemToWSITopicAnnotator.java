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
package de.tudarmstadt.ukp.dkpro.wsd.wsi.annotator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.type.WSDItem;
import de.tudarmstadt.ukp.dkpro.wsd.wsi.type.WSITopic;

//import de.tudarmstadt.ukp.wsd.induction.type.WSITopic;

/**
 * Annotates a document from semeval 2010 wsi task with WSDItem annotations
 * 
 * @author zorn
 * 
 */
public class AddWSDItemToWSITopicAnnotator
    extends JCasAnnotator_ImplBase
{

    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

    }

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
        typeSystemInit(aJCas.getTypeSystem());
        final DocumentMetaData documentMetaData = DocumentMetaData.get(aJCas);
        final String docId = documentMetaData.getDocumentId();
        final String collectionId = documentMetaData.getCollectionId();
        final String url = documentMetaData.getDocumentUri();
        try {
            final WSITopic wsiTopic = JCasUtil.selectSingle(aJCas, WSITopic.class);
            final String term = wsiTopic.getSubjectOfDisambiguation().toLowerCase();
            String[] terms = term.split(" ");
            int index = 0;
            Token t1 = null;
            for (final Token token : JCasUtil.select(aJCas, Token.class)) {
                String lemma = token.getFeatureValueAsString(this.lemmaValue);
                if (lemma == null)
                    lemma = token.getCoveredText();

                if (lemma != null && terms[index].equals(lemma.toLowerCase())) {
                    if (index == terms.length - 1) {
                        if (t1 == null)
                            t1 = token;
                        newWsdItem(aJCas, term, null, t1.getBegin(),
                                token.getEnd() - t1.getBegin(), null, lemma);
                    }
                    else {

                        t1 = token;
                        index++;
                    }
                }
                else
                    index = 0;
            }
            for (final Lemma lemma : JCasUtil.select(aJCas, Lemma.class)) {

                if (term.equals(lemma.getValue().toLowerCase())) {

                    final WSDItem wsdItem = newWsdItem(aJCas, term, null, lemma.getBegin(),
                            lemma.getEnd() - lemma.getBegin(), null, lemma.getValue());

                }
            }
        }
        catch (Exception e) {
        }

    }

    protected POS sensevalPosToPOS(String pos)
    {
        if (pos == null) {
            return null;
        }
        if (pos.equals("a")) {
            return POS.ADJ;
        }
        else if (pos.equals("r")) {
            return POS.ADV;
        }
        else if (pos.equals("v")) {
            return POS.VERB;
        }
        else if (pos.equals("n")) {
            return POS.NOUN;
        }
        else {
            return null;
        }
    }

    /**
     * Creates a new WSDItem annotation and adds it to the annotation index.
     * 
     * @param jCas
     *            The CAS in which to create the annotation.
     * @param id
     *            An identifier for the annotation.
     * @param constituentType
     *            A string representing the constituent type (e.g., "head", "satellite").
     * @param offset
     *            The index of the first character of the annotation in the document.
     * @param length
     *            The length, in characters, of the annotation.
     * @param pos
     *            The part of speech, if known, otherwise null.
     * @param lemma
     *            The lemmatized form, if known, otherwise null.
     * @return The new annotation.
     */
    protected WSDItem newWsdItem(JCas jCas, String id, String constituentType, int offset,
            int length, String pos, String lemma)
    {
        final WSDItem w = new WSDItem(jCas);
        w.setBegin(offset);
        w.setEnd(offset + length);
        w.setId(id);
        if (pos != null) {
            w.setPos(sensevalPosToPOS(pos).toString());
        }
        else
            w.setPos(POS.NOUN.name());
        w.setSubjectOfDisambiguation(id);
        w.addToIndexes();
        // System.out.println(w);
        return w;
    }

    private Type lemmaTag;
    private Type tokenTag;
    private Feature lemmaValue;
    private Feature featLemma;
    private Feature featPos;

    public void typeSystemInit(TypeSystem aTypeSystem)
        throws AnalysisEngineProcessException
    {

        this.lemmaTag = aTypeSystem.getType(Lemma.class.getName());
        this.tokenTag = aTypeSystem.getType(Token.class.getName());
        this.lemmaValue = this.lemmaTag.getFeatureByBaseName("value");
        this.featLemma = this.tokenTag.getFeatureByBaseName("lemma");
        this.featPos = this.tokenTag.getFeatureByBaseName("pos");
    }

}
