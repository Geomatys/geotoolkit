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
import org.geotoolkit.swe.xml.AbstractConditionalValue;
import org.geotoolkit.swe.xml.AbstractDataArray;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AbstractEnvelope;
import org.geotoolkit.swe.xml.AbstractGeoLocationArea;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.Position;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.swe.xml.Vector;
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
    private JAXBElement<? extends AbstractDataRecordType> abstractDataRecord;
    
    @XmlTransient
    private JAXBElement<? extends AbstractDataRecordType> hiddenAbstractDataRecord;
    
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
    
    public static final DataComponentPropertyType LATITUDE_FIELD  = new DataComponentPropertyType("Latitude", null, new QuantityType("urn:ogc:phenomenon:latitude:wgs84", "degree"));

    public static final DataComponentPropertyType LONGITUDE_FIELD = new DataComponentPropertyType("Longitude", null,new QuantityType("urn:ogc:phenomenon:longitude:wgs84", "degree"));

    public static final DataComponentPropertyType FEATURE_FIELD   = new DataComponentPropertyType("FeatureID", null, new Text("urn:ogc:data:feature", null));

    public static final DataComponentPropertyType PRESSION_FIELD  = new DataComponentPropertyType("Pression",  null, new QuantityType("urn:ogc:phenomenon:PRES", "decibar"));
    
    public static final DataComponentPropertyType DEPTH_FIELD     = new DataComponentPropertyType("Depth",  null, new QuantityType("urn:ogc:phenomenon:depth", "metres"));

    public static final DataComponentPropertyType TIME_FIELD      = new DataComponentPropertyType("Time",      null, new TimeType("urn:ogc:data:time:iso8601"));
    
    /**
     * An empty constructor used by JAXB
     */
    DataComponentPropertyType(){
        
    }

    public DataComponentPropertyType(final String name, final String role, final TimeRange timeRange) {
        this.name      = name;
        this.role      = role;
        this.timeRange = timeRange;
    }

    public DataComponentPropertyType(final String name, final String role, final TimeType time) {
        this.name      = name;
        this.role      = role;
        this.time      = time;
    }

    public DataComponentPropertyType(final String name, final String role, final QuantityType quantity) {
        this.name      = name;
        this.role      = role;
        this.quantity  = quantity;
    }

    public DataComponentPropertyType(final String name, final String role, final BooleanType bool) {
        this.name      = name;
        this.role      = role;
        this._boolean  = bool;
    }

    public DataComponentPropertyType(final String name, final String role, final JAXBElement<? extends AbstractDataRecordType> dataRecord) {
        this.name      = name;
        this.role      = role;
        this.abstractDataRecord = dataRecord;
    }

    public DataComponentPropertyType(final String name, final String role, final QuantityRange quantityRange) {
        this.name      = name;
        this.role      = role;
        this.quantityRange = quantityRange;
    }
    
    public DataComponentPropertyType(final String name, final String role, final Category category) {
        this.name      = name;
        this.role      = role;
        this.category  = category;
    }
    
    public DataComponentPropertyType(final String name, final String role, final Text text) {
        this.name      = name;
        this.role      = role;
        this.text      = text;
    }

    public DataComponentPropertyType(final DataComponentProperty d) {
        if (d != null){
            this.actuate = d.getActuate();
            if (d.getBoolean() != null) {
                this._boolean = new BooleanType(d.getBoolean());
            }
            if (d.getAbstractRecord() != null) {
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
                } else if (record instanceof Vector) {
                    record = new VectorType((Vector)record);
                    this.abstractDataRecord = sweFactory.createVector((VectorType) record);
                } else if (record instanceof Position) {
                    record = new PositionType((Position)record);
                    this.abstractDataRecord = sweFactory.createPosition((PositionType) record);
                } else if (record instanceof AbstractConditionalValue) {
                    record = new ConditionalValueType((AbstractConditionalValue)record);
                    this.abstractDataRecord = sweFactory.createConditionalValue((ConditionalValueType) record);

                } else {
                    throw new IllegalArgumentException("this type is not yet handled in dataComponentPropertyType:" + record);
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

    /**
     * 
     */
    public DataComponentPropertyType(final AbstractDataRecordType component, final String name) {
        this.name = name;
        if (component instanceof SimpleDataRecordType) {
            this.abstractDataRecord = sweFactory.createSimpleDataRecord((SimpleDataRecordType)component);
        }else if (component instanceof DataRecordType) {
            this.abstractDataRecord = sweFactory.createDataRecord((DataRecordType)component);
        } else {
            throw new IllegalArgumentException("this type is not yet handled in dataComponentPropertyType:" + component);
        }
    }
    /**
     * Gets the value of the timeGeometricPrimitive property.
      */
    public AbstractDataRecordType getAbstractRecord() {
        if (abstractDataRecord != null) {
            return abstractDataRecord.getValue();
        } else if (hiddenAbstractDataRecord != null){
            return hiddenAbstractDataRecord.getValue();
        }
        return null;
    }

    public void setToHref() {
        if (abstractDataRecord != null) {
            if (abstractDataRecord.getValue().getId() != null) {
                this.setHref(abstractDataRecord.getValue().getId());
            } else if (abstractDataRecord.getValue().getName() != null) {
                this.setHref(abstractDataRecord.getValue().getName());
            } else {
                this.setHref(name);
            }
            this.name = null;
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
        } else if (obj != null ){
            Logger.getLogger("org.geotoolkit.swe.xml.v101").warning("Unimplemented case:" + obj.getClass().getName());
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
        } else {
            return null;
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
        return type;
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
            boolean eq = false;
            final DataComponentPropertyType that = (DataComponentPropertyType) object;
            if (this.abstractDataRecord != null && that.abstractDataRecord != null) {
                eq = Utilities.equals(this.abstractDataRecord.getValue(),that.abstractDataRecord.getValue());
            } else {
                eq = (this.abstractDataRecord == null && that.abstractDataRecord == null);
            }

            return eq                                                                 &&
                   Utilities.equals(this.actuate,            that.actuate)            &&
                   Utilities.equals(this.arcrole,            that.arcrole)            &&
                   Utilities.equals(this.type,               that.type)               &&
                   Utilities.equals(this.href,               that.href)               &&
                   Utilities.equals(this.remoteSchema,       that.remoteSchema)       &&
                   Utilities.equals(this.show,               that.show)               &&
                   Utilities.equals(this.role,               that.role)               &&
                   Utilities.equals(this.title,              that.title)              &&
                   Utilities.equals(this.getBoolean(),       that.getBoolean())       &&
                   Utilities.equals(this.getCategory(),      that.getCategory())      &&
                   Utilities.equals(this.getCount(),         that.getCount())         &&
                   Utilities.equals(this.getCountRange(),    that.getCountRange())    &&
                   Utilities.equals(this.name,               that.name)               &&
                   Utilities.equals(this.getQuantity(),      that.getQuantity())      &&
                   Utilities.equals(this.getQuantityRange(), that.getQuantityRange()) &&
                   Utilities.equals(this.getTime(),          that.getTime())          &&
                   Utilities.equals(this.getText(),          that.getText())          &&
                   Utilities.equals(this.getTimeRange(),     that.getTimeRange());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.getCount() != null ? this.getCount().hashCode() : 0);
        hash = 19 * hash + (this.getQuantity() != null ? this.getQuantity().hashCode() : 0);
        hash = 19 * hash + (this.getTime() != null ? this.getTime().hashCode() : 0);
        hash = 19 * hash + (this.getBoolean() != null ? this.getBoolean().hashCode() : 0);
        hash = 19 * hash + (this.getCategory() != null ? this.getCategory().hashCode() : 0);
        hash = 19 * hash + (this.getText() != null ? this.getText().hashCode() : 0);
        hash = 19 * hash + (this.getQuantityRange() != null ? this.getQuantityRange().hashCode() : 0);
        hash = 19 * hash + (this.getCountRange() != null ? this.getCountRange().hashCode() : 0);
        hash = 19 * hash + (this.getTimeRange() != null ? this.getTimeRange().hashCode() : 0);
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
        if (getBoolean() != null) {
            sb.append("boolean: ").append(getBoolean()).append('\n');
        }
        if (abstractDataRecord != null) {
            sb.append("data record: ").append(abstractDataRecord.getValue()).append('\n');
        }
        if (getCategory() != null) {
            sb.append("category: ").append(getCategory()).append('\n');
        }
        if (getCount() != null) {
            sb.append("count: ").append(getCount()).append('\n');
        }
        if (getCountRange() != null) {
            sb.append("count range: ").append(getCountRange()).append('\n');
        }
        if (name != null) {
            sb.append("name: ").append(name).append('\n');
        }
        if (getQuantity() != null) {
            sb.append("quantity: ").append(getQuantity()).append('\n');
        }
        if (getQuantityRange() != null) {
            sb.append("quantityRange: ").append(getQuantityRange()).append('\n');
        }
        if (getText() != null) {
            sb.append("text: ").append(getText()).append('\n');
        }
        if (getTime() != null) {
            sb.append("time: ").append(getTime()).append('\n');
        }
        if (getTimeRange() != null) {
            sb.append("timeRange: ").append(getTimeRange()).append('\n');
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
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @param href the href to set
     */
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * @param role the role to set
     */
    public void setRole(final String role) {
        this.role = role;
    }

    /**
     * @param arcrole the arcrole to set
     */
    public void setArcrole(final String arcrole) {
        this.arcrole = arcrole;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @param show the show to set
     */
    public void setShow(final String show) {
        this.show = show;
    }

    /**
     * @param actuate the actuate to set
     */
    public void setActuate(final String actuate) {
        this.actuate = actuate;
    }

    /**
     * @return the count
     */
    public Count getCount() {
        return count;
    }

    /**
     * @return the quantity
     */
    public QuantityType getQuantity() {
        return quantity;
    }

    /**
     * @return the time
     */
    public TimeType getTime() {
        return time;
    }

    /**
     * @return the _boolean
     */
    public BooleanType getBoolean() {
        return _boolean;
    }

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @return the text
     */
    public Text getText() {
        return text;
    }

    /**
     * @return the quantityRange
     */
    public QuantityRange getQuantityRange() {
        return quantityRange;
    }

    /**
     * @return the countRange
     */
    public CountRange getCountRange() {
        return countRange;
    }

    /**
     * @return the timeRange
     */
    public TimeRange getTimeRange() {
        return timeRange;
    }

    public AbstractDataArray getAbstractArray() {
        return null;
    }
}

