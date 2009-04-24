/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml.v311modified;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;


/**
 * Direct position instances hold the coordinates for a position within some coordinate reference system (CRS). 
 * Since direct positions, as data types, 
 * will often be included in larger objects (such as geometry elements) that have references to CRS, 
 * the srsName attribute will in general be missing, 
 * if this particular direct position is included in a larger element with such a reference to a CRS.
 * In this case, the CRS is implicitly assumed to take on the value of the containing object's CRS.
 * if no srsName attribute is given, 
 * the CRS shall be specified as part of the larger context this geometry element is part of,
 * typically a geometric object like a point, curve, etc.
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectPositionType", propOrder = {
    "value"
})
public class DirectPositionType {

    @XmlValue
    private List<Double> value;
    @XmlAttribute
    private Integer srsDimension;
    @XmlAttribute
    private String srsName;
    @XmlAttribute
    private List<String> axisLabels;
    @XmlAttribute
    private List<String> uomLabels;

    /**
     * Empty constructor used by JAXB.
     */
    public DirectPositionType() {

    }

    /**
     * Build a full Direct position.
     * @param srsName
     * @param srsDimension
     * @param axisLabels
     * @param value
     * @param uomLabels
     */
    public DirectPositionType(String srsName, int srsDimension, List<String> axisLabels,
            List<Double> value, List<String> uomLabels)
    {
        this.srsName      = srsName;
        this.srsDimension = srsDimension;
        this.axisLabels   = axisLabels;
        this.value        = value;
        this.uomLabels    = uomLabels;
    }

    /**
     * Build a full Direct position.
     * @param srsName
     * @param srsDimension
     * @param axisLabels
     * @param value
     */
    public DirectPositionType(String srsName, int srsDimension, List<String> axisLabels,
            List<Double> value)
    {
        this.srsName      = srsName;
        this.srsDimension = srsDimension;
        this.axisLabels   = axisLabels;
        this.value        = value;
    }

    /**
     * Build a full Direct position.
     * @param srsName
     * @param srsDimension
     * @param value
     */
    public DirectPositionType(String srsName, int srsDimension, List<Double> value) {
        this.srsName      = srsName;
        this.srsDimension = srsDimension;
        this.value        = value;
    }

    /**
     * Build a light direct position.
     *
     * @param
     * @param value a List of coordinates.
     */
    public DirectPositionType(List<Double> value) {
        this.value        = value;
        this.srsDimension = null;
    }

    /**
     * Build a light direct position.
     *
     * @param values a List of coordinates.
     */
    public DirectPositionType(Double... values) {
        this.value = new ArrayList<Double>();
        for (Double pt: values) {
            this.value.add(pt);
        }
        this.srsDimension = null;
    }

    /**
     * A type for a list of values of the respective simple type.
     * Gets the value of the value property.
     * (unmodifiable)
     */
    public List<Double> getValue() {
        if (value == null) {
            value = new ArrayList<Double>();
        }
        return value;
    }

    /**
     * Gets the value of the srsDimension property.
     */
    public int getDimension() {
        return srsDimension;
    }

    /**
     * Gets the value of the srsName property.
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * Gets the value of the axisLabels property.
     * (unmodifiable)
     */
    public List<String> getAxisLabels() {
        if (axisLabels == null) {
            axisLabels = new ArrayList<String>();
        }
        return axisLabels;
    }

    /**
     * Gets the value of the uomLabels property.
     * (unmodifiable)
     */
    public List<String> getUomLabels() {
        if (uomLabels == null) {
            uomLabels = new ArrayList<String>();
        }
        return uomLabels;
    }

    /**
     * Return a description of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[DirectPositionType]:");
        if (srsName != null) {
            s.append("srsName = ").append(srsName).append('\n');
            s.append(" srsDimension = ").append(srsDimension).append('\n');
        }
        if (value != null) {
            s.append(" value: ").append('\n');
            for(double v :value) {
                s.append(v).append(", ");
            }
        }
        return s.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DirectPositionType) {
            final DirectPositionType that = (DirectPositionType) object;
            return  Utilities.equals(this.axisLabels, that.axisLabels)     &&
                    Utilities.equals(this.srsDimension, that.srsDimension) &&
                    Utilities.equals(this.srsName, that.srsName)           &&
                    Utilities.equals(this.uomLabels, that.uomLabels)       &&
                    Utilities.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 71 * hash + this.srsDimension;
        hash = 71 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        hash = 71 * hash + (this.axisLabels != null ? this.axisLabels.hashCode() : 0);
        hash = 71 * hash + (this.uomLabels != null ? this.uomLabels.hashCode() : 0);
        return hash;
    }
}
