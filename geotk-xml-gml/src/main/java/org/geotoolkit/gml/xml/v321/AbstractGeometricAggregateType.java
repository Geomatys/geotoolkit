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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractGeometricAggregateType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractGeometricAggregateType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGeometryType">
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}AggregationAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGeometricAggregateType")
@XmlSeeAlso({
    MultiCurveType.class,
    MultiSurfaceType.class,
    MultiGeometryType.class,
    MultiSolidType.class,
    MultiPointType.class
})
public abstract class AbstractGeometricAggregateType extends AbstractGeometryType {

    public AbstractGeometricAggregateType(){
    }

    public AbstractGeometricAggregateType(final String srsName){
        super(srsName);
    }

    @XmlAttribute
    private AggregationType aggregationType;

    /**
     * Gets the value of the aggregationType property.
     *
     * @return
     *     possible object is
     *     {@link AggregationType }
     *
     */
    public AggregationType getAggregationType() {
        return aggregationType;
    }

    /**
     * Sets the value of the aggregationType property.
     *
     * @param value
     *     allowed object is
     *     {@link AggregationType }
     *
     */
    public void setAggregationType(AggregationType value) {
        this.aggregationType = value;
    }

}
