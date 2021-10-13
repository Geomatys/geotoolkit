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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractCoordinateSystemType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractCoordinateSystemType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}IdentifiedObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}axis" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}AggregationAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "axis"
})
@XmlSeeAlso({
    TimeCSType.class,
    ObliqueCartesianCSType.class,
    VerticalCSType.class,
    LinearCSType.class,
    SphericalCSType.class,
    EllipsoidalCSType.class,
    CartesianCSType.class,
    CylindricalCSType.class,
    AffineCSType.class,
    TemporalCSType.class,
    UserDefinedCSType.class,
    PolarCSType.class
})
public abstract class AbstractCoordinateSystemType extends IdentifiedObjectType {

    @XmlElementRef(name = "axis", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private List<JAXBElement<CoordinateSystemAxisPropertyType>> axis;
    @XmlAttribute
    private AggregationType aggregationType;

    /**
     * Gets the value of the axis property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link CoordinateSystemAxisPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link CoordinateSystemAxisPropertyType }{@code >}
     *
     *
     */
    public List<JAXBElement<CoordinateSystemAxisPropertyType>> getAxis() {
        if (axis == null) {
            axis = new ArrayList<JAXBElement<CoordinateSystemAxisPropertyType>>();
        }
        return this.axis;
    }

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
