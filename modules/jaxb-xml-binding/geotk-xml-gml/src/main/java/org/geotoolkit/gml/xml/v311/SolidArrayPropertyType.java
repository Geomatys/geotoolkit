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
 * A container for an array of solids. The elements are always contained in the array property, referencing geometry elements or arrays of geometry elements is not supported.
 * 
 * <p>Java class for SolidArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SolidArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractSolid"/>
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
@XmlType(name = "SolidArrayPropertyType", propOrder = {
    "abstractSolid"
})
public class SolidArrayPropertyType {

    @XmlElementRef(name = "AbstractSolid", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractSolidType>> abstractSolid;

    /**
     * Gets the value of the abstractSolid property.
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     * {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     * 
     */
    public List<JAXBElement<? extends AbstractSolidType>> getJbAbstractSolid() {
        if (abstractSolid == null) {
            abstractSolid = new ArrayList<JAXBElement<? extends AbstractSolidType>>();
        }
        return this.abstractSolid;
    }

    /**
     * Sets the value of the abstractSolid property.
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     * {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *
     */
    public void setJbAbstractSolid(final List<JAXBElement<? extends AbstractSolidType>> abstractSolid) {
        this.abstractSolid = abstractSolid;
    }

    /**
     * Gets the value of the abstractSolid property.
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     * {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *
     */
    public List<? extends AbstractSolidType> getAbstractSolid() {
        if (abstractSolid == null) {
            abstractSolid = new ArrayList<JAXBElement<? extends AbstractSolidType>>();
        }
        final List<AbstractSolidType> result = new ArrayList<AbstractSolidType>();
        for (JAXBElement<? extends AbstractSolidType> jb : abstractSolid) {
            result.add(jb.getValue());
        }
        return result;
    }

    /**
     * Gets the value of the abstractSolid property.
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     * {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     *
     */
    public void setAbstractSolid(final List<? extends AbstractSolidType> abstractSolid) {
        if (abstractSolid != null) {
            if (this.abstractSolid == null) {
               this.abstractSolid = new ArrayList<JAXBElement<? extends AbstractSolidType>>();
            }
            final ObjectFactory factory = new ObjectFactory();
            for (AbstractSolidType solid : abstractSolid) {
                if (solid instanceof SolidType) {
                    this.abstractSolid.add(factory.createSolid((SolidType) solid));
                } else if (solid instanceof AbstractSolidType) {
                    this.abstractSolid.add(factory.createAbstractSolid((AbstractSolidType) solid));
                }
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
        if (object instanceof SolidArrayPropertyType) {
            final SolidArrayPropertyType that = (SolidArrayPropertyType) object;

            if (this.abstractSolid != null && that.abstractSolid != null) {
                for (int i = 0; i < abstractSolid.size(); i++) {
                    AbstractSolidType thisGeom = this.abstractSolid.get(i).getValue();
                    AbstractSolidType thatGeom = that.abstractSolid.get(i).getValue();

                    if (!Utilities.equals(thisGeom,   thatGeom))
                        return false;
                }
                return true;
            } else if (this.abstractSolid == null && that.abstractSolid == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.abstractSolid != null ? this.abstractSolid.hashCode() : 0);
        return hash;
    }
}
