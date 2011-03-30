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
package org.geotoolkit.ebrim.xml.v250;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ebrim.xml.LocalizedString;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for LocalizedStringType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocalizedStringType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="lang" type="{http://www.w3.org/2001/XMLSchema}language" default="en-US" />
 *       &lt;attribute name="charset" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="UTF-8" />
 *       &lt;attribute name="value" use="required" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}FreeFormText" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocalizedStringType")
public class LocalizedStringType implements LocalizedString {

    @XmlAttribute(namespace = "urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    private String lang;
    @XmlAttribute
    @XmlSchemaType(name = "anySimpleType")
    private String charset;
    @XmlAttribute(required = true)
    private String value;

    /**
     * Gets the value of the lang property.
     *     
     */
    public String getLang() {
        if (lang == null) {
            return "en-US";
        } else {
            return lang;
        }
    }

    /**
     * Sets the value of the lang property.
     */
    public void setLang(final String value) {
        this.lang = value;
    }

    /**
     * Gets the value of the charset property.
     */
    public String getCharset() {
        if (charset == null) {
            return "UTF-8";
        } else {
            return charset;
        }
    }

    /**
     * Sets the value of the charset property.
     */
    public void setCharset(final String value) {
        this.charset = value;
    }

    /**
     * Gets the value of the value property.
    */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[LocalizedStringType]\n");
        if (charset != null) {
            sb.append("charset:").append(charset).append('\n');
        }
        if (lang != null) {
            sb.append("lang:").append(lang).append('\n');
        }
        if (value != null) {
            sb.append("value:").append(value).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LocalizedStringType) {
            final LocalizedStringType that = (LocalizedStringType) obj;
            return Utilities.equals(this.getCharset(), that.getCharset()) &&
                   Utilities.equals(this.getLang(), that.getLang()) &&
                   Utilities.equals(this.value,   that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.getLang() != null ? this.getLang().hashCode() : 0);
        hash = 17 * hash + (this.getCharset() != null ? this.getCharset().hashCode() : 0);
        hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}

    
