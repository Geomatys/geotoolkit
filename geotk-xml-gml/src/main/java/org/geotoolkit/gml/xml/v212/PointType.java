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
package org.geotoolkit.gml.xml.v212;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *         A Point is defined by a single coordinate tuple.
 *
 *
 * <p>Java class for PointType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PointType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}coord"/>
 *           &lt;element ref="{http://www.opengis.net/gml}coordinates"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PointType", propOrder = {
    "coord",
    "coordinates"
})
public class PointType extends AbstractGeometryType {

    private CoordType coord;
    private CoordinatesType coordinates;

    public PointType() {

    }

    public PointType(final CoordType coord) {
        this.coord = coord;
    }

    public PointType(final CoordinatesType coord) {
        this.coordinates = coord;
    }

    public PointType(final PointType that) {
        super(that);
        if (that != null) {
            if (that.coordinates != null) {
                this.coordinates = new CoordinatesType(that.coordinates);
            }
            if (that.coord != null) {
                this.coord = new CoordType(that.coord);
            }
        }
    }

    /**
     * Gets the value of the coord property.
     *
     * @return
     *     possible object is
     *     {@link CoordType }
     *
     */
    public CoordType getCoord() {
        return coord;
    }

    /**
     * Sets the value of the coord property.
     *
     * @param value
     *     allowed object is
     *     {@link CoordType }
     *
     */
    public void setCoord(final CoordType value) {
        this.coord = value;
    }

    /**
     * Gets the value of the coordinates property.
     *
     * @return
     *     possible object is
     *     {@link CoordinatesType }
     *
     */
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of the coordinates property.
     *
     * @param value
     *     allowed object is
     *     {@link CoordinatesType }
     *
     */
    public void setCoordinates(final CoordinatesType value) {
        this.coordinates = value;
    }

    @Override
    public AbstractGeometryType getClone() {
        return new PointType(this);
    }
}
