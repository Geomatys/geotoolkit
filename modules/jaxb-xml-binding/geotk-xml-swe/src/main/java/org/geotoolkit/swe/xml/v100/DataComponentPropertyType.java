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

import java.util.logging.Logger;
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
import org.geotoolkit.swe.xml.AbstractBoolean;
import org.geotoolkit.swe.xml.AbstractCategory;
import org.geotoolkit.swe.xml.AbstractConditionalData;
import org.geotoolkit.swe.xml.AbstractConditionalValue;
import org.geotoolkit.swe.xml.AbstractCount;
import org.geotoolkit.swe.xml.AbstractCountRange;
import org.geotoolkit.swe.xml.AbstractCurve;
import org.geotoolkit.swe.xml.AbstractDataArray;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AbstractEnvelope;
import org.geotoolkit.swe.xml.AbstractGeoLocationArea;
import org.geotoolkit.swe.xml.AbstractNormalizedCurve;
import org.geotoolkit.swe.xml.AbstractQuantityRange;
import org.geotoolkit.swe.xml.AbstractSquareMatrix;
import org.geotoolkit.swe.xml.AbstractText;
import org.geotoolkit.swe.xml.AbstractTime;
import org.geotoolkit.swe.xml.AbstractTimeRange;
import org.geotoolkit.swe.xml.DataArray;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.Position;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.swe.xml.Vector;
import org.geotoolkit.util.Utilities;


