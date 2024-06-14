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

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import org.opengis.filter.Expression;
import org.opengis.temporal.TemporalPrimitive;
import org.opengis.util.ScopedName;

/**
 * The abstract supertype for temporal geometric primitives.
 *        A temporal geometry must be associated with a temporal reference system via URI.
 *        The Gregorian calendar with UTC is the default reference system, following ISO
 *        8601. Other reference systems in common use include the GPS calendar and the
 *        Julian calendar.
 *
 * <p>Java class for AbstractTimeGeometricPrimitiveType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractTimeGeometricPrimitiveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractTimePrimitiveType">
 *       &lt;attribute name="frame" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="#ISO-8601" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractTimeGeometricPrimitiveType")
@XmlSeeAlso({
    TimePeriodType.class,
    TimeInstantType.class
})
public abstract class AbstractTimeGeometricPrimitiveType extends AbstractTimePrimitiveType implements Expression {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String frame;

    public AbstractTimeGeometricPrimitiveType() {
    }

    public AbstractTimeGeometricPrimitiveType(final String id) {
        super(id);
    }

    public AbstractTimeGeometricPrimitiveType(TemporalPrimitive that) {
        super(that);
        if (that instanceof AbstractTimeGeometricPrimitiveType) {
            this.frame = ((AbstractTimeGeometricPrimitiveType)that).frame;
        }
    }

    /**
     * Gets the value of the frame property.
     *
     * @return
     *     possible object is
     *     {@link String }
     */
    public String getFrame() {
        return frame;
    }

    /**
     * Sets the value of the frame property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    public void setFrame(final String value) {
        this.frame = value;
    }

    @Override
    public ScopedName getFunctionName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class getResourceClass() {
        return null;        // Undetermined class.
    }

    @Override
    public List<Expression> getParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object apply(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression toValueType(Class type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
