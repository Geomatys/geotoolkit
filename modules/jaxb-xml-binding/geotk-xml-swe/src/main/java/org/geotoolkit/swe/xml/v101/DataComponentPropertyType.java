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
 *
 * @author Guilhem Legal (Geomatys).
 * @module pending
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
        "role",
        "name",
        "remoteSchema",
        "type",
        "href",
        "arcrole",
        "title",
        "show",
        "actuate"
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
    
    @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataRecordEntry> abstractDataRecord;
    
    @XmlTransient
    private JAXBElement<? extends AbstractDataRecordEntry> hiddenAbstractDataRecord;
    
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
    private static final ObjectFactory sweFactory = new ObjectFactory();
    
    /**
     * An empty constructor used by JAXB
     */
    DataComponentPropertyType(){
        
    }

    public DataComponentPropertyType(String name, String role, TimeRange timeRange) {
        this.name      = name;
        this.role      = role;
        this.timeRange = timeRange;
    }

    public DataComponentPropertyType(String name, String role, TimeType time) {
        this.name      = name;
        this.role      = role;
        this.time      = time;
    }

    public DataComponentPropertyType(String name, String role, QuantityType quantity) {
        this.name      = name;
        this.role      = role;
        this.quantity  = quantity;
    }

    public DataComponentPropertyType(String name, String role, BooleanType bool) {
        this.name      = name;
        this.role      = role;
        this._boolean  = bool;
    }

    public DataComponentPropertyType(String name, String role, JAXBElement<? extends AbstractDataRecordEntry> dataRecord) {
        this.name      = name;
        this.role      = role;
        this.abstractDataRecord = dataRecord;
    }

    public DataComponentPropertyType(String name, String role, QuantityRange quantityRange) {
        this.name      = name;
        this.role      = role;
        this.quantityRange = quantityRange;
    }

    /**
     * 
     */
    public DataComponentPropertyType(AbstractDataRecordEntry component, String name) {
        this.name = name;
        if (component instanceof SimpleDataRecordEntry) {
            this.abstractDataRecord = sweFactory.createSimpleDataRecord((SimpleDataRecordEntry)component);
        }else if (component instanceof DataRecordType) {
            this.abstractDataRecord = sweFactory.createDataRecord((DataRecordType)component);
        } else {
            throw new IllegalArgumentException("only SimpleDataRecord is allowed in dataComponentPropertyType");
        }
    }
    /**
     * Gets the value of the timeGeometricPrimitive property.
      */
    public AbstractDataRecordEntry getAbstractRecord() {
        if (abstractDataRecord != null) {
            return abstractDataRecord.getValue();
        } else if (hiddenAbstractDataRecord != null){
            return hiddenAbstractDataRecord.getValue();
        }
        return null;
    }

    public void setToHref() {
        if (abstractDataRecord != null) {
            this.setHref(abstractDataRecord.getValue().getId());
            hiddenAbstractDataRecord = abstractDataRecord;
            abstractDataRecord       = null;
        }
    }
    /**
     * 
     */
    public String getName(){
        return this.name;
    }
    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema() {
        return remoteSchema;
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
     * Gets the value of the href property.
     */
    public String getHref() {
        return href;
    }

    /**
     * Gets the value of the role property.
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
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
            boolean time = false;
            final DataComponentPropertyType that = (DataComponentPropertyType) object;
            if (this.abstractDataRecord != null && that.abstractDataRecord != null) {
                time = Utilities.equals(this.abstractDataRecord.getValue(),that.abstractDataRecord.getValue());
            } else {
                time = (this.abstractDataRecord == null && that.abstractDataRecord == null);
            }

            return time                                                             &&
                   Utilities.equals(this.actuate,            that.actuate)          &&
                   Utilities.equals(this.arcrole,            that.arcrole)          &&
                   Utilities.equals(this.type,               that.type)             &&
                   Utilities.equals(this.href,               that.href)             &&
                   Utilities.equals(this.remoteSchema,       that.remoteSchema)     &&
                   Utilities.equals(this.show,               that.show)             &&
                   Utilities.equals(this.role,               that.role)             &&
                   Utilities.equals(this.title,              that.title)            &&
                   Utilities.equals(this._boolean,     that._boolean)               &&
                   Utilities.equals(this.category,     that.category)      &&
                   Utilities.equals(this.count,        that.count)         &&
                   Utilities.equals(this.countRange,   that.countRange)    &&
                   Utilities.equals(this.name,         that.name)          &&
                   Utilities.equals(this.quantity,     that.quantity)      &&
                   Utilities.equals(this.quantityRange,that.quantityRange) &&
                   Utilities.equals(this.time,         that.time)          &&
                   Utilities.equals(this.timeRange,    that.timeRange);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.count != null ? this.count.hashCode() : 0);
        hash = 19 * hash + (this.quantity != null ? this.quantity.hashCode() : 0);
        hash = 19 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 19 * hash + (this._boolean != null ? this._boolean.hashCode() : 0);
        hash = 19 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 19 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 19 * hash + (this.quantityRange != null ? this.quantityRange.hashCode() : 0);
        hash = 19 * hash + (this.countRange != null ? this.countRange.hashCode() : 0);
        hash = 19 * hash + (this.timeRange != null ? this.timeRange.hashCode() : 0);
        hash = 19 * hash + (this.abstractDataRecord != null ? this.abstractDataRecord.hashCode() : 0);
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 19 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 19 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 19 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 19 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 19 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 19 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 19 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 19 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[DataComponentPropertyType]").append("\n");
        if (_boolean != null) {
            sb.append("boolean: ").append(_boolean).append('\n');
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

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @param arcrole the arcrole to set
     */
    public void setArcrole(String arcrole) {
        this.arcrole = arcrole;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param show the show to set
     */
    public void setShow(String show) {
        this.show = show;
    }

    /**
     * @param actuate the actuate to set
     */
    public void setActuate(String actuate) {
        this.actuate = actuate;
    }
}

