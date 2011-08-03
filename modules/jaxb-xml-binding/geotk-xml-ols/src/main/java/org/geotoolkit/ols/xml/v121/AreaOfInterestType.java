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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CircleByCenterPointType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.gml.xml.v311.PolygonType;


/**
 * <p>Java class for AreaOfInterestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AreaOfInterestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractDataType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/gml}CircleByCenterPoint"/>
 *         &lt;element ref="{http://www.opengis.net/gml}Polygon"/>
 *         &lt;element ref="{http://www.opengis.net/gml}Envelope"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AreaOfInterestType", propOrder = {
    "circleByCenterPoint",
    "polygon",
    "envelope"
})
public class AreaOfInterestType extends AbstractDataType {

    @XmlElement(name = "CircleByCenterPoint", namespace = "http://www.opengis.net/gml")
    private CircleByCenterPointType circleByCenterPoint;
    @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml")
    private PolygonType polygon;
    @XmlElement(name = "Envelope", namespace = "http://www.opengis.net/gml")
    private EnvelopeType envelope;

    /**
     * Gets the value of the circleByCenterPoint property.
     * 
     * @return
     *     possible object is
     *     {@link CircleByCenterPointType }
     *     
     */
    public CircleByCenterPointType getCircleByCenterPoint() {
        return circleByCenterPoint;
    }

    /**
     * Sets the value of the circleByCenterPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link CircleByCenterPointType }
     *     
     */
    public void setCircleByCenterPoint(CircleByCenterPointType value) {
        this.circleByCenterPoint = value;
    }

    /**
     * Gets the value of the polygon property.
     * 
     * @return
     *     possible object is
     *     {@link PolygonType }
     *     
     */
    public PolygonType getPolygon() {
        return polygon;
    }

    /**
     * Sets the value of the polygon property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolygonType }
     *     
     */
    public void setPolygon(PolygonType value) {
        this.polygon = value;
    }

    /**
     * Gets the value of the envelope property.
     * 
     * @return
     *     possible object is
     *     {@link EnvelopeType }
     *     
     */
    public EnvelopeType getEnvelope() {
        return envelope;
    }

    /**
     * Sets the value of the envelope property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnvelopeType }
     *     
     */
    public void setEnvelope(EnvelopeType value) {
        this.envelope = value;
    }

}
