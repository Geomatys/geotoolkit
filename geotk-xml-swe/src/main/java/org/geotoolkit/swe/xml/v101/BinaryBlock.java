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
package org.geotoolkit.swe.xml.v101;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractBinaryBlock;
import org.geotoolkit.swe.xml.BinaryBlockMember;
import org.geotoolkit.swe.xml.BinaryBlockMemberBlock;
import org.geotoolkit.swe.xml.BinaryBlockMemberComponent;
import org.geotoolkit.swe.xml.ByteEncoding;
import org.geotoolkit.swe.xml.ByteOrder;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractEncodingType">
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
 *       &lt;attribute name="byteEncoding" use="required" type="{http://www.opengis.net/swe/1.0.1}byteEncoding" />
 *       &lt;attribute name="byteOrder" use="required" type="{http://www.opengis.net/swe/1.0.1}byteOrder" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "member"
})
@XmlRootElement(name = "BinaryBlock")
public class BinaryBlock extends AbstractEncodingType  implements AbstractBinaryBlock {

    @XmlElement(required = true)
    private List<BinaryBlock.Member> member;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer byteLength;
    @XmlAttribute(required = true)
    private ByteEncoding byteEncoding;
    @XmlAttribute(required = true)
    private ByteOrder byteOrder;

    public BinaryBlock() {

    }

