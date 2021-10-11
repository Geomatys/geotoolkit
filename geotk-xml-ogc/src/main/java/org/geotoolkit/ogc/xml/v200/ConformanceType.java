/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.Conformance;
import org.geotoolkit.ows.xml.v110.DomainType;


/**
 * <p>Java class for ConformanceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ConformanceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Constraint" type="{http://www.opengis.net/ows/1.1}DomainType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConformanceType", propOrder = {
    "constraint"
})
public class ConformanceType implements Conformance {

    @XmlElement(name = "Constraint", required = true)
    private List<DomainType> constraint;

    public ConformanceType() {

    }

    public ConformanceType(final List<DomainType> constraint) {
        this.constraint = constraint;
    }

    /**
     * Gets the value of the constraint property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link DomainType }
     *
     */
    public List<DomainType> getConstraint() {
        if (constraint == null) {
            constraint = new ArrayList<>();
        }
        return this.constraint;
    }

}
