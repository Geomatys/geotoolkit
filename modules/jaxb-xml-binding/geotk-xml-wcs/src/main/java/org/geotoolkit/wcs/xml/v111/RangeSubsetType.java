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
package org.geotoolkit.wcs.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;


/**
 * Selection of desired subset of the coverage's range fields, (optionally) the interpolation method applied to each field, and (optionally) field subsets. 
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
 *         &lt;element name="FieldSubset" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
 *                   &lt;element name="InterpolationType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element ref="{http://www.opengis.net/wcs/1.1.1}AxisSubset" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RangeSubsetType", propOrder = {
    "fieldSubset"
})
public class RangeSubsetType {

    @XmlElement(name = "FieldSubset", required = true)
    private List<RangeSubsetType.FieldSubset> fieldSubset;

    /**
     * An empty constructor used by JAXB
     */
    RangeSubsetType() {
        
    }
    
    public RangeSubsetType(List<FieldSubset> fieldSubset) {
        this.fieldSubset = fieldSubset;
    }
    
    /**
     * Gets the value of the fieldSubset property.
     * (unmodifable).
     */
    public List<RangeSubsetType.FieldSubset> getFieldSubset() {
        if (fieldSubset == null) {
            fieldSubset = new ArrayList<RangeSubsetType.FieldSubset>();
        }
        return Collections.unmodifiableList(fieldSubset);
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
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
     *         &lt;element name="InterpolationType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element ref="{http://www.opengis.net/wcs/1.1.1}AxisSubset" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "identifier",
        "interpolationType",
        "axisSubset"
    })
    public static class FieldSubset {

        @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
        private CodeType identifier;
        @XmlElement(name = "InterpolationType")
        private String interpolationType;
        @XmlElement(name = "AxisSubset")
        private List<AxisSubset> axisSubset;

        /**
         * an empty constructor used by JAXB
         */
        FieldSubset() {
            
        }
        
        public FieldSubset(String identifier, String interpolationType) {
            this.identifier        = new CodeType(identifier);
            this.interpolationType = interpolationType;
        }
        /**
         * Identifier of this requested Field. This identifier must be unique for this Coverage. 
         */
        public String getIdentifier() {
            if (identifier != null) {
                return identifier.getValue();
            }
            return null;
        }

        /**
         * Gets the value of the interpolationType property.
         */
        public String getInterpolationType() {
            return interpolationType;
        }

        /**
         * Unordered list of zero or more axis subsets for this field. 
         * TBD. Gets the value of the axisSubset property.
         * (unmodifiable).
         */
        public List<AxisSubset> getAxisSubset() {
            if (axisSubset == null) {
                axisSubset = new ArrayList<AxisSubset>();
            }
            return Collections.unmodifiableList(axisSubset);
        }

    }

}
