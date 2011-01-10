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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * A container for an array of surfaces. The elements are always contained in the array property, referencing geometry elements or arrays of geometry elements is not supported.
 * 
 * <p>Java class for SurfaceArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SurfaceArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractSurface" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SurfaceArrayPropertyType", propOrder = {
    "abstractSurface"
})
public class SurfaceArrayPropertyType {

    @XmlElementRef(name = "AbstractSurface", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractSurfaceType>> abstractSurface;

    /**
     * Gets the value of the abstractSurface property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link TriangulatedSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolyhedralSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     * {@link JAXBElement }{@code <}{@link TinType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends AbstractSurfaceType>> getJbAbstractSurface() {
        if (abstractSurface == null) {
            abstractSurface = new ArrayList<JAXBElement<? extends AbstractSurfaceType>>();
        }
        return this.abstractSurface;
    }

    /**
     * Gets the value of the abstractSurface property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link TriangulatedSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolyhedralSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     * {@link JAXBElement }{@code <}{@link TinType }{@code >}
     *
     *
     */
    public void setJbAbstractSurface(final List<JAXBElement<? extends AbstractSurfaceType>> abstractSurface) {
        this.abstractSurface = abstractSurface;
    }

    /**
     * Gets the value of the abstractSurface property.
     *
     * Objects of the following type(s) are allowed in the list
     * @Ignore{@code <}{@link SurfaceType }{@code >}
     * @Ignore{@code <}{@link OrientableSurfaceType }{@code >}
     * @Ignore{@code <}{@link AbstractSurfaceType }{@code >}
     * @Ignore{@code <}{@link TriangulatedSurfaceType }{@code >}
     * @Ignore{@code <}{@link PolyhedralSurfaceType }{@code >}
     * @Ignore{@code <}{@link PolygonType }{@code >}
     * @Ignore{@code <}{@link TinType }{@code >}
     *
     *
     */
    public List<? extends AbstractSurfaceType> getAbstractSurface() {
        if (abstractSurface == null) {
            abstractSurface = new ArrayList<JAXBElement<? extends AbstractSurfaceType>>();
        }
        final List<AbstractSurfaceType> result = new ArrayList<AbstractSurfaceType>();
        for (JAXBElement<? extends AbstractSurfaceType> jb : this.abstractSurface) {
            result.add(jb.getValue());
        }
        return result;
    }

    /**
     * Gets the value of the abstractSurface property.
     *
     * Objects of the following type(s) are allowed in the list
     * @Ignore{@code <}{@link SurfaceType }{@code >}
     * @Ignore{@code <}{@link OrientableSurfaceType }{@code >}
     * @Ignore{@code <}{@link AbstractSurfaceType }{@code >}
     * @Ignore{@code <}{@link TriangulatedSurfaceType }{@code >}
     * @Ignore{@code <}{@link PolyhedralSurfaceType }{@code >}
     * @Ignore{@code <}{@link PolygonType }{@code >}
     * @Ignore{@code <}{@link TinType }{@code >}
     *
     *
     */
    public void setAbstractSurface(final List<? extends AbstractSurfaceType> abstractSurface) {
        this.abstractSurface = new ArrayList<JAXBElement<? extends AbstractSurfaceType>>();
        for (AbstractSurfaceType value : abstractSurface) {
            final ObjectFactory factory = new ObjectFactory();
            if (value instanceof TinType) {
                this.abstractSurface.add(factory.createTin((TinType) value));
            } else if (value instanceof PolygonType) {
                this.abstractSurface.add(factory.createPolygon((PolygonType) value));
            } else if (value instanceof PolyhedralSurfaceType) {
                this.abstractSurface.add(factory.createPolyhedralSurface((PolyhedralSurfaceType) value));
            } else if (value instanceof TriangulatedSurfaceType) {
                this.abstractSurface.add(factory.createTriangulatedSurface((TriangulatedSurfaceType) value));
            } else if (value instanceof SurfaceType) {
                this.abstractSurface.add(factory.createSurface((SurfaceType) value));
            } else if (value instanceof OrientableSurfaceType) {
                this.abstractSurface.add(factory.createOrientableSurface((OrientableSurfaceType) value));
            } else if (value instanceof AbstractSurfaceType) {
                this.abstractSurface.add(factory.createAbstractSurface((AbstractSurfaceType) value));
            }
        }
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SurfaceArrayPropertyType) {
            final SurfaceArrayPropertyType that = (SurfaceArrayPropertyType) object;
            return Utilities.equals(this.getAbstractSurface(), that.getAbstractSurface());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.getAbstractSurface() != null ? this.getAbstractSurface().hashCode() : 0);
        return hash;
    }
}
