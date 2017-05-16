/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractAllowedValues;


/**
 * <p>Java class for AllowedValuesType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AllowedValuesType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSWEType">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}double" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="interval" type="{http://www.opengis.net/swe/2.0}RealPair" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="significantFigures" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AllowedValuesType", propOrder = {
    "value",
    "interval",
    "significantFigures"
})
public class AllowedValuesType extends AbstractSWEType implements AbstractAllowedValues {

    @XmlElement(type = Double.class)
    private List<Double> value;
    @XmlElementRef(name = "interval", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
    private List<JAXBElement<List<Double>>> interval;
    private BigInteger significantFigures;

    /**
     * Gets the value of the value property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     *
     */
    public List<Double> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return this.value;
    }

    @Override
    public List<Double> getInterval() {
        for (JAXBElement<List<Double>> jb : getJbInterval()) {
            if (jb.getName().getLocalPart().equals("interval")) {
                return jb.getValue();
            } else {
                System.out.println("locpart:" + jb.getName().getLocalPart());
            }
        }
        return null;
    }

    /**
     * Gets the value of the interval property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}
     *
     */
    public List<JAXBElement<List<Double>>> getJbInterval() {
        if (interval == null) {
            interval = new ArrayList<>();
        }
        return this.interval;
    }

    /**
     * Gets the value of the significantFigures property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getSignificantFigures() {
        return significantFigures;
    }

    /**
     * Sets the value of the significantFigures property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setSignificantFigures(BigInteger value) {
        this.significantFigures = value;
    }

    @Override
    public Double getMin() {
        final List<Double> i = getInterval();
        if (i != null && !i.isEmpty()) {
            return i.get(0);
        }
        return null;
    }

    @Override
    public void setMin(Double value) {
        List<Double> i = getInterval();
        if (i == null) {
            i = new ArrayList<>(2);
            i.add(value);
            i.add(Double.NaN);
            final ObjectFactory factory = new ObjectFactory();
            interval = new ArrayList<>();
            interval.add(factory.createAllowedValuesTypeInterval(i));

        } else if (!i.isEmpty()) {
            i.set(0, value);
        } else {
            i.add(value);
            i.add(Double.NaN);
        }
    }

    @Override
    public Double getMax() {
        final List<Double> i = getInterval();
        if (i != null && i.size() > 1) {
            return i.get(1);
        }
        return null;
    }

    @Override
    public void setMax(Double value) {
        List<Double> i = getInterval();
        if (i == null) {
            i = new ArrayList<>(2);
            i.add(Double.NaN);
            i.add(value);
            final ObjectFactory factory = new ObjectFactory();
            interval = new ArrayList<>();
            interval.add(factory.createAllowedValuesTypeInterval(i));

        } else if (!i.isEmpty() && i.size() > 1) {
            i.set(1, value);
        } else if (!i.isEmpty() && i.size() == 1) {
            i.add(value);
        } else {
            i.add(value);
            i.add(Double.NaN);
        }
    }

    @Override
    public List<Double> getValueList() {
        return value;
    }
}
