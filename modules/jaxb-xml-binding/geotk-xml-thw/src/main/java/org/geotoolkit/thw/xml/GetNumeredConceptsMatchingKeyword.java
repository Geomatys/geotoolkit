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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getNumeredConceptsMatchingKeyword", propOrder = {
    "keyword",
    "csw",
    "language",
    "outputFormat",
    "ignoreCsw",
    "thesaurus",
    "ignoreThesaurus",
    "theme",
    "showDeactivated",
    "searchMode"
})
@XmlRootElement(name = "GetNumeredConceptsMatchingKeyword", namespace = "http://ws.geotk.org/")
public class GetNumeredConceptsMatchingKeyword {

    private String keyword;
    private List<String> csw;
    private String language;
    private String outputFormat;
    private List<String> ignoreCsw;
    private List<String> thesaurus;
    private List<String> ignoreThesaurus;
    private List<String> theme;
    private Boolean showDeactivated;
    private int searchMode;
    
    public GetNumeredConceptsMatchingKeyword() {

    }

    public GetNumeredConceptsMatchingKeyword(final String keyword, final List<String> csw, final String language, final String outputFormat, 
            final List<String> ignoreCsw, final List<String> theme, final List<String> thesaurusList, final Boolean showDeactivated,
            final int searchMode) {
        this.keyword         = keyword;
        this.language        = language;
        this.csw             = csw;
        this.outputFormat    = outputFormat;
        this.ignoreCsw       = ignoreCsw;
        this.theme           = theme;
        this.thesaurus       = thesaurusList;
        this.showDeactivated = showDeactivated;
        this.searchMode      = searchMode;
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
    public void setKeyword(final String value) {
        this.keyword = value;
    }

    /**
     * Gets the value of the thesaurus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public List<String> getCsw() {
        return csw;
    }

    /**
     * Sets the value of the thesaurus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsw(final List<String> value) {
        this.csw = value;
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
    public void setLanguage(final String value) {
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
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the ignoreThesaurus
     */
    public List<String> getIgnoreCsw() {
        if (ignoreCsw == null) {
            this.ignoreCsw = new ArrayList<String>();
        }
        return ignoreCsw;
    }

    /**
     * @param ignoreThesaurus the ignoreThesaurus to set
     */
    public void setIgnoreCsw(final List<String> ignoreCsw) {
        this.ignoreCsw = ignoreCsw;
    }

    /**
     * @return the thesaurus
     */
    public List<String> getThesaurus() {
        return thesaurus;
    }

    /**
     * @param thesaurus the thesaurus to set
     */
    public void setThesaurus(List<String> thesaurus) {
        this.thesaurus = thesaurus;
    }

    /**
     * @return the ignoreThesaurus
     */
    public List<String> getIgnoreThesaurus() {
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

    /**
     * @return the searchMode
     */
    public int getSearchMode() {
        return searchMode;
    }

    /**
     * @param searchMode the searchMode to set
     */
    public void setSearchMode(int searchMode) {
        this.searchMode = searchMode;
    }
}
