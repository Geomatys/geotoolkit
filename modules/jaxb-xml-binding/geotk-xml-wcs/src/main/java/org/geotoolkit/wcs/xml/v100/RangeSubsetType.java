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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wcs.xml.RangeSubset;


/**
 * Definition of a subset of the named coverage range(s). Currently, only a value enumeration definition of a range subset. 
 * 
 * <p>Java class for RangeSubsetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RangeSubsetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="axisSubset" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opengis.net/wcs}valueEnumBaseType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RangeSubsetType", propOrder = {
    "axisSubset"
})
public class RangeSubsetType implements RangeSubset {

    @XmlElement(required = true)
    private List<RangeSubsetType.AxisSubset> axisSubset;

    public RangeSubsetType(){}

    public RangeSubsetType(final List<RangeSubsetType.AxisSubset> axisSubset) {
        this.axisSubset = axisSubset;
    }

    /**
     * Gets the value of the axisSubset property.
     * (unmodifiable)
     */
    public List<RangeSubsetType.AxisSubset> getAxisSubset() {
        if (axisSubset == null) {
            axisSubset = new ArrayList<RangeSubsetType.AxisSubset>();
        }
        return Collections.unmodifiableList(axisSubset);
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.opengis.net/wcs}valueEnumBaseType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class AxisSubset extends ValueEnumBaseType {

        @XmlAttribute(required = true)
        private String name;

        public AxisSubset() {}

        public AxisSubset(final String name, final List<Object> objects) {
            super(objects);
            this.name = name;
        }

        /**
         * Gets the value of the name property.
         */
        public String getName() {
            return name;
        }
    }
}
