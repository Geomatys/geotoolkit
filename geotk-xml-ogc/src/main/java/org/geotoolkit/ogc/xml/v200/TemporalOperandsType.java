/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for TemporalOperandsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TemporalOperandsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TemporalOperand" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalOperandsType", propOrder = {
    "temporalOperand"
})
public class TemporalOperandsType {

    @XmlElement(name = "TemporalOperand", required = true)
    private List<TemporalOperandsType.TemporalOperand> temporalOperand;

    /**
     * Empty constructor used by JAXB
     */
    public TemporalOperandsType() {
    }

    /**
     * build a new temporal Operands object with the specified array of TemporalOperand (from geoAPI)
     */
    public TemporalOperandsType(org.geotoolkit.filter.capability.TemporalOperand[] tmpOperands) {
        if (tmpOperands == null) {
            tmpOperands = new org.geotoolkit.filter.capability.TemporalOperand[0];
        }
        temporalOperand = new ArrayList<TemporalOperandsType.TemporalOperand>();
        for (org.geotoolkit.filter.capability.TemporalOperand g: tmpOperands) {
            temporalOperand.add(new TemporalOperand(new QName("http://www.opengis.net/fes/2.0", g.name())));
        }
    }

    /**
     * build a new geometry Operands object with the specified array of GeometryOperand (from geoAPI)
     */
    public TemporalOperandsType(List<QName> tmpOperands) {
        if (tmpOperands == null) {
            tmpOperands = new ArrayList<QName>();
        }
        this.temporalOperand = new ArrayList<TemporalOperand>();
        for (QName qn : tmpOperands) {
            this.temporalOperand.add(new TemporalOperand(qn));
        }
    }

    /**
     * Gets the value of the temporalOperand property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link TemporalOperandsType.TemporalOperand }
     */
    public List<TemporalOperandsType.TemporalOperand> getTemporalOperand() {
        if (temporalOperand == null) {
            temporalOperand = new ArrayList<TemporalOperandsType.TemporalOperand>();
        }
        return this.temporalOperand;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TemporalOperand {

        @XmlAttribute(required = true)
        private QName name;

        public TemporalOperand() {
        }

        public TemporalOperand(final QName name) {
            this.name = name;
        }

        /**
         * Gets the value of the name property.
         */
        public QName getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         */
        public void setName(QName value) {
            this.name = value;
        }
    }
}
