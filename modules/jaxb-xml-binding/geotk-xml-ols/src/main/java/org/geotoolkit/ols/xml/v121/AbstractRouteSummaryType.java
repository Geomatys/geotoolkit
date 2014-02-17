/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.geotoolkit.gml.xml.v311.EnvelopeType;


/**
 * Abstract type which specifies a route's overall characteristics.
 * 
 * <p>Java class for AbstractRouteSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractRouteSummaryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractDataType">
 *       &lt;sequence>
 *         &lt;element name="TotalTime" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         &lt;element name="TotalDistance" type="{http://www.opengis.net/xls}DistanceType"/>
 *         &lt;element ref="{http://www.opengis.net/xls}BoundingBox"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractRouteSummaryType", propOrder = {
    "totalTime",
    "totalDistance",
    "boundingBox"
})
@XmlSeeAlso({
    RouteSummaryType.class
})
public abstract class AbstractRouteSummaryType extends AbstractDataType {

    @XmlElement(name = "TotalTime", required = true)
    private Duration totalTime;
    @XmlElement(name = "TotalDistance", required = true)
    private DistanceType totalDistance;
    @XmlElement(name = "BoundingBox", required = true)
    private EnvelopeType boundingBox;

    /**
     * Gets the value of the totalTime property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getTotalTime() {
        return totalTime;
    }

    /**
     * Sets the value of the totalTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setTotalTime(Duration value) {
        this.totalTime = value;
    }

    /**
     * Gets the value of the totalDistance property.
     * 
     * @return
     *     possible object is
     *     {@link DistanceType }
     *     
     */
    public DistanceType getTotalDistance() {
        return totalDistance;
    }

    /**
     * Sets the value of the totalDistance property.
     * 
     * @param value
     *     allowed object is
     *     {@link DistanceType }
     *     
     */
    public void setTotalDistance(DistanceType value) {
        this.totalDistance = value;
    }

    /**
     * Rectangular area bounding the complete route.
     * 
     * @return
     *     possible object is
     *     {@link EnvelopeType }
     *     
     */
    public EnvelopeType getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of the boundingBox property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnvelopeType }
     *     
     */
    public void setBoundingBox(EnvelopeType value) {
        this.boundingBox = value;
    }

}
