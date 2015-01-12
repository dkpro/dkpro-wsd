/*******************************************************************************
 * Copyright 2015
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

package de.tudarmstadt.ukp.dkpro.wsd.si.lsr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity.PoS;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.ResourceLoaderException;
import de.tudarmstadt.ukp.dkpro.wsd.UnorderedPair;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseTaxonomy;
import de.tudarmstadt.ukp.dkpro.wsd.si.lsr.util.LsrSenseInventoryUtil;
import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * A sense inventory wrapping JLSR
 *
 * @author Torsten Zesch <zesch@ukp.informatik.tu-darmstadt.de>
 *
 */
public class LsrSenseInventory
    implements SenseTaxonomy
{

    private final LexicalSemanticResource lsr;

    public LsrSenseInventory(String resource, String language)
        throws ResourceLoaderException
    {
        lsr = ResourceFactory.getInstance().get(resource, language);
    }

    @Override
    public String getMostFrequentSense(String sod)
        throws SenseInventoryException, UnsupportedOperationException
    {
        try {
            return lsr.getMostFrequentEntity(sod).getId();
        }
        catch (LexicalSemanticResourceException e) {
            throw new SenseInventoryException(e);
        }
    }

    @Override
    public String getMostFrequentSense(String sod, POS pos)
        throws SenseInventoryException, UnsupportedOperationException
    {
        try {
            PoS lsrPos = LsrSenseInventoryUtil.convertPos(pos);
            return lsr.getMostFrequentEntity(sod, lsrPos).getId();
        }
        catch (LexicalSemanticResourceException e) {
            throw new SenseInventoryException(e);
        }
    }

    @Override
    public String getSenseDescription(String senseId)
        throws SenseInventoryException
    {
        try {
            String description = lsr.getGloss(lsr.getEntityById(senseId));
            return (description == null) ? "" : description;
        }
        catch (LexicalSemanticResourceException e) {
            throw new SenseInventoryException(e);
        }
    }

    @Override
    public POS getPos(String senseId)
        throws SenseInventoryException
    {
        Entity entity;
        try {
            entity = lsr.getEntityById(senseId);
        }
        catch (LexicalSemanticResourceException e) {
            throw new SenseInventoryException(e);
        }
        return LsrSenseInventoryUtil.convertPos(entity.getPos());
    }

    @Override
    public Map<String, List<String>> getSenseInventory()
        throws SenseInventoryException
    {
        System.out.println("Be careful, this is a quite slow operation.");
        Map<String, List<String>> senseInventory = new HashMap<String, List<String>>();

        try {
            for (Entity entity : lsr.getEntities()) {
                for (String lexeme : entity.getLexemes()) {
                    if (!senseInventory.containsKey(lexeme)) {
                        senseInventory.put(lexeme, new ArrayList<String>());
                    }
                    String sense = entity.getId();
                    List<String> senses = senseInventory.get(lexeme);
                    senses.add(sense);
                    senseInventory.put(lexeme, senses);
                }
            }
        }
        catch (LexicalSemanticResourceException e) {
            throw new SenseInventoryException(e);
        }

        return senseInventory;
    }

    @Override
    public String getSenseInventoryName()
    {
        return lsr.getResourceName() + "_" + lsr.getResourceVersion() + "_LSR";
    }

    @Override
    public List<String> getSenses(String sod)
        throws SenseInventoryException
    {
        try {
            List<String> senses = new ArrayList<String>();
            for (Entity entity : lsr.getEntity(sod)) {
                senses.add(entity.getId());
            }
            return senses;
        }
        catch (LexicalSemanticResourceException e) {
            throw new SenseInventoryException(e);
        }
    }

    @Override
    public List<String> getSenses(String sod, POS pos)
        throws SenseInventoryException
    {
        try {
            PoS lsrPos = LsrSenseInventoryUtil.convertPos(pos);
            List<String> senses = new ArrayList<String>();
            for (Entity entity : lsr.getEntity(sod, lsrPos)) {
                senses.add(entity.getId());
            }
            return senses;
        }
        catch (LexicalSemanticResourceException e) {
            throw new SenseInventoryException(e);
        }
    }

    @Override
    public UndirectedGraph<String, UnorderedPair<String>> getUndirectedGraph()
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getSenseNeighbours(String senseId)
        throws SenseInventoryException, UnsupportedOperationException
    {
        Set<Entity> entities;
        try {
            entities = lsr.getNeighbors(lsr.getEntityById(senseId));
        }
        catch (LexicalSemanticResourceException e) {
            throw new SenseInventoryException(e);
        }
        Set<String> neighbours = new HashSet<String>();
        for (Entity entity : entities) {
            neighbours.add(entity.getId());
        }
        return neighbours;
    }

    @Override
    public void setUndirectedGraph(
            UndirectedGraph<String, UnorderedPair<String>> graph)
        throws SenseInventoryException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUseCount(String senseId)
        throws SenseInventoryException
    {
        throw new UnsupportedOperationException();
    }
}