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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for AbstractConditionalType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractConditionalType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractDataRecordType">
 *       &lt;sequence>
 *         &lt;element name="condition" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;group ref="{http://www.opengis.net/swe/1.0.1}AnyData" minOccurs="0"/>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractConditionalType", propOrder = {
    "condition"
})
@XmlSeeAlso({
    ConditionalValueType.class
})
public class AbstractConditionalType extends AbstractDataRecordEntry {

    @XmlElement(required = true)
    private List<AbstractConditionalType.Condition> condition;

    /**
     * Gets the value of the condition property.
     * 
     * 
     */
    public List<AbstractConditionalType.Condition> getCondition() {
        if (condition == null) {
            condition = new ArrayList<AbstractConditionalType.Condition>();
        }
        return this.condition;
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
     *       &lt;group ref="{http://www.opengis.net/swe/1.0.1}AnyData" minOccurs="0"/>
     *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
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
    public static class Condition {

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
        @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
        private JAXBElement<? extends AbstractDataRecordEntry> abstractDataRecord;
        @XmlElementRef(name = "AbstractDataArray", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
        private JAXBElement<? extends AbstractDataArrayEntry> abstractDataArray;
        @XmlAttribute(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        private String name;
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
         *     {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ConditionalDataType }{@code >}
         *     {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}
         *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
         *     {@link JAXBElement }{@code <}{@link NormalizedCurveType }{@code >}
         *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractDataRecordEntry> getAbstractDataRecord() {
            return abstractDataRecord;
        }

        /**
         * Sets the value of the abstractDataRecord property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ConditionalDataType }{@code >}
         *     {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}
         *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
         *     {@link JAXBElement }{@code <}{@link NormalizedCurveType }{@code >}
         *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
         *     {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
         *     
         */
        public void setAbstractDataRecord(JAXBElement<? extends AbstractDataRecordEntry> value) {
            this.abstractDataRecord = ((JAXBElement<? extends AbstractDataRecordEntry> ) value);
        }

        /**
         * Gets the value of the abstractDataArray property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractDataArrayEntry> getAbstractDataArray() {
            return abstractDataArray;
        }

        /**
         * Sets the value of the abstractDataArray property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
         *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
         *     {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}
         *     
         */
        public void setAbstractDataArray(JAXBElement<? extends AbstractDataArrayEntry> value) {
            this.abstractDataArray = ((JAXBElement<? extends AbstractDataArrayEntry> ) value);
        }

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
