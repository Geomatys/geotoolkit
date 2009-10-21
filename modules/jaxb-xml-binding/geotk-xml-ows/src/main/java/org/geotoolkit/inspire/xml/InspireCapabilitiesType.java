/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.inspire.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Additional capabilities for INSPIRE
 * 
 * <p>Java class for InspireCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InspireCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Languages" type="{http://www.inspire.org}LanguagesType" minOccurs="0"/>
 *         &lt;element name="TranslatedCapabilities" type="{http://www.inspire.org}TranslatedCapabilitiesType" minOccurs="0"/>
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
@XmlType(name = "InspireCapabilitiesType", propOrder = {
    "languages",
    "translatedCapabilities"
})
public class InspireCapabilitiesType {

    @XmlElement(name = "Languages")
    private LanguagesType languages;
    @XmlElement(name = "TranslatedCapabilities")
    private TranslatedCapabilitiesType translatedCapabilities;

    public InspireCapabilitiesType() {

    }

    public InspireCapabilitiesType(List<String> languages) {
        this.languages = new LanguagesType(languages);
        this.translatedCapabilities = null;
    }

    public InspireCapabilitiesType(LanguagesType languages, TranslatedCapabilitiesType translatedCapabilities) {
        this.languages = languages;
        this.translatedCapabilities = translatedCapabilities;
    }

    /**
     * Gets the value of the languages property.
     */
    public LanguagesType getLanguages() {
        return languages;
    }

    /**
     * Sets the value of the languages property.
     */
    public void setLanguages(LanguagesType value) {
        this.languages = value;
    }

    /**
     * Gets the value of the translatedCapabilities property.
     */
    public TranslatedCapabilitiesType getTranslatedCapabilities() {
        return translatedCapabilities;
    }

    /**
     * Sets the value of the translatedCapabilities property.
     */
    public void setTranslatedCapabilities(TranslatedCapabilitiesType value) {
        this.translatedCapabilities = value;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InspireCapabilitiesType) {
            final InspireCapabilitiesType that = (InspireCapabilitiesType) object;
            return Utilities.equals(this.languages, that.languages) &&
                   Utilities.equals(this.translatedCapabilities, that.translatedCapabilities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.languages != null ? this.languages.hashCode() : 0);
        hash = 43 * hash + (this.translatedCapabilities != null ? this.translatedCapabilities.hashCode() : 0);
        return hash;
    }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[InspireCapabilitiesType]\n");
        if (languages != null) {
            sb.append("languages:\n").append(languages).append('\n');
        }
        if (translatedCapabilities != null) {
            sb.append("translated Capabilities:\n").append(translatedCapabilities).append('\n');
        }
        return sb.toString();
    }

}
