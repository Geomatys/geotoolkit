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
package org.geotoolkit.thw.xml;

import org.geotoolkit.thw.model.Mapping;
import org.geotoolkit.thw.model.LanguageString;
import org.geotoolkit.thw.model.XmlThesaurus;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.geotk.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName GET_ALL_PREFERED_LABEL_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getAllPreferedLabelResponse");
    private static final QName GET_GEOMETRIC_CONCEPT_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getGeometricConceptResponse");
    private static final QName GET_CONCEPT_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getConceptResponse");
    private static final QName GET_NUMERED_CONCEPT_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getNumeredConceptResponse");
    private static final QName GET_AGGREGATED_CONCEPT_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getAggregatedConceptResponse");
    private static final QName GET_CONCEPT_QNAME = new QName("http://ws.geotk.org/", "getConcept");
    private static final QName GET_NUMERED_CONCEPT_QNAME = new QName("http://ws.geotk.org/", "getNumeredConcept");
    private static final QName GET_AGGREGATED_CONCEPT_QNAME = new QName("http://ws.geotk.org/", "getAggregatedConcept");
    private static final QName GET_AGGREGATEDIDS_RESPONSE_CONCEPT_QNAME = new QName("http://ws.geotk.org/", "getAggregatedConceptIdsResponse");
    private static final QName GET_TOPMOST_CONCEPTS_QNAME = new QName("http://ws.geotk.org/", "getTopmostConcepts");
    private static final QName FETCH_THEMES_QNAME = new QName("http://ws.geotk.org/", "fetchThemes");
    private static final QName GET_AVAILABLE_LANGUAGES_QNAME = new QName("http://ws.geotk.org/", "getAvailableLanguages");
    private static final QName GET_ALL_CONCEPT_RELATIVES_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getAllConceptRelativesResponse");
    private static final QName GET_CONCEPTS_MATCHING_REGEX_BY_THESAURUS_QNAME = new QName("http://ws.geotk.org/", "getConceptsMatchingRegexByThesaurus");
    private static final QName GET_TOPMOST_CONCEPTS_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getTopmostConceptsResponse");
    private static final QName THESAURUS_QNAME = new QName("http://ws.geotk.org/", "Thesaurus");
    private static final QName GET_NUMERED_CONCEPTS_MATCHING_KEYWORD_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getNumeredConceptsMatchingKeywordResponse");
    private static final QName GET_AGGREGATED_CONCEPTS_MATCHING_KEYWORD_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getAggregatedConceptsMatchingKeywordResponse");
    private static final QName GET_CONCEPTS_MATCHING_KEYWORD_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getConceptsMatchingKeywordResponse");
    private static final QName GET_ALL_TRANSLATIONS_FOR_CONCEPT_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getAllTranslationsForConceptResponse");
    private static final QName GET_LINKED_CSW_QNAME = new QName("http://ws.geotk.org/", "getLinkedCsw");
    private static final QName GET_LINKED_CSW_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getLinkedCswResponse");
    private static final QName GET_AVAILABLE_LANGUAGES_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getAvailableLanguagesResponse");
    private static final QName GET_SUPPORTED_LANGS_QNAME = new QName("http://ws.geotk.org/", "getSupportedLangs");
    private static final QName GET_SUPPORTED_LANGS_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getSupportedLangsResponse");
    private static final QName FETCH_THEMES_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "fetchThemesResponse");
    private static final QName GET_AVAILABLE_THESAURI_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getAvailableThesauriResponse");
    private static final QName GET_AVAILABLE_THESAURI_QNAME = new QName("http://ws.geotk.org/", "getAvailableThesauri");
    private static final QName FETCH_GROUPS_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "fetchGroupsResponse");
    private static final QName GET_RELEATED_CONCEPTS_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getRelatedConceptsResponse");
    private static final QName GET_ALL_CONCEPT_RELATIVES_QNAME = new QName("http://ws.geotk.org/", "getAllConceptRelatives");
    private static final QName FETCH_GROUPS_QNAME = new QName("http://ws.geotk.org/", "fetchGroups");
    private static final QName GET_ALL_TRANSLATIONS_FOR_CONCEPT_QNAME = new QName("http://ws.geotk.org/", "getAllTranslationsForConcept");
    private static final QName GET_CONCEPTS_MATCHING_REGEX_BY_THESAURUS_RESPONSE_QNAME = new QName("http://ws.geotk.org/", "getConceptsMatchingRegexByThesaurusResponse");
    private static final QName GET_RELATED_CONCEPTS_QNAME = new QName("http://ws.geotk.org/", "getRelatedConcepts");
    private static final QName GET_CONCEPTS_MATCHING_KEYWORD_QNAME = new QName("http://ws.geotk.org/", "getConceptsMatchingKeyword");
    private static final QName GET_ALL_PREFERED_LABEL_QNAME = new QName("http://ws.geotk.org/", "getAllPreferedLabel");
    private static final QName GET_GEOMETRIC_CONCEPT_QNAME = new QName("http://ws.geotk.org/", "getGeometricConcept");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.geotk.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAllPreferedLabel }
     *
     */
    public GetAllPreferedLabel createGetAllPreferedLabel() {
        return new GetAllPreferedLabel();
    }

    /**
     * Create an instance of {@link GetAllPreferedLabelResponse }
     *
     */
    public GetAllPreferedLabelResponse createGetAllPreferedLabelResponse() {
        return new GetAllPreferedLabelResponse();
    }

    /**
     * Create an instance of {@link FetchGroupsResponse }
     * 
     */
    public FetchGroupsResponse createFetchGroupsResponse() {
        return new FetchGroupsResponse();
    }

    /**
     * Create an instance of {@link FetchThemes }
     * 
     */
    public FetchThemes createFetchThemes() {
        return new FetchThemes();
    }

    /**
     * Create an instance of {@link GetTopmostConceptsResponse }
     * 
     */
    public GetTopmostConceptsResponse createGetTopmostConceptsResponse() {
        return new GetTopmostConceptsResponse();
    }

    /**
     * Create an instance of {@link GetAllTranslationsForConceptResponse }
     * 
     */
    public GetAllTranslationsForConceptResponse createGetAllTranslationsForConceptResponse() {
        return new GetAllTranslationsForConceptResponse();
    }

    /**
     * Create an instance of {@link FetchGroups }
     * 
     */
    public FetchGroups createFetchGroups() {
        return new FetchGroups();
    }

    /**
     * Create an instance of {@link GetRelatedConcepts }
     * 
     */
    public GetRelatedConcepts createGetRelatedConcepts() {
        return new GetRelatedConcepts();
    }

    /**
     * Create an instance of {@link GetAvailableLanguagesResponse }
     * 
     */
    public GetAvailableLanguagesResponse createGetAvailableLanguagesResponse() {
        return new GetAvailableLanguagesResponse();
    }

    /**
     * Create an instance of {@link GetRelatedConceptsResponse }
     * 
     */
    public GetRelatedConceptsResponse createGetRelatedConceptsResponse() {
        return new GetRelatedConceptsResponse();
    }

    /**
     * Create an instance of {@link LanguageString }
     * 
     */
    public LanguageString createLanguageString() {
        return new LanguageString();
    }

    /**
     * Create an instance of {@link GetConceptsMatchingKeyword }
     * 
     */
    public GetConceptsMatchingKeyword createGetConceptsMatchingKeyword() {
        return new GetConceptsMatchingKeyword();
    }

    /**
     * Create an instance of {@link GetConceptsMatchingRegexByThesaurusResponse }
     * 
     */
    public GetConceptsMatchingRegexByThesaurusResponse createGetConceptsMatchingRegexByThesaurusResponse() {
        return new GetConceptsMatchingRegexByThesaurusResponse();
    }

    /**
     * Create an instance of {@link FetchThemesResponse }
     * 
     */
    public FetchThemesResponse createFetchThemesResponse() {
        return new FetchThemesResponse();
    }

    /**
     * Create an instance of {@link XmlThesaurus }
     * 
     */
    public XmlThesaurus createXmlThesaurus() {
        return new XmlThesaurus();
    }

    /**
     * Create an instance of {@link GetConcept }
     * 
     */
    public GetConcept createGetConcept() {
        return new GetConcept();
    }

    /**
     * Create an instance of {@link GetAvailableThesauriResponse }
     * 
     */
    public GetAvailableThesauriResponse createGetAvailableThesauriResponse() {
        return new GetAvailableThesauriResponse();
    }

    /**
     * Create an instance of {@link GetAllConceptRelativesResponse }
     * 
     */
    public GetAllConceptRelativesResponse createGetAllConceptRelativesResponse() {
        return new GetAllConceptRelativesResponse();
    }

    /**
     * Create an instance of {@link GetAllTranslationsForConcept }
     * 
     */
    public GetAllTranslationsForConcept createGetAllTranslationsForConcept() {
        return new GetAllTranslationsForConcept();
    }

    /**
     * Create an instance of {@link GetAllConceptRelatives }
     * 
     */
    public GetAllConceptRelatives createGetAllConceptRelatives() {
        return new GetAllConceptRelatives();
    }

    /**
     * Create an instance of {@link GetConceptResponse }
     * 
     */
    public GetConceptResponse createGetConceptResponse() {
        return new GetConceptResponse();
    }

    /**
     * Create an instance of {@link GetTopmostConcepts }
     * 
     */
    public GetTopmostConcepts createGetTopmostConcepts() {
        return new GetTopmostConcepts();
    }

    /**
     * Create an instance of {@link GetConceptsMatchingKeywordResponse }
     * 
     */
    public GetConceptsMatchingKeywordResponse createGetConceptsMatchingKeywordResponse() {
        return new GetConceptsMatchingKeywordResponse();
    }
    
    /**
     * Create an instance of {@link GetNumeredConceptsMatchingKeywordResponse }
     * 
     */
    public GetNumeredConceptsMatchingKeywordResponse createGetNumeredConceptsMatchingKeywordResponse() {
        return new GetNumeredConceptsMatchingKeywordResponse();
    }
    
    /**
     * Create an instance of {@link GetConceptsMatchingKeywordResponse }
     * 
     */
    public GetAggregatedConceptsMatchingKeywordResponse createGetAggregatedConceptsMatchingKeywordResponse() {
        return new GetAggregatedConceptsMatchingKeywordResponse();
    }
    
    /**
     * Create an instance of {@link GetConceptsMatchingKeyword }
     * 
     */
    public GetLinkedCsw createGetLinkedCsw() {
        return new GetLinkedCsw();
    }
    
    /**
     * Create an instance of {@link GetConceptsMatchingKeyword }
     * 
     */
    public GetLinkedCswResponse createGetLinkedCswResponse() {
        return new GetLinkedCswResponse();
    }
    
    /**
     * Create an instance of {@link GetConceptsMatchingKeywordResponse }
     * 
     */
    public GetNumeredConceptsMatchingKeyword createGetNumeredConceptsMatchingKeyword() {
        return new GetNumeredConceptsMatchingKeyword();
    }
    
    /**
     * Create an instance of {@link GetConceptsMatchingKeywordResponse }
     * 
     */
    public GetAggregatedConceptsMatchingKeyword createGetAggregatedConceptsMatchingKeyword() {
        return new GetAggregatedConceptsMatchingKeyword();
    }

    /**
     * Create an instance of {@link GetAvailableLanguages }
     * 
     */
    public GetAvailableLanguages createGetAvailableLanguages() {
        return new GetAvailableLanguages();
    }

    /**
     * Create an instance of {@link GetConceptsMatchingRegexByThesaurus }
     * 
     */
    public GetConceptsMatchingRegexByThesaurus createGetConceptsMatchingRegexByThesaurus() {
        return new GetConceptsMatchingRegexByThesaurus();
    }

    /**
     * Create an instance of {@link GetAvailableThesauri }
     * 
     */
    public GetAvailableThesauri createGetAvailableThesauri() {
        return new GetAvailableThesauri();
    }

    /**
     * Create an instance of {@link Mapping }
     * 
     */
    public Mapping createMapping() {
        return new Mapping();
    }

    /**
     * Create an instance of {@link GetSupportedLangs }
     * 
     */
    public GetSupportedLangs createGetSupportedLangs() {
        return new GetSupportedLangs();
    }

    /**
     * Create an instance of {@link GetSupportedLangsResponse }
     * 
     */
    public GetSupportedLangsResponse createGetSupportedLangsResponse() {
        return new GetSupportedLangsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPreferedLabelResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAllPreferedLabelResponse")
    public JAXBElement<GetAllPreferedLabelResponse> createGetAllPreferedLabelResponse(GetAllPreferedLabelResponse value) {
        return new JAXBElement<GetAllPreferedLabelResponse>(GET_ALL_PREFERED_LABEL_RESPONSE_QNAME, GetAllPreferedLabelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPreferedLabelResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAggregatedConceptIdsResponse")
    public JAXBElement<GetAggregatedConceptIdsResponse> createGetAggregatedConceptIdsResponse(GetAggregatedConceptIdsResponse value) {
        return new JAXBElement<GetAggregatedConceptIdsResponse>(GET_AGGREGATEDIDS_RESPONSE_CONCEPT_QNAME, GetAggregatedConceptIdsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPreferedLabelResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getGeometricConceptResponse")
    public JAXBElement<GetGeometricConceptResponse> createGetGeometricConceptResponse(GetGeometricConceptResponse value) {
        return new JAXBElement<GetGeometricConceptResponse>(GET_GEOMETRIC_CONCEPT_RESPONSE_QNAME, GetGeometricConceptResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPreferedLabel }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAllPreferedLabel")
    public JAXBElement<GetAllPreferedLabel> createGetAllPreferedLabel(GetAllPreferedLabel value) {
        return new JAXBElement<GetAllPreferedLabel>(GET_ALL_PREFERED_LABEL_QNAME, GetAllPreferedLabel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPreferedLabel }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getGeometricConcept")
    public JAXBElement<GetGeometricConcept> createGetGeometricConcept(GetGeometricConcept value) {
        return new JAXBElement<GetGeometricConcept>(GET_GEOMETRIC_CONCEPT_QNAME, GetGeometricConcept.class, null, value);
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getConceptResponse")
    public JAXBElement<GetConceptResponse> createGetConceptResponse(GetConceptResponse value) {
        return new JAXBElement<GetConceptResponse>(GET_CONCEPT_RESPONSE_QNAME, GetConceptResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getNumeredConceptResponse")
    public JAXBElement<GetNumeredConceptResponse> createGetNumeredConceptResponse(GetNumeredConceptResponse value) {
        return new JAXBElement<GetNumeredConceptResponse>(GET_NUMERED_CONCEPT_RESPONSE_QNAME, GetNumeredConceptResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAggregatedConceptResponse")
    public JAXBElement<GetAggregatedConceptResponse> createGetAggregatedConceptResponse(GetAggregatedConceptResponse value) {
        return new JAXBElement<GetAggregatedConceptResponse>(GET_AGGREGATED_CONCEPT_RESPONSE_QNAME, GetAggregatedConceptResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConcept }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getConcept")
    public JAXBElement<GetConcept> createGetConcept(GetConcept value) {
        return new JAXBElement<GetConcept>(GET_CONCEPT_QNAME, GetConcept.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTopmostConcepts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getTopmostConcepts")
    public JAXBElement<GetTopmostConcepts> createGetTopmostConcepts(GetTopmostConcepts value) {
        return new JAXBElement<GetTopmostConcepts>(GET_TOPMOST_CONCEPTS_QNAME, GetTopmostConcepts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchThemes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "fetchThemes")
    public JAXBElement<FetchThemes> createFetchThemes(FetchThemes value) {
        return new JAXBElement<FetchThemes>(FETCH_THEMES_QNAME, FetchThemes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableLanguages }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAvailableLanguages")
    public JAXBElement<GetAvailableLanguages> createGetAvailableLanguages(GetAvailableLanguages value) {
        return new JAXBElement<GetAvailableLanguages>(GET_AVAILABLE_LANGUAGES_QNAME, GetAvailableLanguages.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllConceptRelativesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAllConceptRelativesResponse")
    public JAXBElement<GetAllConceptRelativesResponse> createGetAllConceptRelativesResponse(GetAllConceptRelativesResponse value) {
        return new JAXBElement<GetAllConceptRelativesResponse>(GET_ALL_CONCEPT_RELATIVES_RESPONSE_QNAME, GetAllConceptRelativesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptsMatchingRegexByThesaurus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getConceptsMatchingRegexByThesaurus")
    public JAXBElement<GetConceptsMatchingRegexByThesaurus> createGetConceptsMatchingRegexByThesaurus(GetConceptsMatchingRegexByThesaurus value) {
        return new JAXBElement<GetConceptsMatchingRegexByThesaurus>(GET_CONCEPTS_MATCHING_REGEX_BY_THESAURUS_QNAME, GetConceptsMatchingRegexByThesaurus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTopmostConceptsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getTopmostConceptsResponse")
    public JAXBElement<GetTopmostConceptsResponse> createGetTopmostConceptsResponse(GetTopmostConceptsResponse value) {
        return new JAXBElement<GetTopmostConceptsResponse>(GET_TOPMOST_CONCEPTS_RESPONSE_QNAME, GetTopmostConceptsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlThesaurus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "Thesaurus")
    public JAXBElement<XmlThesaurus> createThesaurus(XmlThesaurus value) {
        return new JAXBElement<XmlThesaurus>(THESAURUS_QNAME, XmlThesaurus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptsMatchingKeywordResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getConceptsMatchingKeywordResponse")
    public JAXBElement<GetConceptsMatchingKeywordResponse> createGetConceptsMatchingKeywordResponse(GetConceptsMatchingKeywordResponse value) {
        return new JAXBElement<GetConceptsMatchingKeywordResponse>(GET_CONCEPTS_MATCHING_KEYWORD_RESPONSE_QNAME, GetConceptsMatchingKeywordResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptsMatchingKeywordResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getNumeredConceptsMatchingKeywordResponse")
    public JAXBElement<GetNumeredConceptsMatchingKeywordResponse> createGetNumeredConceptsMatchingKeywordResponse(GetNumeredConceptsMatchingKeywordResponse value) {
        return new JAXBElement<GetNumeredConceptsMatchingKeywordResponse>(GET_NUMERED_CONCEPTS_MATCHING_KEYWORD_RESPONSE_QNAME, GetNumeredConceptsMatchingKeywordResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptsMatchingKeywordResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAggregatedConceptsMatchingKeywordResponse")
    public JAXBElement<GetAggregatedConceptsMatchingKeywordResponse> createGetAggregatedConceptsMatchingKeywordResponse(GetAggregatedConceptsMatchingKeywordResponse value) {
        return new JAXBElement<GetAggregatedConceptsMatchingKeywordResponse>(GET_AGGREGATED_CONCEPTS_MATCHING_KEYWORD_RESPONSE_QNAME, GetAggregatedConceptsMatchingKeywordResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllTranslationsForConceptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAllTranslationsForConceptResponse")
    public JAXBElement<GetAllTranslationsForConceptResponse> createGetAllTranslationsForConceptResponse(GetAllTranslationsForConceptResponse value) {
        return new JAXBElement<GetAllTranslationsForConceptResponse>(GET_ALL_TRANSLATIONS_FOR_CONCEPT_RESPONSE_QNAME, GetAllTranslationsForConceptResponse.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllTranslationsForConceptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getLinkedCswResponse")
    public JAXBElement<GetLinkedCswResponse> createGetLinkedCswResponse(GetLinkedCswResponse value) {
        return new JAXBElement<GetLinkedCswResponse>(GET_LINKED_CSW_RESPONSE_QNAME, GetLinkedCswResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableLanguagesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAvailableLanguagesResponse")
    public JAXBElement<GetAvailableLanguagesResponse> createGetAvailableLanguagesResponse(GetAvailableLanguagesResponse value) {
        return new JAXBElement<GetAvailableLanguagesResponse>(GET_AVAILABLE_LANGUAGES_RESPONSE_QNAME, GetAvailableLanguagesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSupportedLangs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getSupportedLangs")
    public JAXBElement<GetSupportedLangs> createGetSupportedLangs(GetSupportedLangs value) {
        return new JAXBElement<GetSupportedLangs>(GET_SUPPORTED_LANGS_QNAME, GetSupportedLangs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSupportedLangsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getSupportedLangsResponse")
    public JAXBElement<GetSupportedLangsResponse> createGetSupportedLangsResponse(GetSupportedLangsResponse value) {
        return new JAXBElement<GetSupportedLangsResponse>(GET_SUPPORTED_LANGS_RESPONSE_QNAME, GetSupportedLangsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchThemesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "fetchThemesResponse")
    public JAXBElement<FetchThemesResponse> createFetchThemesResponse(FetchThemesResponse value) {
        return new JAXBElement<FetchThemesResponse>(FETCH_THEMES_RESPONSE_QNAME, FetchThemesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableThesauriResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAvailableThesauriResponse")
    public JAXBElement<GetAvailableThesauriResponse> createGetAvailableThesauriResponse(GetAvailableThesauriResponse value) {
        return new JAXBElement<GetAvailableThesauriResponse>(GET_AVAILABLE_THESAURI_RESPONSE_QNAME, GetAvailableThesauriResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAvailableThesauri }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAvailableThesauri")
    public JAXBElement<GetAvailableThesauri> createGetAvailableThesauri(GetAvailableThesauri value) {
        return new JAXBElement<GetAvailableThesauri>(GET_AVAILABLE_THESAURI_QNAME, GetAvailableThesauri.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchGroupsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "fetchGroupsResponse")
    public JAXBElement<FetchGroupsResponse> createFetchGroupsResponse(FetchGroupsResponse value) {
        return new JAXBElement<FetchGroupsResponse>(FETCH_GROUPS_RESPONSE_QNAME, FetchGroupsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRelatedConceptsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getRelatedConceptsResponse")
    public JAXBElement<GetRelatedConceptsResponse> createGetRelatedConceptsResponse(GetRelatedConceptsResponse value) {
        return new JAXBElement<GetRelatedConceptsResponse>(GET_RELEATED_CONCEPTS_RESPONSE_QNAME, GetRelatedConceptsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllConceptRelatives }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAllConceptRelatives")
    public JAXBElement<GetAllConceptRelatives> createGetAllConceptRelatives(GetAllConceptRelatives value) {
        return new JAXBElement<GetAllConceptRelatives>(GET_ALL_CONCEPT_RELATIVES_QNAME, GetAllConceptRelatives.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FetchGroups }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "fetchGroups")
    public JAXBElement<FetchGroups> createFetchGroups(FetchGroups value) {
        return new JAXBElement<FetchGroups>(FETCH_GROUPS_QNAME, FetchGroups.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllTranslationsForConcept }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getAllTranslationsForConcept")
    public JAXBElement<GetAllTranslationsForConcept> createGetAllTranslationsForConcept(GetAllTranslationsForConcept value) {
        return new JAXBElement<GetAllTranslationsForConcept>(GET_ALL_TRANSLATIONS_FOR_CONCEPT_QNAME, GetAllTranslationsForConcept.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptsMatchingRegexByThesaurusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getConceptsMatchingRegexByThesaurusResponse")
    public JAXBElement<GetConceptsMatchingRegexByThesaurusResponse> createGetConceptsMatchingRegexByThesaurusResponse(GetConceptsMatchingRegexByThesaurusResponse value) {
        return new JAXBElement<GetConceptsMatchingRegexByThesaurusResponse>(GET_CONCEPTS_MATCHING_REGEX_BY_THESAURUS_RESPONSE_QNAME, GetConceptsMatchingRegexByThesaurusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRelatedConcepts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getRelatedConcepts")
    public JAXBElement<GetRelatedConcepts> createGetRelatedConcepts(GetRelatedConcepts value) {
        return new JAXBElement<GetRelatedConcepts>(GET_RELATED_CONCEPTS_QNAME, GetRelatedConcepts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConceptsMatchingKeyword }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.geotk.org/", name = "getConceptsMatchingKeyword")
    public JAXBElement<GetConceptsMatchingKeyword> createGetConceptsMatchingKeyword(GetConceptsMatchingKeyword value) {
        return new JAXBElement<GetConceptsMatchingKeyword>(GET_CONCEPTS_MATCHING_KEYWORD_QNAME, GetConceptsMatchingKeyword.class, null, value);
    }

}
