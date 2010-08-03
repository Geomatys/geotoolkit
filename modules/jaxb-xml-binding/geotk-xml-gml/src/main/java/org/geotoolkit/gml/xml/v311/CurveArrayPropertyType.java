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
 * A container for an array of curves. The elements are always contained in the array property, referencing geometry elements 
 * 			or arrays of geometry elements is not supported.
 * 
 * <p>Java class for CurveArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CurveArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractCurve" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "CurveArrayPropertyType", propOrder = {
    "abstractCurve"
})
public class CurveArrayPropertyType {

    @XmlElementRef(name = "AbstractCurve", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractCurveType>> abstractCurve;

    /**
     * Gets the value of the abstractCurve property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     * 
     */
    public List<JAXBElement<? extends AbstractCurveType>> getJbAbstractCurve() {
        if (abstractCurve == null) {
            abstractCurve = new ArrayList<JAXBElement<? extends AbstractCurveType>>();
        }
        return this.abstractCurve;
    }

    /**
     * Sets the value of the abstractCurve property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *
     */
    public void setJbAbstractCurve(List<JAXBElement<? extends AbstractCurveType>> abstractCurve) {
        this.abstractCurve = abstractCurve;
    }

    /**
     * Gets the value of the abstractCurve property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *
     */
    public List<? extends AbstractCurveType> getAbstractCurve() {
        if (abstractCurve == null) {
            abstractCurve = new ArrayList<JAXBElement<? extends AbstractCurveType>>();
        }
        final List<AbstractCurveType> result = new ArrayList<AbstractCurveType>();
        for (JAXBElement<? extends AbstractCurveType> jb : abstractCurve) {
            result.add(jb.getValue());
        }
        return result;
    }

    /**
     * Sets the value of the abstractCurve property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *
     */
    public void setAbstractCurve(List<? extends AbstractCurveType> abstractCurve) {
        if (abstractCurve != null) {
            this.abstractCurve = new ArrayList<JAXBElement<? extends AbstractCurveType>>();
            final ObjectFactory factory = new ObjectFactory();
            for (AbstractCurveType curve : abstractCurve) {
                if (curve instanceof LineStringType) {
                    this.abstractCurve.add(factory.createLineString((LineStringType) curve));
                } else if (curve instanceof CurveType) {
                    this.abstractCurve.add(factory.createCurve((CurveType) curve));
                } else if (curve instanceof OrientableCurveType) {
                    this.abstractCurve.add(factory.createOrientableCurve((OrientableCurveType) curve));
                } else if (curve instanceof AbstractCurveType) {
                    this.abstractCurve.add(factory.createAbstractCurve((AbstractCurveType) curve));
                }
            }
        }
    }

    /**
     * Sets the value of the abstractCurve property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     *
     */
    public void setAbstractCurve(AbstractCurveType abstractCurve) {
        if (abstractCurve != null) {
            if (this.abstractCurve == null) {
                this.abstractCurve = new ArrayList<JAXBElement<? extends AbstractCurveType>>();
            }
            final ObjectFactory factory = new ObjectFactory();
            if (abstractCurve instanceof LineStringType) {
                this.abstractCurve.add(factory.createLineString((LineStringType) abstractCurve));
            } else if (abstractCurve instanceof CurveType) {
                this.abstractCurve.add(factory.createCurve((CurveType) abstractCurve));
            } else if (abstractCurve instanceof OrientableCurveType) {
                this.abstractCurve.add(factory.createOrientableCurve((OrientableCurveType) abstractCurve));
            } else if (abstractCurve instanceof AbstractCurveType) {
                this.abstractCurve.add(factory.createAbstractCurve((AbstractCurveType) abstractCurve));
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
        if (object instanceof CurveArrayPropertyType) {
            final CurveArrayPropertyType that = (CurveArrayPropertyType) object;

            if (this.abstractCurve != null && that.abstractCurve != null) {
                for (int i = 0; i < abstractCurve.size(); i++) {
                    AbstractCurveType thisGeom = this.abstractCurve.get(i).getValue();
                    AbstractCurveType thatGeom = that.abstractCurve.get(i).getValue();

                    if (!Utilities.equals(thisGeom,   thatGeom))
                        return false;
                }
                return true;
            } else if (this.abstractCurve == null && that.abstractCurve == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.abstractCurve != null ? this.abstractCurve.hashCode() : 0);
        return hash;
    }
}
