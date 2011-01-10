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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LanguageType {

    @XmlValue
    private String value;

    @XmlAttribute
    private Boolean default_;

    public LanguageType() {

    }

    public LanguageType(final String value) {
        this.value = value;
    }

    public LanguageType(final String value, final Boolean default_) {
        this.value    = value;
        this.default_ = default_;
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
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @return the default_
     */
    public Boolean getDefault_() {
        return default_;
    }

    /**
     * @param default_ the default_ to set
     */
    public void setDefault_(final Boolean default_) {
        this.default_ = default_;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LanguageType) {
            final LanguageType that = (LanguageType) object;
            return Utilities.equals(this.default_, that.default_) &&
                   Utilities.equals(this.value,    that.value) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 29 * hash + (this.default_ != null ? this.default_.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[LanguageType]\n");
        if ( value != null) {
            sb.append("value:").append(value);
        }
        if ( default_ != null) {
            sb.append("default_:").append(default_);
        }
        return sb.toString();
    }
}
