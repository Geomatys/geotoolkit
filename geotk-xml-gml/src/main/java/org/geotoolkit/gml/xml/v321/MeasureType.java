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

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import org.opengis.observation.BaseUnit;
import org.opengis.observation.Measure;


/**
 * gml:MeasureType supports recording an amount encoded as a value of XML Schema double, together with a units of measure indicated by an attribute uom, short for "units Of measure". The value of the uom attribute identifies a reference system for the amount, usually a ratio or interval scale.
 *
 * <p>Java class for MeasureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MeasureType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *       &lt;attribute name="uom" use="required" type="{http://www.opengis.net/gml/3.2}UomIdentifier" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "value"
})
@XmlSeeAlso({
    LengthType.class,
    AngleType.class,
    Quantity.class,
    VolumeType.class,
    GridLengthType.class,
    SpeedType.class,
    ScaleType.class,
    AreaType.class,
    TimeType.class
})
public class MeasureType implements Measure {

    @XmlValue
    private double value;
    @XmlAttribute(required = true)
    private String uom;

    public MeasureType() {

    }

    public MeasureType(final String uom, final double value) {
        this.uom = uom;
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     *
     */
    @Override
    public float getValue() {
        return (float) value;
    }

    /**
     * Sets the value of the value property.
     *
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets the value of the uom property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public BaseUnit getUom() {
        return null; // problem
    }

    public String getUomStr() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUom(String value) {
        this.uom = value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        return object instanceof MeasureType that &&
               Objects.equals(this.uom,   that.uom) &&
               Objects.equals(this.value, that.value) ;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        hash = 89 * hash + Objects.hashCode(this.uom);
        return hash;
    }


    /**
     * Retourne une description de l'objet (debug).
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[MeasureType]");
        if (uom != null) {
            s.append("uom =").append(uom).append('\n');
        }
        s.append(" value=").append(value).append('\n');
        return  s.toString();
    }
}
