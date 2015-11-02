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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getConceptsMatchingKeyword complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getConceptsMatchingKeyword">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="keyword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="searchMode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="thesaurus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getConceptsMatchingKeyword", propOrder = {
    "keyword",
    "searchMode",
    "thesaurus",
    "language",
    "outputFormat",
    "geometric",
    "ignoreThesaurus",
    "theme",
    "showDeactivated"
})
public class GetConceptsMatchingKeyword {

    private String keyword;
    private int searchMode;
    private List<String> thesaurus;
    private String language;
    private String outputFormat;
    private boolean geometric;
    private List<String> ignoreThesaurus;
    private List<String> theme;
    private Boolean showDeactivated;

    public GetConceptsMatchingKeyword() {

    }

    public GetConceptsMatchingKeyword(final String keyword, final int searchMode, final List<String> thesaurus, 
            final String language, final String outputFormat, final boolean geometric, final List<String> ignoreThesaurus, final List<String> themes,
            final Boolean showDeactivated) {
        this.keyword         = keyword;
        this.language        = language;
        this.searchMode      = searchMode;
        this.thesaurus       = thesaurus;
        this.outputFormat    = outputFormat;
        this.geometric       = geometric;
        this.ignoreThesaurus = ignoreThesaurus;
        this.theme           = themes;
        this.showDeactivated = showDeactivated;
    }

    public GetConceptsMatchingKeyword(final String keyword, final int searchMode, final URI thesaurus, final String language,
            final String outputFormat, final boolean geometric, final List<URI> ignoreThesaurus, final List<String> themes) {
        this.keyword         = keyword;
        this.language        = language;
        this.searchMode      = searchMode;
        if (thesaurus != null) {
            this.thesaurus   = Arrays.asList(thesaurus.toString());
        }
        this.outputFormat    = outputFormat;
        this.geometric       = geometric;
        if (ignoreThesaurus != null) {
            this.ignoreThesaurus = new ArrayList<String>();
            for (URI uri : ignoreThesaurus) {
                this.ignoreThesaurus.add(uri.toString());
            }
        }
        this.theme = themes;
    }

    /**
     * Gets the value of the keyword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Sets the value of the keyword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyword(String value) {
        this.keyword = value;
    }

    /**
     * Gets the value of the searchMode property.
     * 
     */
    public int getSearchMode() {
        return searchMode;
    }

    /**
     * Sets the value of the searchMode property.
     * 
     */
    public void setSearchMode(int value) {
        this.searchMode = value;
    }

    /**
     * Gets the value of the thesaurus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public List<String> getThesaurus() {
        return thesaurus;
    }

    /**
     * Sets the value of the thesaurus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThesaurus(List<String> value) {
        this.thesaurus = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean getGeometric() {
        return geometric;
    }

    public void setGeometric(boolean geometric) {
        this.geometric = geometric;
    }

    /**
     * @return the ignoreThesaurus
     */
    public List<String> getIgnoreThesaurus() {
        if (ignoreThesaurus == null) {
            this.ignoreThesaurus = new ArrayList<String>();
        }
        return ignoreThesaurus;
    }

    /**
     * @param ignoreThesaurus the ignoreThesaurus to set
     */
    public void setIgnoreThesaurus(List<String> ignoreThesaurus) {
        this.ignoreThesaurus = ignoreThesaurus;
    }

    /**
     * @return the theme
     */
    public List<String> getTheme() {
        return theme;
    }

    /**
     * @param theme the theme to set
     */
    public void setTheme(List<String> theme) {
        this.theme = theme;
    }

    /**
     * @return the showDeactivated
     */
    public Boolean getShowDeactivated() {
        if (showDeactivated == null) {
            return false;
        }
        return showDeactivated;
    }

    /**
     * @param showDeactivated the showDeactivated to set
     */
    public void setShowDeactivated(Boolean showDeactivated) {
        this.showDeactivated = showDeactivated;
    }

}
