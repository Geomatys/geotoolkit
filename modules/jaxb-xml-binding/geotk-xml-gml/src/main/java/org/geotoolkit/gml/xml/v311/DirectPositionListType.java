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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;


/**
 * DirectPositionList instances hold the coordinates for a sequence of direct positions within the same coordinate 
 * 			reference system (CRS).
 * 
 * <p>Java class for DirectPositionListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectPositionListType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/gml>doubleList">
 *       &lt;attGroup ref="{http://www.opengis.net/gml}SRSReferenceGroup"/>
 *       &lt;attribute name="count" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectPositionListType", propOrder = {
    "value"
})
public class DirectPositionListType {

    @XmlValue
    private List<Double> value;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer count;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer srsDimension;
    @XmlAttribute
    private List<String> axisLabels;
    @XmlAttribute
    private List<String> uomLabels;

    /**
     * XML List based on XML Schema double type.
     * An element of this type contains a space-separated list of double values Gets the value of the value property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
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
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     */
    public void setValue(final List<Double> value) {
        this.value = value;
    }

    /**
     * XML List based on XML Schema double type.
     * An element of this type contains a space-separated list of double values Gets the value of the value property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     */
    public void setValue(final Double value) {
        if (this.value == null) {
            this.value = new ArrayList<Double>();
        }
        this.value.add(value);
    }
    
    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCount(final Integer value) {
        this.count = value;
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
     *     {@link Integer }
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
     *     {@link Integer }
     *     
     */
    public void setSrsDimension(final Integer value) {
        this.srsDimension = value;
    }

    /**
     * Gets the value of the axisLabels property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
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
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUomLabels() {
        if (uomLabels == null) {
            uomLabels = new ArrayList<String>();
        }
        return this.uomLabels;
    }

    /**
     * Return a String description of the object.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[DirectPositionListType}\n");
        if (count != null) {
            s.append("count : ").append(count).append('\n');
        }
        if (srsDimension != null) {
            s.append("srsDimension : ").append(srsDimension).append('\n');
        }
        if (srsName != null) {
            s.append("srsName : ").append(srsName).append('\n');
        }
        if (axisLabels != null) {
            s.append("axisLabels : ").append('\n');
            for (String a : axisLabels) {
                s.append(a).append('\n');
            }
        }
        if (uomLabels != null) {
            s.append("uomLabels : ").append('\n');
            for (String a : uomLabels) {
                s.append(a).append('\n');
            }
        }
        if (value != null) {
            s.append("value : ").append('\n');
            for (Double a : value) {
                s.append(a).append('\n');
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
        if (object instanceof DirectPositionListType) {
            final DirectPositionListType that = (DirectPositionListType) object;
            return  Utilities.equals(this.getAxisLabels(), that.getAxisLabels()) &&
                    Utilities.equals(this.srsDimension,    that.srsDimension)    &&
                    Utilities.equals(this.srsName,         that.srsName)         &&
                    Utilities.equals(this.count,           that.count)           &&
                    Utilities.equals(this.getUomLabels(),  that.getUomLabels())  &&
                    Utilities.equals(this.value,           that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 53 * hash + (this.count != null ? this.count.hashCode() : 0);
        hash = 53 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        hash = 53 * hash + (this.srsDimension != null ? this.srsDimension.hashCode() : 0);
        hash = 53 * hash + (this.axisLabels != null ? this.axisLabels.hashCode() : 0);
        hash = 53 * hash + (this.uomLabels != null ? this.uomLabels.hashCode() : 0);
        return hash;
    }


}
