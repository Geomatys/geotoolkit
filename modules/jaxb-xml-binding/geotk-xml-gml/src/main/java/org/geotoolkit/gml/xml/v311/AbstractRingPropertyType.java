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
 * Encapsulates a ring to represent the surface boundary property of a surface.
 * 
 * <p>Java class for AbstractRingPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractRingPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractRing"/>
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
@XmlType(name = "AbstractRingPropertyType", propOrder = {
    "abstractRing"
})
public class AbstractRingPropertyType {

    @XmlElementRef(name = "AbstractRing", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractRingType> abstractRing;

    AbstractRingPropertyType() {}

    public AbstractRingPropertyType(AbstractRingType ring) {
        if (ring != null) {
            ObjectFactory factory = new ObjectFactory();
            if (ring instanceof RingType) {
                abstractRing = factory.createRing((RingType) ring);
            } else if (ring instanceof LinearRingType) {
                abstractRing = factory.createLinearRing((LinearRingType) ring);
            } else {
                throw new IllegalArgumentException("unexpected sub type of AbstractRingType");
            }
        }
    }

    /**
     * Gets the value of the abstractRing property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractRingType> getJbAbstractRing() {
        return abstractRing;
    }

    /**
     * Sets the value of the abstractRing property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractRingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     *     
     */
    public void setJbAbstractRing(JAXBElement<? extends AbstractRingType> value) {
        this.abstractRing = ((JAXBElement<? extends AbstractRingType> ) value);
    }

    /**
     * Gets the value of the abstractRing property.
     *
     * @return
     *     possible object is
     *     {@code <}{@link AbstractRingType }{@code >}
     *     {@code <}{@link RingType }{@code >}
     *     {@code <}{@link LinearRingType }{@code >}
     *
     */
    public AbstractRingType getAbstractRing() {
        if (abstractRing != null) {
            return abstractRing.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the abstractRing property.
     *
     * @param value
     *     allowed object is
     *     {@code <}{@link AbstractRingType }{@code >}
     *     {@code <}{@link RingType }{@code >}
     *     {@code <}{@link LinearRingType }{@code >}
     *
     */
    public void setAbstractRing(AbstractRingType ring) {
        if (ring != null) {
            ObjectFactory factory = new ObjectFactory();
            if (ring instanceof RingType) {
                abstractRing = factory.createRing((RingType) ring);
            } else if (ring instanceof LinearRingType) {
                abstractRing = factory.createLinearRing((LinearRingType) ring);
            } else {
                throw new IllegalArgumentException("unexpected sub type of AbstractRingType");
            }
        } else {
            ring = null;
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
        if (object instanceof AbstractRingPropertyType) {
            final AbstractRingPropertyType that = (AbstractRingPropertyType) object;

            return Utilities.equals(this.getAbstractRing(), that.getAbstractRing());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.getAbstractRing() != null ? this.getAbstractRing().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[AbstractRingPropertyType]\n");
        if (abstractRing != null) {
            sb.append("abstractRing:").append(abstractRing.getValue()).append('\n');
        }
        return sb.toString();
    }

}
