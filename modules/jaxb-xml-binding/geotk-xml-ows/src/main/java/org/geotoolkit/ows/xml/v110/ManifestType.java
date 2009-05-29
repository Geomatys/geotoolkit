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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Unordered list of one or more groups of references to remote and/or local resources. 
 * 
 * <p>Java class for ManifestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ManifestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}BasicIdentificationType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}ReferenceGroup" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManifestType", propOrder = {
    "referenceGroup"
})
public class ManifestType extends BasicIdentificationType {

    @XmlElementRef(name = "ReferenceGroup", namespace = "http://www.opengis.net/ows/1.1", type = JAXBElement.class)
    private List<JAXBElement<ReferenceGroupType>> referenceGroup;

    /**
     * Gets the value of the referenceGroup property.
     */
    public List<JAXBElement<ReferenceGroupType>> getReferenceGroup() {
        if (referenceGroup == null) {
            referenceGroup = new ArrayList<JAXBElement<ReferenceGroupType>>();
        }
        return this.referenceGroup;
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ManifestType && super.equals(object)) {
            final ManifestType that = (ManifestType) object;
            return Utilities.equals(this.referenceGroup, that.referenceGroup);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.referenceGroup != null ? this.referenceGroup.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("referenceGroup:").append('\n');
        if (referenceGroup != null) {
            for (int i = 0; i < referenceGroup.size(); i++) {
                s.append(referenceGroup.get(i).toString()).append('\n');
            }
        }
        return s.toString();
    }

}
