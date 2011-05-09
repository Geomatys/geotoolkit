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


package org.geotoolkit.wfs.xml.v200;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>QName">
 *       &lt;attGroup ref="{http://www.opengis.net/wfs/2.0}StandardResolveParameters"/>
 *       &lt;attribute name="resolvePath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
public class PropertyName {

    @XmlValue
    private QName value;
    @XmlAttribute
    private String resolvePath;
    @XmlAttribute
    private ResolveValueType resolve;
    @XmlAttribute
    private String resolveDepth;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger resolveTimeout;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setValue(QName value) {
        this.value = value;
    }

    /**
     * Gets the value of the resolvePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolvePath() {
        return resolvePath;
    }

    /**
     * Sets the value of the resolvePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolvePath(String value) {
        this.resolvePath = value;
    }

    /**
     * Gets the value of the resolve property.
     * 
     * @return
     *     possible object is
     *     {@link ResolveValueType }
     *     
     */
    public ResolveValueType getResolve() {
        if (resolve == null) {
            return ResolveValueType.NONE;
        } else {
            return resolve;
        }
    }

    /**
     * Sets the value of the resolve property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResolveValueType }
     *     
     */
    public void setResolve(ResolveValueType value) {
        this.resolve = value;
    }

    /**
     * Gets the value of the resolveDepth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolveDepth() {
        if (resolveDepth == null) {
            return "*";
        } else {
            return resolveDepth;
        }
    }

    /**
     * Sets the value of the resolveDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolveDepth(String value) {
        this.resolveDepth = value;
    }

    /**
     * Gets the value of the resolveTimeout property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getResolveTimeout() {
        if (resolveTimeout == null) {
            return new BigInteger("300");
        } else {
            return resolveTimeout;
        }
    }

    /**
     * Sets the value of the resolveTimeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setResolveTimeout(BigInteger value) {
        this.resolveTimeout = value;
    }

}
