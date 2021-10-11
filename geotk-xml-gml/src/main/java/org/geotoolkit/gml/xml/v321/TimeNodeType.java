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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TimeNodeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TimeNodeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractTimeTopologyPrimitiveType">
 *       &lt;sequence>
 *         &lt;element name="previousEdge" type="{http://www.opengis.net/gml/3.2}TimeEdgePropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="nextEdge" type="{http://www.opengis.net/gml/3.2}TimeEdgePropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="position" type="{http://www.opengis.net/gml/3.2}TimeInstantPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeNodeType", propOrder = {
    "previousEdge",
    "nextEdge",
    "position"
})
public class TimeNodeType extends AbstractTimeTopologyPrimitiveType implements Serializable {

    private List<TimeEdgePropertyType> previousEdge;
    private List<TimeEdgePropertyType> nextEdge;
    private TimeInstantPropertyType position;

    public TimeNodeType() {

    }

    public TimeNodeType(final TimeNodeType that) {
        super(that);
        if (that != null) {
            if (that.position != null) {
                this.position = new TimeInstantPropertyType(that.position);
            }
            if (that.nextEdge != null) {
                this.nextEdge = new ArrayList<TimeEdgePropertyType>();
                for (TimeEdgePropertyType te : that.nextEdge) {
                    final TimeEdgePropertyType nte = new TimeEdgePropertyType(te);
                    this.nextEdge.add(nte);
                }
            }
            if (that.previousEdge != null) {
                this.previousEdge = new ArrayList<TimeEdgePropertyType>();
                for (TimeEdgePropertyType te : that.previousEdge) {
                    final TimeEdgePropertyType nte = new TimeEdgePropertyType(te);
                    this.previousEdge.add(nte);
                }
            }
        }
    }
    /**
     * Gets the value of the previousEdge property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link TimeEdgePropertyType }
     *
     *
     */
    public List<TimeEdgePropertyType> getPreviousEdge() {
        if (previousEdge == null) {
            previousEdge = new ArrayList<TimeEdgePropertyType>();
        }
        return this.previousEdge;
    }

    /**
     * Gets the value of the nextEdge property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link TimeEdgePropertyType }
     *
     *
     */
    public List<TimeEdgePropertyType> getNextEdge() {
        if (nextEdge == null) {
            nextEdge = new ArrayList<TimeEdgePropertyType>();
        }
        return this.nextEdge;
    }

    /**
     * Gets the value of the position property.
     *
     * @return
     *     possible object is
     *     {@link TimeInstantPropertyType }
     *
     */
    public TimeInstantPropertyType getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     *
     * @param value
     *     allowed object is
     *     {@link TimeInstantPropertyType }
     *
     */
    public void setPosition(TimeInstantPropertyType value) {
        this.position = value;
    }

    @Override
    public AbstractTimeObjectType getClone() {
        return new TimeNodeType(this);
    }

}
