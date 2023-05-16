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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.DirectPosition;
import org.geotoolkit.gml.xml.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 *
 *         The Box structure defines an extent using a pair of coordinate tuples.
 *
 *
 * <p>Java class for BoxType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BoxType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometryType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}coord" maxOccurs="2" minOccurs="2"/>
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
@XmlType(name = "BoxType", propOrder = {
    "coord",
    "coordinates"
})
public class BoxType extends AbstractGeometryType implements Envelope{

    private List<CoordType> coord;
    private CoordinatesType coordinates;

    public BoxType() {

    }

    public BoxType(final List<CoordType> coord, final String srsName) {
        super(srsName);
        this.coord = coord;
    }

    public BoxType(final BoxType that) {
        super(that);
        if (that != null) {
            if (that.coordinates != null) {
                this.coordinates = new CoordinatesType(that.coordinates);
            }
            if (that.coord != null) {
                this.coord = new ArrayList<>();
                for (CoordType c : that.coord) {
                    this.coord.add(new CoordType(c));
                }
            }
        }
    }

    /**
     * Gets the value of the coord property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link CoordType }
     *
     *
     */
    public List<CoordType> getCoord() {
        if (coord == null) {
            coord = new ArrayList<>();
        }
        return this.coord;
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
    public void setCoordinates(final CoordinatesType value) {
        this.coordinates = value;
    }

    @Override
    public AbstractGeometryType getClone() {
        return new BoxType(this);
    }

    @Override
    public void setSrsDimension(Integer dim) {
        // do nothing
    }

    @Override
    public List<String> getAxisLabels() {
        return new ArrayList<>();
    }

    @Override
    public void setAxisLabels(List<String> axisLabels) {
        // do nothing
    }

    @Override
    public List<String> getUomLabels() {
        return new ArrayList<>();
    }

    @Override
    public List<? extends DirectPosition> getPos() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCompleteEnvelope2D() {
         return getLowerCorner() != null && getUpperCorner() != null &&
               getLowerCorner().getDimension() == 2 && getUpperCorner().getDimension() == 2;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDimension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public org.opengis.geometry.DirectPosition getLowerCorner() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public org.opengis.geometry.DirectPosition getUpperCorner() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMinimum(int dimension) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMaximum(int dimension) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMedian(int dimension) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getSpan(int dimension) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getSrsDimension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
