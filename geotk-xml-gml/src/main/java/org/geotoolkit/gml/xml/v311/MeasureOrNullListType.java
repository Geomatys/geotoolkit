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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * List of numbers with a uniform scale.
 *       A member of the list may be a typed null.
 *       The value of uom (Units Of Measure) attribute is a reference to
 *       a Reference System for the amount, either a ratio or position scale.
 *
 * <p>Java class for MeasureOrNullListType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MeasureOrNullListType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/gml>doubleOrNullList">
 *       &lt;attribute name="uom" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeasureOrNullListType", propOrder = {
    "value"
})
@XmlSeeAlso({
    QuantityExtentType.class
})
public class MeasureOrNullListType {

    @XmlValue
    protected List<String> value;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String uom;

    /**
     * XML List based on the union type defined above.  An element declared with this type contains a space-separated list of double values with null values interspersed as needed Gets the value of the value property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the value property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValue().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return this.value;
    }

    /**
     * Gets the value of the uom property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUom() {
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
    public void setUom(final String value) {
        this.uom = value;
    }

}
