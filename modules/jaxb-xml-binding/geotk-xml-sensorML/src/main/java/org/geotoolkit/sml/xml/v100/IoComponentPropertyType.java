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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.IoComponent;
import org.geotoolkit.swe.xml.v100.AbstractDataArrayType;
import org.geotoolkit.swe.xml.v100.BooleanType;
import org.geotoolkit.swe.xml.v100.Category;
import org.geotoolkit.swe.xml.v100.Count;
import org.geotoolkit.swe.xml.v100.CountRange;
import org.geotoolkit.swe.xml.v100.ObservableProperty;
import org.geotoolkit.swe.xml.v100.QuantityType;
import org.geotoolkit.swe.xml.v100.QuantityRange;
import org.geotoolkit.swe.xml.v100.Text;
import org.geotoolkit.swe.xml.v100.TimeType;
import org.geotoolkit.swe.xml.v100.TimeRange;
import org.geotoolkit.swe.xml.v100.AbstractDataRecordType;
import org.geotoolkit.swe.xml.v100.DataArrayType;
import org.geotoolkit.swe.xml.v100.DataRecordType;
import org.geotoolkit.swe.xml.v100.SimpleDataRecordType;
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
 *         &lt;group ref="{http://www.opengis.net/swe/1.0}AnyData"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}ObservableProperty"/>
 *       &lt;/choice>
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

    @XmlElement(name = "Count", namespace = "http://www.opengis.net/swe/1.0")
    private Count count;
    @XmlElement(name = "Quantity", namespace = "http://www.opengis.net/swe/1.0")
    private QuantityType quantity;
    @XmlElement(name = "Time", namespace = "http://www.opengis.net/swe/1.0")
    private TimeType time;
    @XmlElement(name = "Boolean", namespace = "http://www.opengis.net/swe/1.0")
    private BooleanType _boolean;
    @XmlElement(name = "Category", namespace = "http://www.opengis.net/swe/1.0")
    private Category category;
    @XmlElement(name = "Text", namespace = "http://www.opengis.net/swe/1.0")
    private Text text;
    @XmlElement(name = "QuantityRange", namespace = "http://www.opengis.net/swe/1.0")
    private QuantityRange quantityRange;
    @XmlElement(name = "CountRange", namespace = "http://www.opengis.net/swe/1.0")
    private CountRange countRange;
    @XmlElement(name = "TimeRange", namespace = "http://www.opengis.net/swe/1.0")
    private TimeRange timeRange;
    @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataRecordType> abstractDataRecord;
    @XmlElementRef(name = "AbstractDataArray", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataArrayType> abstractDataArray;
    @XmlElement(name = "ObservableProperty", namespace = "http://www.opengis.net/swe/1.0")
    private ObservableProperty observableProperty;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String name;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;

    @XmlTransient
    private org.geotoolkit.swe.xml.v100.ObjectFactory factory = new org.geotoolkit.swe.xml.v100.ObjectFactory();

    public IoComponentPropertyType() {

    }

    public IoComponentPropertyType(String name, ObservableProperty observableProperty) {
        this.name = name;
        this.observableProperty = observableProperty;
    }

    public IoComponentPropertyType(String name, QuantityType quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public IoComponentPropertyType(String name, SimpleDataRecordType abstractDataRecord) {
        this.name = name;
        this.abstractDataRecord = factory.createSimpleDataRecord(abstractDataRecord);
    }

    public IoComponentPropertyType(ObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
    }

    public IoComponentPropertyType(QuantityType quantity) {
        this.quantity = quantity;
    }

    public IoComponentPropertyType(SimpleDataRecordType abstractDataRecord) {
        this.abstractDataRecord = factory.createSimpleDataRecord(abstractDataRecord);
    }

    public IoComponentPropertyType(DataRecordType abstractDataRecord) {
        this.abstractDataRecord = factory.createDataRecord(abstractDataRecord);
    }

    public IoComponentPropertyType(String name, JAXBElement<? extends AbstractDataRecordType> abstractDataRecord) {
        this.name = name;
        this.abstractDataRecord = abstractDataRecord;
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
     */
    public void setAbstractDataRecord(JAXBElement<? extends AbstractDataRecordType> value) {
        this.abstractDataRecord = ((JAXBElement<? extends AbstractDataRecordType> ) value);
    }

    /**
     * Gets the value of the abstractDataArray property.
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
     */
    public void setAbstractDataArray(JAXBElement<? extends AbstractDataArrayType> value) {
        this.abstractDataArray = ((JAXBElement<? extends AbstractDataArrayType> ) value);
    }

    /**
     * Gets the value of the observableProperty property.
     */
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    /**
     * Sets the value of the observableProperty property.
     */
    public void setObservableProperty(ObservableProperty value) {
        this.observableProperty = value;
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
     * Gets the value of the nilReason property.
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
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
     * Gets the value of the type property.
     */
    public String getType() {
        return type;
     }

    /**
     * Sets the value of the type property.
     */
    public void setType(String value) {
        this.type = value;
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
        if (nilReason != null) {
            sb.append("nilReason:").append('\n');
            for (String k : nilReason) {
                sb.append("nilReason: ").append(k).append('\n');
            }
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
            return Utilities.equals(this.actuate,             that.actuate)            &&
                   Utilities.equals(this.arcrole,             that.arcrole)            &&
                   Utilities.equals(this.href,                that.href)               &&
                   absDataRec                                                          &&
                   absDataArr                                                          &&
                   Utilities.equals(this.remoteSchema,        that.remoteSchema)       &&
                   Utilities.equals(this.role,                that.role)               &&
                   Utilities.equals(this.show,                that.show)               &&
                   Utilities.equals(this.title,               that.title)              &&
                   Utilities.equals(this._boolean,            that._boolean)           &&
                   Utilities.equals(this.category,            that.category)           &&
                   Utilities.equals(this.count,               that.count)              &&
                   Utilities.equals(this.countRange,          that.countRange)         &&
                   Utilities.equals(this.name,                that.name)               &&
                   Utilities.equals(this.quantity,            that.quantity)           &&
                   Utilities.equals(this.quantityRange,       that.quantityRange)      &&
                   Utilities.equals(this.time,                that.time)               &&
                   Utilities.equals(this.timeRange,           that.timeRange)          &&
                   Utilities.equals(this.text,                that.text)               &&
                   Utilities.equals(this.nilReason,           that.nilReason)          &&
                   Utilities.equals(this.observableProperty,  that.observableProperty) &&
                   Utilities.equals(this.getType(),           that.getType());
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
        hash = 31 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
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
