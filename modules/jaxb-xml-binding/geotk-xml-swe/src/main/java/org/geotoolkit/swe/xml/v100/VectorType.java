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
package org.geotoolkit.swe.xml.v100;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.Coordinate;
import org.geotoolkit.swe.xml.Vector;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for VectorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VectorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractVectorType">
 *       &lt;sequence>
 *         &lt;element name="coordinate" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;group ref="{http://www.opengis.net/swe/1.0}AnyNumerical" minOccurs="0"/>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VectorType", propOrder = {
    "coordinate"
})
public class VectorType extends AbstractVectorType implements Vector {

    @XmlElement(required = true)
    private List<CoordinateType> coordinate;

    public VectorType() {

    }

    public VectorType(final Vector v) {
        super(v);
        if (v != null && v.getCoordinate() != null) {
            this.coordinate = new ArrayList<CoordinateType>();
            for (Coordinate c : v.getCoordinate()) {
                this.coordinate.add(new CoordinateType(c));
            }
        }
    }
    
    public VectorType(final URI referenceFrame, final URI localFrame, final List<CoordinateType> coordinate) {
        super(referenceFrame, localFrame);
        this.coordinate = coordinate;
    }

    public VectorType(final URI definition, final List<CoordinateType> coordinate) {
        super(definition);
        this.coordinate = coordinate;
    }

    public VectorType(final List<CoordinateType> coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * Gets the value of the coordinate property.
     */
    public List<CoordinateType> getCoordinate() {
        if (coordinate == null) {
            coordinate = new ArrayList<CoordinateType>();
        }
        return this.coordinate;
    }

    /**
     * Gets the value of the coordinate property.
     */
    public void setCoordinate(final List<CoordinateType> coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * Gets the value of the coordinate property.
     */
    public void setCoordinate(final CoordinateType coordinate) {
        if (this.coordinate == null) {
            this.coordinate = new ArrayList<CoordinateType>();
        }
        this.coordinate.add(coordinate);
    }

    /**
     * Gets the value of the coordinate property.
     */
    public void setCoordinate(final QuantityType coordinate) {
        if (this.coordinate == null) {
            this.coordinate = new ArrayList<CoordinateType>();
        }
        this.coordinate.add(new CoordinateType(coordinate));
    }

    /**
     * Gets the value of the coordinate property.
     */
    public void setCoordinate(final Count coordinate) {
        if (this.coordinate == null) {
            this.coordinate = new ArrayList<CoordinateType>();
        }
        this.coordinate.add(new CoordinateType(coordinate));
    }

    /**
     * Gets the value of the coordinate property.
     */
    public void setCoordinate(final TimeType coordinate) {
        if (this.coordinate == null) {
            this.coordinate = new ArrayList<CoordinateType>();
        }
        this.coordinate.add(new CoordinateType(coordinate));
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof VectorType && super.equals(object, mode)) {
            final VectorType  that = (VectorType ) object;
            return Utilities.equals(this.coordinate, that.coordinate);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.coordinate != null ? this.coordinate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (coordinate != null) {
            s.append("coordinate:").append(coordinate).append('\n');
        }
        return s.toString();
    }

}
