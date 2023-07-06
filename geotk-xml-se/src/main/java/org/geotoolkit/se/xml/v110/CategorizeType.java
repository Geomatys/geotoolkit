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
package org.geotoolkit.se.xml.v110;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CategorizeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CategorizeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}FunctionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}LookupValue"/>
 *         &lt;element ref="{http://www.opengis.net/se}Value"/>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/se}Threshold"/>
 *           &lt;element ref="{http://www.opengis.net/se}TValue"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="threshholdsBelongTo" type="{http://www.opengis.net/se}ThreshholdsBelongToType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CategorizeType", propOrder = {
    "lookupValue",
    "value",
    "thresholdAndTValue"
})
public class CategorizeType
    extends FunctionType
{

    @XmlElement(name = "LookupValue", required = true)
    protected ParameterValueType lookupValue;
    @XmlElement(name = "Value", required = true)
    protected ParameterValueType value;
    @XmlElementRefs({
        @XmlElementRef(name = "Threshold", namespace = "http://www.opengis.net/se", type = JAXBElement.class),
        @XmlElementRef(name = "TValue", namespace = "http://www.opengis.net/se", type = JAXBElement.class)
    })
    protected List<JAXBElement<ParameterValueType>> thresholdAndTValue;
    @XmlAttribute
    protected ThreshholdsBelongToType threshholdsBelongTo;

    /**
     * Gets the value of the lookupValue property.
     *
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     */
    public ParameterValueType getLookupValue() {
        return lookupValue;
    }

    /**
     * Sets the value of the lookupValue property.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     */
    public void setLookupValue(final ParameterValueType value) {
        this.lookupValue = value;
    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     */
    public ParameterValueType getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     */
    public void setValue(final ParameterValueType value) {
        this.value = value;
    }

    /**
     * Gets the value of the thresholdAndTValue property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the thresholdAndTValue property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getThresholdAndTValue().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}
     * {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}
     */
    public List<JAXBElement<ParameterValueType>> getThresholdAndTValue() {
        if (thresholdAndTValue == null) {
            thresholdAndTValue = new ArrayList<JAXBElement<ParameterValueType>>();
        }
        return this.thresholdAndTValue;
    }

    /**
     * Gets the value of the threshholdsBelongTo property.
     *
     * @return
     *     possible object is
     *     {@link ThreshholdsBelongToType }
     */
    public ThreshholdsBelongToType getThreshholdsBelongTo() {
        return threshholdsBelongTo;
    }

    /**
     * Sets the value of the threshholdsBelongTo property.
     *
     * @param value
     *     allowed object is
     *     {@link ThreshholdsBelongToType }
     */
    public void setThreshholdsBelongTo(final ThreshholdsBelongToType value) {
        this.threshholdsBelongTo = value;
    }
}
