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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.DataComponentProperty;
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

    public DataComponentPropertyType(String name, String role, TimeRange timeRange) {
        this.name      = name;
        this.role      = role;
        this.timeRange = timeRange;
    }

    public DataComponentPropertyType(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public DataComponentPropertyType(String name, String role, TimeType time) {
        this.name      = name;
        this.role      = role;
        this.time      = time;
    }

    public DataComponentPropertyType(TimeType time) {
        this.time      = time;
    }

    public DataComponentPropertyType(String name, String role, QuantityType quantity) {
        this.name      = name;
        this.role      = role;
        this.quantity  = quantity;
    }

    public DataComponentPropertyType(QuantityType quantity) {
        this.quantity  = quantity;
    }

    public DataComponentPropertyType(String name, String role, BooleanType bool) {
        this.name      = name;
        this.role      = role;
        this._boolean  = bool;
    }

    public DataComponentPropertyType(BooleanType bool) {
        this._boolean  = bool;
    }

    public DataComponentPropertyType(String name, String role, JAXBElement<? extends AbstractDataRecordType> dataRecord) {
        this.name      = name;
        this.role      = role;
        this.abstractDataRecord = dataRecord;
    }

    public DataComponentPropertyType(DataRecordType dataRecord) {
        ObjectFactory factory = new ObjectFactory();
        this.abstractDataRecord = factory.createDataRecord(dataRecord);
    }
    
    public DataComponentPropertyType(String name, String role, QuantityRange quantityRange) {
        this.name      = name;
        this.role      = role;
        this.quantityRange = quantityRange;
    }

    public DataComponentPropertyType(QuantityRange quantityRange) {
        this.quantityRange = quantityRange;
    }

    public void setToHref() {
        if (abstractDataRecord != null) {
            this.href = abstractDataRecord.getValue().getId();
            hiddenAbstractDataRecord = abstractDataRecord;
            abstractDataRecord       = null;
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
    public void setCount(Count value) {
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
    public void setQuantity(QuantityType value) {
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
    public void setTime(TimeType value) {
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
    public void setBoolean(BooleanType value) {
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
    public void setCategory(Category value) {
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
    public void setText(Text value) {
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
    public void setQuantityRange(QuantityRange value) {
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
    public void setCountRange(CountRange value) {
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
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(String value) {
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
    public void setRemoteSchema(String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
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
     */
    public void setType(String value) {
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
    public void setHref(String value) {
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
    public void setRole(String value) {
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
    public void setArcrole(String value) {
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
    public void setTitle(String value) {
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
    public void setShow(String value) {
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
    public void setActuate(String value) {
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

