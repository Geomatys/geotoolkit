/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.gmlcov.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractGeometricAggregateType;
import org.geotoolkit.gml.xml.v321.DirectPositionListType;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * <p>Java class for SimpleMultiPointType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SimpleMultiPointType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGeometricAggregateType">
 *       &lt;sequence>
 *         &lt;element name="positions" type="{http://www.opengis.net/gml/3.2}DirectPositionListType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleMultiPointType", propOrder = {
    "positions"
})
public class SimpleMultiPointType extends AbstractGeometricAggregateType {

    @XmlElement(required = true)
    private DirectPositionListType positions;

    /**
     * Gets the value of the positions property.
     *
     * @return
     *     possible object is
     *     {@link DirectPositionListType }
     *
     */
    public DirectPositionListType getPositions() {
        return positions;
    }

    /**
     * Sets the value of the positions property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectPositionListType }
     *
     */
    public void setPositions(DirectPositionListType value) {
        this.positions = value;
    }

    @Override
    public Object evaluate(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T evaluate(Object o, Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(ExpressionVisitor ev, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
