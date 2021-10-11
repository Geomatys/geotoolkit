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
package org.geotoolkit.sml.xml.v101;

import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.geotoolkit.sml.xml.IoComponent;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.DataArray;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.swe.xml.v101.AbstractDataArrayType;
import org.geotoolkit.swe.xml.v101.AbstractDataRecordType;
import org.geotoolkit.swe.xml.v101.BooleanType;
import org.geotoolkit.swe.xml.v101.Category;
import org.geotoolkit.swe.xml.v101.Count;
import org.geotoolkit.swe.xml.v101.CountRange;
import org.geotoolkit.swe.xml.v101.DataArrayType;
import org.geotoolkit.swe.xml.v101.DataRecordType;
import org.geotoolkit.swe.xml.v101.ObservableProperty;
import org.geotoolkit.swe.xml.v101.QuantityType;
import org.geotoolkit.swe.xml.v101.QuantityRange;
import org.geotoolkit.swe.xml.v101.SimpleDataRecordType;
import org.geotoolkit.swe.xml.v101.Text;
import org.geotoolkit.swe.xml.v101.TimeType;
import org.geotoolkit.swe.xml.v101.TimeRange;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;



/**
 * <p>Java class for IoComponentPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="IoComponentPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;group ref="{http://www.opengis.net/swe/1.0.1}AnyData"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}ObservableProperty"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IoComponentPropertyType", propOrder = {
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
    "abstractDataArray",
    "observableProperty"
})
public class IoComponentPropertyType extends SensorObject implements IoComponent {

    @XmlElement(name = "Count", namespace = "http://www.opengis.net/swe/1.0.1")
    private Count count;
    @XmlElement(name = "Quantity", namespace = "http://www.opengis.net/swe/1.0.1")
    private QuantityType quantity;
    @XmlElement(name = "Time", namespace = "http://www.opengis.net/swe/1.0.1")
    private TimeType time;
    @XmlElement(name = "Boolean", namespace = "http://www.opengis.net/swe/1.0.1")
    private BooleanType _boolean;
    @XmlElement(name = "Category", namespace = "http://www.opengis.net/swe/1.0.1")
    private Category category;
    @XmlElement(name = "Text", namespace = "http://www.opengis.net/swe/1.0.1")
    private Text text;
    @XmlElement(name = "QuantityRange", namespace = "http://www.opengis.net/swe/1.0.1")
    private QuantityRange quantityRange;
    @XmlElement(name = "CountRange", namespace = "http://www.opengis.net/swe/1.0.1")
    private CountRange countRange;
    @XmlElement(name = "TimeRange", namespace = "http://www.opengis.net/swe/1.0.1")
    private TimeRange timeRange;
    @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataRecordType> abstractDataRecord;
    @XmlElementRef(name = "AbstractDataArray", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataArrayType> abstractDataArray;
    @XmlElement(name = "ObservableProperty", namespace = "http://www.opengis.net/swe/1.0.1")
    private ObservableProperty observableProperty;
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

    @XmlTransient
    private org.geotoolkit.swe.xml.v101.ObjectFactory factory = new org.geotoolkit.swe.xml.v101.ObjectFactory();

    public IoComponentPropertyType() {

    }

    public IoComponentPropertyType(final IoComponent io) {
        if (io != null) {
            this.actuate      = io.getActuate();
            this.arcrole      = io.getArcrole();
            this.href         = io.getHref();
            this.remoteSchema = io.getRemoteSchema();
            this.role         = io.getRole();
            this.show         = io.getShow();
            this.title        = io.getTitle();
            this.type         = io.getType();
            this.name         = io.getName();
            if (io.getBoolean() != null) {
                this._boolean = new BooleanType(io.getBoolean());
            }
            if (io.getCategory() != null) {
                this.category = new Category(io.getCategory());
            }
            if (io.getCount() != null) {
                this.count = new Count(io.getCount());
            }
            if (io.getCountRange() != null) {
                this.countRange = new CountRange(io.getCountRange());
            }
            if (io.getObservableProperty() != null) {
                this.observableProperty = new ObservableProperty(io.getObservableProperty());
            }
            if (io.getQuantity() != null) {
                this.quantity = new QuantityType(io.getQuantity());
            }
            if (io.getQuantityRange() != null) {
                this.quantityRange = new QuantityRange(io.getQuantityRange());
            }
            if (io.getText() != null) {
                this.text = new Text(io.getText());
            }
            if (io.getTime() != null) {
                this.time = new TimeType(io.getTime());
            }
            if (io.getTimeRange() != null) {
                this.timeRange = new TimeRange(io.getTimeRange());
            }
            if (io.getDataRecord() != null) {
                AbstractDataRecord record = (AbstractDataRecord) io.getDataRecord();
                if (record instanceof SimpleDataRecord) {
                    abstractDataRecord = factory.createSimpleDataRecord(new SimpleDataRecordType((SimpleDataRecord)record));
                } else if (record instanceof DataRecord) {
                    abstractDataRecord = factory.createDataRecord(new DataRecordType((DataRecord)record));
                } else {
                    System.out.println("UNINPLEMENTED CASE:" + record);
                }
            }
            if (io.getDataArray() instanceof DataArray) {
                    abstractDataArray = factory.createDataArray(new DataArrayType((DataArray)io.getDataArray()));
            }
        }
    }

    public IoComponentPropertyType(final String name, final ObservableProperty observableProperty) {
        this.name               = name;
        this.observableProperty = observableProperty;
    }

    public IoComponentPropertyType(final String name, final QuantityType quantity) {
        this.name     = name;
        this.quantity = quantity;
    }

    public IoComponentPropertyType(final String name, final SimpleDataRecordType abstractDataRecord) {
        this.name = name;
        this.abstractDataRecord = factory.createSimpleDataRecord(abstractDataRecord);
    }

    public IoComponentPropertyType(final String name, final DataRecordType abstractDataRecord) {
        this.name = name;
        this.abstractDataRecord = factory.createDataRecord(abstractDataRecord);
    }

    public void setValue(Object obj) {
        if (obj instanceof JAXBElement) {
             obj = ((JAXBElement)obj).getValue();
        }

        if (obj instanceof Count) {
            count = (Count) obj;
        } else if (obj instanceof QuantityType) {
            quantity = (QuantityType) obj;
        } else if (obj instanceof TimeType) {
            time = (TimeType) obj;
        } else if (obj instanceof BooleanType) {
            _boolean = (BooleanType) obj;
        } else if (obj instanceof Category) {
            category = (Category) obj;
        } else if (obj instanceof Text) {
            text = (Text) obj;
        } else if (obj instanceof ObservableProperty) {
            observableProperty = (ObservableProperty) obj;
        } else if (obj instanceof QuantityRange) {
            quantityRange = (QuantityRange) obj;
        } else if (obj instanceof CountRange) {
            countRange = (CountRange) obj;
        } else if (obj instanceof TimeRange) {
            timeRange = (TimeRange) obj;
        } else if (obj instanceof SimpleDataRecordType) {
            abstractDataRecord = factory.createSimpleDataRecord((SimpleDataRecordType) obj);
        } else if (obj instanceof DataRecordType) {
            abstractDataRecord = factory.createDataRecord((DataRecordType) obj);
        } else if (obj instanceof DataArrayType) {
            abstractDataArray = factory.createDataArray((DataArrayType) obj);

        } else {
            System.out.println("UNINPLEMENTED CASE:" + obj.getClass().getName());
        }
    }

    public Object getValue() {

        if (count != null) {
            return count;
        } else if (quantity != null) {
            return quantity;
        } else if (time != null) {
            return time;
        } else if (_boolean != null) {
            return _boolean;
        } else if (category != null) {
            return category;
        } else if (text != null) {
            return text;
        } else if (observableProperty != null) {
            return observableProperty;
        } else if (countRange != null) {
            return countRange;
        } else if (quantityRange != null) {
            return quantityRange;
        } else if (timeRange != null) {
            return timeRange;
        } else if (abstractDataRecord != null) {
            return abstractDataRecord.getValue();
        } else if (abstractDataArray != null) {
            return abstractDataArray.getValue();
        } else {
            return null;
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
    public JAXBElement<? extends AbstractDataRecordType> getAbstractDataRecord() {
        return abstractDataRecord;
    }

    public AbstractDataRecordType getDataRecord() {
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
    public void setAbstractDataRecord(final JAXBElement<? extends AbstractDataRecordType> value) {
        this.abstractDataRecord = ((JAXBElement<? extends AbstractDataRecordType> ) value);
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
    public JAXBElement<? extends AbstractDataArrayType> getAbstractDataArray() {
        return abstractDataArray;
    }

    public AbstractDataArrayType getDataArray() {
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
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}
     *
     */
    public void setAbstractDataArray(final JAXBElement<? extends AbstractDataArrayType> value) {
        this.abstractDataArray = ((JAXBElement<? extends AbstractDataArrayType> ) value);
    }

    /**
     * Gets the value of the observableProperty property.
     *
     * @return
     *     possible object is
     *     {@link ObservableProperty }
     *
     */
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    /**
     * Sets the value of the observableProperty property.
     *
     * @param value
     *     allowed object is
     *     {@link ObservableProperty }
     *
     */
    public void setObservableProperty(final ObservableProperty value) {
        this.observableProperty = value;
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
    public void setName(final String value) {
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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof IoComponentPropertyType) {
            final IoComponentPropertyType that = (IoComponentPropertyType) object;
            boolean absDataRec = false;
            if (this.abstractDataRecord != null && that.abstractDataRecord != null) {
                absDataRec = Objects.equals(this.abstractDataRecord.getValue(), that.abstractDataRecord.getValue());
            } else if (this.abstractDataRecord == null && that.abstractDataRecord == null) {
                absDataRec = true;
            }
            boolean absDataArr = false;
            if (this.abstractDataArray != null && that.abstractDataArray != null) {
                absDataArr = Objects.equals(this.abstractDataArray.getValue(), that.abstractDataArray.getValue());
            } else if (this.abstractDataArray == null && that.abstractDataArray == null) {
                absDataArr = true;
            }
            return Objects.equals(this.actuate,      that.actuate)       &&
                   Objects.equals(this.arcrole,      that.arcrole)       &&
                   Objects.equals(this.href,         that.href)          &&
                   absDataRec                                              &&
                   absDataArr                                              &&
                   Objects.equals(this.remoteSchema, that.remoteSchema)  &&
                   Objects.equals(this.role,         that.role)          &&
                   Objects.equals(this.show,         that.show)          &&
                   Objects.equals(this.title,        that.title)         &&
                   Objects.equals(this._boolean,     that._boolean)      &&
                   Objects.equals(this.category,     that.category)      &&
                   Objects.equals(this.count,        that.count)         &&
                   Objects.equals(this.countRange,   that.countRange)    &&
                   Objects.equals(this.name,         that.name)          &&
                   Objects.equals(this.quantity,     that.quantity)      &&
                   Objects.equals(this.quantityRange,that.quantityRange) &&
                   Objects.equals(this.time,         that.time)          &&
                   Objects.equals(this.timeRange,    that.timeRange)     &&
                   Objects.equals(this.text,         that.text)          &&
                   Objects.equals(this.observableProperty,  that.observableProperty) &&
                   Objects.equals(this.type,         that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.count != null ? this.count.hashCode() : 0);
        hash = 31 * hash + (this.quantity != null ? this.quantity.hashCode() : 0);
        hash = 31 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 31 * hash + (this._boolean != null ? this._boolean.hashCode() : 0);
        hash = 31 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 31 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 31 * hash + (this.quantityRange != null ? this.quantityRange.hashCode() : 0);
        hash = 31 * hash + (this.countRange != null ? this.countRange.hashCode() : 0);
        hash = 31 * hash + (this.timeRange != null ? this.timeRange.hashCode() : 0);
        hash = 31 * hash + (this.abstractDataRecord != null ? this.abstractDataRecord.hashCode() : 0);
        hash = 31 * hash + (this.abstractDataArray != null ? this.abstractDataArray.hashCode() : 0);
        hash = 31 * hash + (this.observableProperty != null ? this.observableProperty.hashCode() : 0);
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 31 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 31 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 31 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 31 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 31 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 31 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 31 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 31 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[IoComponentPropertyType]").append("\n");
        if (_boolean != null) {
            sb.append("boolean: ").append(_boolean).append('\n');
        }
        if (abstractDataArray != null) {
            sb.append("data array: ").append(abstractDataArray.getValue()).append('\n');
        }
        if (abstractDataRecord != null) {
            sb.append("data record: ").append(abstractDataRecord.getValue()).append('\n');
        }
        if (category != null) {
            sb.append("category: ").append(category).append('\n');
        }
        if (count != null) {
            sb.append("count: ").append(count).append('\n');
        }
        if (countRange != null) {
            sb.append("count range: ").append(countRange).append('\n');
        }
        if (name != null) {
            sb.append("name: ").append(name).append('\n');
        }
        if (observableProperty != null) {
            sb.append("observable property: ").append(observableProperty).append('\n');
        }
        if (quantity != null) {
            sb.append("quantity: ").append(quantity).append('\n');
        }
        if (quantityRange != null) {
            sb.append("quantityRange: ").append(quantityRange).append('\n');
        }
        if (text != null) {
            sb.append("text: ").append(text).append('\n');
        }
        if (time != null) {
            sb.append("time: ").append(time).append('\n');
        }
        if (timeRange != null) {
            sb.append("timeRange: ").append(timeRange).append('\n');
        }
        if (remoteSchema != null) {
            sb.append("remoteSchema: ").append(remoteSchema).append('\n');
        }
        if (actuate != null) {
            sb.append("actuate: ").append(actuate).append('\n');
        }
        if (arcrole != null) {
            sb.append("actuate: ").append(arcrole).append('\n');
        }
        if (href != null) {
            sb.append("href: ").append(href).append('\n');
        }
        if (role != null) {
            sb.append("role: ").append(role).append('\n');
        }
        if (show != null) {
            sb.append("show: ").append(show).append('\n');
        }
        if (title != null) {
            sb.append("title: ").append(title).append('\n');
        }
        if (type != null) {
            sb.append("type: ").append(type).append('\n');
        }
        return sb.toString();
    }

}
