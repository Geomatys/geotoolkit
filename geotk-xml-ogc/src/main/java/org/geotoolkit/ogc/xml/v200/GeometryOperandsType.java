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
 * <p>Java class for GeometryOperandsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GeometryOperandsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GeometryOperand" maxOccurs="unbounded">
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
@XmlType(name = "GeometryOperandsType", propOrder = {
    "geometryOperand"
})
public class GeometryOperandsType {

    @XmlElement(name = "GeometryOperand", required = true)
    private List<GeometryOperandsType.GeometryOperand> geometryOperand;

    /**
     * Empty constructor used by JAXB
     */
    public GeometryOperandsType() {
    }

    /**
     * build a new geometry Operands object with the specified array of GeometryOperand (from geoAPI)
     */
    public GeometryOperandsType(org.opengis.filter.capability.GeometryOperand[] geometryOperands) {
        if (geometryOperands == null) {
            geometryOperands = new org.opengis.filter.capability.GeometryOperand[0];
        }
        geometryOperand = new ArrayList<>();
        for (org.opengis.filter.capability.GeometryOperand g: geometryOperands) {
            geometryOperand.add(new GeometryOperand(new QName("http://www.opengis.net/gml/3.2", g.name())));
        }
    }

    /**
     * build a new geometry Operands object with the specified array of GeometryOperand (from geoAPI)
     */
    public GeometryOperandsType(List<QName> geometryOperands) {
        if (geometryOperands == null) {
            geometryOperands = new ArrayList<>();
        }
        this.geometryOperand = new ArrayList<>();
        for (QName qn : geometryOperands) {
            this.geometryOperand.add(new GeometryOperand(qn));
        }
    }

    /**
     * Gets the value of the geometryOperand property.
     *
     */
    public List<GeometryOperandsType.GeometryOperand> getGeometryOperand() {
        if (geometryOperand == null) {
            geometryOperand = new ArrayList<>();
        }
        return this.geometryOperand;
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
    public static class GeometryOperand {

        @XmlAttribute(required = true)
        private QName name;

        public GeometryOperand() {
        }

        public GeometryOperand(final QName name) {
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
