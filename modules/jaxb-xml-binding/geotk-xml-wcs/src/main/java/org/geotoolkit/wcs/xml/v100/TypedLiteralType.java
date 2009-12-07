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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * A single value for a variable, encoded as a string. This type can be used for one value, for a spacing between allowed values, or for the default value of a parameter. The "type" attribute indicates the datatype of this value (default is a string). The value for a typed literal is found by applying the datatype mapping associated with the datatype URI to the lexical form string. 
 * 
 * <p>Java class for TypedLiteralType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TypedLiteralType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute ref="{http://www.opengis.net/wcs}type"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TypedLiteralType", propOrder = {
    "value"
})
public class TypedLiteralType {

    @XmlValue
    private String value;
    @XmlAttribute(namespace = "http://www.opengis.net/wcs")
    @XmlSchemaType(name = "anyURI")
    private String type;

    public TypedLiteralType() {}

    public TypedLiteralType(final String value, final String type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Should be included unless the datatype is xs:string, 
     * or this "type" attribute is included in an enclosing element. 
    */
    public String getType() {
        return type;
    }
}
