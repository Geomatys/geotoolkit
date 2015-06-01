/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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

package org.geotoolkit.processing.chain.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ElementManual extends Element {

    @XmlElement(name = "description")
    private String description;

    public ElementManual() {
    }

    public ElementManual(Integer id) {
        super(id);
    }

    public ElementManual(final int id, final int x, final int y) {
        super(id,x,y);
    }

    public ElementManual(final ElementManual toCopy) {
        super(toCopy.id);
        this.description = toCopy.description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ElementManual) {
            final ElementManual that = (ElementManual) obj;
            return Objects.equals(this.id,          that.id) &&
                   Objects.equals(this.description, that.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + super.hashCode();
        hash = 11 * hash + this.id;
        hash = 11 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("id:").append(id).append('\n');
        sb.append("description:").append(description).append('\n');
        return sb.toString();
    }

    @Override
    public Element copy() {
        return new ElementManual(this);
    }

}
