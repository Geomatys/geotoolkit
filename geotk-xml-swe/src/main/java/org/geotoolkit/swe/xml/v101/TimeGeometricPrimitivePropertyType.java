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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;


/**
 * Property type not provided by GML
 *
 * <p>Java class for TimeGeometricPrimitivePropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TimeGeometricPrimitivePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}_TimeGeometricPrimitive"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeGeometricPrimitivePropertyType", propOrder = {
    "timeGeometricPrimitive",
    "timePeriod",
    "timeInstant"
})
public class TimeGeometricPrimitivePropertyType {

    @XmlElement(name = "_TimeGeometricPrimitive", namespace = "http://www.opengis.net/gml")
    private AbstractTimeGeometricPrimitiveType timeGeometricPrimitive;
    @XmlElement(name = "TimePeriod", namespace = "http://www.opengis.net/gml")
    private TimePeriodType timePeriod;
    @XmlElement(name = "TimeInstant", namespace = "http://www.opengis.net/gml")
    private TimeInstantType timeInstant;
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
     * An empty constructor used by JAXB
     */
    TimeGeometricPrimitivePropertyType(){

    }

    /**
     *
     */
    public TimeGeometricPrimitivePropertyType(final AbstractTimeGeometricPrimitiveType time) {
        if (time instanceof TimePeriodType) {
            this.timePeriod = (TimePeriodType) time;
        } else if (time instanceof TimeInstantType) {
            this.timeInstant = (TimeInstantType) time;
        } else {
            this.timeGeometricPrimitive = time;
        }
    }
    /**
     * Gets the value of the timeGeometricPrimitive property.
      */
    public AbstractTimeGeometricPrimitiveType getTimeGeometricPrimitive() {
        if (timeGeometricPrimitive != null) {
            return timeGeometricPrimitive;
        } else if (timeInstant != null) {
            return timeInstant;
        } else if (timePeriod != null) {
            return timePeriod;
        }
        return null;
    }

    /**
     * Sets the value of the timeGeometricPrimitive property.
     */
    public void setTimeGeometricPrimitive(final AbstractTimeGeometricPrimitiveType value) {
        if (value instanceof TimePeriodType) {
            this.timePeriod = (TimePeriodType) value;
            this.timeInstant = null;
            this.timeGeometricPrimitive = null;
        } else if (value instanceof TimeInstantType) {
            this.timePeriod  = null;
            this.timeInstant = (TimeInstantType) value;
            this.timeGeometricPrimitive = null;
        } else {
            this.timePeriod  = null;
            this.timeGeometricPrimitive = value;
            this.timeInstant = null;
        }
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
        if (!(object instanceof TimeGeometricPrimitivePropertyType)) {
            return false;
        }
        final TimeGeometricPrimitivePropertyType that = (TimeGeometricPrimitivePropertyType) object;
        return Objects.equals(this.timeGeometricPrimitive, that.timeGeometricPrimitive) &&
               Objects.equals(this.timeInstant,            that.timeInstant)            &&
               Objects.equals(this.timePeriod,             that.timePeriod)             &&
               Objects.equals(this.actuate,                that.actuate)                &&
               Objects.equals(this.arcrole,                that.arcrole)                &&
               Objects.equals(this.type,                   that.type)                   &&
               Objects.equals(this.href,                   that.href)                   &&
               Objects.equals(this.remoteSchema,           that.remoteSchema)           &&
               Objects.equals(this.show,                   that.show)                   &&
               Objects.equals(this.role,                   that.role)                   &&
               Objects.equals(this.title,                  that.title);
    }


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.timeGeometricPrimitive != null ? this.timeGeometricPrimitive.hashCode() : 0);
        hash = 47 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 47 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 47 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 47 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 47 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 47 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 47 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 47 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (timeGeometricPrimitive != null) {
            s.append(timeGeometricPrimitive).append('\n');
        }
        if (timeInstant != null) {
            s.append(timeInstant).append('\n');
        }
        if (timePeriod != null) {
            s.append(timePeriod).append('\n');
        }
        if(actuate != null) {
            s.append("actuate=").append(actuate).append('\n');
        }
        if(arcrole != null) {
            s.append("arcrole=").append(arcrole).append('\n');
        }
        if(href != null) {
            s.append("href=").append(href).append('\n');
        }
        if(role != null) {
            s.append("role=").append(role).append('\n');
        }
        if(show != null) {
            s.append("show=").append(show).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        return s.toString();
    }

}
