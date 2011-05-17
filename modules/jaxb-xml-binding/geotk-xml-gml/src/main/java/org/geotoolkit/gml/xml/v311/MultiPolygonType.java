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
 * A MultiPolygon is defined by one or more Polygons, referenced through polygonMember elements. Deprecated with GML version 3.0. Use MultiSurfaceType instead.
 * 
 * <p>Java class for MultiPolygonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultiPolygonType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometricAggregateType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}polygonMember" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "MultiPolygonType", propOrder = {
    "polygonMember"
})
public class MultiPolygonType extends AbstractGeometricAggregateType {

    private List<PolygonPropertyType> polygonMember;

    /**
     * Gets the value of the polygonMember property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link PolygonPropertyType }
     * 
     * 
     */
    public List<PolygonPropertyType> getPolygonMember() {
        if (polygonMember == null) {
            polygonMember = new ArrayList<PolygonPropertyType>();
        }
        return this.polygonMember;
    }

    public void setPolygonMember(final List<PolygonPropertyType> polygonMember) {
        this.polygonMember = polygonMember;
    }

    public void setPolygonMember(final PolygonPropertyType polygonMember) {
        if (polygonMember != null) {
            if (this.polygonMember == null) {
                this.polygonMember = new ArrayList<PolygonPropertyType>();
            }
            this.polygonMember.add(polygonMember);
        }
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
        if (object instanceof MultiPolygonType && super.equals(object, mode)) {
            final MultiPolygonType that = (MultiPolygonType) object;

            return Utilities.equals(this.polygonMember, that.polygonMember);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.polygonMember != null ? this.polygonMember.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (polygonMember != null) {
            sb.append("polygonMember:").append('\n');
            for (PolygonPropertyType sp : polygonMember) {
                sb.append(sp).append('\n');
            }
        }
        return sb.toString();
    }
}
