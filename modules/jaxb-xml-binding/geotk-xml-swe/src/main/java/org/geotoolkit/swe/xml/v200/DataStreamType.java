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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for DataStreamType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataStreamType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSWEIdentifiableType">
 *       &lt;sequence>
 *         &lt;element name="elementCount" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swe/2.0}Count"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="elementType">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractDataComponentPropertyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="encoding">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="values" type="{http://www.opengis.net/swe/2.0}EncodedValuesPropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataStreamType", propOrder = {
    "elementCount",
    "elementType",
    "encoding",
    "values"
})
public class DataStreamType
    extends AbstractSWEIdentifiableType
{

    private DataStreamType.ElementCount elementCount;
    @XmlElement(required = true)
    private DataStreamType.ElementType elementType;
    @XmlElement(required = true)
    private DataStreamType.Encoding encoding;
    @XmlElement(required = true)
    private EncodedValuesPropertyType values;

    /**
     * Gets the value of the elementCount property.
     * 
     * @return
     *     possible object is
     *     {@link DataStreamType.ElementCount }
     *     
     */
    public DataStreamType.ElementCount getElementCount() {
        return elementCount;
    }

    /**
     * Sets the value of the elementCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataStreamType.ElementCount }
     *     
     */
    public void setElementCount(DataStreamType.ElementCount value) {
        this.elementCount = value;
    }

    /**
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link DataStreamType.ElementType }
     *     
     */
    public DataStreamType.ElementType getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataStreamType.ElementType }
     *     
     */
    public void setElementType(DataStreamType.ElementType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link DataStreamType.Encoding }
     *     
     */
    public DataStreamType.Encoding getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataStreamType.Encoding }
     *     
     */
    public void setEncoding(DataStreamType.Encoding value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the values property.
     * 
     * @return
     *     possible object is
     *     {@link EncodedValuesPropertyType }
     *     
     */
    public EncodedValuesPropertyType getValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     * 
     * @param value
     *     allowed object is
     *     {@link EncodedValuesPropertyType }
     *     
     */
    public void setValues(EncodedValuesPropertyType value) {
        this.values = value;
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
     *         &lt;element ref="{http://www.opengis.net/swe/2.0}Count"/>
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
        "count"
    })
    public static class ElementCount {

        @XmlElement(name = "Count", required = true)
        private CountType count;

        /**
         * Gets the value of the count property.
         * 
         * @return
         *     possible object is
         *     {@link CountType }
         *     
         */
        public CountType getCount() {
            return count;
        }

        /**
         * Sets the value of the count property.
         * 
         * @param value
         *     allowed object is
         *     {@link CountType }
         *     
         */
        public void setCount(CountType value) {
            this.count = value;
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
     *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractDataComponentPropertyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ElementType
        extends AbstractDataComponentPropertyType
    {

        @XmlAttribute(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        private String name;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
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
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
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
        "abstractEncoding"
    })
    public static class Encoding {

        @XmlElementRef(name = "AbstractEncoding", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
        private JAXBElement<? extends AbstractEncodingType> abstractEncoding;

        /**
         * Gets the value of the abstractEncoding property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractEncodingType> getAbstractEncoding() {
            return abstractEncoding;
        }

        /**
         * Sets the value of the abstractEncoding property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
         *     
         */
        public void setAbstractEncoding(JAXBElement<? extends AbstractEncodingType> value) {
            this.abstractEncoding = ((JAXBElement<? extends AbstractEncodingType> ) value);
        }

    }

}
