/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Geomatys
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


package org.geotoolkit.sml.xml.v101;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractLinkRef;
import org.apache.sis.util.ComparisonMode;
/**
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class LinkRef extends SensorObject implements AbstractLinkRef {

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String ref;

    public LinkRef() {
    }

    public LinkRef(final String ref) {
        this.ref = ref;
    }

    /**
     * Gets the value of the ref property.
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     */
    public void setRef(final String value) {
        this.ref = value;
    }

    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof LinkRef) {
            final LinkRef that = (LinkRef) object;
            return Objects.equals(this.ref, that.ref);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[LinkRef]").append("\n");
        if (ref != null) {
            sb.append("ref: ").append(ref).append('\n');
        }
        return sb.toString();
    }
}
