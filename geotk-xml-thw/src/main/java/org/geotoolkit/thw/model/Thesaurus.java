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
public interface Thesaurus extends AutoCloseable {

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
     * Change the description of the thesaurus.
     *
     * @param description The description to set
     */
    void setDescription(final String description);

    /**
     * Return the default language of the thesaurus.
     *
     * @return
     */
    ISOLanguageCode getDefaultLanguage();

    /**
     * Change the default language of the thesaurus.
     *
     * @param lang A ISO language.
     */
    void setDefaultLanguage(final ISOLanguageCode lang);

    /**
     * Return the database schema (specific to SQL implementation) of the thesaurus.
     * @return
     */
    String getSchema();

    /**
     * Return the name of the thesaurus.
     * @return
     */
    String getName();

    /**
     * Chnage the name of the thesaurus.
     *
     * @param name The new name of the thesaurus
     */
    void setName(String name);


    /**
     * Return the state (enabled / disabled) of the thesaurus.
     * @return
     */
    boolean getState();

    /**
     * Change the state (enabled / disabled) of the thesaurus.
     * @param state
     */
    void setState(boolean state);

    /**
     * Return the supported languages of this Thesaurus.
     * @return
     */
    List<ISOLanguageCode> getLanguage();

    /**
     * Try to find the concept matching the specified term.
     *
     * @param brutTerm The term to search.
     * @param language if not {@code null} add a language filter to the search.
     * @return
     */
    List<ScoredConcept> search(final String brutTerm, final ISOLanguageCode language);

    /**
     * Try to find the concept matching the specified term.
     *
     * @param brutTerm The term to search.
     * @param searchMode The mode used to search.
     * @param geometric Special flag for geometric thesaurus.
     * @param themes If not {@code null} add a theme filter to the search.
     * @param language if not {@code null} add a language filter to the search.
     *
     * @return
     */
    List<Concept> search(final String brutTerm, final int searchMode, final boolean geometric, final List<String> themes, final ISOLanguageCode language);

    /**
     * Return the top most concept of this thesaurus.
     *
     * @param themes If not {@code null} add a theme filter to the search.
     * @param language if not {@code null} add a language filter to the search.
     *
     * @return A list of root concepts.
     */
    List<Concept> getTopMostConcepts(final List<String> themes, final ISOLanguageCode language);

    /**
     * Return the hierarchy roots concept of this thesaurus.
     *
     * @param themes If not {@code null} add a theme filter to the search.
     *
     * @return A list of hierarchy root concepts.
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
     * @param uriConcept The unique identifier of the concept
     * @param language if not {@code null} the return concept will be localized only with this language.
     * @return
     */
    Concept getConcept(final String uriConcept, final ISOLanguageCode language);

    /**
     * Return The complete list of terms (prefered + alternative label) of the thesaurus
     *
     * @param language if not {@code null} the return labels will be localized only with this language
     * @return  A list of localized labels
     */
    List<String> getAllLabels(final ISOLanguageCode language);

    /**
     * Return The complete list of terms (prefered + alternative label) of the thesaurus,
     * Limited by the specified parameters.
     *
     * @param limit if not equal to -1, the results size will not exceed this limit.
     * @param language if not {@code null} the return labels will be localized only with this language
     * @return  A list of localized labels
     */
    List<String> getAllLabels(final int limit, final ISOLanguageCode language);

    /**
     * Return The complete list of terms (prefered + alternative label) of the thesaurus
     * @param language if not {@code null} the return labels will be localized only with this language
     *
     * @return  A list of localized labels
     */
    List<String> getAllPreferedLabels(final ISOLanguageCode language);

    /**
     * Return The complete list of prefered label of the thesaurus.
     * Limited by the specified parameters.
     *
     * @param limit if not equal to -1, the results size will not exceed this limit.
     * @param language if not {@code null} the return labels will be localized only with this language
     *
     * @return
     */
    List<String> getAllPreferedLabels(final int limit, final ISOLanguageCode language);

    /**
     * Return The complete list of concept of the thesaurus,
     * Limited by the specified parameters.
     *
     * @param limit if not equal to -1, the results size will not exceed this limit.
     * @return A full list of concept in this thesaurus.
     */
    List<Concept> getAllConcepts(final int limit);

    /**
     * Try to find the concept matching the specified term.
     *
     * @param brutTerm The term to search.
     * @param searchMode The mode used to search.
     * @param themes If not {@code null} add a theme filter to the search.
     * @param language if not {@code null} add a language filter to the search.
     *
     * @return
     */
    List<String> searchLabels(final String brutTerm, final int searchMode,final List<String> themes, final ISOLanguageCode language);

    /**
     * Returns a List that contains all words from the thesaurus.
     *
     * @param buffer The buffer to be fill with word.
     * @param language if not {@code null} add a language filter to the search.
     *
     * @return
     */
    List<Word> getWords(final List<Word> buffer, final ISOLanguageCode language);

    /**
     * Close the ressources and clear the cache.
     * (depending on implementation)
     */
    @Override
    void close();

    String getConceptTheme(String uriConcept);

    /**
     *  Return a full description of the thesaurus in RDF format.
     * @return
     */
    RDF toRDF();


    /**
     * Return a partial description of the thesaurus resulting in all the sub Concept of the specified one
     * in RDF format.
     *
     * @param root A concept or {@code null} for a full descripton of the thesaurus.
     * @return
     */
    RDF toRDF(final Concept root);
}
