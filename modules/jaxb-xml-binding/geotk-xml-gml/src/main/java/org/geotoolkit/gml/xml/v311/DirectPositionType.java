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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * DirectPosition instances hold the coordinates for a position within some coordinate reference system (CRS). Since 
 * 			DirectPositions, as data types, will often be included in larger objects (such as geometry elements) that have references to CRS, the 
 * 			"srsName" attribute will in general be missing, if this particular DirectPosition is included in a larger element with such a reference to a 
 * 			CRS. In this case, the CRS is implicitly assumed to take on the value of the containing object's CRS.
 * 
 * <p>Java class for DirectPositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectPositionType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/gml>doubleList">
 *       &lt;attGroup ref="{http://www.opengis.net/gml}SRSReferenceGroup"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectPositionType", propOrder = {
    "value"
})
public class DirectPositionType implements DirectPosition {

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
    public DirectPositionType(String srsName, int srsDimension, List<String> axisLabels,
            List<Double> value, List<String> uomLabels)
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
    public DirectPositionType(String srsName, int srsDimension, List<String> axisLabels,
            List<Double> value)
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
    public DirectPositionType(String srsName, int srsDimension, List<Double> value) {
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
     * Build a light direct position.
     *
     * @param values a List of coordinates.
     */
    public DirectPositionType(DirectPosition position) {
        if (position != null) {
            this.value = new ArrayList<Double>();
            for (double d : position.getCoordinate()) {
                value.add(d);
            }
            CoordinateReferenceSystem crs = position.getCoordinateReferenceSystem();
            if ( crs != null) {
                try {
                    this.srsName = CRS.lookupIdentifier(crs, true);
                } catch (FactoryException ex) {
                    Logging.getLogger(DirectPositionType.class).log(Level.SEVERE, null, ex);
                }
            }
            this.srsDimension = position.getDimension();
        }
    }

    /**
     * XML List based on XML Schema double type.
     * An element of this type contains a space-separated list of double values Gets the value of the value property.
     * 
     */
    public List<Double> getValue() {
        if (value == null) {
            value = new ArrayList<Double>();
        }
        return this.value;
    }

    /**
     * XML List based on XML Schema double type.
     * An element of this type contains a space-separated list of double values Gets the value of the value property.
     *
     */
    public void setValue(List<Double> value) {
        this.value = value;
    }

    /**
     * XML List based on XML Schema double type.
     * An element of this type contains a space-separated list of double values Gets the value of the value property.
     *
     */
    public void setValue(Double value) {
        if (this.value == null) {
            this.value = new ArrayList<Double>();
        }
        this.value.add(value);
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
    public void setSrsName(String value) {
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
    public void setSrsDimension(Integer value) {
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
        if (srsName != null) {
            try {
                return CRS.decode(srsName);
            } catch (NoSuchAuthorityCodeException ex) {
                Logging.getLogger(DirectPositionType.class).log(Level.SEVERE, null, ex);
            } catch (FactoryException ex) {
                Logging.getLogger(DirectPositionType.class).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public int getDimension() {
        if (srsDimension != null) {
            return srsDimension.intValue();
        }
        return value.size();
    }

    @Override
    public double[] getCoordinate() {
        double[] coords = new double[value.size()];
        for(int i=0,n=value.size(); i<n; i++){
            coords[i] = value.get(i);
        }
        return coords;
    }

    @Override
    public double getOrdinate(int dimension) throws IndexOutOfBoundsException {
        return value.get(dimension);
    }

    @Override
    public void setOrdinate(int dimension, double value) throws IndexOutOfBoundsException, UnsupportedOperationException {
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
            return  Utilities.equals(this.getAxisLabels(), that.getAxisLabels()) &&
                    Utilities.equals(this.srsDimension,    that.srsDimension)    &&
                    Utilities.equals(this.srsName,         that.srsName)         &&
                    Utilities.equals(this.getUomLabels(),  that.getUomLabels())  &&
                    Utilities.equals(this.value,           that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 71 * hash + (this.srsDimension != null ? this.srsDimension.intValue() : 0);
        hash = 71 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        hash = 71 * hash + (this.axisLabels != null ? this.axisLabels.hashCode() : 0);
        hash = 71 * hash + (this.uomLabels != null ? this.uomLabels.hashCode() : 0);
        return hash;
    }
}
