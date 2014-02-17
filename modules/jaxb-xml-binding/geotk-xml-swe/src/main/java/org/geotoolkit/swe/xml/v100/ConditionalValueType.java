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
import org.geotoolkit.swe.xml.AbstractConditionalData;
import org.geotoolkit.swe.xml.AbstractConditionalValue;
import org.geotoolkit.swe.xml.AbstractCurve;
import org.geotoolkit.swe.xml.AbstractData;
import org.geotoolkit.swe.xml.AbstractDataArray;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AbstractEnvelope;
import org.geotoolkit.swe.xml.AbstractGeoLocationArea;
import org.geotoolkit.swe.xml.AbstractNormalizedCurve;
import org.geotoolkit.swe.xml.AbstractSquareMatrix;
import org.geotoolkit.swe.xml.DataArray;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.Position;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.swe.xml.Vector;


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
public class ConditionalValueType extends AbstractConditionalType implements AbstractConditionalValue {

    @XmlElement(required = true)
    private ConditionalValueType.Data data;

    public ConditionalValueType() {
        
    }

    public ConditionalValueType(final AbstractConditionalValue cv) {
        super(cv);
        if (cv != null && cv.getData() != null) {
            this.data = new Data(cv.getData());
        }
    }

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
    public void setData(final ConditionalValueType.Data value) {
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
    public static class Data implements AbstractData {

        @XmlElement(name = "Count")
        private Count count;
        @XmlElement(name = "Quantity")
        private QuantityType quantity;
        @XmlElement(name = "Time")
        private TimeType time;
        @XmlElement(name = "Boolean")
        private BooleanType _boolean;
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

        public Data() {

        }

        public Data(final AbstractData d) {
            if (d != null){
                this.actuate = d.getActuate();
                if (d.getBoolean() != null) {
                    this._boolean = new BooleanType(d.getBoolean());
                }
                if (d.getAbstractDataRecord() != null) {
                    ObjectFactory sweFactory = new ObjectFactory();
                    AbstractDataRecord record = d.getAbstractDataRecord();
                    if (record instanceof SimpleDataRecord) {
                        record = new SimpleDataRecordType((SimpleDataRecord)record);
                        this.abstractDataRecord = sweFactory.createSimpleDataRecord((SimpleDataRecordType) record);
                    } else if (record instanceof DataRecord) {
                        record = new DataRecordType((DataRecord)record);
                        this.abstractDataRecord = sweFactory.createDataRecord((DataRecordType) record);
                    } else if (record instanceof AbstractEnvelope) {
                        record = new EnvelopeType((AbstractEnvelope)record);
                        this.abstractDataRecord = sweFactory.createEnvelope((EnvelopeType) record);
                    } else if (record instanceof AbstractGeoLocationArea) {
                        record = new GeoLocationArea((AbstractGeoLocationArea)record);
                        this.abstractDataRecord = sweFactory.createGeoLocationArea((GeoLocationArea) record);
                    } else if (record instanceof AbstractNormalizedCurve) {
                        record = new NormalizedCurveType((AbstractNormalizedCurve)record);
                        this.abstractDataRecord = sweFactory.createNormalizedCurve((NormalizedCurveType) record);
                    } else if (record instanceof Vector) {
                        record = new VectorType((Vector)record);
                        this.abstractDataRecord = sweFactory.createVector((VectorType) record);
                    } else if (record instanceof Position) {
                        record = new PositionType((Position)record);
                        this.abstractDataRecord = sweFactory.createPosition((PositionType) record);
                    } else if (record instanceof AbstractConditionalData) {
                        record = new ConditionalDataType((AbstractConditionalData)record);
                        this.abstractDataRecord = sweFactory.createConditionalData((ConditionalDataType) record);
                    } else if (record instanceof AbstractConditionalValue) {
                        record = new ConditionalValueType((AbstractConditionalValue)record);
                        this.abstractDataRecord = sweFactory.createConditionalValue((ConditionalValueType) record);

                    } else {
                        throw new IllegalArgumentException("this type is not yet handled in dataComponentPropertyType:" + record);
                    }
                }

                if (d.getAbstractDataArray() != null) {
                    ObjectFactory sweFactory = new ObjectFactory();
                    AbstractDataArray array = d.getAbstractDataArray();
                    if (array instanceof AbstractCurve) {
                        array = new CurveType((AbstractCurve)array);
                        this.abstractDataArray = sweFactory.createCurve((CurveType) array);
                    } else if (array instanceof DataArray) {
                        array = new DataArrayType((DataArray)array);
                        this.abstractDataArray = sweFactory.createDataArray((DataArrayType) array);
                    } else if (array instanceof AbstractSquareMatrix) {
                        array = new SquareMatrixType((AbstractSquareMatrix)array);
                        this.abstractDataArray = sweFactory.createSquareMatrix((SquareMatrixType) array);
                    } else {
                        throw new IllegalArgumentException("this type is not yet handled in dataComponentPropertyType:" + array);
                    }
                }

                this.arcrole = d.getArcrole();
                if (d.getCategory() != null) {
                    this.category = new Category(d.getCategory());
                }
                if (d.getCount() != null) {
                    this.count = new Count(d.getCount());
                }
                if (d.getCountRange() != null) {
                    this.countRange = new CountRange(d.getCountRange());
                }
                this.href = d.getHref();
                if (d.getQuantity() != null) {
                    this.quantity = new QuantityType(d.getQuantity());
                }
                if (d.getQuantityRange() != null) {
                    this.quantityRange = new QuantityRange(d.getQuantityRange());
                }
                this.remoteSchema = d.getRemoteSchema();
                this.role = d.getRole();
                this.show = d.getShow();
                if (d.getText() != null) {
                    this.text = new Text(d.getText());
                }
                if (d.getTime() != null) {
                    this.time = new TimeType(d.getTime());
                }
                if (d.getTimeRange() != null) {
                    this.timeRange = new TimeRange(d.getTimeRange());
                }
                this.title = d.getTitle();
                this.type = d.getType();
            }
        }
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
        public void setCount(final Count value) {
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
        public void setQuantity(final QuantityType value) {
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
        public void setTime(final TimeType value) {
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
        public BooleanType getBoolean() {
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
        public void setBoolean(final BooleanType value) {
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
        public void setCategory(final Category value) {
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
        public void setText(final Text value) {
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
        public void setQuantityRange(final QuantityRange value) {
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
        public void setCountRange(final CountRange value) {
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
        public void setTimeRange(final TimeRange value) {
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
        public JAXBElement<? extends AbstractDataRecordType> getJbAbstractDataRecord() {
            return abstractDataRecord;
        }

        public AbstractDataRecordType getAbstractDataRecord() {
            if (abstractDataRecord != null) {
                return abstractDataRecord.getValue();
            }
            return null;
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
        public void setAbstractDataRecord(final JAXBElement<? extends AbstractDataRecordType> value) {
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
        public JAXBElement<? extends AbstractDataArrayType> getJbAbstractDataArray() {
            return abstractDataArray;
        }

        public AbstractDataArrayType getAbstractDataArray() {
            if (abstractDataArray != null) {
                return abstractDataArray.getValue();
            }
            return null;
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
        public void setAbstractDataArray(final JAXBElement<? extends AbstractDataArrayType> value) {
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
        public void setRemoteSchema(final String value) {
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
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(final String value) {
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
        public void setHref(final String value) {
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
        public void setRole(final String value) {
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
        public void setArcrole(final String value) {
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
        public void setTitle(final String value) {
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
        public void setShow(final String value) {
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
        public void setActuate(final String value) {
            this.actuate = value;
        }

    }

}