    public BinaryBlock(final AbstractBinaryBlock bb) {
        super(bb);
        if (bb != null) {
            this.byteLength = bb.getByteLength();
            if (bb.getMember() != null) {
                this.member = new ArrayList<Member>();
                for (BinaryBlockMember m : bb.getMember()) {
                    this.member.add(new Member(m));
                }
            }
            this.byteEncoding = bb.getByteEncoding();
            this.byteOrder    = bb.getByteOrder();
        }
    }

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
    public void setByteLength(final Integer value) {
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
    public void setByteEncoding(final ByteEncoding value) {
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
    public void setByteOrder(final ByteOrder value) {
        this.byteOrder = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BinaryBlock && super.equals(object)) {
            final BinaryBlock that = (BinaryBlock) object;

            return Objects.equals(this.byteEncoding, that.byteEncoding) &&
                   Objects.equals(this.byteLength,   that.byteLength)   &&
                   Objects.equals(this.member,       that.member)   &&
                   Objects.equals(this.byteOrder,    that.byteOrder);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 59 * hash + (this.byteLength != null ? this.byteLength.hashCode() : 0);
        hash = 59 * hash + (this.byteEncoding != null ? this.byteEncoding.hashCode() : 0);
        hash = 59 * hash + (this.byteOrder != null ? this.byteOrder.hashCode() : 0);
        return hash;
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
    public static class Member implements BinaryBlockMember {

        @XmlElement(name = "Component")
        private BinaryBlock.Member.Component component;
        @XmlElement(name = "Block")
        private BinaryBlock.Member.Block block;

        public Member() {

        }

        public Member(final BinaryBlockMember m) {
            if (m != null) {
                if (m.getBlock() != null) {
                    this.block = new Block(m.getBlock());
                }
                if (m.getComponent() != null) {
                    this.component = new Component(m.getComponent());
                }
            }

        }

        /**
         * Gets the value of the component property.
         */
        public BinaryBlock.Member.Component getComponent() {
            return component;
        }

        /**
         * Sets the value of the component property.
         */
        public void setComponent(final BinaryBlock.Member.Component value) {
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
        public void setBlock(final BinaryBlock.Member.Block value) {
            this.block = value;
        }

        /**
         * Verify if this entry is identical to specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Member) {
                final Member that = (Member) object;

                return Objects.equals(this.block, that.block) &&
                       Objects.equals(this.component, that.component);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + (this.component != null ? this.component.hashCode() : 0);
            hash = 71 * hash + (this.block != null ? this.block.hashCode() : 0);
            return hash;
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
        public static class Block implements BinaryBlockMemberBlock {

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

            public Block() {

            }

            public Block(final BinaryBlockMemberBlock bl) {
                if (bl != null) {
                    this.byteLength         = bl.getByteLength();
                    this.compression        = bl.getCompression();
                    this.encryption         = bl.getEncryption();
                    this.paddingBytesAfter  = bl.getPaddingBytesAfter();
                    this.paddingBytesBefore = bl.getPaddingBytesBefore();
                    this.ref                = bl.getRef();
                }

            }
            /**
             * Gets the value of the ref property.
             *
             */
            public String getRef() {
                return ref;
            }

            /**
             * Sets the value of the ref property.
             *
             */
            public void setRef(final String value) {
                this.ref = value;
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
            public void setByteLength(final Integer value) {
                this.byteLength = value;
            }

            /**
             * Gets the value of the paddingBytesBefore property.
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
             */
            public void setPaddingBytesBefore(final Integer value) {
                this.paddingBytesBefore = value;
            }

            /**
             * Gets the value of the paddingBytesAfter property.
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
             */
            public void setPaddingBytesAfter(final Integer value) {
                this.paddingBytesAfter = value;
            }

            /**
             * Gets the value of the encryption property.
             */
            public String getEncryption() {
                return encryption;
            }

            /**
             * Sets the value of the encryption property.
             */
            public void setEncryption(final String value) {
                this.encryption = value;
            }

            /**
             * Gets the value of the compression property.
             */
            public String getCompression() {
                return compression;
            }

            /**
             * Sets the value of the compression property.
             */
            public void setCompression(final String value) {
                this.compression = value;
            }

            /**
             * Verify if this entry is identical to specified object.
             */
            @Override
            public boolean equals(final Object object) {
                if (object == this) {
                    return true;
                }
                if (object instanceof Block) {
                    final Block that = (Block) object;

                    return Objects.equals(this.byteLength,  that.byteLength)                &&
                           Objects.equals(this.encryption,  that.encryption)                &&
                           Objects.equals(this.paddingBytesAfter,  that.paddingBytesAfter)  &&
                           Objects.equals(this.paddingBytesBefore, that.paddingBytesBefore) &&
                           Objects.equals(this.ref,         that.ref)                       &&
                           Objects.equals(this.compression, that.compression);
                }
                return false;
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 29 * hash + (this.ref != null ? this.ref.hashCode() : 0);
                hash = 29 * hash + (this.byteLength != null ? this.byteLength.hashCode() : 0);
                hash = 29 * hash + (this.paddingBytesBefore != null ? this.paddingBytesBefore.hashCode() : 0);
                hash = 29 * hash + (this.paddingBytesAfter != null ? this.paddingBytesAfter.hashCode() : 0);
                hash = 29 * hash + (this.encryption != null ? this.encryption.hashCode() : 0);
                hash = 29 * hash + (this.compression != null ? this.compression.hashCode() : 0);
                return hash;
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
        public static class Component implements BinaryBlockMemberComponent {

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

            public Component() {

            }

            public Component(final BinaryBlockMemberComponent bc) {
                if (bc != null) {
                    this.bitLength         = bc.getBitLength();
                    this.dataType          = bc.getDataType();
                    this.encryption        = bc.getEncryption();
                    this.paddingBitsAfter  = bc.getPaddingBitsAfter();
                    this.paddingBitsBefore = bc.getPaddingBitsBefore();
                    this.ref               = bc.getRef();
                    this.significantBits   = bc.getSignificantBits();
                }
            }

            /**
             * Gets the value of the ref property.
             *
             */
            public String getRef() {
                return ref;
            }

            /**
             * Sets the value of the ref property.
             */
            public void setRef(final String value) {
                this.ref = value;
            }

            /**
             * Gets the value of the dataType property.
             */
            public String getDataType() {
                return dataType;
            }

            /**
             * Sets the value of the dataType property.
             */
            public void setDataType(final String value) {
                this.dataType = value;
            }

            /**
             * Gets the value of the significantBits property.
             */
            public Integer getSignificantBits() {
                return significantBits;
            }

            /**
             * Sets the value of the significantBits property.
             */
            public void setSignificantBits(final Integer value) {
                this.significantBits = value;
            }

            /**
             * Gets the value of the bitLength property.
             *
             */
            public Integer getBitLength() {
                return bitLength;
            }

            /**
             * Sets the value of the bitLength property.
             *
             */
            public void setBitLength(final Integer value) {
                this.bitLength = value;
            }

            /**
             * Gets the value of the paddingBitsBefore property.
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
             */
            public void setPaddingBitsBefore(final Integer value) {
                this.paddingBitsBefore = value;
            }

            /**
             * Gets the value of the paddingBitsAfter property.
             */
            public Integer getPaddingBitsAfter() {
                if (paddingBitsAfter == null) {
                    return new Integer("0");
                } else {
                    return paddingBitsAfter;
                }
            }

            /**
             * Sets the value of the paddingBitsAfter property.
             */
            public void setPaddingBitsAfter(final Integer value) {
                this.paddingBitsAfter = value;
            }

            /**
             * Gets the value of the encryption property.
             *
             */
            public String getEncryption() {
                return encryption;
            }

            /**
             * Sets the value of the encryption property.
             */
            public void setEncryption(final String value) {
                this.encryption = value;
            }

            /**
             * Verify if this entry is identical to specified object.
             */
            @Override
            public boolean equals(final Object object) {
                if (object == this) {
                    return true;
                }
                if (object instanceof Component) {
                    final Component that = (Component) object;

                    return Objects.equals(this.bitLength,   that.bitLength)                 &&
                           Objects.equals(this.encryption,  that.encryption)                &&
                           Objects.equals(this.dataType,    that.dataType)                  &&
                           Objects.equals(this.paddingBitsAfter,  that.paddingBitsAfter)    &&
                           Objects.equals(this.paddingBitsBefore, that.paddingBitsBefore)   &&
                           Objects.equals(this.ref,         that.ref)                       &&
                           Objects.equals(this.significantBits, that.significantBits);
                }
                return false;
            }

            @Override
            public int hashCode() {
                int hash = 3;
                hash = 61 * hash + (this.ref != null ? this.ref.hashCode() : 0);
                hash = 61 * hash + (this.dataType != null ? this.dataType.hashCode() : 0);
                hash = 61 * hash + (this.significantBits != null ? this.significantBits.hashCode() : 0);
                hash = 61 * hash + (this.bitLength != null ? this.bitLength.hashCode() : 0);
                hash = 61 * hash + (this.paddingBitsBefore != null ? this.paddingBitsBefore.hashCode() : 0);
                hash = 61 * hash + (this.paddingBitsAfter != null ? this.paddingBitsAfter.hashCode() : 0);
                hash = 61 * hash + (this.encryption != null ? this.encryption.hashCode() : 0);
                return hash;
            }

        }

    }

}
