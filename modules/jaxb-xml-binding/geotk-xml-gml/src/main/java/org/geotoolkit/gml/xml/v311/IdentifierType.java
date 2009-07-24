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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * An identification of a CRS object. 
 * The first use of the IdentifierType for an object, if any, is normally the primary identification code,
 * and any others are aliases.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentifierType", propOrder = {
    "name",
    "version",
    "remarks"
})
public class IdentifierType {

    @XmlElementRef(name = "name", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<CodeType> name;
    private String version;
    private StringOrRefType remarks;

    /**
     * The code or name for this Identifier, often from a controlled list or pattern defined by a code space. The optional codeSpace attribute is normally included to identify or reference a code space within which one or more codes are defined. This code space is often defined by some authority organization, where one organization may define multiple code spaces. The range and format of each Code Space identifier is defined by that code space authority. Information about that code space authority can be included as metaDataProperty elements which are optionally allowed in all CRS objects.
     * 
     */
    public JAXBElement<CodeType> getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     */
    public void setName(JAXBElement<CodeType> value) {
        this.name = ((JAXBElement<CodeType> ) value);
    }

    /**
     * Gets the value of the version property.
     * 
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Remarks about this code or alias.
     * 
     */
    public StringOrRefType getRemarks() {
        return remarks;
    }

    /**
     * Sets the value of the remarks property.
     * 
     */
    public void setRemarks(StringOrRefType value) {
        this.remarks = value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof IdentifierType) {
            IdentifierType that = (IdentifierType) object;
            boolean proc = false;
            if (this.name != null && that.name != null) {
                proc = Utilities.equals(this.name.getValue(), that.name.getValue());
            } else if (this.name == null && that.name == null) {
                proc = true;
            }

            return Utilities.equals(this.remarks, that.remarks)  &&
                   proc                                          &&
                   Utilities.equals(this.version, that.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 97 * hash + (this.remarks != null ? this.remarks.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[IdentifierType]").append("\n");
        if (name != null) {
            sb.append("name: ").append(name.getValue()).append('\n');
        }
        if (remarks != null) {
            sb.append("remarks: ").append(remarks).append('\n');
        }
        if (version != null) {
            sb.append("version: ").append(version).append('\n');
        }
        return sb.toString();
     }

}
