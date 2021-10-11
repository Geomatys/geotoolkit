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
package org.geotoolkit.ows.xml.v110;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.ows.xml.LanguageString;


/**
 * Text string with the language of the string identified as recommended in the XML 1.0 W3C Recommendation, section 2.12.
 *
 * <p>Java class for LanguageStringType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LanguageStringType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LanguageStringType", propOrder = {
    "value"
})
public class LanguageStringType implements LanguageString {

    @XmlValue
    private String value;
    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    private String lang;

    /**
     * Empty constructor used by JAXB.
     */
    LanguageStringType(){
    }

    /**
     * Build a new String in the specified language.
     */
    public LanguageStringType(final String value, final String lang){
        this.lang  = lang;
        this.value = value;
    }

    /**
     * Build a new String without specifying the language.
     */
    public LanguageStringType(final String value){
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Gets the value of the lang property.
     */
    @Override
    public String getLang() {
        return lang;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LanguageStringType) {
            final LanguageStringType that = (LanguageStringType) object;

            return Objects.equals(this.lang,  that.lang)  &&
                   Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + (this.lang != null ? this.lang.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "class:LanguageStringType  value=" + value + " lang=" + lang;
    }

}
