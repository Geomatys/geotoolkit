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

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Bearing;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Point;


/**
 * A Point is defined by a single coordinate tuple.
 *
 * <p>Java class for PointType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PointType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometricPrimitiveType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}pos"/>
 *           &lt;element ref="{http://www.opengis.net/gml}coordinates"/>
 *           &lt;element ref="{http://www.opengis.net/gml}coord"/>
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
    "pos",
    "coordinates"
})
@XmlRootElement(name="Point")
public class PointType extends AbstractGeometricPrimitiveType implements Point,  org.geotoolkit.gml.xml.Point {

    private DirectPositionType pos;
    private CoordinatesType coordinates;

    /**
     * An empty constructor used by JAXB.
     */
    PointType() {}

    /**
     * Build a new Point with the specified identifier and DirectPositionType
     *
     * @param id The identifier of the point.
     * @param pos A direcPosition locating the point.
     */
    public PointType(final String id, final DirectPosition pos) {
        super(id, null);
        this.pos = (pos instanceof DirectPositionType) ? (DirectPositionType)pos : new DirectPositionType(pos);
        if (this.pos.getSrsName() == null) {
            this.pos.setSrsName(getSrsName());
        }
    }

    public PointType(final String id, final String crsName, final DirectPosition pos) {
        super(id, crsName);
        this.pos = (pos instanceof DirectPositionType) ? (DirectPositionType)pos : new DirectPositionType(pos);
        if (this.pos.getSrsName() == null) {
            this.pos.setSrsName(getSrsName());
        }
    }

    /**
     * Build a new Point with the specified DirectPositionType
     *
     * @param pos A direcPosition locating the point.
     */
    public PointType(final DirectPosition pos) {
        this.pos = (pos instanceof DirectPositionType) ? (DirectPositionType)pos : new DirectPositionType(pos, true);
        if (this.pos.getSrsName() == null) {
            this.pos.setSrsName(getSrsName());
        }
    }

    /**
     * Build a new Point with the specified DirectPositionType
     *
     * @param pos A direcPosition locating the point.
     * @param srsInfo if true the srs information will be applied to the directPosition
     */
    public PointType(final DirectPosition pos, final boolean srsInfo) {
        this.pos = (pos instanceof DirectPositionType) ? (DirectPositionType)pos : new DirectPositionType(pos, srsInfo);
        if (this.pos.getSrsName() == null) {
            this.pos.setSrsName(getSrsName());
        }
    }

    /**
     * Build a point Type with the specified coordinates.
     *
     * @param coordinates a list of coordinates.
     */
    public PointType(final CoordinatesType coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Gets the value of the pos property.
     *
     * @return
     *     possible object is
     *     {@link DirectPositionType }
     *
     */
    @Override
    public DirectPositionType getPos() {
        return pos;
    }

    /**
     * Sets the value of the pos property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectPositionType }
     *
     */
    public void setPos(final DirectPositionType value) {
        this.pos = value;
    }

    /**
     * Deprecated with GML version 3.1.0 for coordinates with ordinate values that are numbers.
     * Use "pos" instead.
     * The "coordinates" element shall only be used for coordinates with ordinates that require a string
     * representation, e.g. DMS representations.
     *
     * @return
     *     possible object is
     *     {@link CoordinatesType }
     *
     */
    @Override
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * Deprecated with GML version 3.1.0 for coordinates with ordinate values that are numbers.
     * Use "pos"instead.
     * The "coordinates" element shall only be used for coordinates with ordinates that require a string
     * representation, e.g. DMS representations.
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
    public DirectPosition getDirectPosition() {
        return pos;
    }

    public void setDirectPosition(final DirectPosition position) throws UnsupportedOperationException {
        this.pos = new DirectPositionType(position);
    }

    @Override
    public OrientablePrimitive[] getProxy() {
        return null;
    }

    @Override
    public PointType clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Bearing getBearing(final Position toPoint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Return a String description of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n');
        if (pos != null) {
            s.append("position : ").append(pos.toString()).append('\n');
        }
        if (coordinates != null) {
            s.append(" coordinates : ").append(coordinates.toString()).append('\n');
        }

        return s.toString();
    }

    /**
     * Verify that the point is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof PointType && super.equals(object, mode)) {
            final PointType that = (PointType) object;
            return  Objects.equals(this.pos, that.pos) &&
                    Objects.equals(this.coordinates, that.coordinates);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.pos != null ? this.pos.hashCode() : 0);
        hash = 17 * hash + (this.coordinates != null ? this.coordinates.hashCode() : 0);
        return hash;
    }
}