/**
 * Complex Type for all properties taking the AnyData Group
 * 
 * <p>Java class for DataComponentPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataComponentPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.opengis.net/swe/1.0}AnyData" minOccurs="0"/>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataComponentPropertyType", propOrder = {
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
public class DataComponentPropertyType implements DataComponentProperty {

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

    @XmlTransient
    private JAXBElement<? extends AbstractDataRecordType> hiddenAbstractDataRecord;

    @XmlElementRef(name = "AbstractDataArray", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataArrayType> abstractDataArray;
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

    public DataComponentPropertyType() {

    }

    public DataComponentPropertyType(final String name, final String role, final TimeRange timeRange) {
        this.name      = name;
        this.role      = role;
        this.timeRange = timeRange;
    }

    public DataComponentPropertyType(final TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public DataComponentPropertyType(final String name, final String role, final TimeType time) {
        this.name      = name;
        this.role      = role;
        this.time      = time;
    }

    public DataComponentPropertyType(final TimeType time) {
        this.time      = time;
    }

    public DataComponentPropertyType(final String name, final String role, final QuantityType quantity) {
        this.name      = name;
        this.role      = role;
        this.quantity  = quantity;
    }

    public DataComponentPropertyType(final QuantityType quantity) {
        this.quantity  = quantity;
    }

    public DataComponentPropertyType(final String name, final String role, final BooleanType bool) {
        this.name      = name;
        this.role      = role;
        this._boolean  = bool;
    }

    public DataComponentPropertyType(final BooleanType bool) {
        this._boolean  = bool;
    }

    public DataComponentPropertyType(final String name, final String role, final JAXBElement<? extends AbstractDataRecordType> dataRecord) {
        this.name      = name;
        this.role      = role;
        this.abstractDataRecord = dataRecord;
    }

    public DataComponentPropertyType(final DataRecordType dataRecord) {
        ObjectFactory factory = new ObjectFactory();
        this.abstractDataRecord = factory.createDataRecord(dataRecord);
    }
    
    public DataComponentPropertyType(final String name, final String role, final QuantityRange quantityRange) {
        this.name      = name;
        this.role      = role;
        this.quantityRange = quantityRange;
    }

    public DataComponentPropertyType(final QuantityRange quantityRange) {
        this.quantityRange = quantityRange;
    }

    public DataComponentPropertyType(final DataComponentProperty d) {
        if (d != null){
            this.actuate = d.getActuate();
            if (d.getBoolean() != null) {
                this._boolean = new BooleanType(d.getBoolean());
            }
            if (d.getAbstractRecord() != null) {
                ObjectFactory sweFactory = new ObjectFactory();
                AbstractDataRecord record = d.getAbstractRecord();
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

            if (d.getAbstractArray() != null) {
                ObjectFactory sweFactory = new ObjectFactory();
                AbstractDataArray array = d.getAbstractArray();
                if (array instanceof AbstractCurve) {
                    array = new CurveType((AbstractCurve) array);
                    this.abstractDataArray = sweFactory.createCurve((CurveType) array);
                } else if (array instanceof DataArray) {
                    array = new DataArrayType((DataArray) array);
                    this.abstractDataArray = sweFactory.createDataArray((DataArrayType) array);
                } else if (array instanceof AbstractSquareMatrix) {
                    array = new SquareMatrixType((AbstractSquareMatrix) array);
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
            this.name = d.getName();
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

    public DataComponentPropertyType(final AbstractDataComponent d) {
        if (d != null){
            if (d instanceof AbstractBoolean) {
                this._boolean = new BooleanType((AbstractBoolean)d);
            }
            if (d instanceof AbstractDataRecord) {
                ObjectFactory sweFactory = new ObjectFactory();
                AbstractDataRecord record = (AbstractDataRecord) d;
                if (record instanceof SimpleDataRecord) {
                    record = new SimpleDataRecordType((SimpleDataRecord)record);
                    this.abstractDataRecord = sweFactory.createSimpleDataRecord((SimpleDataRecordType) record);
                } else if (record instanceof DataRecord) {
                    record = new DataRecordType((DataRecord)record);
                    this.abstractDataRecord = sweFactory.createDataRecord((DataRecordType) record);
                } else {
                    throw new IllegalArgumentException("this type is not yet handled in dataComponentPropertyType:" + record);
                }
            }

            if (d instanceof AbstractCategory) {
                this.category = new Category((AbstractCategory) d);
            }
            if (d instanceof AbstractCount) {
                this.count = new Count((AbstractCount) d);
            }
            if (d instanceof AbstractCountRange) {
                this.countRange = new CountRange((AbstractCountRange) d);
            }
            this.name = d.getName();
            if (d instanceof Quantity) {
                this.quantity = new QuantityType((Quantity) d);
            }
            if (d instanceof QuantityRange) {
                this.quantityRange = new QuantityRange((AbstractQuantityRange) d);
            }
            if (d instanceof  AbstractText) {
                this.text = new Text((AbstractText) d);
            }
            if (d instanceof  AbstractTime) {
                this.time = new TimeType((AbstractTime) d);
            }
            if (d instanceof AbstractTimeRange) {
                this.timeRange = new TimeRange((AbstractTimeRange)d);
            }
        }
    }
    
    public void setToHref() {
        if (abstractDataRecord != null) {
            this.href = abstractDataRecord.getValue().getId();
            hiddenAbstractDataRecord = abstractDataRecord;
            abstractDataRecord       = null;
        }
    }

    public void setValue(Object obj) {
        ObjectFactory factory = new ObjectFactory();
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement) obj).getValue();
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
        } else if (obj instanceof EnvelopeType) {
            abstractDataRecord = factory.createEnvelope((EnvelopeType) obj);
        } else if (obj instanceof ConditionalValueType) {
            abstractDataRecord = factory.createConditionalValue((ConditionalValueType) obj);
        } else if (obj instanceof GeoLocationArea) {
            abstractDataRecord = factory.createGeoLocationArea((GeoLocationArea) obj);
        } else if (obj instanceof PositionType) {
            abstractDataRecord = factory.createPosition((PositionType) obj);
        } else if (obj instanceof VectorType) {
            abstractDataRecord = factory.createVector((VectorType) obj);
        } else if (obj instanceof DataArrayType) {
            abstractDataArray = factory.createDataArray((DataArrayType) obj);
        } else if (obj != null) {
            Logger.getLogger("org.geotoolkit.swe.xml.v100").warning("Unimplemented case:" + obj.getClass().getName());
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
     */
    public Count getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     */
    public void setCount(final Count value) {
        this.count = value;
    }

    /**
     * Gets the value of the quantity property.
     */
    public QuantityType getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     */
    public void setQuantity(final QuantityType value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the time property.
     */
    public TimeType getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     */
    public void setTime(final TimeType value) {
        this.time = value;
    }

    /**
     * Gets the value of the boolean property.
     */
    public BooleanType getBoolean() {
        return _boolean;
    }

    /**
     * Sets the value of the boolean property.
     */
    public void setBoolean(final BooleanType value) {
        this._boolean = value;
    }

    /**
     * Gets the value of the category property.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     */
    public void setCategory(final Category value) {
        this.category = value;
    }

    /**
     * Gets the value of the text property.
     */
    public Text getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     */
    public void setText(final Text value) {
        this.text = value;
    }

    /**
     * Gets the value of the quantityRange property.
     */
    public QuantityRange getQuantityRange() {
        return quantityRange;
    }

    /**
     * Sets the value of the quantityRange property.
     */
    public void setQuantityRange(final QuantityRange value) {
        this.quantityRange = value;
    }

    /**
     * Gets the value of the countRange property.
     */
    public CountRange getCountRange() {
        return countRange;
    }

    /**
     * Sets the value of the countRange property.
     */
    public void setCountRange(final CountRange value) {
        this.countRange = value;
    }

    /**
     * Gets the value of the timeRange property.
     */
    public TimeRange getTimeRange() {
        return timeRange;
    }

    /**
     * Sets the value of the timeRange property.
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
    public JAXBElement<? extends AbstractDataRecordType> getAbstractDataRecord() {
        return abstractDataRecord;
    }

    public AbstractDataRecordType getAbstractRecord() {
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
    public void setAbstractDataArray(final JAXBElement<? extends AbstractDataArrayType> value) {
        this.abstractDataArray = ((JAXBElement<? extends AbstractDataArrayType> ) value);
    }

    public AbstractDataArrayType getAbstractArray() {
        if (abstractDataArray != null) {
            return abstractDataArray.getValue();
        }
        return null;
    }

    /**
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     */
    public void setRemoteSchema(final String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     */
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     */
    public void setRole(final String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     */
    public void setArcrole(final String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     */
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     */
    public void setShow(final String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     */
    public void setActuate(final String value) {
        this.actuate = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof DataComponentPropertyType) {
            final DataComponentPropertyType that = (DataComponentPropertyType) object;
            boolean absDataRec = false;
            if (this.abstractDataRecord != null && that.abstractDataRecord != null) {
                absDataRec = Utilities.equals(this.abstractDataRecord.getValue(), that.abstractDataRecord.getValue());
            } else if (this.abstractDataRecord == null && that.abstractDataRecord == null) {
                absDataRec = true;
            }
            boolean absDataArr = false;
            if (this.abstractDataArray != null && that.abstractDataArray != null) {
                absDataArr = Utilities.equals(this.abstractDataArray.getValue(), that.abstractDataArray.getValue());
            } else if (this.abstractDataArray == null && that.abstractDataArray == null) {
                absDataArr = true;
            }
            return Utilities.equals(this.actuate,      that.actuate)       &&
                   Utilities.equals(this.arcrole,      that.arcrole)       &&
                   Utilities.equals(this.href,         that.href)          &&
                   absDataRec                                              &&
                   absDataArr                                              &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema)  &&
                   Utilities.equals(this.role,         that.role)          &&
                   Utilities.equals(this.show,         that.show)          &&
                   Utilities.equals(this.title,        that.title)         &&
                   Utilities.equals(this._boolean,     that._boolean)      &&
                   Utilities.equals(this.category,     that.category)      &&
                   Utilities.equals(this.count,        that.count)         &&
                   Utilities.equals(this.countRange,   that.countRange)    &&
                   Utilities.equals(this.name,         that.name)          &&
                   Utilities.equals(this.quantity,     that.quantity)      &&
                   Utilities.equals(this.quantityRange,that.quantityRange) &&
                   Utilities.equals(this.time,         that.time)          &&
                   Utilities.equals(this.timeRange,    that.timeRange)     &&
                   Utilities.equals(this.type,         that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.count != null ? this.count.hashCode() : 0);
        hash = 17 * hash + (this.quantity != null ? this.quantity.hashCode() : 0);
        hash = 17 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 17 * hash + (this._boolean != null ? this._boolean.hashCode() : 0);
        hash = 17 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 17 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 17 * hash + (this.quantityRange != null ? this.quantityRange.hashCode() : 0);
        hash = 17 * hash + (this.countRange != null ? this.countRange.hashCode() : 0);
        hash = 17 * hash + (this.timeRange != null ? this.timeRange.hashCode() : 0);
        hash = 17 * hash + (this.abstractDataRecord != null ? this.abstractDataRecord.hashCode() : 0);
        hash = 17 * hash + (this.abstractDataArray != null ? this.abstractDataArray.hashCode() : 0);
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 17 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 17 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 17 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 17 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 17 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 17 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 17 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 17 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[DataComponentPropertyType]").append("\n");
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

