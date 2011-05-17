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
 * A MultiCurve is defined by one or more Curves, referenced through curveMember elements.
 * 
 * <p>Java class for MultiCurveType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultiCurveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometricAggregateType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}curveMember" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}curveMembers" minOccurs="0"/>
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
@XmlType(name = "MultiCurveType", propOrder = {
    "curveMember",
    "curveMembers"
})
public class MultiCurveType extends AbstractGeometricAggregateType {

    private List<CurvePropertyType> curveMember;
    private CurveArrayPropertyType curveMembers;

    /**
     * Gets the value of the curveMember property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link CurvePropertyType }
     * 
     * 
     */
    public List<CurvePropertyType> getCurveMember() {
        if (curveMember == null) {
            curveMember = new ArrayList<CurvePropertyType>();
        }
        return this.curveMember;
    }

    /**
     * Gets the value of the curveMembers property.
     * 
     * @return
     *     possible object is
     *     {@link CurveArrayPropertyType }
     *     
     */
    public CurveArrayPropertyType getCurveMembers() {
        return curveMembers;
    }

    /**
     * Sets the value of the curveMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurveArrayPropertyType }
     *     
     */
    public void setCurveMembers(final CurveArrayPropertyType value) {
        this.curveMembers = value;
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
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof MultiCurveType && super.equals(object, mode)) {
            final MultiCurveType that = (MultiCurveType) object;

            return Utilities.equals(this.curveMember,  that.curveMember) &&
                   Utilities.equals(this.curveMembers, that.curveMembers) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.curveMember != null ? this.curveMember.hashCode() : 0);
        hash = 97 * hash + (this.curveMembers != null ? this.curveMembers.hashCode() : 0);
        return hash;
    }

    



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (curveMember != null) {
            sb.append("curveMember:").append('\n');
            for (CurvePropertyType sp : curveMember) {
                sb.append(sp).append('\n');
            }
        }
        if (curveMembers != null) {
            sb.append("curveMember:").append(curveMembers).append('\n');
        }
        return sb.toString();
    }
}
