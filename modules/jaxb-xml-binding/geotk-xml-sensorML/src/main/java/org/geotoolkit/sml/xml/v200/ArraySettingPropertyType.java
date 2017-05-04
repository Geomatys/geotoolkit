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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.v200.AbstractEncodingType;
import org.geotoolkit.swe.xml.v200.BinaryEncodingType;
import org.geotoolkit.swe.xml.v200.EncodedValuesPropertyType;
import org.geotoolkit.swe.xml.v200.TextEncodingType;
import org.geotoolkit.swe.xml.v200.XMLEncodingType;


/**
 * <p>Java class for ArraySettingPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ArraySettingPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ArrayValues">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="encoding">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="value" type="{http://www.opengis.net/swe/2.0}EncodedValuesPropertyType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlType(name = "ArraySettingPropertyType", propOrder = {
    "arrayValues"
})
public class ArraySettingPropertyType {

    @XmlElement(name = "ArrayValues", required = true)
    protected ArraySettingPropertyType.ArrayValues arrayValues;
    @XmlAttribute(name = "ref", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String ref;

    /**
     * Gets the value of the arrayValues property.
     *
     * @return
     *     possible object is
     *     {@link ArraySettingPropertyType.ArrayValues }
     *
     */
    public ArraySettingPropertyType.ArrayValues getArrayValues() {
        return arrayValues;
    }

    /**
     * Sets the value of the arrayValues property.
     *
     * @param value
     *     allowed object is
     *     {@link ArraySettingPropertyType.ArrayValues }
     *
     */
    public void setArrayValues(ArraySettingPropertyType.ArrayValues value) {
        this.arrayValues = value;
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
     *         &lt;element name="value" type="{http://www.opengis.net/swe/2.0}EncodedValuesPropertyType"/>
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
        "encoding",
        "value"
    })
    public static class ArrayValues {

        @XmlElement(required = true)
        protected ArraySettingPropertyType.ArrayValues.Encoding encoding;
        @XmlElement(required = true)
        protected EncodedValuesPropertyType value;

        /**
         * Gets the value of the encoding property.
         *
         * @return
         *     possible object is
         *     {@link ArraySettingPropertyType.ArrayValues.Encoding }
         *
         */
        public ArraySettingPropertyType.ArrayValues.Encoding getEncoding() {
            return encoding;
        }

        /**
         * Sets the value of the encoding property.
         *
         * @param value
         *     allowed object is
         *     {@link ArraySettingPropertyType.ArrayValues.Encoding }
         *
         */
        public void setEncoding(ArraySettingPropertyType.ArrayValues.Encoding value) {
            this.encoding = value;
        }

        /**
         * Gets the value of the value property.
         *
         * @return
         *     possible object is
         *     {@link EncodedValuesPropertyType }
         *
         */
        public EncodedValuesPropertyType getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         *
         * @param value
         *     allowed object is
         *     {@link EncodedValuesPropertyType }
         *
         */
        public void setValue(EncodedValuesPropertyType value) {
            this.value = value;
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
            protected JAXBElement<? extends AbstractEncodingType> abstractEncoding;

            /**
             * Gets the value of the abstractEncoding property.
             *
             * @return
             *     possible object is
             *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
             *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
             *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
             *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
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
             *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
             *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
             *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
             *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
             *
             */
            public void setAbstractEncoding(JAXBElement<? extends AbstractEncodingType> value) {
                this.abstractEncoding = value;
            }

        }

    }

}
