/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.inspire.xml.vs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * List of languages defined by a 3-letter code as described in ISO 639-2 that are supported by this service instance.
 * 
 * <p>Java class for LanguagesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LanguagesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Language" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LanguagesType", propOrder = {
    "language"
})
public class LanguagesType {

    @XmlElement(name = "Language")
    private List<LanguageType> language;

    public LanguagesType() {

    }

    public LanguagesType(final List<LanguageType> language) {
        this.language = language;
    }

    public LanguagesType(final LanguageType language) {
        this.language = new ArrayList<LanguageType>();
        this.language.add(language);
    }

    /**
     * Gets the value of the language property.
     */
    public List<LanguageType> getLanguage() {
        if (language == null) {
            language = new ArrayList<LanguageType>();
        }
        return this.language;
    }

    /**
     * Sets the value of the language property.
     */
    public void setLanguage(List<LanguageType> language) {
        if (language == null) {
            language = new ArrayList<LanguageType>();
        }
        this.language = language;
    }

    /**
     * Sets the value of the language property.
     */
    public void setLanguage(final LanguageType language) {
        if (this.language == null) {
            this.language = new ArrayList<LanguageType>();
        }
        this.language.add(language);
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LanguagesType) {
            final LanguagesType that = (LanguagesType) object;
            return Utilities.equals(this.language, that.language);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.language != null ? this.language.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[LanguagesType]\n");
        if ( language != null) {
            sb.append("language:\n");
            for (LanguageType d: language) {
                sb.append(d).append('\n');
            }
        }
        return sb.toString();
    }

}
