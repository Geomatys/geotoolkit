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
package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConditionalValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConditionalValueType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractConditionalType">
 *       &lt;sequence>
 *         &lt;element name="data">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;group ref="{http://www.opengis.net/swe/1.0}AnyData" minOccurs="0"/>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionalValueType", propOrder = {
    "data"
})
public class ConditionalValueType extends AbstractConditionalType {

    @XmlElement(required = true)
    private ConditionalValueType.Data data;

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link ConditionalValueType.Data }
     *     
     */
    public ConditionalValueType.Data getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionalValueType.Data }
     *     
     */
    public void setData(ConditionalValueType.Data value) {
        this.data = value;
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
     *       &lt;group ref="{http://www.opengis.net/swe/1.0}AnyData" minOccurs="0"/>
     *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "count",
        "quantity",
        "time",
        "_boolean",
        "category",
        "text",
        "quantityRange",
        "countRange",
        "timeRange",
        "abstractDataRecord",
        "abstractDataArray"
    })
    public static class Data {

        @XmlElement(name = "Count")
        private Count count;
        @XmlElement(name = "Quantity")
        private QuantityType quantity;
        @XmlElement(name = "Time")
        private TimeType time;
        @XmlElement(name = "Boolean")
        private Boolean _boolean;
        @XmlElement(name = "Category")
        private Category category;
        @XmlElement(name = "Text")
        private Text text;
        @XmlElement(name = "QuantityRange")
        private QuantityRange quantityRange;
        @XmlElement(name = "CountRange")
        private CountRange countRange;
        @XmlElement(name = "TimeRange")
        private TimeRange timeRange;
        @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
        private JAXBElement<? extends AbstractDataRecordType> abstractDataRecord;
        @XmlElementRef(name = "AbstractDataArray", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
        private JAXBElement<? extends AbstractDataArrayType> abstractDataArray;
        @XmlAttribute(namespace = "http://www.opengis.net/gml")
        @XmlSchemaType(name = "anyURI")
        private String remoteSchema;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String type;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String href;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String role;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String arcrole;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String title;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String show;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String actuate;

        /**
         * Gets the value of the count property.
         * 
         * @return
         *     possible object is
         *     {@link Count }
         *     
         */
        public Count getCount() {
            return count;
        }

        /**
         * Sets the value of the count property.
         * 
         * @param value
         *     allowed object is
         *     {@link Count }
         *     
         */
        public void setCount(Count value) {
            this.count = value;
        }

        /**
         * Gets the value of the quantity property.
         * 
         * @return
         *     possible object is
         *     {@link Quantity }
         *     
         */
        public QuantityType getQuantity() {
            return quantity;
        }

        /**
         * Sets the value of the quantity property.
         * 
         * @param value
         *     allowed object is
         *     {@link Quantity }
         *     
         */
        public void setQuantity(QuantityType value) {
            this.quantity = value;
        }

        /**
         * Gets the value of the time property.
         * 
         * @return
         *     possible object is
         *     {@link Time }
         *     
         */
        public TimeType getTime() {
            return time;
        }

        /**
         * Sets the value of the time property.
         * 
         * @param value
         *     allowed object is
         *     {@link Time }
         *     
         */
        public void setTime(TimeType value) {
            this.time = value;
        }

        /**
         * Gets the value of the boolean property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean getBoolean() {
            return _boolean;
        }

        /**
         * Sets the value of the boolean property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setBoolean(Boolean value) {
            this._boolean = value;
        }

        /**
         * Gets the value of the category property.
         * 
         * @return
         *     possible object is
         *     {@link Category }
         *     
         */
        public Category getCategory() {
            return category;
        }

        /**
         * Sets the value of the category property.
         * 
         * @param value
         *     allowed object is
         *     {@link Category }
         *     
         */
        public void setCategory(Category value) {
            this.category = value;
        }

        /**
         * Gets the value of the text property.
         * 
         * @return
         *     possible object is
         *     {@link Text }
         *     
         */
        public Text getText() {
            return text;
        }

        /**
         * Sets the value of the text property.
         * 
         * @param value
         *     allowed object is
         *     {@link Text }
         *     
         */
        public void setText(Text value) {
            this.text = value;
        }

        /**
         * Gets the value of the quantityRange property.
         * 
         * @return
         *     possible object is
         *     {@link QuantityRange }
         *     
         */
        public QuantityRange getQuantityRange() {
            return quantityRange;
        }

        /**
         * Sets the value of the quantityRange property.
         * 
         * @param value
         *     allowed object is
         *     {@link QuantityRange }
         *     
         */
        public void setQuantityRange(QuantityRange value) {
            this.quantityRange = value;
        }

        /**
         * Gets the value of the countRange property.
         * 
         * @return
         *     possible object is
         *     {@link CountRange }
         *     
         */
        public CountRange getCountRange() {
            return countRange;
        }

        /**
         * Sets the value of the countRange property.
         * 
         * @param value
         *     allowed object is
         *     {@link CountRange }
         *     
         */
        public void setCountRange(CountRange value) {
            this.countRange = value;
        }

        /**
         * Gets the value of the timeRange property.
         * 
         * @return
         *     possible object is
         *     {@link TimeRange }
         *     
         */
        public TimeRange getTimeRange() {
            return timeRange;
        }

        /**
         * Sets the value of the timeRange property.
         * 
         * @param value
         *     allowed object is
         *     {@link TimeRange }
         *     
         */
        public void setTimeRange(TimeRange value) {
            this.timeRange = value;
        }

        /**
         * Gets the value of the abstractDataRecord property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ConditionalDataType }{@code >}
         *     {@link JAXBElement }{@code <}{@link NormalizedCurveType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractDataRecordType> getAbstractDataRecord() {
            return abstractDataRecord;
        }

        /**
         * Sets the value of the abstractDataRecord property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ConditionalDataType }{@code >}
         *     {@link JAXBElement }{@code <}{@link NormalizedCurveType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
         *     
         */
        public void setAbstractDataRecord(JAXBElement<? extends AbstractDataRecordType> value) {
            this.abstractDataRecord = ((JAXBElement<? extends AbstractDataRecordType> ) value);
        }

        /**
         * Gets the value of the abstractDataArray property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractDataArrayType> getAbstractDataArray() {
            return abstractDataArray;
        }

        /**
         * Sets the value of the abstractDataArray property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}
         *     
         */
        public void setAbstractDataArray(JAXBElement<? extends AbstractDataArrayType> value) {
            this.abstractDataArray = ((JAXBElement<? extends AbstractDataArrayType> ) value);
        }

        /**
         * Gets the value of the remoteSchema property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRemoteSchema() {
            return remoteSchema;
        }

        /**
         * Sets the value of the remoteSchema property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRemoteSchema(String value) {
            this.remoteSchema = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            if (type == null) {
                return "simple";
            } else {
                return type;
            }
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }

        /**
         * Gets the value of the href property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHref() {
            return href;
        }

        /**
         * Sets the value of the href property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHref(String value) {
            this.href = value;
        }

        /**
         * Gets the value of the role property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRole() {
            return role;
        }

        /**
         * Sets the value of the role property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRole(String value) {
            this.role = value;
        }

        /**
         * Gets the value of the arcrole property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Sets the value of the arcrole property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setArcrole(String value) {
            this.arcrole = value;
        }

        /**
         * Gets the value of the title property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTitle(String value) {
            this.title = value;
        }

        /**
         * Gets the value of the show property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getShow() {
            return show;
        }

        /**
         * Sets the value of the show property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setShow(String value) {
            this.show = value;
        }

        /**
         * Gets the value of the actuate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Sets the value of the actuate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setActuate(String value) {
            this.actuate = value;
        }

    }

}
