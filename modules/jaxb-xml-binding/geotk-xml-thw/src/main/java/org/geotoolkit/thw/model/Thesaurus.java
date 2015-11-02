/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.thw.model;

import java.util.List;
import org.geotoolkit.skos.xml.Concept;
import org.geotoolkit.skos.xml.RDF;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface Thesaurus {

    /**
     * Return an URI identifying the thesaurus.
     *
     * @return
     */
    String getURI();

    /**
     * Return the version of the thesaurus.
     *
     * @return
     */
    String getVersion();

    /**
     * Return a description of the thesaurus.
     *
     * @return
     */
    String getDescription();
    
    /**
     * Set the description of the thesaurus.
     */
    void setDescription(final String description);

    /**
     * Return the default language of the thesaurus.
     *
     * @return
     */
    ISOLanguageCode getDefaultLanguage();
    
    /**
     * Set the default language of the thesaurus.
     */
    void setDefaultLanguage(final ISOLanguageCode lang);

    /**
     * Return the database schema (specific to SQL implementation) of the thesaurus.
     */
    String getSchema();
    
    /**
     * Return the name of the thesaurus.
     */
    String getName();
    
    /**
     * Set the name of the thesaurus.
     */
    void setName(String name);
    
    
    /**
     * Return the state (enabled / disabled) of the thesaurus.
     * @return
     */
    boolean getState();
    
    /**
     * Set the state (enabled / disabled) of the thesaurus.
     */
    void setState(boolean state);

    /**
     * Return the supported languages of this Thesaurus.
     */
    List<ISOLanguageCode> getLanguage();
    
    /**
     * Search in the thesaurus the concept matching the given term.
     * Rteun a list of concpet with a score of match for each concept.
     * 
     * @param term
     * @return
     */
    List<ScoredConcept> search(final String term, final ISOLanguageCode language);

    /**
     * Search in the thesaurus the concept matching the given term with the specified search mode.
     *
     * @param term
     * @param searchMode
     * @param geometric
     * @return
     */
    List<Concept> search(final String term, final int searchMode, final boolean geometric, final List<String> themes, final ISOLanguageCode language);
    
    /**
     * Return the top most concept of this thesaurus.
     * 
     * @param themes can be {@code null}
     */
    List<Concept> getTopMostConcepts(final List<String> themes, final ISOLanguageCode language);
    
    /**
     * Return the hierarchy roots concept of this thesaurus.
     * 
     * @param themes can be {@code null}
     */
    List<Concept> getHierarchyRoots(final List<String> themes);

    /**
     * Return the concept identified by the specified URI only if the concept got a geometry bounded.
     *
     * @param uriConcept
     * @return
     */
    Concept getGeometricConcept(final String uriConcept);

    /**
     * Return the concept identified by the specified URI.
     *
     * @param uriConcept
     * @return
     */
    Concept getConcept(final String uriConcept);
    
    /**
     * Return the concept identified by the specified URI.
     * with only the localized label in a specific language
     *
     * @param uriConcept
     * @return
     */
    Concept getConcept(final String uriConcept, final ISOLanguageCode language);

    /**
     * Return The complete list of terms (prefered + alternative label) of the thesaurus
     */
    List<String> getAllLabels(final ISOLanguageCode language);

    /**
     * Return The complete list of terms (prefered + alternative label) of the thesaurus,
     * Limited by the specified parameters.
     */
    List<String> getAllLabels(final int limit, final ISOLanguageCode language);

    /**
     * Return The complete list of terms (prefered + alternative label) of the thesaurus
     */
    List<String> getAllPreferedLabels(final ISOLanguageCode language);

    /**
     * Return The complete list of prefered label of the thesaurus.
     * Limited by the specified parameters.
     */
    List<String> getAllPreferedLabels(final int limit, final ISOLanguageCode language);
    
    /**
     * Return The complete list of concept of the thesaurus,
     * Limited by the specified parameters.
     */
    List<Concept> getAllConcepts(final int limit);

    /**
     * Search in the thesaurus the labels matching the given term with the specified search mode.
     *
     * @param term
     * @param searchMode
     * @param geometric
     * @return
     */
    List<String> searchLabels(final String term, final int searchMode,final List<String> themes, final ISOLanguageCode language);
    
    /**
     * Returns a List that contains all words from the thesaurus.
     * 
     * @param thesaurus
     * @param buffer
     * @return 
     */
    List<Word> getWords(final List<Word> buffer, final ISOLanguageCode language);

    /**
     * Close the connection to the database and clear the cache. 
     * (depending on implementation)
     */
    void close();

    String getConceptTheme(String uriConcept);

    /**
     *  Return a full description of the thesaurus in RDF format.
     */
    RDF toRDF();
    

    /**
     * Return a partial description of the thesaurus resulting in all the sub Concept of the specified one
     * in RDF format.
     * 
     * @param root A concept or {@code null} for a full descripton of the thesaurus.
     */
    RDF toRDF(final Concept root);
}
