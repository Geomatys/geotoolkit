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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * The range of an interval. If the "min" or "max" element is not included, there is no value limit in that direction. Inclusion of the specified minimum and maximum values in the range shall be defined by the "closure". (The interval can be bounded or semi-bounded with different closures.) The data type and the semantic of the values are inherited by children and may be superceded by them. This range may be qualitative, i.e., nominal (age range) or qualitative (percentage) meaning that a value between min/max can be queried. 
 * 
 * <p>Java class for valueRangeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="valueRangeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="min" type="{http://www.opengis.net/wcs}TypedLiteralType" minOccurs="0"/>
 *         &lt;element name="max" type="{http://www.opengis.net/wcs}TypedLiteralType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/wcs}type"/>
 *       &lt;attribute ref="{http://www.opengis.net/wcs}semantic"/>
 *       &lt;attribute name="atomic" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute ref="{http://www.opengis.net/wcs}closure"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valueRangeType", propOrder = {
    "min",
    "max"
})
@XmlSeeAlso({
    IntervalType.class
})
public class ValueRangeType {

    private TypedLiteralType min;
    private TypedLiteralType max;
    @XmlAttribute(namespace = "http://www.opengis.net/wcs")
    @XmlSchemaType(name = "anyURI")
    private String type;
    @XmlAttribute(namespace = "http://www.opengis.net/wcs")
    @XmlSchemaType(name = "anyURI")
    private String semantic;
    @XmlAttribute
    private Boolean atomic;
    @XmlAttribute(namespace = "http://www.opengis.net/wcs")
    private List<String> closure;

    /**
     * Gets the value of the min property.
     */
    public TypedLiteralType getMin() {
        return min;
    }

    /**
     * Gets the value of the max property.
     */
    public TypedLiteralType getMax() {
        return max;
    }

    /**
     * Can be omitted when the datatype of values in this interval is xs:string, 
     * or the "type" attribute is included in an enclosing element. 
     */
    public String getType() {
        return type;
    }

    /**
     * Can be omitted when the semantics or meaning of values in this interval 
     * is clearly specified elsewhere,
     * or the "semantic" attribute is included in an enclosing element. 
     */
    public String getSemantic() {
        return semantic;
    }

    /**
     * Gets the value of the atomic property.
    */
    public boolean isAtomic() {
        if (atomic == null) {
            return false;
        } else {
            return atomic;
        }
    }

    /**
     * Shall be included unless the default value applies. 
     * Gets the value of the closure property.
     * (unmodifiable) 
     */
    public List<String> getClosure() {
        if (closure == null) {
            closure = new ArrayList<String>();
        }
        return Collections.unmodifiableList(closure);
    }

}
