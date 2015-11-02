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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetNumeredConcept", propOrder = {
    "uri",
    "language",
    "outputFormat",
    "theme",
    "thesaurusUri"
})
public class GetNumeredConcept {
    
    private String uri;
    private String language;
    private String outputFormat;
    private String theme;
    private String thesaurusUri;

    public GetNumeredConcept() {

    }

    public GetNumeredConcept(final String uri, final String language) {
        this.language = language;
        this.uri      = uri;
    }

    public GetNumeredConcept(final String uri, final String language, final String outputFormat) {
        this.language = language;
        this.uri      = uri;
        this.outputFormat = outputFormat;
    }

    public GetNumeredConcept(final String uri, final String language, final String outputFormat, final String theme, final String thesaurusUri) {
        this.language     = language;
        this.uri          = uri;
        this.outputFormat = outputFormat;
        this.theme        = theme;
        this.thesaurusUri = thesaurusUri;
    }
    
    public GetNumeredConcept(final URI uri, final String language) {
        this.language = language;
        if (uri != null) {
            this.uri      = uri.toString();
        }
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUri(String value) {
        this.uri = value;
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

    /**
     * @return the theme
     */
    public String getTheme() {
        return theme;
    }

    /**
     * @param theme the theme to set
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * @return the thesaurusUri
     */
    public String getThesaurusUri() {
        return thesaurusUri;
    }

    /**
     * @param thesaurusUri the thesaurusUri to set
     */
    public void setThesaurusUri(String thesaurusUri) {
        this.thesaurusUri = thesaurusUri;
    }
}
