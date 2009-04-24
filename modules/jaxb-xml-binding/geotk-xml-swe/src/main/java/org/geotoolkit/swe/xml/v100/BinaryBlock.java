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


package org.geotoolkit.swe.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractEncodingType">
 *       &lt;sequence>
 *         &lt;element name="member" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="Component">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                           &lt;attribute name="dataType" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                           &lt;attribute name="significantBits" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *                           &lt;attribute name="bitLength" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *                           &lt;attribute name="paddingBits-before" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
 *                           &lt;attribute name="paddingBits-after" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
 *                           &lt;attribute name="encryption" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Block">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                           &lt;attribute name="byteLength" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *                           &lt;attribute name="paddingBytes-before" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
 *                           &lt;attribute name="paddingBytes-after" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
 *                           &lt;attribute name="encryption" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                           &lt;attribute name="compression" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="byteLength" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="byteEncoding" use="required" type="{http://www.opengis.net/swe/1.0}byteEncoding" />
 *       &lt;attribute name="byteOrder" use="required" type="{http://www.opengis.net/swe/1.0}byteOrder" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "member"
})
@XmlRootElement(name = "BinaryBlock")
public class BinaryBlock extends AbstractEncodingType {

    @XmlElement(required = true)
    private List<BinaryBlock.Member> member;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer byteLength;
    @XmlAttribute(required = true)
    private ByteEncoding byteEncoding;
    @XmlAttribute(required = true)
    private ByteOrder byteOrder;

    /**
     * Gets the value of the member property.
     */
    public List<BinaryBlock.Member> getMember() {
        if (member == null) {
            member = new ArrayList<BinaryBlock.Member>();
        }
        return this.member;
    }

    /**
     * Gets the value of the byteLength property.
     */
    public Integer getByteLength() {
        return byteLength;
    }

    /**
     * Sets the value of the byteLength property.
     */
    public void setByteLength(Integer value) {
        this.byteLength = value;
    }

    /**
     * Gets the value of the byteEncoding property.
     */
    public ByteEncoding getByteEncoding() {
        return byteEncoding;
    }

    /**
     * Sets the value of the byteEncoding property.
     */
    public void setByteEncoding(ByteEncoding value) {
        this.byteEncoding = value;
    }

