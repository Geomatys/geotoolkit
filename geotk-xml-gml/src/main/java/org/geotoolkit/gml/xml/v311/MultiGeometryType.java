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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.gml.xml.MultiGeometry;


/**
 * A geometry collection must include one or more geometries, referenced through geometryMember elements.
 *
 * <p>Java class for MultiGeometryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MultiGeometryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometricAggregateType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}geometryMember" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}geometryMembers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiGeometryType", propOrder = {
    "geometryMember",
    "geometryMembers"
})
@XmlRootElement(name = "MultiGeometry")
public class MultiGeometryType extends AbstractGeometricAggregateType implements MultiGeometry {

    private List<GeometryPropertyType> geometryMember;
    private GeometryArrayPropertyType geometryMembers;

    MultiGeometryType() {}

    public MultiGeometryType(final List<GeometryPropertyType> geometryMember) {
        this.geometryMember = geometryMember;
    }

    /**
     * Gets the value of the geometryMember property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link GeometryPropertyType }
     */
    @Override
    public List<GeometryPropertyType> getGeometryMember() {
        if (geometryMember == null) {
            geometryMember = new ArrayList<GeometryPropertyType>();
        }
        return this.geometryMember;
    }

    /**
     * Sets the value of the geometryMember property.
     */
    public void setGeometryMember(final List<GeometryPropertyType> geometryMember) {
        this.geometryMember = geometryMember;
    }

    /**
     * Gets the value of the geometryMembers property.
     *
     * @return
     *     possible object is
     *     {@link GeometryArrayPropertyType }
     */
    public GeometryArrayPropertyType getGeometryMembers() {
        return geometryMembers;
    }

    /**
     * Sets the value of the geometryMembers property.
     *
     * @param value
     *     allowed object is
     *     {@link GeometryArrayPropertyType }
     */
    public void setGeometryMembers(final GeometryArrayPropertyType value) {
        this.geometryMembers = value;
    }

    /**
     * Return a String description of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n');
        if (geometryMember != null) {
            s.append("geometryMember: ").append('\n');
            for (GeometryPropertyType geoProp : geometryMember)  {
                s.append(geoProp).append('\n');
            }
        }
        if (geometryMembers != null) {
            s.append("geometryMembers: ").append(geometryMembers).append('\n');
        }
        return s.toString();
    }

    /**
     * Verify that the point is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof MultiGeometryType && super.equals(object, mode)) {
            final MultiGeometryType that = (MultiGeometryType) object;
            return  Objects.equals(this.geometryMember,  that.geometryMember) &&
                    Objects.equals(this.geometryMembers, that.geometryMembers);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 61 * hash + (this.geometryMember != null ? this.geometryMember.hashCode() : 0);
        hash = 61 * hash + (this.geometryMembers != null ? this.geometryMembers.hashCode() : 0);
        return hash;
    }
}
