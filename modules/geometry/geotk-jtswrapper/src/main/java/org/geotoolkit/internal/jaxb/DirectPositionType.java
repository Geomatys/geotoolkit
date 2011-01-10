/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectPositionType", propOrder = {
    "value"
})
public class DirectPositionType implements DirectPosition {

    public static final Map<String, CoordinateReferenceSystem> cachedCRS = new HashMap<String, CoordinateReferenceSystem>();


    @XmlValue
    protected List<Double> value;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    protected Integer srsDimension;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String srsName;
    @XmlAttribute
    protected List<String> axisLabels;
    @XmlAttribute
    protected List<String> uomLabels;

    /**
     * Empty constructor used by JAXB.
     */
    DirectPositionType() {}

    /**
     * Build a full Direct position.
     * @param srsName
     * @param srsDimension
     * @param axisLabels
     * @param value
     * @param uomLabels
     */
    public DirectPositionType(final String srsName, final int srsDimension, final List<String> axisLabels,
            final List<Double> value, final List<String> uomLabels)
    {
        this.srsName      = srsName;
        this.srsDimension = Integer.valueOf(srsDimension);
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
    public DirectPositionType(final String srsName, final int srsDimension, final List<String> axisLabels,
            final List<Double> value)
    {
        this.srsName      = srsName;
        this.srsDimension = Integer.valueOf(srsDimension);
        this.axisLabels   = axisLabels;
        this.value        = value;
    }

    /**
     * Build a full Direct position.
     * @param srsName
     * @param srsDimension
     * @param value
     */
    public DirectPositionType(final String srsName, final int srsDimension, final List<Double> value) {
        this.srsName      = srsName;
        this.srsDimension = Integer.valueOf(srsDimension);
        this.value        = value;
    }

    /**
     * Build a light direct position.
     *
     * @param
     * @param value a List of coordinates.
     */
    public DirectPositionType(final List<Double> value) {
        this.value        = value;
        this.srsDimension = null;
    }

    /**
     * Build a light direct position.
     *
     * @param values a List of coordinates.
     */
    public DirectPositionType(final Double... values) {
        this.value = new ArrayList<Double>();
        for (Double pt: values) {
            this.value.add(pt);
        }
        this.srsDimension = null;
    }

    /**
     * Build a light direct position.
     *
     * @param values a List of coordinates.
     */
    public DirectPositionType(final Position position) {
        if (position != null) {
            this.value = new ArrayList<Double>();
            for (double d : position.getDirectPosition().getCoordinate()) {
                value.add(d);
            }
            /*
             *  For simplified feature GML profile we don't fill the srsName and dimension attribute
             *
             CoordinateReferenceSystem crs = position.getDirectPosition().getCoordinateReferenceSystem();
            srsName = CoordinateReferenceSystemAdapter.getSrsName(crs);
            this.srsDimension = position.getDirectPosition().getDimension();
             
             */
        }
    }

    /**
     * XML List based on XML Schema double type.
     * An element of this type contains a space-separated list of double values Gets the value of the value property.
     */
    public List<Double> getValue() {
        if (value == null) {
            value = new ArrayList<Double>();
        }
        return this.value;
    }

    /**
     * Gets the value of the srsName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * Sets the value of the srsName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSrsName(final String value) {
        this.srsName = value;
    }

    /**
     * Gets the value of the srsDimension property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public Integer getSrsDimension() {
        return srsDimension;
    }

    /**
     * Sets the value of the srsDimension property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setSrsDimension(final Integer value) {
        this.srsDimension = value;
    }

    /**
     * Gets the value of the axisLabels property.
     *
     */
    public List<String> getAxisLabels() {
        if (axisLabels == null) {
            axisLabels = new ArrayList<String>();
        }
        return this.axisLabels;
    }

    /**
     * Gets the value of the uomLabels property.
     */
    public List<String> getUomLabels() {
        if (uomLabels == null) {
            uomLabels = new ArrayList<String>();
        }
        return this.uomLabels;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        CoordinateReferenceSystem crs = null;
        if (srsName != null) {
            try {
                if (cachedCRS.containsKey(srsName)) {
                    return cachedCRS.get(srsName);
                } else {
                    crs =  CRS.decode(srsName);
                }
            } catch (NoSuchAuthorityCodeException ex) {
                Logging.getLogger(DirectPositionType.class).log(Level.WARNING, ex.getMessage());
            } catch (FactoryException ex) {
                Logging.getLogger(DirectPositionType.class).log(Level.WARNING, null, ex);
            }
            cachedCRS.put(srsName, crs);
        }
        return crs;
    }

    @Override
    public int getDimension() {
        return getSrsDimension().intValue();
    }

    @Override
    public double[] getCoordinate() {
        double[] coords = new double[value.size()];
        for(int i = 0, n = value.size(); i < n; i++){
            coords[i] = value.get(i);
        }
        return coords;
    }

    @Override
    public double getOrdinate(final int dimension) throws IndexOutOfBoundsException {
        return value.get(dimension);
    }

    @Override
    public void setOrdinate(final int dimension, final double value) throws IndexOutOfBoundsException, UnsupportedOperationException {
        this.value.remove(dimension);
        this.value.add(dimension, value);
    }

    @Override
    public DirectPosition getDirectPosition() {
        return this;
    }


    /**
     * Return a description of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[DirectPositionType]:");
        if (srsName != null) {
            s.append("srsName = ").append(srsName).append('\n');
        }
        if (srsDimension != null) {
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
        hash = 71 * hash + this.srsDimension.intValue();
        hash = 71 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        hash = 71 * hash + (this.axisLabels != null ? this.axisLabels.hashCode() : 0);
        hash = 71 * hash + (this.uomLabels != null ? this.uomLabels.hashCode() : 0);
        return hash;
    }
}
