/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BlockType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BlockType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSWEType">
 *       &lt;attribute name="compression" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="encryption" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="paddingBytes-after" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="paddingBytes-before" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="byteLength" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BlockType")
public class BlockType extends AbstractSWEType {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String compression;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String encryption;
    @XmlAttribute(name = "paddingBytes-after")
    private BigInteger paddingBytesAfter;
    @XmlAttribute(name = "paddingBytes-before")
    private BigInteger paddingBytesBefore;
    @XmlAttribute
    private BigInteger byteLength;
    @XmlAttribute(required = true)
    private String ref;

    /**
     * Gets the value of the compression property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCompression() {
        return compression;
    }

    /**
     * Sets the value of the compression property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCompression(String value) {
        this.compression = value;
    }

    /**
     * Gets the value of the encryption property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEncryption() {
        return encryption;
    }

    /**
     * Sets the value of the encryption property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEncryption(String value) {
        this.encryption = value;
    }

    /**
     * Gets the value of the paddingBytesAfter property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getPaddingBytesAfter() {
        return paddingBytesAfter;
    }

    /**
     * Sets the value of the paddingBytesAfter property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setPaddingBytesAfter(BigInteger value) {
        this.paddingBytesAfter = value;
    }

    /**
     * Gets the value of the paddingBytesBefore property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getPaddingBytesBefore() {
        return paddingBytesBefore;
    }

    /**
     * Sets the value of the paddingBytesBefore property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setPaddingBytesBefore(BigInteger value) {
        this.paddingBytesBefore = value;
    }

    /**
     * Gets the value of the byteLength property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getByteLength() {
        return byteLength;
    }

    /**
     * Sets the value of the byteLength property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setByteLength(BigInteger value) {
        this.byteLength = value;
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
