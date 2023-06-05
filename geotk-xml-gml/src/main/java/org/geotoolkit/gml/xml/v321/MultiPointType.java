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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.gml.xml.MultiPoint;


/**
 * <p>Java class for MultiPointType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MultiPointType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGeometricAggregateType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}pointMember" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}pointMembers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiPointType", propOrder = {
    "pointMember",
    "pointMembers"
})
@XmlRootElement(name="MultiPoint")
public class MultiPointType extends AbstractGeometricAggregateType implements MultiPoint {

    private List<PointPropertyType> pointMember;
    private PointArrayPropertyType pointMembers;

    public MultiPointType() {
    }

    public MultiPointType(final String srsName, final List<PointPropertyType> pointMember) {
        super(srsName);
        this.pointMember = pointMember;
    }

    /**
     * Gets the value of the pointMember property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link PointPropertyType }
     */
    @Override
    public List<PointPropertyType> getPointMember() {
        if (pointMember == null) {
            pointMember = new ArrayList<>();
        }
        return this.pointMember;
    }

    /**
     * Sets the value of the pointMember property.
     */
    public void setPointMember(final List<PointPropertyType> pointMember) {
        this.pointMember = pointMember;
    }

    /**
     * Sets the value of the pointMember property.
     */
    public void setPointMember(final PointPropertyType pointMember) {
        if (pointMember != null) {
            if (this.pointMember == null) {
                this.pointMember = new ArrayList<>();
            }
            this.pointMember.add(pointMember);
        }
    }

    /**
     * Gets the value of the pointMembers property.
     *
     * @return
     *     possible object is
     *     {@link PointArrayPropertyType }
     *
     */
    public PointArrayPropertyType getPointMembers() {
        return pointMembers;
    }

    /**
     * Sets the value of the pointMembers property.
     *
     * @param value
     *     allowed object is
     *     {@link PointArrayPropertyType }
     *
     */
    public void setPointMembers(PointArrayPropertyType value) {
        this.pointMembers = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof MultiPointType && super.equals(object, mode)) {
            final MultiPointType that = (MultiPointType) object;

            return Objects.equals(this.pointMember,  that.pointMember) &&
                   Objects.equals(this.pointMembers, that.pointMembers) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.pointMember != null ? this.pointMember.hashCode() : 0);
        hash = 19 * hash + (this.pointMembers != null ? this.pointMembers.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (pointMember != null) {
            sb.append("pointMember:").append('\n');
            for (PointPropertyType sp : pointMember) {
                sb.append(sp).append('\n');
            }
        }
        if (pointMembers != null) {
            sb.append("pointMembers:").append(pointMembers).append('\n');
        }
        return sb.toString();
    }
}
