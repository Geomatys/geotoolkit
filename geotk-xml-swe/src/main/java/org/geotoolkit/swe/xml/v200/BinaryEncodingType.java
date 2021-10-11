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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BinaryEncodingType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BinaryEncodingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractEncodingType">
 *       &lt;sequence>
 *         &lt;element name="member" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;group ref="{http://www.opengis.net/swe/2.0}ComponentOrBlock"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="byteOrder" use="required" type="{http://www.opengis.net/swe/2.0}ByteOrderType" />
 *       &lt;attribute name="byteEncoding" use="required" type="{http://www.opengis.net/swe/2.0}ByteEncodingType" />
 *       &lt;attribute name="byteLength" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryEncodingType", propOrder = {
    "member"
})
public class BinaryEncodingType
    extends AbstractEncodingType
{

    @XmlElement(required = true)
    private List<BinaryEncodingType.Member> member;
    @XmlAttribute(required = true)
    private ByteOrderType byteOrder;
    @XmlAttribute(required = true)
    private ByteEncodingType byteEncoding;
    @XmlAttribute
    private BigInteger byteLength;

    /**
     * Gets the value of the member property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link BinaryEncodingType.Member }
     *
     */
    public List<BinaryEncodingType.Member> getMember() {
        if (member == null) {
            member = new ArrayList<BinaryEncodingType.Member>();
        }
        return this.member;
    }

    /**
     * Gets the value of the byteOrder property.
     *
     * @return
     *     possible object is
     *     {@link ByteOrderType }
     *
     */
    public ByteOrderType getByteOrder() {
        return byteOrder;
    }

    /**
     * Sets the value of the byteOrder property.
     *
     * @param value
     *     allowed object is
     *     {@link ByteOrderType }
     *
     */
    public void setByteOrder(ByteOrderType value) {
        this.byteOrder = value;
    }

    /**
     * Gets the value of the byteEncoding property.
     *
     * @return
     *     possible object is
     *     {@link ByteEncodingType }
     *
     */
    public ByteEncodingType getByteEncoding() {
        return byteEncoding;
    }

    /**
     * Sets the value of the byteEncoding property.
     *
     * @param value
     *     allowed object is
     *     {@link ByteEncodingType }
     *
     */
    public void setByteEncoding(ByteEncodingType value) {
        this.byteEncoding = value;
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
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;group ref="{http://www.opengis.net/swe/2.0}ComponentOrBlock"/>
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
        "component",
        "block"
    })
    public static class Member {

        @XmlElement(name = "Component")
        private ComponentType component;
        @XmlElement(name = "Block")
        private BlockType block;

        /**
         * Gets the value of the component property.
         *
         * @return
         *     possible object is
         *     {@link ComponentType }
         *
         */
        public ComponentType getComponent() {
            return component;
        }

        /**
         * Sets the value of the component property.
         *
         * @param value
         *     allowed object is
         *     {@link ComponentType }
         *
         */
        public void setComponent(ComponentType value) {
            this.component = value;
        }

        /**
         * Gets the value of the block property.
         *
         * @return
         *     possible object is
         *     {@link BlockType }
         *
         */
        public BlockType getBlock() {
            return block;
        }

        /**
         * Sets the value of the block property.
         *
         * @param value
         *     allowed object is
         *     {@link BlockType }
         *
         */
        public void setBlock(BlockType value) {
            this.block = value;
        }

    }

}
