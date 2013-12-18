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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.gml.xml.MultiSurface;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * <p>Java class for MultiSurfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultiSurfaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGeometricAggregateType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}surfaceMember" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}surfaceMembers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiSurfaceType", propOrder = {
    "surfaceMember",
    "surfaceMembers"
})
public class MultiSurfaceType extends AbstractGeometricAggregateType implements MultiSurface {

    private List<SurfacePropertyType> surfaceMember;
    private SurfaceArrayPropertyType surfaceMembers;

    public MultiSurfaceType() {
        
    }
    
    public MultiSurfaceType(final String srsName, final List<SurfacePropertyType> surfaceMember) {
        super(srsName);
        this.surfaceMember = surfaceMember;
    }
    
    /**
     * Gets the value of the surfaceMember property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link SurfacePropertyType }
     * 
     * 
     */
    @Override
    public List<SurfacePropertyType> getSurfaceMember() {
        if (surfaceMember == null) {
            surfaceMember = new ArrayList<>();
        }
        return this.surfaceMember;
    }
    
    public void setSurfaceMember(final List<SurfacePropertyType> surfaceMember) {
        this.surfaceMember = surfaceMember;
    }

    public void setSurfaceMember(final SurfacePropertyType surfaceMember) {
        if (this.surfaceMember == null) {
            this.surfaceMember = new ArrayList<>();
        }
        this.surfaceMember.add(surfaceMember);
    }


    /**
     * Gets the value of the surfaceMembers property.
     * 
     * @return
     *     possible object is
     *     {@link SurfaceArrayPropertyType }
     *     
     */
    public SurfaceArrayPropertyType getSurfaceMembers() {
        return surfaceMembers;
    }

    /**
     * Sets the value of the surfaceMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link SurfaceArrayPropertyType }
     *     
     */
    public void setSurfaceMembers(SurfaceArrayPropertyType value) {
        this.surfaceMembers = value;
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
        if (object instanceof MultiSurfaceType && super.equals(object, mode)) {
            final MultiSurfaceType that = (MultiSurfaceType) object;

            return Objects.equals(this.surfaceMember,  that.surfaceMember) &&
                   Objects.equals(this.surfaceMembers, that.surfaceMembers);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.surfaceMember != null ? this.surfaceMember.hashCode() : 0);
        hash = 97 * hash + (this.surfaceMembers != null ? this.surfaceMembers.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (surfaceMembers != null) {
            sb.append("surfaceMembers:").append(surfaceMembers).append('\n');
        }
        if (surfaceMember != null) {
            sb.append("surfaceMember:").append('\n');
            for (SurfacePropertyType sp : surfaceMember) {
                sb.append(sp).append('\n');
            }
        }
        return sb.toString();
    }
}