    /**
     * Gets the value of the byteOrder property.
     */
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    /**
     * Sets the value of the byteOrder property.
     */
    public void setByteOrder(ByteOrder value) {
        this.byteOrder = value;
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
     *       &lt;choice>
     *         &lt;element name="Component">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
     *                 &lt;attribute name="dataType" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *                 &lt;attribute name="significantBits" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
     *                 &lt;attribute name="bitLength" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
     *                 &lt;attribute name="paddingBits-before" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
     *                 &lt;attribute name="paddingBits-after" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
     *                 &lt;attribute name="encryption" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Block">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
     *                 &lt;attribute name="byteLength" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
     *                 &lt;attribute name="paddingBytes-before" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
     *                 &lt;attribute name="paddingBytes-after" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
     *                 &lt;attribute name="encryption" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *                 &lt;attribute name="compression" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/choice>
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
        private BinaryBlock.Member.Component component;
        @XmlElement(name = "Block")
        private BinaryBlock.Member.Block block;

        /**
         * Gets the value of the component property.
         */
        public BinaryBlock.Member.Component getComponent() {
            return component;
        }

        /**
         * Sets the value of the component property.
         */
        public void setComponent(BinaryBlock.Member.Component value) {
            this.component = value;
        }

        /**
         * Gets the value of the block property.
         */
        public BinaryBlock.Member.Block getBlock() {
            return block;
        }

        /**
         * Sets the value of the block property.
         */
        public void setBlock(BinaryBlock.Member.Block value) {
            this.block = value;
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
         *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
         *       &lt;attribute name="byteLength" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
         *       &lt;attribute name="paddingBytes-before" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
         *       &lt;attribute name="paddingBytes-after" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
         *       &lt;attribute name="encryption" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *       &lt;attribute name="compression" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Block {

            @XmlAttribute(required = true)
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            private String ref;
            @XmlAttribute
            @XmlSchemaType(name = "positiveInteger")
            private Integer byteLength;
            @XmlAttribute(name = "paddingBytes-before")
            @XmlSchemaType(name = "nonNegativeInteger")
            private Integer paddingBytesBefore;
            @XmlAttribute(name = "paddingBytes-after")
            @XmlSchemaType(name = "nonNegativeInteger")
            private Integer paddingBytesAfter;
            @XmlAttribute
            @XmlSchemaType(name = "anyURI")
            private String encryption;
            @XmlAttribute
            @XmlSchemaType(name = "anyURI")
            private String compression;

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

            /**
             * Gets the value of the byteLength property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getByteLength() {
                return byteLength;
            }

            /**
             * Sets the value of the byteLength property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setByteLength(Integer value) {
                this.byteLength = value;
            }

            /**
             * Gets the value of the paddingBytesBefore property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getPaddingBytesBefore() {
                if (paddingBytesBefore == null) {
                    return new Integer("0");
                } else {
                    return paddingBytesBefore;
                }
            }

            /**
             * Sets the value of the paddingBytesBefore property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setPaddingBytesBefore(Integer value) {
                this.paddingBytesBefore = value;
            }

            /**
             * Gets the value of the paddingBytesAfter property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getPaddingBytesAfter() {
                if (paddingBytesAfter == null) {
                    return new Integer("0");
                } else {
                    return paddingBytesAfter;
                }
            }

            /**
             * Sets the value of the paddingBytesAfter property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setPaddingBytesAfter(Integer value) {
                this.paddingBytesAfter = value;
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
         *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
         *       &lt;attribute name="dataType" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *       &lt;attribute name="significantBits" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
         *       &lt;attribute name="bitLength" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
         *       &lt;attribute name="paddingBits-before" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
         *       &lt;attribute name="paddingBits-after" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
         *       &lt;attribute name="encryption" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Component {

            @XmlAttribute(required = true)
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            private String ref;
            @XmlAttribute
            @XmlSchemaType(name = "anyURI")
            private String dataType;
            @XmlAttribute
            @XmlSchemaType(name = "positiveInteger")
            private Integer significantBits;
            @XmlAttribute
            @XmlSchemaType(name = "positiveInteger")
            private Integer bitLength;
            @XmlAttribute(name = "paddingBits-before")
            @XmlSchemaType(name = "nonNegativeInteger")
            private Integer paddingBitsBefore;
            @XmlAttribute(name = "paddingBits-after")
            @XmlSchemaType(name = "nonNegativeInteger")
            private Integer paddingBitsAfter;
            @XmlAttribute
            @XmlSchemaType(name = "anyURI")
            private String encryption;

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

            /**
             * Gets the value of the dataType property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDataType() {
                return dataType;
            }

            /**
             * Sets the value of the dataType property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDataType(String value) {
                this.dataType = value;
            }

            /**
             * Gets the value of the significantBits property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getSignificantBits() {
                return significantBits;
            }

            /**
             * Sets the value of the significantBits property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setSignificantBits(Integer value) {
                this.significantBits = value;
            }

            /**
             * Gets the value of the bitLength property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getBitLength() {
                return bitLength;
            }

            /**
             * Sets the value of the bitLength property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setBitLength(Integer value) {
                this.bitLength = value;
            }

            /**
             * Gets the value of the paddingBitsBefore property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getPaddingBitsBefore() {
                if (paddingBitsBefore == null) {
                    return new Integer("0");
                } else {
                    return paddingBitsBefore;
                }
            }

            /**
             * Sets the value of the paddingBitsBefore property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setPaddingBitsBefore(Integer value) {
                this.paddingBitsBefore = value;
            }

            /**
             * Gets the value of the paddingBitsAfter property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getPaddingBitsAfter() {
                if (paddingBitsAfter == null) {
                    return 0;
                } else {
                    return paddingBitsAfter;
                }
            }

            /**
             * Sets the value of the paddingBitsAfter property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setPaddingBitsAfter(Integer value) {
                this.paddingBitsAfter = value;
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

        }

    }

}
