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

package org.constellation.sml.v101;

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

import org.constellation.sml.IoComponent;
import org.constellation.swe.v101.AbstractDataArrayEntry;
import org.constellation.swe.v101.AbstractDataRecordEntry;
import org.constellation.swe.v101.Category;
import org.constellation.swe.v101.Count;
import org.constellation.swe.v101.CountRange;
import org.constellation.swe.v101.ObservableProperty;
import org.constellation.swe.v101.QuantityType;
import org.constellation.swe.v101.QuantityRange;
import org.constellation.swe.v101.Text;
import org.constellation.swe.v101.TimeType;
import org.constellation.swe.v101.TimeRange;
import org.geotoolkit.util.Utilities;



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
public class IoComponentPropertyType implements IoComponent {

    @XmlElement(name = "Count", namespace = "http://www.opengis.net/swe/1.0.1")
    private Count count;
    @XmlElement(name = "Quantity", namespace = "http://www.opengis.net/swe/1.0.1")
    private QuantityType quantity;
    @XmlElement(name = "Time", namespace = "http://www.opengis.net/swe/1.0.1")
    private TimeType time;
    @XmlElement(name = "Boolean", namespace = "http://www.opengis.net/swe/1.0.1")
    private Boolean _boolean;
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
    private JAXBElement<? extends AbstractDataRecordEntry> abstractDataRecord;
    @XmlElementRef(name = "AbstractDataArray", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataArrayEntry> abstractDataArray;
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

    public IoComponentPropertyType() {

    }

    public IoComponentPropertyType(String name, ObservableProperty observableProperty) {
        this.name = name;
        this.observableProperty = observableProperty;
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
    public void setObservableProperty(ObservableProperty value) {
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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof IoComponentPropertyType) {
            final IoComponentPropertyType that = (IoComponentPropertyType) object;
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
                   Utilities.equals(this.text,         that.text)          &&
                   Utilities.equals(this.observableProperty,  that.observableProperty) &&
                   Utilities.equals(this.type,         that.type);
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

}
