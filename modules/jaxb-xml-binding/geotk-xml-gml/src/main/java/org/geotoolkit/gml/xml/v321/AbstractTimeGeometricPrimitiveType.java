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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.Expression;
import org.opengis.temporal.Duration;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.ScopedName;


/**
 * <p>Java class for AbstractTimeGeometricPrimitiveType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractTimeGeometricPrimitiveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractTimePrimitiveType">
 *       &lt;attribute name="frame" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="#ISO-8601" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractTimeGeometricPrimitiveType")
@XmlSeeAlso({
    TimeInstantType.class,
    TimePeriodType.class
})
public abstract class AbstractTimeGeometricPrimitiveType extends AbstractTimePrimitiveType implements TemporalGeometricPrimitive, Expression {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String frame;

    public AbstractTimeGeometricPrimitiveType() {
    }

    public AbstractTimeGeometricPrimitiveType(final String id) {
        super(id);
    }

    public AbstractTimeGeometricPrimitiveType(final AbstractTimeGeometricPrimitiveType that) {
        super(that);
        if (that != null) {
            this.frame = that.frame;
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
        if (frame == null) {
            return "#ISO-8601";
        } else {
            return frame;
        }
    }

    /**
     * Sets the value of the frame property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    public void setFrame(String value) {
        this.frame = value;
    }

    @Override
    public Duration distance(final TemporalGeometricPrimitive tgp) {
        return null;
    }

    @Override
    public Duration length() {
        return null;
    }

    @Override
    public ScopedName getFunctionName() {
        throw new UnsupportedOperationException("Not supported yet.");
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
