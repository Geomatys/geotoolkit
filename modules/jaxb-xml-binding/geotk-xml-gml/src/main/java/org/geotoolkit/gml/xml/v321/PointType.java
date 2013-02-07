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
import javax.xml.bind.annotation.XmlType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.UnmodifiableGeometryException;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Bearing;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Point;


/**
 * <p>Java class for PointType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PointType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGeometricPrimitiveType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}pos"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}coordinates"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PointType", propOrder = {
    "pos",
    "coordinates"
})
public class PointType extends AbstractGeometricPrimitiveType implements Point,  org.geotoolkit.gml.xml.Point{

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
        super.setId(id);
        this.pos = (pos instanceof DirectPositionType) ? (DirectPositionType)pos : new DirectPositionType(pos, true);
        if (this.pos.srsName == null) {
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
        if (this.pos.srsName == null) {
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
        if (this.pos.srsName == null) {
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
    public void setPos(DirectPositionType value) {
        this.pos = value;
    }

    /**
     * Gets the value of the coordinates property.
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
     * Sets the value of the coordinates property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoordinatesType }
     *     
     */
    public void setCoordinates(CoordinatesType value) {
        this.coordinates = value;
    }

    @Override
    public DirectPosition getDirectPosition() {
        return pos;
    }

    @Override
    public void setDirectPosition(final DirectPosition position) throws UnmodifiableGeometryException {
        this.pos = new DirectPositionType(position, true);
    }

    @Override
    public void setPosition(final DirectPosition position) throws UnmodifiableGeometryException {
        pos = new DirectPositionType(position, true);
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
}
