/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.sml.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.v200.AllowedTimesType;
import org.geotoolkit.swe.xml.v200.AllowedTokensType;
import org.geotoolkit.swe.xml.v200.AllowedValuesType;


/**
 * <p>Java class for ConstraintSettingPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ConstraintSettingPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/sensorml/2.0}Constraint"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ref" use="required" type="{http://www.opengis.net/sensorml/2.0}DataComponentPathPropertyType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConstraintSettingPropertyType", propOrder = {
    "allowedTimes",
    "allowedTokens",
    "allowedValues"
})
public class ConstraintSettingPropertyType {

    @XmlElement(name = "AllowedTimes", namespace = "http://www.opengis.net/swe/2.0")
    protected AllowedTimesType allowedTimes;
    @XmlElement(name = "AllowedTokens", namespace = "http://www.opengis.net/swe/2.0")
    protected AllowedTokensType allowedTokens;
    @XmlElement(name = "AllowedValues", namespace = "http://www.opengis.net/swe/2.0")
    protected AllowedValuesType allowedValues;
    @XmlAttribute(name = "ref", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String ref;

    /**
     * Gets the value of the allowedTimes property.
     *
     * @return
     *     possible object is
     *     {@link AllowedTimesType }
     *
     */
    public AllowedTimesType getAllowedTimes() {
        return allowedTimes;
    }

    /**
     * Sets the value of the allowedTimes property.
     *
     * @param value
     *     allowed object is
     *     {@link AllowedTimesType }
     *
     */
    public void setAllowedTimes(AllowedTimesType value) {
        this.allowedTimes = value;
    }

    /**
     * Gets the value of the allowedTokens property.
     *
     * @return
     *     possible object is
     *     {@link AllowedTokensType }
     *
     */
    public AllowedTokensType getAllowedTokens() {
        return allowedTokens;
    }

    /**
     * Sets the value of the allowedTokens property.
     *
     * @param value
     *     allowed object is
     *     {@link AllowedTokensType }
     *
     */
    public void setAllowedTokens(AllowedTokensType value) {
        this.allowedTokens = value;
    }

    /**
     * Gets the value of the allowedValues property.
     *
     * @return
     *     possible object is
     *     {@link AllowedValuesType }
     *
     */
    public AllowedValuesType getAllowedValues() {
        return allowedValues;
    }

    /**
     * Sets the value of the allowedValues property.
     *
     * @param value
     *     allowed object is
     *     {@link AllowedValuesType }
     *
     */
    public void setAllowedValues(AllowedValuesType value) {
        this.allowedValues = value;
    }

    /**
     * Gets the value of the ref property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRef(String value) {
        this.ref = value;
    }

}
