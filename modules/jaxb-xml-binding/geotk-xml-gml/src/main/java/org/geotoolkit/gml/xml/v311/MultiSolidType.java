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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * A MultiSolid is defined by one or more Solids, referenced through solidMember elements.
 * 
 * <p>Java class for MultiSolidType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultiSolidType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometricAggregateType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}solidMember" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}solidMembers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiSolidType", propOrder = {
    "solidMember",
    "solidMembers"
})
public class MultiSolidType extends AbstractGeometricAggregateType {

    private List<SolidPropertyType> solidMember;
    private SolidArrayPropertyType solidMembers;

    /**
     * Gets the value of the solidMember property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link SolidPropertyType }
     * 
     * 
     */
    public List<SolidPropertyType> getSolidMember() {
        if (solidMember == null) {
            solidMember = new ArrayList<SolidPropertyType>();
        }
        return this.solidMember;
    }

    /**
     * Gets the value of the solidMembers property.
     * 
     * @return
     *     possible object is
     *     {@link SolidArrayPropertyType }
     *     
     */
    public SolidArrayPropertyType getSolidMembers() {
        return solidMembers;
    }

    /**
     * Sets the value of the solidMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link SolidArrayPropertyType }
     *     
     */
    public void setSolidMembers(final SolidArrayPropertyType value) {
        this.solidMembers = value;
    }

    @Override
    public Object evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T evaluate(final Object object, final Class<T> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(final ExpressionVisitor visitor, final Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Return a String description of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n');
        if (solidMember != null) {
            s.append("solidMember: ").append('\n');
            for (SolidPropertyType geoProp : solidMember)  {
                s.append(geoProp).append('\n');
            }
        }

        if (solidMembers != null) {
            s.append("solidMembers: ").append(solidMembers).append('\n');
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
        if (object instanceof MultiSolidType && super.equals(object)) {
            final MultiSolidType that = (MultiSolidType) object;
            return  Utilities.equals(this.solidMember,  that.solidMember) &&
                    Utilities.equals(this.solidMembers, that.solidMembers);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.solidMember != null ? this.solidMember.hashCode() : 0);
        hash = 53 * hash + (this.solidMembers != null ? this.solidMembers.hashCode() : 0);
        return hash;
    }

    
}
