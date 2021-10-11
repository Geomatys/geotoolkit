/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Description of an input to a process.
 *
 * In this use, the Description shall describe a process input.


 <p>Java class for GenericInput complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GenericInput">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}Description">
 *       &lt;sequence>
 *         &lt;element name="Input" type="{http://www.opengis.net/wps/2.0}GenericInput" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/2001/XMLSchema}occurs"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericInputType", propOrder = {
    "input"
})
public class GenericInput
    extends Description
{

    @XmlElement(name = "Input")
    protected List<GenericInput> input;
    @XmlAttribute(name = "minOccurs")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer minOccurs;
    @XmlAttribute(name = "maxOccurs")
    @XmlSchemaType(name = "allNNI")
    protected String maxOccurs;

    /**
     * Gets the value of the input property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the input property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInput().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GenericInput }
     *
     *
     */
    public List<GenericInput> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
    }

    /**
     * Gets the value of the minOccurs property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMinOccurs() {
        if (minOccurs == null) {
            return 1;
        } else {
            return minOccurs;
        }
    }

    /**
     * Sets the value of the minOccurs property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMinOccurs(Integer value) {
        this.minOccurs = value;
    }

    /**
     * Gets the value of the maxOccurs property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaxOccurs() {
        if (maxOccurs == null) {
            return "1";
        } else {
            return maxOccurs;
        }
    }

    /**
     * Sets the value of the maxOccurs property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaxOccurs(String value) {
        this.maxOccurs = value;
    }

}
