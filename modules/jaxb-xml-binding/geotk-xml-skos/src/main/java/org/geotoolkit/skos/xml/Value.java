/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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
package org.geotoolkit.skos.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Value {
    
    @XmlValue
    private String value;
    
    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    private String lang;

    public Value() {
        
    }
    
    public Value(final String value) {
        this.value = value;
    }
    
    public Value(final String value, final String lang) {
        this.value = value;
        this.lang  = lang;
    }
    
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * @param lang the lang to set
     */
    public void setLang(String lang) {
        this.lang = lang;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Value]:").append('\n');
        sb.append("language:").append(lang).append(" value:").append(value).append('\n');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Value) {
            final Value that = (Value) object;
            return Utilities.equals(this.lang,  that.lang) &&
                   Utilities.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.lang != null ? this.lang.hashCode() : 0);
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
