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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getConceptsMatchingRegexByThesaurus complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getConceptsMatchingRegexByThesaurus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="regex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "getConceptsMatchingRegexByThesaurus", propOrder = {
    "regex",
    "thesaurus",
    "language"
})
@XmlRootElement(name = "GetConceptsMatchingRegexByThesaurus", namespace = "http://ws.geotk.org/")
public class GetConceptsMatchingRegexByThesaurus {

    protected String regex;
    protected String thesaurus;
    protected String language;

    /**
     * Gets the value of the regex property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Sets the value of the regex property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRegex(String value) {
        this.regex = value;
    }

    /**
     * Gets the value of the thesaurus property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getThesaurus() {
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
    public void setThesaurus(String value) {
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

}
